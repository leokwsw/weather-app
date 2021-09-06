package dev.leonardpark.app.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.leonardpark.app.material_serarch_view.MaterialSearchView
import dev.leonardpark.app.weatherapp.PermissionUtils
import dev.leonardpark.app.weatherapp.PermissionUtils.isPermissionGranted
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.Utils
import dev.leonardpark.app.weatherapp.databinding.ActivityMainBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import dev.leonardpark.app.weatherapp.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener,
  SearchRecyclerInterface, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
  GoogleMap.OnMyLocationClickListener {

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private const val DEFAULT_ZOOM = 15f
  }

  private lateinit var mBinding: ActivityMainBinding
  private lateinit var mViewModel: MainActivityViewModel
  private lateinit var mSearchAdapter: SearchRecyclerAdapter
  private var isDark = false
  private lateinit var map: GoogleMap
  private var permissionDenied = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mBinding.root)

    isDark = Utils.isUsingNightModeResources(this)

    setSupportActionBar(mBinding.toolbar)

    initBinding()
    initSearch()
    initGoogleMap(savedInstanceState)
  }

  // region Binding
  private fun initBinding() {
    mViewModel = MainActivityViewModel(this)
    mViewModel.weatherResponse.observe(this) { response ->
      val mapLatLng = LatLng(response.coord.lat, response.coord.lon)
      map.addMarker(MarkerOptions().position(mapLatLng).title(response.name))
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(mapLatLng, DEFAULT_ZOOM))
      mBinding.tvResponse.text = response.toString()
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
    map.clear()
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
    map.clear()
    mViewModel.querySearch(query)
  }

  override fun onSearchDeleteClicked(searchEntity: SearchEntity) {
    mViewModel.deleteEntity(searchEntity)
  }

  // endregion

  // region Google Map
  private fun initGoogleMap(savedInstanceState: Bundle?) {
    val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
    mBinding.map.let {
      it.onCreate(mapViewBundle)
      it.getMapAsync(this)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().also {
      outState.putBundle(MAPVIEW_BUNDLE_KEY, it)
    }
    mBinding.map.onSaveInstanceState(mapViewBundle)
  }

  override fun onResume() {
    super.onResume()
    mBinding.map.onResume()
  }

  override fun onStart() {
    super.onStart()
    mBinding.map.onStart()
  }

  override fun onStop() {
    super.onStop()
    mBinding.map.onStop()
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    googleMap.setOnMyLocationButtonClickListener(this)
    googleMap.setOnMyLocationClickListener(this)
    enableMyLocation()
  }

  private fun enableMyLocation() {
    if (!::map.isInitialized) return
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED
    ) {
      map.isMyLocationEnabled = true
    } else {
      PermissionUtils.requestPermission(
        this, LOCATION_PERMISSION_REQUEST_CODE,
        Manifest.permission.ACCESS_FINE_LOCATION, true
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      LOCATION_PERMISSION_REQUEST_CODE -> {
        if (
          isPermissionGranted(
            permissions,
            grantResults,
            Manifest.permission.ACCESS_FINE_LOCATION
          )
        ) {
          enableMyLocation()
        } else {
          permissionDenied = true
        }
      }
      else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  override fun onPause() {
    mBinding.map.onPause()
    super.onPause()
  }

  override fun onDestroy() {
    mBinding.map.onDestroy()
    super.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mBinding.map.onLowMemory()
  }

  @SuppressLint("MissingPermission")
  override fun onMyLocationButtonClick(): Boolean {
    mViewModel.querySearch("${map.myLocation.latitude},${map.myLocation.longitude}")
    return false
  }

  override fun onMyLocationClick(location: Location) {
    mViewModel.querySearch("${location.latitude},${location.longitude}")
    val temp = LatLng(location.latitude, location.longitude)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, DEFAULT_ZOOM))
  }
  // endregion
}