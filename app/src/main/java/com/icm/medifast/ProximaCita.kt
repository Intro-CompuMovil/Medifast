package com.icm.medifast

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Binder
import android.os.StrictMode
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.icm.medifast.databinding.ActivityProximaCitaBinding
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


class ProximaCita : AppCompatActivity() {

    private lateinit var binding: ActivityProximaCitaBinding
    //Variablers para el sensor de luz
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var sensorEventListener: SensorEventListener
    lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    private var PosDoctor = GeoPoint(4.6327, -74.0677)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_proxima_cita)

        // Configuracion binding
        binding = ActivityProximaCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpMapa()
        cambioMapa()
        // configuracion de el sensor de luz
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Obtiene una referencia al sensor de luminosidad
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            Toast.makeText(this, "No se encuentra el sensor de luminosidad", Toast.LENGTH_SHORT).show()
        }


        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )
        val permissionsNotGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNotGranted.toTypedArray(),
                PermissionRequestCodes.MULTIPLE_PERMISSIONS2
            )
        }



        // Check and request location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionRequestCodes.LOCATION
            )
        }

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PermissionRequestCodes.CAMERA
            )
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted, perform location-related operations.

            verificarPermisoParaMapa()
        } else {
            // Permissions are not granted, request permissions.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionRequestCodes.LOCATION
            )
        }


        binding.cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted, open the camera.
                openCamera()
            } else {
                // Permission is not granted, request it.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PermissionRequestCodes.CAMERA)
            }
        }


    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, PermissionRequestCodes.REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionRequestCodes.MULTIPLE_PERMISSIONS2) {
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

    fun verificarPermisoParaMapa(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (PosDoctor != null) {
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

            verificarPermisoParaMapa()
        }
        cambioMapa()

    }

    //Override del onPause
    override fun onPause() {
        super.onPause()
        binding.map.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }
}