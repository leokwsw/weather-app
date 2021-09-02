package dev.leonardpark.app.weatherapp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dev.leonardpark.app.material_serarch_view.MaterialSearchView
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.api.WeatherResponse
import dev.leonardpark.app.weatherapp.databinding.ActivityMainBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import dev.leonardpark.app.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener,
  SearchRecyclerInterface {

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
  }

  private lateinit var binding: ActivityMainBinding
  private lateinit var weatherViewModel: WeatherViewModel

  private var isLoading: Boolean = false
  private var mWeatherResponse: WeatherResponse? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    weatherViewModel = WeatherViewModel(this)

    setSupportActionBar(binding.toolbar)

    weatherViewModel.isLoading.observe(this) {
      isLoading = it
    }

    weatherViewModel.weatherResponse.observe(this) {
      mWeatherResponse = it
      binding.tvResponse.text = mWeatherResponse.toString()
    }

    weatherViewModel.searchMethod.observe(this) {
      binding.tvSearchMethod.text = "Method: $it"
    }

    initSearch()
    initLocation()
  }

  // region SearchView

  private lateinit var searchAdapter: SearchRecyclerAdapter

  private fun initSearch() {
    searchAdapter = SearchRecyclerAdapter(this)
    binding.searchHolder.setSearchRecyclerAdapter(searchAdapter)
    binding.searchHolder.addQueryTextListener(this)
    weatherViewModel.getSearchListLive.observe(this) { list ->
      searchAdapter.setItems(list)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return if (item.itemId == R.id.action_search) {
      binding.searchHolder.showSearch()
      true
    } else {
      super.onOptionsItemSelected(item)
    }
  }

  override fun onQueryTextSubmit(query: String?): Boolean {
    binding.searchHolder.hideRecycler()
    weatherViewModel.search(query ?: "")
    return true
  }

  override fun onQueryTextChange(newText: String?): Boolean {
    binding.searchHolder.showRecycler()
    return false
  }

  override fun onSearchItemClicked(query: String) {
    binding.searchHolder.setSearchText(query)
    binding.searchHolder.hideRecycler()
    weatherViewModel.search(query)
    isUsingLocation = false
    changeLocationUI(false)
  }

  override fun onSearchDeleteClicked(searchEntity: SearchEntity) {
    weatherViewModel.deleteEntity(searchEntity)
  }

  // endregion

  // region Location
  private var isUsingLocation = false

  private fun initLocation() {

    val hasGPSFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    val locationVisibility = if (hasGPSFeature) {
      View.VISIBLE
    } else {
      View.GONE
    }

    binding.tvLocationStatus.visibility = locationVisibility
    binding.ibLocation.visibility = locationVisibility

    if (hasGPSFeature) {
      val statusStr = if (
        ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        "Location Permission : Denied"
      } else {
        "Location Permission : Granted"
      }

      binding.tvLocationStatus.text = statusStr

      binding.ibLocation.setOnClickListener {
        if (!isUsingLocation) {
          isUsingLocation = true
          val location = checkLocationPermission()

          location?.let {
            searchLocation(it)
          }
        } else {
          isUsingLocation = false
          changeLocationUI(false)
        }
      }
    }
  }

  private fun checkLocationPermission(): Location? {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED ||
      ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        LOCATION_PERMISSION_REQUEST_CODE
      )
      return null
    } else {
      val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
      return if (location == null) {
        Log.d("testmo", "Network Location")
        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
      } else {
        Log.d("testmo", "GPS Location")
        location
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      LOCATION_PERMISSION_REQUEST_CODE -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          searchLocation(checkLocationPermission()!!)
        } else {
          Snackbar.make(binding.root, "Location Permission is Denied", Snackbar.LENGTH_INDEFINITE)
            .show()
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  private fun searchLocation(location: Location) {
    binding.tvLocationStatus.text = "Location Permission : Granted"
    changeLocationUI(true)

    weatherViewModel.search("${location.latitude},${location.longitude}")
  }

  private fun changeLocationUI(status: Boolean) {
    binding.ibLocation.setImageDrawable(
      ContextCompat.getDrawable(
        this,
        if (status) R.drawable.ic_baseline_location_on_24 else R.drawable.ic_baseline_location_off_24
      )
    )
    binding.ibLocation.setColorFilter(
      ContextCompat.getColor(
        this,
        if (status) R.color.location_on else R.color.location_off
      ), android.graphics.PorterDuff.Mode.SRC_IN
    )
  }

  // endregion

}