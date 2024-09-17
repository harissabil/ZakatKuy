package com.harissabil.zakatkuy.ui.screen.catetin

import android.app.Activity
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.harissabil.zakatkuy.core.recorder.AndroidAudioRecorder
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.form.components.FormTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatetinScreen(
    modifier: Modifier = Modifier,
    viewModel: CatetinViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val recorder by lazy {
        AndroidAudioRecorder(context)
    }

    var audioFile: File? by remember { mutableStateOf(null) }

    var isRecording by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CatetinViewModel.UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = state.isSubmitSuccess) {
        if (state.isSubmitSuccess) {
            Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
            onNavigateUp()
        }
    }

    val requestRecordAudioPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted
            } else {
                Toast.makeText(
                    context,
                    "Izin akses mikrofon dibutuhkan untuk menggunakan fitur ini!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(key1 = Unit) {
        if (!checkPermission(android.Manifest.permission.RECORD_AUDIO)) {
            requestRecordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val currentText = state.speechToTextResult ?: ""
                viewModel.onSpeechToTextResult(currentText + result?.get(0) + " ")
            } else {
                Toast.makeText(context, "Voice input failed", Toast.LENGTH_SHORT).show()
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Color.Transparent,
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 16.dp
                )
                .padding(innerPadding)
                .then(modifier),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Catet Zakat",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Sebutkan informasi Anda dalam urutan berikut: Nama yang mewakili, Nama pembayar, Nama yang dizakatkan, Alamat pembayar, Tanggal bayar, Nominal zakat, Jenis zakat, dan keterangan tambahan.",
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (state.zakatDocumentation == null) {
                        Text(
                            text = state.speechToTextResult ?: "",
                            fontSize = 16.sp,
                        )
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.nama_yang_mewakili ?: "",
                            onValueChange = viewModel::onNamaYangMewakiliChanged,
                            placeHolder = "Nama yang Mewakili",
                            conditionCheck = { it.isNotEmpty() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.nama_pembayar ?: "",
                            onValueChange = viewModel::onNamaPembayarChanged,
                            placeHolder = "Nama Pembayar",
                            conditionCheck = { it.isNotEmpty() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.nama_yang_dibayarkan_zakat ?: "",
                            onValueChange = viewModel::onNamaYangDibayarkanZakatChanged,
                            placeHolder = "Nama yang Dizakatkan",
                            conditionCheck = { it.isNotEmpty() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.alamat_pembayar ?: "",
                            onValueChange = viewModel::onAlamatPembayarChanged,
                            placeHolder = "Alamat Pembayar",
                            conditionCheck = { it.isNotEmpty() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.nominal_zakat?.toString() ?: "",
                            onValueChange = {
                                viewModel.onNominalZakatChanged(
                                    it.toLongOrNull() ?: 0L
                                )
                            },
                            placeHolder = "Nominal Zakat",
                            conditionCheck = { it.isNotEmpty() && it.all { char -> char.isDigit() } }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.jenis_zakat ?: "",
                            onValueChange = viewModel::onJenisZakatChanged,
                            placeHolder = "Jenis Zakat",
                            conditionCheck = { it.isNotEmpty() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormTextField(
                            textValue = state.zakatDocumentation!!.keterangan ?: "",
                            onValueChange = viewModel::onKeteranganChanged,
                            placeHolder = "Keterangan",
                            conditionCheck = { it.isNotEmpty() }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
//                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//                    putExtra(
//                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//                    )
//                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id_ID") // Set bahasa Indonesia
//                    putExtra(
//                        RecognizerIntent.EXTRA_PROMPT,
//                        "Silakan berbicara..."
//                    ) // Pesan prompt untuk user
//                    putExtra(
//                        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
//                        10000L
//                    ) // 10 detik setelah benar-benar diam
//                    putExtra(
//                        RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
//                        10000L
//                    ) // 5 detik untuk jeda
//                    putExtra(
//                        RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
//                        10000L
//                    ) // Minimal 3 detik input aktif
//                }
//                launcher.launch(intent)
                    if (isRecording) {
                        recorder.stop()
                        scope.launch {
                            delay(1000)
                            isRecording = false
                            audioFile?.let { viewModel.onUploadFile(it) }
                            audioFile = null
                        }
                    } else {
                        File(
                            context.cacheDir,
                            "audio_${Clock.System.now().toEpochMilliseconds()}.mp3"
                        ).also {
                            recorder.start(it)
                            audioFile = it
                        }
                        isRecording = true
                    }
                },
                colors = if (!isRecording) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = if (!isRecording) Icons.Outlined.Mic else Icons.Default.Stop,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (!isRecording) "Mulai Deteksi Suara" else "Selesai Deteksi Suara")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = viewModel::aiCorrection,
                    enabled = state.speechToTextResult?.isNotEmpty() == true
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Koreksi AI")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::onSubmit,
                    enabled = state.zakatDocumentation != null
                ) {
                    Text(text = "Submit")
                }
            }
        }
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}