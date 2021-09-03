package dev.leonardpark.app.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dev.leonardpark.app.material_serarch_view.MaterialSearchView
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.Utils
import dev.leonardpark.app.weatherapp.databinding.ActivityMainBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import dev.leonardpark.app.weatherapp.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener,
  SearchRecyclerInterface {

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val LOCATION_PERMISSION_ARRAY = arrayOf(
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION
    )
  }

  private lateinit var mBinding: ActivityMainBinding
  private lateinit var mViewModel: MainActivityViewModel
  private lateinit var mSearchAdapter: SearchRecyclerAdapter
  private var isDark = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mBinding.root)

    isDark = Utils.isUsingNightModeResources(this)

    setSupportActionBar(mBinding.toolbar)

    initBinding()
    initSearch()
    initLocation()
  }

  // region Binding
  private fun initBinding() {
    mViewModel = MainActivityViewModel(this)
    mViewModel.weatherResponse.observe(this) { response ->
      mBinding.tvResponse.text = response.toString()
    }

    mViewModel.searchMethod.observe(this) { method ->
      mBinding.tvSearchMethod.text = String.format(getString(R.string.request_method), method)
    }

    mViewModel.locationStatus.observe(this) { status ->
      mBinding.ibLocation.apply {
        setImageDrawable(
          ContextCompat.getDrawable(
            this@MainActivity,
            if (status) R.drawable.ic_baseline_location_on_24 else R.drawable.ic_baseline_location_off_24
          )
        )

        setColorFilter(
          ContextCompat.getColor(
            this@MainActivity,
            if (status) R.color.location_on else if (isDark) R.color.white else R.color.black
          ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        setOnClickListener {
          if (status) {
            checkLocationPermission()?.let {
              mViewModel.apply {
                isPermissionGranted.value = true
                locationStatus.value = true
                querySearch("${it.latitude},${it.longitude}")
              }
            }
          }
        }
      }
    }

    mViewModel.isPermissionGranted.observe(this) { granted ->
      mBinding.tvLocationStatus.text =
        String.format(
          getString(R.string.location_permission_),
          getString(if (granted) R.string.granted else R.string.denied)
        )
    }
  }
  // endregion

  // region SearchView
  private fun initSearch() {
    mSearchAdapter = SearchRecyclerAdapter(this, isDark, this)
    mBinding.searchHolder.setSearchRecyclerAdapter(mSearchAdapter)
    mBinding.searchHolder.addQueryTextListener(this)
    mViewModel.searchList.observe(this) { list ->
      mSearchAdapter.setItems(list)
    }
    mBinding.searchHolder.getImageBack()
      .setColorFilter(
        ContextCompat.getColor(
          this,
          if (isDark) R.color.white else R.color.black
        ), android.graphics.PorterDuff.Mode.SRC_IN
      )
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return if (item.itemId == R.id.action_search) {
      mBinding.searchHolder.showSearch()
      true
    } else {
      super.onOptionsItemSelected(item)
    }
  }

  override fun onQueryTextSubmit(query: String?): Boolean {
    mBinding.searchHolder.hideRecycler()
    mViewModel.querySearch(query ?: "")
    return true
  }

  override fun onQueryTextChange(newText: String?): Boolean {
    mBinding.searchHolder.showRecycler()
    return false
  }

  override fun onSearchItemClicked(query: String) {
    mBinding.searchHolder.setSearchText(query)
    mBinding.searchHolder.hideRecycler()
    mViewModel.querySearch(query)
    mViewModel.locationStatus.value = false
  }

  override fun onSearchDeleteClicked(searchEntity: SearchEntity) {
    mViewModel.deleteEntity(searchEntity)
  }

  // endregion

  // region Location

  private fun initLocation() {

    val hasGPSFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)

    mBinding.tvLocationStatus.visibility = if (hasGPSFeature) View.VISIBLE else View.GONE
    mBinding.ibLocation.visibility = if (hasGPSFeature) View.VISIBLE else View.GONE

    mViewModel.isPermissionGranted.value = hasGPSFeature && isLocationGrantedPermission()

  }

  @SuppressLint("MissingPermission")
  private fun checkLocationPermission(): Location? {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (
      isLocationGrantedPermission()
    ) {
      ActivityCompat.requestPermissions(
        this,
        LOCATION_PERMISSION_ARRAY,
        LOCATION_PERMISSION_REQUEST_CODE
      )
      null
    } else {
      locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
    }
  }

  private fun isLocationGrantedPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      this,
      LOCATION_PERMISSION_ARRAY[0]
    ) == PackageManager.PERMISSION_GRANTED &&
      ContextCompat.checkSelfPermission(
        this,
        LOCATION_PERMISSION_ARRAY[1]
      ) == PackageManager.PERMISSION_GRANTED
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      LOCATION_PERMISSION_REQUEST_CODE -> {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mViewModel.apply {
            isPermissionGranted.value = true
            locationStatus.value = true
            val location = checkLocationPermission()!!
            querySearch("${location.latitude},${location.longitude}")
          }
        } else {
          Snackbar.make(
            mBinding.root,
            String.format(getString(R.string.location_permission_), getString(R.string.denied)),
            Snackbar.LENGTH_INDEFINITE
          ).show()
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }
  // endregion
}