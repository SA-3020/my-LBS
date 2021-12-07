package com.example.notify_around

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback,LocationListener,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {
    private var mMap: GoogleMap? = null
    lateinit var mapView:MapView
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey" //bundles for passing data from one activity to another
    private val DEFAULT_ZOOM = 15f
    private var fusedLocationProviderClient: FusedLocationProviderClient?=null //to retrieve the device's last known location. The fused location provider is one of the location APIs in Google Play services
    private lateinit var tvCurrentAddress: TextView
    private lateinit var okBtn: Button
    private var currentAd=" "
    private lateinit var searchView: EditText


    override fun onMapReady(googleMap: GoogleMap) {
        mapView.onResume()
        mMap=googleMap

        askPermissionLocation()
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return
        }
        mMap!!.setMyLocationEnabled(true)
        mMap!!.setOnCameraMoveListener(this)
        mMap!!.setOnCameraMoveStartedListener(this)
        mMap!!.setOnCameraIdleListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // tvCurrentAddress=findViewById(R.id.tvAdd)
        mapView=findViewById<MapView>(R.id.map1)
        okBtn=findViewById(R.id.okButton)
        searchView = findViewById(R.id.searchView)

        askPermissionLocation()
        var mapViewBundle:Bundle?=null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle((MAP_VIEW_BUNDLE_KEY))
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)

        //On button click, send address to location text View in Post Add form i.e other Activity
        okBtn.setOnClickListener{

           finish()


        }


        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if(intent!=null){

                    searchView.setText(intent.getStringExtra("location"))
                    val lat=intent.getStringExtra("lat")
                    val lng=intent.getStringExtra("lng")


                    val intent=Intent()
                    intent.putExtra("location",searchView.text.toString())
                    intent.putExtra("lat",lat.toString())
                    intent.putExtra("lng",lng.toString())
                    setResult(Activity.RESULT_OK,intent)


                }

            }
        }

       searchView.setOnClickListener {

            startForResult.launch(Intent(this,SearchLocation::class.java))
        }

    }

    public override fun onSaveInstanceState(
            outState: Bundle) {
        super.onSaveInstanceState(outState)
        askPermissionLocation()
        var mapViewBundle= outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if(mapViewBundle==null){
            mapViewBundle= Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY,mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }
    private fun askPermissionLocation(){
        askPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            getCurrentLocation()
        }.onDeclined { e ->
            if (e.hasDenied()) {
                e.denied.forEach{
                }
                AlertDialog.Builder(this)
                        .setMessage("Please enable Location")
                        .setPositiveButton("Yes") { _, _ ->
                            e.askAgain()
                        }
                        .setNegativeButton("no") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }
            if (e.hasForeverDenied()) {
                e.foreverDenied.forEach {

                }
                e.goToSettings();
            } }

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MapActivity)
        try{
            @SuppressLint("Missing Permission")
            val location = fusedLocationProviderClient!!.getLastLocation()
            location.addOnCompleteListener(object: OnCompleteListener<Location> {
                override fun onComplete(loc: Task<Location>) {
                    if(loc.isSuccessful){
                        val currentLocation= loc.result as Location?
                        if(currentLocation != null){
                            moveCamera(
                                    LatLng(currentLocation.latitude, currentLocation.longitude),
                                    DEFAULT_ZOOM)
                        }
                        else{
                            askPermissionLocation()
                        }
                    }
                    else{
                        Toast.makeText(this@MapActivity,"Current Location not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        catch(se: Exception){
            Log.e("TAG","Security Exception")
        }
    }
    private fun moveCamera(latLng: LatLng, zoom:Float){
        try{
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))
        }catch(e: NullPointerException){
            askPermissionLocation()
        }
        /*mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))*/
    }

    override fun onLocationChanged(location: Location) {
        val geocoder=Geocoder(this, Locale.getDefault())
        var addresses: List<Address>?=null
        try{
            addresses=geocoder.getFromLocation(location!!.latitude,location!!.longitude,1)

        }catch(e: IOException){
            e.printStackTrace()
        }
        setAddress(addresses!![0],location.latitude,location.longitude)
    }

    private fun setAddress(address: Address,lat:Double,lng:Double) {
        if(address!=null){
            if(address.getAddressLine(0)!=null){
                /*Toast.makeText(this@MainActivity, address.getAddressLine(0), Toast.LENGTH_SHORT).show()*/
                currentAd= address.getAddressLine(0)

                val intent=Intent()
                intent.putExtra("location",currentAd)
                intent.putExtra("lat",lat.toString())
                intent.putExtra("lng",lng.toString())
                setResult(Activity.RESULT_OK,intent)

                //tvCurrentAddress!!.setText(address.getAddressLine(0))
            }
            if(address.getAddressLine(1)!=null){
                /*Toast.makeText(this@MainActivity, address.getAddressLine(1), Toast.LENGTH_SHORT).show()*/
                currentAd= address.getAddressLine(1)
                val intent=Intent()
                intent.putExtra("location",currentAd)
                intent.putExtra("lat",lat.toString())
                intent.putExtra("lng",lng.toString())
                setResult(Activity.RESULT_OK,intent)
            }
        }

    }

    override fun onCameraMove() {

    }

    override fun onCameraMoveStarted(p0: Int) {
    }

    override fun onCameraIdle() {
        var addresses:List<Address>?=null
        val geocoder= Geocoder(this, Locale.getDefault())
        try{
            val lat=mMap!!.getCameraPosition().target.latitude
            val lng=mMap!!.getCameraPosition().target.longitude

            addresses=geocoder.getFromLocation(lat,lng ,1)
            setAddress(addresses!![0],lat,lng)

        }catch(e: IndexOutOfBoundsException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }


}


