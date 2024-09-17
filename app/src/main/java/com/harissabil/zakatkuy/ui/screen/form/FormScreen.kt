package com.harissabil.zakatkuy.ui.screen.form

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.harissabil.zakatkuy.R
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.form.components.CropperTopAppBar
import com.harissabil.zakatkuy.ui.screen.form.components.FormTextField
import com.harissabil.zakatkuy.ui.theme.ZakatKuyTheme
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    viewModel: FormViewModel = hiltViewModel(),
    onNavigateToHomeScreen: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is FormViewModel.UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = state.isSuccessful) {
        if (state.isSuccessful) {
            onNavigateToHomeScreen()
        }
    }

    val directory = File(context.cacheDir, "images")
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val authority = stringResource(id = R.string.fileprovider)

    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState
    if (cropState != null) ImageCropperDialog(
        state = cropState,
        topBar = { CropperTopAppBar(cropState) }
    )

    // for takePhotoLauncher used
    fun getTempUri(): Uri? {
        directory.let {
            it.mkdirs()
            val file = File.createTempFile(
                "image_" + System.currentTimeMillis().toString(),
                ".jpg",
                it
            )

            return FileProvider.getUriForFile(
                context,
                authority,
                file
            )
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { _ ->
            tempUri.value?.let { imageUri ->
                scope.launch {
                    val result = imageCropper.crop(
                        uri = imageUri,
                        context = context
                    )
                    when (result) {
                        CropError.LoadingError -> {
                            tempUri.value = null
                        }

                        CropError.SavingError -> {
                            tempUri.value = null
                        }

                        CropResult.Cancelled -> {
                            tempUri.value = null
                        }

                        is CropResult.Success -> {
                            viewModel.ocrFromKtp(
                                ktpImage = result.bitmap.asAndroidBitmap(),
                            )
                        }
                    }
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, launch takePhotoLauncher
            val tmpUri = getTempUri()
            tempUri.value = tmpUri
            tempUri.value?.let { takePhotoLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        FormContent(
            modifier = modifier.padding(innerPadding),
            state = state,
            onScanFromKtp = {
                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission is already granted, proceed to step 2
                    val tmpUri = getTempUri()
                    tempUri.value = tmpUri
                    tempUri.value?.let { takePhotoLauncher.launch(it) }
                } else {
                    // Permission is not granted, request it
                    cameraPermissionLauncher.launch(permission)
                }
            },
            onSubmitData = viewModel::onSubmitData,
            onNikChanged = viewModel::onNikChanged,
            onNamaChanged = viewModel::onNamaChanged,
            onTtlChanged = viewModel::onTtlChanged,
            onJenisKelaminChanged = viewModel::onJenisKelaminChanged,
            onAlamatChanged = viewModel::onAlamatChanged,
            onRtrwChanged = viewModel::onRtrwChanged,
            onKelurahanChanged = viewModel::onKelurahanChanged,
            onKecamatanChanged = viewModel::onKecamatanChanged,
            onAgamaChanged = viewModel::onAgamaChanged,
            onStatusPerkawinanChanged = viewModel::onStatusPerkawinanChanged,
            onPekerjaanChanged = viewModel::onPekerjaanChanged,
            onKewarganegaraanChanged = viewModel::onKewarganegaraanChanged,
            onTujuanZakatChanged = viewModel::onTujuanZakatChanged,
        )
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}

@Composable
fun FormContent(
    modifier: Modifier = Modifier,
    state: FormState,
    onScanFromKtp: () -> Unit,
    onSubmitData: () -> Unit,
    onNikChanged: (String) -> Unit,
    onNamaChanged: (String) -> Unit,
    onTtlChanged: (String) -> Unit,
    onJenisKelaminChanged: (String) -> Unit,
    onAlamatChanged: (String) -> Unit,
    onRtrwChanged: (String) -> Unit,
    onKelurahanChanged: (String) -> Unit,
    onKecamatanChanged: (String) -> Unit,
    onAgamaChanged: (String) -> Unit,
    onStatusPerkawinanChanged: (String) -> Unit,
    onPekerjaanChanged: (String) -> Unit,
    onKewarganegaraanChanged: (String) -> Unit,
    onTujuanZakatChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
            .then(modifier),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Lengkapi Data Yuk!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Data akan digunakan oleh amil untuk membantu dalam memudahkan proses distribusi zakat.",
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
                FormTextField(
                    textValue = state.nik ?: "",
                    onValueChange = onNikChanged,
                    placeHolder = "NIK",
                    conditionCheck = { it.isNotEmpty() && it.length == 16 }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.nama ?: "",
                    onValueChange = onNamaChanged,
                    placeHolder = "Nama Lengkap",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.ttl ?: "",
                    onValueChange = onTtlChanged,
                    placeHolder = "Tempat, Tanggal Lahir",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.jenisKelamin ?: "",
                    onValueChange = onJenisKelaminChanged,
                    placeHolder = "Jenis Kelamin",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.alamat ?: "",
                    onValueChange = onAlamatChanged,
                    placeHolder = "Alamat",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.rtrw ?: "",
                    onValueChange = onRtrwChanged,
                    placeHolder = "RT/RW",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.kelurahan ?: "",
                    onValueChange = onKelurahanChanged,
                    placeHolder = "Kelurahan",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.kecamatan ?: "",
                    onValueChange = onKecamatanChanged,
                    placeHolder = "Kecamatan",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.agama ?: "",
                    onValueChange = onAgamaChanged,
                    placeHolder = "Agama",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.statusPerkawinan ?: "",
                    onValueChange = onStatusPerkawinanChanged,
                    placeHolder = "Status Perkawinan",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.pekerjaan ?: "",
                    onValueChange = onPekerjaanChanged,
                    placeHolder = "Pekerjaan",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.kewarganegaraan ?: "",
                    onValueChange = onKewarganegaraanChanged,
                    placeHolder = "Kewarganegaraan",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                FormTextField(
                    textValue = state.tujuanZakat ?: "",
                    onValueChange = onTujuanZakatChanged,
                    placeHolder = "Tujuan Zakat",
                    conditionCheck = { it.isNotEmpty() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(onClick = onScanFromKtp) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Pindai KTP")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onSubmitData
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FormContentPreview() {
    ZakatKuyTheme {
        Surface(
            color = if (isSystemInDarkTheme()) Color.Black else Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            FormContent(
                state = FormState(),
                onScanFromKtp = {},
                onSubmitData = {},
                onNikChanged = {},
                onNamaChanged = {},
                onTtlChanged = {},
                onJenisKelaminChanged = {},
                onAlamatChanged = {},
                onRtrwChanged = {},
                onKelurahanChanged = {},
                onKecamatanChanged = {},
                onAgamaChanged = {},
                onStatusPerkawinanChanged = {},
                onPekerjaanChanged = {},
                onKewarganegaraanChanged = {},
                onTujuanZakatChanged = {},
            )
        }
    }
}