package com.harissabil.zakatkuy.ui.screen.catetin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.content
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import com.harissabil.zakatkuy.data.firestore.models.ZakatDocumentation
import com.harissabil.zakatkuy.data.gemini.GeminiClient
import com.harissabil.zakatkuy.data.groq.GroqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CatetinViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val geminiClient: GeminiClient,
    private val groqRepository: GroqRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatetinState())
    val state: StateFlow<CatetinState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    fun onSpeechToTextResult(result: String) {
        _state.update { it.copy(speechToTextResult = result) }
    }

    fun onUploadFile(audioFile: File) {
        if (_state.value.isLoading) {
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val response = groqRepository.uploadAudio(
                file = MultipartBody.Part.createFormData(
                    "file",
                    audioFile.name,
                    audioFile.asRequestBody()
                )
            )
            _state.update { it.copy(isLoading = false) }

            if (response.isSuccessful) {
                val currentText = _state.value.speechToTextResult
                val textResult = response.body()?.text
                _state.update {
                    it.copy(
                        speechToTextResult = if (currentText.isNullOrBlank()) {
                            textResult
                        } else {
                            "$currentText\n$textResult"
                        }
                    )
                }
            } else {
                _eventFlow.tryEmit(UIEvent.ShowSnackbar("Gagal mengupload file audio!"))
            }
        }
    }

    fun onNamaYangMewakiliChanged(namaYangMewakili: String) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(nama_yang_mewakili = namaYangMewakili)) }
    }

    fun onNamaPembayarChanged(namaPembayar: String) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(nama_pembayar = namaPembayar)) }
    }

    fun onNamaYangDibayarkanZakatChanged(namaYangDibayarkanZakat: String) {
        _state.update {
            it.copy(
                zakatDocumentation = it.zakatDocumentation?.copy(
                    nama_yang_dibayarkan_zakat = namaYangDibayarkanZakat
                )
            )
        }
    }

    fun onAlamatPembayarChanged(alamatPembayar: String) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(alamat_pembayar = alamatPembayar)) }
    }

    fun onNominalZakatChanged(nominalZakat: Long) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(nominal_zakat = nominalZakat)) }
    }

    fun onJenisZakatChanged(jenisZakat: String) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(jenis_zakat = jenisZakat)) }
    }

    fun onKeteranganChanged(keterangan: String) {
        _state.update { it.copy(zakatDocumentation = it.zakatDocumentation?.copy(keterangan = keterangan)) }
    }

    fun aiCorrection() {
        _state.update { it.copy(isLoading = true) }

        val generativeModel = geminiClient.geneminiBaseFlashModel

        val inputContent = content {
            text(_state.value.speechToTextResult!!)
            text(
                """
    Kamu adalah admin pencatat yang bertugas mencatat data-data terkait pembayaran zakat.
    Extract the following information for Zakat Documentation:
    - Nama yang Mewakili, adalah nama yang mewakili keluarga yang membayar zakat
    - Nama Pembayar, adalah nama yang membayar zakat
    - Nama yang Dizakatkan, adalah nama yang dizakatkan
    - Alamat Pembayar, adalah alamat pembayar
    - Tanggal Pembayaran, isi dengan nilai `null`
    - Nominal Zakat, adalah nominal zakat yang dibayarkan pastikan dalam bentuk angka Long (Example: 100000)
    - Jenis Zakat, adalah jenis zakat yang dibayarkan misal 'Zakat Fitrah' atau 'Zakat Mal'
    - Keterangan (Example: "Mewakili keluarga" or "Sisa beras disumbangkan")

    Return the information in the following JSON format:
    {
        "id": null,
        "email": null,
        "nama_yang_mewakili": "<Nama yang Mewakili>",
        "nama_pembayar": "<Nama Pembayar>",
        "nama_yang_dibayarkan_zakat": "<Nama yang Dizakatkan>",
        "alamat_pembayar": "<Alamat Pembayar>",
        "tanggal_pembayaran": null,
        "nominal_zakat": "<Nominal Zakat>",
        "jenis_zakat": "<Jenis Zakat>",
        "keterangan": "<Keterangan>"
    }
    
    If any information is unclear or missing, replace its value with `null`.
""".trimIndent()
            )
        }

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(inputContent)
                Timber.i("Response Zakat Documentation: ${response.text}")

                val gson = Gson()
                val zakatDocumentation =
                    gson.fromJson(response.text, ZakatDocumentation::class.java)

                _state.update {
                    it.copy(
                        isLoading = false,
                        zakatDocumentation = zakatDocumentation.copy(
                            id = UUID.randomUUID().toString(),
                            tanggal_pembayaran = Timestamp.now()
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                _eventFlow.emit(UIEvent.ShowSnackbar("Gagal mengambil data dari KTP: ${e.message}"))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSubmit() {
        if (_state.value.zakatDocumentation == null) {
            _eventFlow.tryEmit(UIEvent.ShowSnackbar("Please correct the AI first!"))
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = firestoreRepository.addZakatDocumentation(
                _state.value.zakatDocumentation!!
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSubmitSuccess = true) }
                }

                is Resource.Error -> {
                    Timber.e("Error: ${result.message}")
                    _eventFlow.emit(UIEvent.ShowSnackbar("Gagal menambahkan Zakat Documentation: ${result.message}"))
                    _state.update { it.copy(isLoading = false) }
                }

                is Resource.Loading -> {}
            }
        }

    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}