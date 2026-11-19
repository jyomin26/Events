
package com.android.events.details

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.android.events.R
import com.android.events.domain.model.Event
import com.android.events.domain.model.UserLocation
import com.android.events.utils.formatEventDateTime
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val context = LocalContext.current

    var permissionDenied by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        permissionDenied = !granted

        if (granted) {
            viewModel.loadCurrentLocation()
        }
    }

    fun requestLocationPermission() {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            permissionDenied = false
            viewModel.loadCurrentLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Automatically request permission when the screen opens.
    LaunchedEffect(Unit) {
        requestLocationPermission()
    }

    when (event) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val currentEvent = (event as UiState.Success<Event>).data
            EventDetailContent(
                event = currentEvent,
                userLocation = userLocation,
                permissionDenied = permissionDenied,
                onRequestLocation = ::requestLocationPermission,
                onNavigate = {
                    val uri = (
                            "geo:${currentEvent.latitude},${currentEvent.longitude}" +
                                    "?q=${currentEvent.latitude},${currentEvent.longitude}(${currentEvent.title})"
                            ).toUri()

                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_VIEW, uri),
                            context.getString(R.string.navigate_to_event)
                        )
                    )
                }
            )
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text((event as UiState.Error).message)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.loadEvent(eventId) }
                ) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventDetailContent(
    event: Event,
    userLocation: UserLocation?,
    permissionDenied: Boolean,
    onRequestLocation: () -> Unit,
    onNavigate: () -> Unit
) {
    val distance = userLocation?.let {
        calculateDistance(
            it.latitude,
            it.longitude,
            event.latitude,
            event.longitude
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = event.imageUrl,
            contentDescription = event.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )


        Text(
            text = event.location,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = formatEventDateTime(event.time),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )


        when {
            distance != null -> {
                Text(
                    text = stringResource(R.string.distance_from_your_current_location_1f_km).format(distance),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            permissionDenied -> {
                Text(
                    text = stringResource(R.string.location_denied_message_for_distance),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Button(
                    onClick = onRequestLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(stringResource(R.string.give_permission))
                }
            }

            else -> {
                Text(
                    text = stringResource(R.string.getting_your_current_location),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(28.dp)
                )
            }
        }

        Button(
            onClick = onNavigate,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(stringResource(R.string.navigate_to_event))
        }
    }
}

private fun calculateDistance(
    startLatitude: Double,
    startLongitude: Double,
    endLatitude: Double,
    endLongitude: Double
): Double {
    val earthRadiusKm = 6371.0

    val latitudeDifference =
        Math.toRadians(endLatitude - startLatitude)

    val longitudeDifference =
        Math.toRadians(endLongitude - startLongitude)

    val a = sin(latitudeDifference / 2).pow(2) +
            cos(Math.toRadians(startLatitude)) *
            cos(Math.toRadians(endLatitude)) *
            sin(longitudeDifference / 2).pow(2)

    return earthRadiusKm * 2 * atan2(sqrt(a), sqrt(1 - a))
}
