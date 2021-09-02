package dev.leonardpark.app.weatherapp.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dev.leonardpark.app.material_serarch_view.MaterialSearchView
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.api.WeatherResponse
import dev.leonardpark.app.weatherapp.databinding.ActivityMainBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import dev.leonardpark.app.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener,
  SearchRecyclerInterface {
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
      Log.d("testmo", "data")
      Log.d("testmo", mWeatherResponse.toString())
    }

    initSearch()
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
  }

  override fun onSearchDeleteClicked(searchEntity: SearchEntity) {
    weatherViewModel.deleteEntity(searchEntity)
  }

  // endregion


}