package com.example.a113_4b_25092025

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SettingsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var wifiStatusText: TextView
    private lateinit var bluetoothStatusText: TextView
    private var gMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        wifiStatusText = findViewById(R.id.wifiStatusText)
        bluetoothStatusText = findViewById(R.id.bluetoothStatusText)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkConnectivityStatus()

        // Inicializa el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnVolver.setOnClickListener { finish() }
    }

    private fun checkConnectivityStatus() {
        // Verificar Wi-Fi
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiStatusText.text = if (wifiManager.isWifiEnabled) {
            "Estado del Wi-Fi: Activado"
        } else {
            "Estado del Wi-Fi: Desactivado"
        }

        // Verificar Bluetooth
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothStatusText.text = when (bluetoothAdapter?.state) {
            BluetoothAdapter.STATE_ON -> "Estado del Bluetooth: Activado"
            BluetoothAdapter.STATE_OFF -> "Estado del Bluetooth: Desactivado"
            else -> "Estado del Bluetooth: No disponible"
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no tenemos permisos, los pedimos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Si ya tenemos permisos, obtenemos la ubicación
            gMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    gMap?.addMarker(MarkerOptions().position(currentLatLng).title("¡Estás aquí!"))
                    gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permiso concedido, intentamos de nuevo obtener la ubicación
                enableMyLocation()
            } else {
                // Permiso denegado
                Toast.makeText(this, "El permiso de ubicación es necesario para mostrar el mapa.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
