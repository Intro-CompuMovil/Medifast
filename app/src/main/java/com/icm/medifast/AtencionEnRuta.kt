package com.icm.medifast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.icm.medifast.databinding.ActivityAtencionEnRutaBinding
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.TilesOverlay
import java.io.IOException

class AtencionEnRuta : AppCompatActivity() {

    private lateinit var binding: ActivityAtencionEnRutaBinding

    //Variablers para el sensor de luz
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var sensorEventListener: SensorEventListener
    lateinit var roadManager: RoadManager
    private var roadOverlay:Polyline? = null
    private var PosDoctor = GeoPoint(4.6327, -74.0677)

    // crear la variable para ver pedir la ubicacion
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val lastLocation = locationResult.lastLocation

            // Obtener la ubicación actual
            if (lastLocation != null){
                val currentLocation = GeoPoint(lastLocation.latitude, lastLocation.longitude)
                val mapController: IMapController = binding.map.controller
                mapController.setZoom(18.0)
                mapController.setCenter(currentLocation)
                val markerPosActual = createMarker(currentLocation,"Mi ubicación",R.drawable.baseline_health_and_safety_24)
                binding.map.overlays.add(markerPosActual)
                val lugar = binding.ubicacion.text.toString()
                Log.i("Lugar enviado", lugar)
                drawRoute(currentLocation,lugar)
                binding.map.invalidate()
            }


        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_atencion_en_ruta)

        // Configuracion binding
        binding = ActivityAtencionEnRutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpMapa()

        // configuracion de el sensor de luz
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Obtiene una referencia al sensor de luminosidad
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            Toast.makeText(this, "No se encuentra el sensor de luminosidad", Toast.LENGTH_SHORT).show()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Pedir permisos
        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )

        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNotGranted.toTypedArray(),
                PermissionRequestCodes.MULTIPLE_PERMISSIONS
            )
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted, perform location-related operations.
            startLocationUpdates()
            verificarPermisoParaMapa()
        } else {
            // Permissions are not granted, request permissions.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionRequestCodes.LOCATION
            )
        }


        // Inicializacion de la variable para pedir la ubicacion

        //startLocationUpdates()
        cambioMapa()
        //verificarPermisoParaMapa()

        binding.llamar.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            startActivity(intent)
        }

        binding.textView15.text = citasActivity.citaEscogida.doctor?.nombre ?: "Adriana Juanita"
        binding.fechafield.text = citasActivity.citaEscogida.fecha


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionRequestCodes.MULTIPLE_PERMISSIONS) {
            // Check if all requested permissions were granted
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Both permissions were granted, handle accordingly
            } else {

                Toast.makeText(
                    this,
                    "Access is required for certain features.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
//        if (requestCode == PermissionRequestCodes.LOCATION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, perform location-related operations.
//                startLocationUpdates()
//                verificarPermisoParaMapa()
//            } else {
//                // Permission denied, handle it (e.g., show a message to the user).
//                // You may want to inform the user that certain features won't work without this permission.
//            }
//        }
    }


    //Funcion para cambiar el color o tema del mapa
    fun cambioMapa(){
        sensorEventListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val luminosityValue = event.values[0]

                    if (luminosityValue < 15157.9) {
                        // Cambia el modo del mapa a oscuro
                        binding.map.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                    } else {
                        // Restaura el modo del mapa a normal
                        binding.map.overlayManager.tilesOverlay.setColorFilter(null)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Puedes manejar cambios en la precisión del sensor aquí si es necesario
            }
        }
    }

    // funcion para pedir la ubicacion cada 5 segundos
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update every 5 seconds
            fastestInterval = 2000 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 30.0f // Minimum displacement of 30 meters
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // Funcion para crear un marcador
    private fun createMarker(p: GeoPoint, desc: String?, iconID: Int): Marker? {
        val geocoder = Geocoder(this)
        var NombreLugar= " "
        val direccioes = geocoder.getFromLocation(p.latitude,p.longitude,1)
        if (direccioes != null) {
            if(direccioes.isNotEmpty()){
                val direccion = direccioes[0]
                NombreLugar = direccion.getAddressLine(0)
            }
            else{
                NombreLugar = "No se encontro el nombre";
            }
        }
        var marker: Marker? = null
        if (binding.map != null) {
            marker = Marker(binding.map)
            marker.title = NombreLugar
            marker.snippet = "Lat: ${p.latitude}, Lon: ${p.longitude}"
            if (iconID != 0) {
                val myIcon = resources.getDrawable(iconID, theme)
                marker.icon = myIcon
            }
            marker.position = p
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.showInfoWindow()
        }
        return marker
    }

    //Funcion para calcular y dibujar la ruta

    private fun drawRoute(start: GeoPoint, finish: String) {
        Log.i("Nombre lugar",finish)
        val geocoder = Geocoder(this)
        var foundLocation = GeoPoint(0.0,0.0)
        try {
            val addressList = geocoder.getFromLocationName(finish, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]
                    foundLocation = GeoPoint(address.latitude, address.longitude)
                    Log.i("lugar",foundLocation.longitude.toString())
                    Log.i("lugar",foundLocation.latitude.toString())
                    val routePoints = ArrayList<GeoPoint>()
                    routePoints.add(start)
                    routePoints.add(foundLocation)
                    val road = roadManager.getRoad(routePoints)
                    Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
                    Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
                    val marcadorCasa = createMarker(foundLocation,"Hogar",R.drawable.baseline_house_24)
                    binding.map.overlays.add(marcadorCasa)
                    if (binding.map != null) {
                        roadOverlay?.let { binding.map.overlays.remove(it) }
                        roadOverlay = RoadManager.buildRoadOverlay(road)
                        roadOverlay?.outlinePaint?.color = Color.RED
                        roadOverlay?.outlinePaint?.strokeWidth = 10f
                        binding.map.overlays.add(roadOverlay)
                        binding.map.invalidate()
                        Toast.makeText(this,"Route length: ${road.mLength} klm" + "Duration: ${road.mDuration / 60} min",Toast.LENGTH_LONG).show()
                    }

                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error searching for location", Toast.LENGTH_SHORT).show()
        }
//        val routePoints = ArrayList<GeoPoint>()
//        routePoints.add(start)
//        routePoints.add(foundLocation)
//        val road = roadManager.getRoad(routePoints)
//        Log.i("OSM_acticity", "Route length: ${road.mLength} klm")
//        Log.i("OSM_acticity", "Duration: ${road.mDuration / 60} min")
//        if (binding.map != null) {
//            roadOverlay?.let { binding.map.overlays.remove(it) }
//            roadOverlay = RoadManager.buildRoadOverlay(road)
//            roadOverlay?.outlinePaint?.color = Color.RED
//            roadOverlay?.outlinePaint?.strokeWidth = 10f
//            binding.map.overlays.add(roadOverlay)
//        }
    }

    // setUp mapa

    fun setUpMapa(){
        //Configuracion del mapa
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        // Inicializar y configurar las cosas para osmBonuspack
        roadManager = OSRMRoadManager(this, "ANDROID")
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    // Override del onResume
    override fun onResume(){
        super.onResume()
        binding.map.onResume()
        sensorManager.registerListener(
            sensorEventListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
            verificarPermisoParaMapa()
        }
        cambioMapa()

    }

    fun verificarPermisoParaMapa(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val currentLocation = PosDoctor
                val mapController: IMapController = binding.map.controller
                mapController.setZoom(18.0)
                mapController.setCenter(currentLocation)


                    setUpMapa()
                    val marcadorPosicionInicial = createMarker(currentLocation, "Mi ubicación", R.drawable.baseline_health_and_safety_24)
                    binding.map.overlays.add(marcadorPosicionInicial)
                    binding.map.overlays.add(marcadorPosicionInicial)
                    val lugar = binding.ubicacion.text.toString()
                    Log.i("Lugar enviado", lugar)
                    drawRoute(currentLocation,lugar)
                    //binding.map.invalidate()

            }
        }
    }

    //Override del onPause
    override fun onPause() {
        super.onPause()
        binding.map.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    //Override dell onDestroy
    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}