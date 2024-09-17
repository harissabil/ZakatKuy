package com.harissabil.zakatkuy.ui.screen.maps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.harissabil.zakatkuy.ui.components.FullscreenLoading
import com.harissabil.zakatkuy.ui.screen.maps.components.MasjidDetailBottomSheet
import com.harissabil.zakatkuy.utils.centerOnLocation
import com.harissabil.zakatkuy.utils.convertPhoneNumberToWhatsAppLink
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    modifier: Modifier = Modifier,
    viewModel: MapsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    val isLocationEnabled by viewModel.isLocationEnabled.collectAsState()
    val locationRequestLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                // User has enabled location
                Timber.d("Location enabled")
                viewModel.requestLocationUpdate()
                viewModel.getNearbyMasjid()
            } else {
                if (!isLocationEnabled) {
                    // If the user cancels, still make a check and then give a snackbar
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState
                            .showSnackbar(
                                message = "Perizinan lokasi dibutuhkan untuk menampilkan masjid terdekat",
                                actionLabel = "Enable",
                                duration = SnackbarDuration.Long
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(intent)
                            }

                            SnackbarResult.Dismissed -> {
                                Timber.d("Snackbar dismissed")
                            }
                        }
                    }
                }
            }
        }

    LaunchedEffect(key1 = isLocationEnabled) {
        if (!isLocationEnabled) {
            viewModel.enableLocationRequest(context = context) {
                locationRequestLauncher.launch(it)
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var isMarkerDetailBottomSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is MapsViewModel.UIEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState()
    val mapProperties = MapProperties(
        isBuildingEnabled = true,
        isMyLocationEnabled = true
    )
    val mapUiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false
    )

    var isLocationCentered by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = state.currentLocation) {
        if (state.currentLocation != null && !isLocationCentered) {
            cameraPositionState.centerOnLocation(state.currentLocation!!)
            isLocationCentered = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Arahin") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.4f)
                .background(MaterialTheme.colorScheme.surface)
        )
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(top = 24.dp)
                .fillMaxHeight(fraction = 0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardColors(
                containerColor = Color(0xFFF5F5F5),
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(modifier)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = mapUiSettings
                ) {
                    state.nearbyMasjid.forEach { masjid ->
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    masjid?.geometry?.location?.lat!!,
                                    masjid.geometry.location.lng!!
                                )
                            ),
                            onClick = {
                                viewModel.getDirection(
                                    selectedMasjid = masjid,
                                    origin = state.currentLocation!!,
                                    destination = LatLng(
                                        masjid.geometry.location.lat,
                                        masjid.geometry.location.lng
                                    )
                                )
                                isMarkerDetailBottomSheetVisible = true
                                true
                            }
                        )
                    }

                    if (state.routePoints?.routePoints?.isNotEmpty() == true) {
                        state.routePoints!!.routePoints.forEach {
                            Polyline(
                                points = it,
                                color = Color(0xFF0F0681),
                            )
                        }
                    }
                }

                if (isMarkerDetailBottomSheetVisible) {
                    state.selectedMasjidDetail?.let {
                        MasjidDetailBottomSheet(
                            sheetState = sheetState,
                            masjidDetail = it,
                            onDismissRequest = {
                                isMarkerDetailBottomSheetVisible = false
                                viewModel.resetSelectedMasjid()
                            },
                            onPhoneNumClick = { phoneNumber ->
                                if (phoneNumber != null) {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setData(
                                        Uri.parse(
                                            convertPhoneNumberToWhatsAppLink(
                                                phoneNumber
                                            )
                                        )
                                    )
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    if (state.isLoading) {
        FullscreenLoading()
    }
}