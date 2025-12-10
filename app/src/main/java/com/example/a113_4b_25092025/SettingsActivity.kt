package com.example.a113_4b_25092025

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class SettingsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    // Vistas de la UI
    private lateinit var wifiStatusText: TextView
    private lateinit var wifiStatusIcon: ImageView
    private lateinit var bluetoothStatusText: TextView
    private lateinit var bluetoothStatusIcon: ImageView
    private lateinit var accelerometerDataText: TextView
    private lateinit var locationCoordinatesText: TextView

    // Mapa y ubicación
    private var gMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Sensores
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // Constantes
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inicialización de vistas
        wifiStatusText = findViewById(R.id.wifiStatusText)
        wifiStatusIcon = findViewById(R.id.wifiStatusIcon)
        bluetoothStatusText = findViewById(R.id.bluetoothStatusText)
        bluetoothStatusIcon = findViewById(R.id.bluetoothStatusIcon)
        accelerometerDataText = findViewById(R.id.accelerometerDataText)
        locationCoordinatesText = findViewById(R.id.locationCoordinatesText)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        // Inicialización de servicios de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicialización de sensores
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Comprobación inicial del estado de la conectividad
        checkConnectivityStatus()

        // Inicialización del mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configuración del botón para volver
        btnVolver.setOnClickListener { finish() }

        // Callback para actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    gMap?.clear() // Limpia marcadores anteriores
                    gMap?.addMarker(MarkerOptions().position(currentLatLng).title("¡Estás aquí!"))
                    gMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))

                    // Actualiza el texto de las coordenadas
                    locationCoordinatesText.text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Registrar listeners
        registerReceiver(connectivityReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION).apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        })
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        // Anular registro de listeners para ahorrar batería
        unregisterReceiver(connectivityReceiver)
        sensorManager.unregisterListener(this)
        stopLocationUpdates()
    }

    private fun checkConnectivityStatus() {
        // Estado del Wi-Fi
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            wifiStatusText.text = "Estado del Wi-Fi: Activado"
            wifiStatusIcon.setImageResource(R.drawable.ic_check)
        } else {
            wifiStatusText.text = "Estado del Wi-Fi: Desactivado"
            wifiStatusIcon.setImageResource(R.drawable.ic_close)
        }

        // Estado del Bluetooth
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            bluetoothStatusText.text = "Estado del Bluetooth: No disponible"
            bluetoothStatusIcon.setImageResource(R.drawable.ic_close)
        } else {
            if (bluetoothAdapter.isEnabled) {
                bluetoothStatusText.text = "Estado del Bluetooth: Activado"
                bluetoothStatusIcon.setImageResource(R.drawable.ic_check)
            } else {
                bluetoothStatusText.text = "Estado del Bluetooth: Desactivado"
                bluetoothStatusIcon.setImageResource(R.drawable.ic_close)
            }
        }
    }

    // BroadcastReceiver para cambios en Wi-Fi y Bluetooth
    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            checkConnectivityStatus()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap?.uiSettings?.isZoomControlsEnabled = true
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            gMap?.isMyLocationEnabled = true
            startLocationUpdates()
            // Mover la cámara a la última ubicación conocida al inicio
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // 5 segundos
            fastestInterval = 2000 // 2 segundos
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "El permiso de ubicación es necesario.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0].toDouble()
            val y = event.values[1].toDouble()
            val z = event.values[2].toDouble()

            // Calcular ángulos de inclinación (pitch y roll) de forma segura
            val pitch = Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z)))
            val roll = Math.toDegrees(Math.atan2(-x, z))

            accelerometerDataText.text = String.format(Locale.getDefault(), "Pitch: %.1f°, Roll: %.1f°", pitch, roll)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementarlo para este caso
    }
}