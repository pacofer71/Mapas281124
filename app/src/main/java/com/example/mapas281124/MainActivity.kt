package com.example.mapas281124

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
            if (
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                gestionarLocalizacion()
            } else {
                Toast.makeText(this, "El usuario denegó los permisos....", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iniciarFragment()
    }

    private fun iniciarFragment() {
        val fragment = SupportMapFragment()
        fragment.getMapAsync(this)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fm_maps, fragment)
        }
    }

    //----------------------------------------------------------------------------------------------
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isZoomControlsEnabled = true
        // map.mapType=GoogleMap.MAP_TYPE_SATELLITE
        ponerMarcador(LatLng(36.85022532839174, -2.465028503902572))
        gestionarLocalizacion()
        //--------------------
        ponerRuta()

    }

    private fun ponerRuta() {
        val c1=LatLng(36.85100682419531, -2.465550276104411)
        val c2=LatLng(36.84928975310162, -2.463350462611055)
        val c3=LatLng(36.84830242760787, -2.4618269678944404)
        val c4=LatLng(36.84922965537378, -2.4607540843428444)
        val c5=LatLng(36.84993365412999, -2.4617625948813444)
        val c6=LatLng(36.849246826152275, -2.4628140207619085)
        val c7=LatLng(36.85055179407254, -2.4645091769169425)
        //val c8=c1
        val polylineOptions=PolylineOptions()
            .add(c1)
            .add(c2)
            .add(c3)
            .add(c4)
            .add(c5)
            .add(c6)
            .add(c7)
            .add(c1)
            .width(25f)
        val polyline=map.addPolyline(polylineOptions)

    }

    //-----------------------------------------------------------------------------------------------
    private fun gestionarLocalizacion() {
        if (!::map.isInitialized) return //:: al ser map de tipo lateinit
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        } else {
            //vamos a pedir los permisos
            pedirPermisos()
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun pedirPermisos() {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            mostrarExplicacion()
        } else {
            escogerPermisos()
        }
    }

    //----------------------------------------------------------------------------------------------
    private fun escogerPermisos() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //----------------------------------------------------------------------------------------------
    private fun mostrarExplicacion() {
        AlertDialog.Builder(this)
            .setTitle("Permisos de Ubicación")
            .setMessage("Para el uso adecuado de esta increible aplicación necesitamos los permisos de ubicacion")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
                dialog.dismiss()

            }
            .create()
            .show()
    }
    //----------------------------------------------------------------------------------------------

    private fun ponerMarcador(coordenadas: LatLng) {
        val marker = MarkerOptions().position(coordenadas).title("Ies Al-Andalus")
        map.addMarker(marker)
        mostrarAnimacion(coordenadas, 15f)
    }

    //----------------------------------------------------------------------------------------------
    private fun mostrarAnimacion(coordenadas: LatLng, zoom: Float) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas, zoom),
            4500,
            null
        )
    }
    //----------------------------------------------------------------------------------------------
    override fun onRestart() {
        super.onRestart()
        gestionarLocalizacion()
    }


}








