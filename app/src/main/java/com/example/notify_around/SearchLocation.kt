package com.example.notify_around

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.example.notify_around.adapters.LocationAdapter
import com.example.notify_around.adapters.LastLocationAdapter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.AutocompletePrediction
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import android.text.TextWatcher
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import android.text.Editable
import android.view.WindowManager
import android.content.Intent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.gms.common.api.ApiException
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.notify_around.models.Locations
import com.example.notify_around.databinding.ActivitySearchLocationBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

@SuppressLint("Registered")
class SearchLocation : AppCompatActivity(), LocationAdapter.ClickListener,
    LastLocationAdapter.ClickListener {
    private var placesClient: PlacesClient? = null
    private var predictionsList: List<AutocompletePrediction>? = null
    var locations_list: MutableList<Locations> = ArrayList()

    var adapter: LocationAdapter? = null
    var last_adapter: LastLocationAdapter? = null
    var location_name: String? = null
    var location_sub_name: String? = null
    var suggestion: String? = null
    var placeId: String? = null
    var last_search_list: MutableList<Locations>? = null
    var last_locations_list: List<Locations>? = null
    var LOCATION_PREFERENCES = "location_preference"

    var search_lat = 0.0
    var search_lng = 0.0


    private lateinit var binding: ActivitySearchLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backBtn.setOnClickListener(View.OnClickListener { finish() })
        binding.removeBtn.setOnClickListener(View.OnClickListener { binding.searchLocation.setText("") })
        val manager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        binding.locationRecyclerView.setLayoutManager(manager)
        val dividerItemDecoration = DividerItemDecoration(
            binding.locationRecyclerView.getContext(),
            manager.orientation
        )
        binding.locationRecyclerView.addItemDecoration(dividerItemDecoration)
        val recent_manager = GridLayoutManager(this, 1, RecyclerView.VERTICAL, false)
        binding.recentLocationRecyclerView.setLayoutManager(recent_manager)
        binding.recentLocationRecyclerView.addItemDecoration(dividerItemDecoration)
        Places.initialize(this, "AIzaSyCbPouBsbOKduWI7bt-w04mo6YEpYFcoss")
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()
        binding.searchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 0) {
                    binding.removeBtn.setVisibility(View.VISIBLE)
                } else {
                    binding.removeBtn.setVisibility(View.GONE)
                }
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry("PK")
                    .setTypeFilter(TypeFilter.REGIONS)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse = task.result
                            if (predictionsResponse != null) {
                                predictionsList = predictionsResponse.autocompletePredictions
                                locations_list.clear()
                                for (i in predictionsList!!.indices) {
                                    val prediction = predictionsList!![i]
                                    val locations = Locations()
                                    locations.placeId = prediction.placeId
                                    locations.full_name = prediction.getFullText(null).toString()
                                    locations.location_name =
                                        prediction.getPrimaryText(null).toString()
                                    locations.location_sub_name =
                                        prediction.getSecondaryText(null).toString()
                                    locations_list.add(locations)
                                }
                                SetAdapter()
                            }
                        } else {
                            Log.e("PlaceTag", task.exception.toString())
                        }
                    }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        GetLastLocations()
    }

    fun setResultIntent(place: String?, search_lat: Double, search_lng: Double) {
        val resultIntent = Intent()
        resultIntent.putExtra("location", place)
        resultIntent.putExtra("lat", search_lat)
        resultIntent.putExtra("lng", search_lng)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    fun SetAdapter() {
        adapter = LocationAdapter(this, locations_list)
        binding.locationRecyclerView!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
        adapter!!.setOnItemClickListener(this)
    }

    override fun onItemClick(position: Int, v: View?, type: Int) {
        if (type == 1) {
            location_name = locations_list[position].location_name
            location_sub_name = locations_list[position].location_sub_name
            suggestion = locations_list[position].full_name
            placeId = locations_list[position].placeId
        } else {
            location_name = last_locations_list!![position].location_name
            location_sub_name = last_locations_list!![position].location_sub_name
            suggestion = last_locations_list!![position].full_name
            placeId = last_locations_list!![position].placeId
        }
        binding.searchLocation!!.setText("")
        binding.searchLocation!!.setText(suggestion)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm != null) {
            imm.hideSoftInputFromWindow(
                binding.searchLocation!!.windowToken,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            val placeField = Arrays.asList(Place.Field.LAT_LNG)
            val fetchPlaceRequest = FetchPlaceRequest.builder(placeId!!, placeField).build()
            placesClient!!.fetchPlace(fetchPlaceRequest)
                .addOnSuccessListener { fetchPlaceResponse ->
                    val place = fetchPlaceResponse.place
                    val latLng = place.latLng
                    search_lat = latLng!!.latitude
                    search_lng = latLng.longitude
                    setResultIntent(suggestion, search_lat, search_lng)
                }.addOnFailureListener { e ->
                if (e is ApiException) {
                    e.printStackTrace()
                    Log.e("PlaceTag", "Place not found" + e.message)
                }
            }
        }
        AddRecentLocations()
    }

    fun GetLastLocations() {
        val prefs = getSharedPreferences(LOCATION_PREFERENCES, MODE_PRIVATE)
        val recentLocations = prefs.getString("lastLocations", "")
        last_locations_list = ArrayList()
        last_locations_list =
            Gson().fromJson(recentLocations, object : TypeToken<List<Locations?>?>() {}.type)
        if (last_locations_list != null) {
            last_adapter = LastLocationAdapter(this, last_locations_list!!)
            binding.recentLocationRecyclerView!!.adapter = last_adapter
            last_adapter!!.notifyDataSetChanged()
            last_adapter!!.setOnItemClickListener(this)
        }
    }

    fun AddRecentLocations() {
        last_search_list = ArrayList()
        val prefs = getSharedPreferences(LOCATION_PREFERENCES, MODE_PRIVATE)
        val recentBooks = prefs.getString("lastLocations", "")
        if (recentBooks != "") {
            last_search_list =
                Gson().fromJson(recentBooks, object : TypeToken<List<Locations?>?>() {}.type)
        }
        var listSize = 0
        val locations = Locations()
        locations.location_name = location_name
        locations.location_sub_name = location_sub_name
        locations.full_name = suggestion
        locations.placeId = placeId
        if (last_search_list != null) {
            listSize = last_search_list!!.size
            if (last_search_list!!.contains(locations)) {
                last_search_list!!.remove(locations)
                last_search_list!!.add(0, locations)
            } else {
                last_search_list!!.add(0, locations)
            }
            if (last_search_list!!.size > 5) {
                last_search_list!!.removeAt(listSize)
            }
            val Recentlocations = Gson().toJson(last_search_list)
            val editor = prefs.edit()
            editor.putString("lastLocations", Recentlocations)
            editor.apply()
        } else {
            last_search_list!!.add(0, locations)
        }
    }
}