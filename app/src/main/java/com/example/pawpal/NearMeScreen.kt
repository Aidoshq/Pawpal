
package com.example.pawpal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

// VETERINARY CLINIC MODEL
data class VetClinic(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double
)

// HTTP CLIENT
private val vetHttpClient = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(12, TimeUnit.SECONDS)
    .callTimeout(15, TimeUnit.SECONDS)
    .build()

// SEARCH NEARBY VETERINARY CLINICS
private suspend fun findNearbyVets(
    latitude: Double,
    longitude: Double
): List<VetClinic> = withContext(Dispatchers.IO) {

    val query = """
        [out:json][timeout:20];
        (
          nwr["amenity"="veterinary"](around:10000,$latitude,$longitude);
        );
        out center;
    """.trimIndent()

    val servers = listOf(
        "https://overpass.kumi.systems/api/interpreter",
        "https://overpass.private.coffee/api/interpreter"
    )

    var lastError: Exception? = null

    for (server in servers) {
        try {
            val requestBody = FormBody.Builder()
                .add("data", query)
                .build()

            val request = Request.Builder()
                .url(server)
                .post(requestBody)
                .header(
                    "User-Agent",
                    "PawPal-Student-App/1.0"
                )
                .build()

            val body = vetHttpClient
                .newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        throw Exception(
                            "Server error: ${response.code()}"
                        )
                    }

                    response.body()?.string()
                        ?: throw Exception("Empty response")
                }

            val elements = JSONObject(body)
                .getJSONArray("elements")

            val results = mutableListOf<VetClinic>()

            for (i in 0 until elements.length()) {
                val item = elements.getJSONObject(i)
                val tags = item.optJSONObject("tags")
                val center = item.optJSONObject("center")

                val lat = if (item.has("lat")) {
                    item.getDouble("lat")
                } else {
                    center?.optDouble("lat") ?: continue
                }

                val lon = if (item.has("lon")) {
                    item.getDouble("lon")
                } else {
                    center?.optDouble("lon") ?: continue
                }

                val distance = FloatArray(1)

                Location.distanceBetween(
                    latitude,
                    longitude,
                    lat,
                    lon,
                    distance
                )

                val name = tags
                    ?.optString("name")
                    ?.takeIf { it.isNotBlank() }
                    ?: "Veterinary Clinic"

                results.add(
                    VetClinic(
                        id = item.getLong("id"),
                        name = name,
                        latitude = lat,
                        longitude = lon,
                        distanceKm = distance[0] / 1000.0
                    )
                )
            }

            return@withContext results
                .sortedBy { it.distanceKm }
                .take(10)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            lastError = e
        }
    }

    throw Exception(
        "Both servers unavailable: ${lastError?.message}"
    )
}

// NEAR ME SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearMeScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var city by remember {
        mutableStateOf("Unknown")
    }

    var coordinates by remember {
        mutableStateOf<Location?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var clinics by remember {
        mutableStateOf<List<VetClinic>>(emptyList())
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var clinicError by remember {
        mutableStateOf<String?>(null)
    }

    var hasSearched by remember {
        mutableStateOf(false)
    }

    // CHECK LOCATION PERMISSION
    fun hasPermission(): Boolean {
        return listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).any { permission ->

            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // DETECT USER LOCATION
    @SuppressLint("MissingPermission")
    fun loadLocation() {

        if (!hasPermission()) {
            error = "Location permission is required"
            return
        }

        isLoading = true
        error = null
        city = "Unknown"
        coordinates = null
        clinics = emptyList()
        clinicError = null
        hasSearched = false

        val tokenSource = CancellationTokenSource()

        locationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            tokenSource.token
        ).addOnSuccessListener { location ->

            if (location == null) {
                error = "Unable to get current location"
                isLoading = false
                return@addOnSuccessListener
            }

            coordinates = location

            try {
                val geocoder = Geocoder(
                    context,
                    Locale.getDefault()
                )

                if (Build.VERSION.SDK_INT >= 33) {

                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    ) { addresses ->

                        city = addresses
                            .firstOrNull()
                            ?.locality
                            ?: "Unknown city"

                        isLoading = false
                    }

                } else {

                    scope.launch {
                        try {
                            val addresses = withContext(
                                Dispatchers.IO
                            ) {
                                @Suppress("DEPRECATION")
                                geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                )
                            }

                            city = addresses
                                ?.firstOrNull()
                                ?.locality
                                ?: "Unknown city"

                        } catch (e: Exception) {
                            city = "Unknown city"
                        } finally {
                            isLoading = false
                        }
                    }
                }

            } catch (e: Exception) {
                city = "Unknown city"
                isLoading = false
            }

        }.addOnFailureListener { exception ->

            error = "Location error: ${exception.message}"
            isLoading = false
        }
    }

    // REQUEST LOCATION PERMISSION
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            if (permissions.values.any { it }) {
                loadLocation()
            } else {
                error = "Location permission denied"
            }
        }

    // SEARCH CLINICS
    fun searchClinics() {

        val location = coordinates ?: return

        scope.launch {

            isSearching = true
            clinicError = null
            hasSearched = true
            clinics = emptyList()

            try {
                clinics = findNearbyVets(
                    location.latitude,
                    location.longitude
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                clinicError =
                    "Unable to find clinics: ${e.message}"

            } finally {
                isSearching = false
            }
        }
    }

    // USER INTERFACE
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Near Me")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TITLE
            item {
                Text(
                    text = "Find your current location",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // DETECT LOCATION BUTTON
            item {
                Button(
                    onClick = {
                        if (hasPermission()) {
                            loadLocation()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !isLoading && !isSearching,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Detect My Location")
                }
            }

            // LOCATION LOADING
            if (isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            // LOCATION INFORMATION
            coordinates?.let { location ->

                item {
                    Text("City: $city")

                    Text(
                        "Latitude: ${location.latitude}"
                    )

                    Text(
                        "Longitude: ${location.longitude}"
                    )
                }

                // SEARCH VET CLINICS BUTTON
                item {
                    Button(
                        onClick = {
                            searchClinics()
                        },
                        enabled = !isSearching,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Find Nearby Vet Clinics")
                    }
                }
            }

            // LOCATION ERROR
            if (error != null) {
                item {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // CLINIC SEARCH LOADING
            if (isSearching) {
                item {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()

                        Text(
                            "Searching for nearby clinics..."
                        )
                    }
                }
            }

            // CLINIC SEARCH ERROR
            if (clinicError != null) {
                item {
                    Text(
                        text = clinicError!!,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            searchClinics()
                        }
                    ) {
                        Text("Retry Search")
                    }
                }
            }

            // NO CLINICS FOUND
            if (
                hasSearched &&
                !isSearching &&
                clinicError == null &&
                clinics.isEmpty()
            ) {
                item {
                    Text(
                        "No veterinary clinics found within 10 km."
                    )
                }
            }

            // CLINIC LIST
            if (clinics.isNotEmpty()) {

                item {
                    Text(
                        text = "Nearby Vet Clinics",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(
                    items = clinics,
                    key = {
                        "${it.latitude}_${it.longitude}_${it.id}"
                    }
                ) { clinic ->

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = clinic.name,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Distance: %.2f km".format(
                                    Locale.US,
                                    clinic.distanceKm
                                )
                            )

                            OutlinedButton(
                                onClick = {

                                    val uri = Uri.parse(
                                        "https://www.google.com/maps/dir/?api=1" +
                                                "&destination=${clinic.latitude},${clinic.longitude}"
                                    )

                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        uri
                                    )

                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Open in Maps")
                            }
                        }
                    }
                }
            }
        }
    }
}
