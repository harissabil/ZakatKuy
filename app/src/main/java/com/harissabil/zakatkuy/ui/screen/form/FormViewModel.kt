package com.harissabil.zakatkuy.ui.screen.form

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercute.share.data.auth.AuthRepository
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.harissabil.zakatkuy.data.Resource
import com.harissabil.zakatkuy.data.firestore.FirestoreRepository
import com.harissabil.zakatkuy.data.firestore.models.UserData
import com.harissabil.zakatkuy.data.gemini.GeminiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val geminiClient: GeminiClient,
) : ViewModel() {

    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow: SharedFlow<UIEvent> = _eventFlow.asSharedFlow()

    fun onNikChanged(nik: String) {
        _state.update { it.copy(nik = nik) }
    }

    fun onNamaChanged(nama: String) {
        _state.update { it.copy(nama = nama) }
    }

    fun onTtlChanged(ttl: String) {
        _state.update { it.copy(ttl = ttl) }
    }

    fun onJenisKelaminChanged(jenisKelamin: String) {
        _state.update { it.copy(jenisKelamin = jenisKelamin) }
    }

    fun onAlamatChanged(alamat: String) {
        _state.update { it.copy(alamat = alamat) }
    }

    fun onRtrwChanged(rtrw: String) {
        _state.update { it.copy(rtrw = rtrw) }
    }

    fun onKelurahanChanged(kelurahan: String) {
        _state.update { it.copy(kelurahan = kelurahan) }
    }

    fun onKecamatanChanged(kecamatan: String) {
        _state.update { it.copy(kecamatan = kecamatan) }
    }

    fun onAgamaChanged(agama: String) {
        _state.update { it.copy(agama = agama) }
    }

    fun onStatusPerkawinanChanged(statusPerkawinan: String) {
        _state.update { it.copy(statusPerkawinan = statusPerkawinan) }
    }

    fun onPekerjaanChanged(pekerjaan: String) {
        _state.update { it.copy(pekerjaan = pekerjaan) }
    }

    fun onKewarganegaraanChanged(kewarganegaraan: String) {
        _state.update { it.copy(kewarganegaraan = kewarganegaraan) }
    }

    fun onTujuanZakatChanged(tujuanZakat: String) {
        _state.update { it.copy(tujuanZakat = tujuanZakat) }
    }

    fun ocrFromKtp(ktpImage: Bitmap) {
        _state.update { it.copy(isLoading = true) }

        val generativeModel = geminiClient.geneminiBaseFlashModel

        val inputContent = content {
            image(ktpImage)
            text(
                """
        Extract the following information from the Indonesian KTP (Identity Card) image:
        - NIK (Nomor Induk Kependudukan)
        - Nama Lengkap (Full Name)
        - Tempat/Tanggal Lahir (Place/Date of Birth)
        - Jenis Kelamin (Gender)
        - Alamat (Address)
        - RT/RW
        - Kelurahan/Desa
        - Kecamatan
        - Agama (Religion)
        - Status Perkawinan (Marital Status)
        - Pekerjaan (Occupation)
        - Kewarganegaraan (Citizenship)
        
        Return the information in the following JSON format:
        {
            "nik": "<NIK>",
            "nama": "<Full Name>",
            "ttl": "<Place/Date of Birth>",
            "jenisKelamin": "<Gender>",
            "alamat": "<Address>",
            "rtrw": "<RT/RW>",
            "kelurahan": "<Kelurahan/Desa>",
            "kecamatan": "<Kecamatan>",
            "agama": "<Religion>",
            "statusPerkawinan": "<Marital Status>",
            "pekerjaan": "<Occupation>",
            "kewarganegaraan": "<Citizenship>"
        }
        
        If any of the information is unclear or not present on the KTP, please fill its value with `null`.
        """.trimIndent()
            )
        }

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(inputContent)
                Timber.i("Response KTP: ${response.text}")

                val gson = Gson()
                val ktpData = gson.fromJson(response.text, KtpData::class.java)

                ktpData?.let {
                    _state.update {
                        it.copy(
                            nik = ktpData.nik ?: "",
                            nama = ktpData.nama ?: "",
                            ttl = ktpData.ttl ?: "",
                            jenisKelamin = ktpData.jenisKelamin ?: "",
                            alamat = ktpData.alamat ?: "",
                            rtrw = ktpData.rtrw ?: "",
                            kelurahan = ktpData.kelurahan ?: "",
                            kecamatan = ktpData.kecamatan ?: "",
                            agama = ktpData.agama ?: "",
                            statusPerkawinan = ktpData.statusPerkawinan ?: "",
                            pekerjaan = ktpData.pekerjaan ?: "",
                            kewarganegaraan = ktpData.kewarganegaraan ?: "",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.tag("Generated Question").e(e)
                _eventFlow.emit(UIEvent.ShowSnackbar("Gagal mengambil data dari KTP: ${e.message}"))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSubmitData() = viewModelScope.launch {
        if (_state.value.nik.isNullOrBlank() || _state.value.nama.isNullOrBlank() || _state.value.ttl.isNullOrBlank() || _state.value.jenisKelamin.isNullOrBlank() || _state.value.alamat.isNullOrBlank() || _state.value.rtrw.isNullOrBlank() || _state.value.kelurahan.isNullOrBlank() || _state.value.kecamatan.isNullOrBlank() || _state.value.agama.isNullOrBlank() || _state.value.statusPerkawinan.isNullOrBlank() || _state.value.pekerjaan.isNullOrBlank() || _state.value.kewarganegaraan.isNullOrBlank() || _state.value.tujuanZakat.isNullOrBlank()) {
            _eventFlow.emit(UIEvent.ShowSnackbar("Mohon isi semua kolom!"))
            return@launch
        }

        _state.update { it.copy(isLoading = true) }

        val result = firestoreRepository.addUserData(
            UserData(
                nik = _state.value.nik!!,
                nama = _state.value.nama!!,
                ttl = _state.value.ttl!!,
                jenisKelamin = _state.value.jenisKelamin!!,
                alamat = _state.value.alamat!!,
                rtrw = _state.value.rtrw!!,
                kelurahan = _state.value.kelurahan!!,
                kecamatan = _state.value.kecamatan!!,
                agama = _state.value.agama!!,
                statusPerkawinan = _state.value.statusPerkawinan!!,
                pekerjaan = _state.value.pekerjaan!!,
                kewarganegaraan = _state.value.kewarganegaraan!!,
                tujuanZakat = _state.value.tujuanZakat!!
            )
        )

        when (result) {
            is Resource.Error -> {
                _eventFlow.emit(UIEvent.ShowSnackbar(result.message ?: "Terjadi kesalahan!"))
                _state.update { it.copy(isLoading = false) }
            }

            is Resource.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }

            is Resource.Success -> {
                _eventFlow.emit(UIEvent.ShowSnackbar("Data berhasil disimpan!"))
                _state.update { it.copy(isLoading = false, isSuccessful = true) }
            }
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }
}