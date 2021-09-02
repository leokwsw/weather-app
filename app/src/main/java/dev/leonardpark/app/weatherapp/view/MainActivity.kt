package dev.leonardpark.app.weatherapp.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import dev.leonardpark.app.weatherapp.MySuggestionProvider
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.data.WeatherResponse
import dev.leonardpark.app.weatherapp.databinding.ActivityMainBinding
import dev.leonardpark.app.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {
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

    weatherViewModel.isLoading.observe(this, Observer {
      isLoading = it
    })

    weatherViewModel.weatherResponse.observe(this, Observer {
      mWeatherResponse = it
      binding.tvResponse.text = mWeatherResponse.toString()
      Log.d("testmo", "data")
      Log.d("testmo", mWeatherResponse.toString())
    })

    setupSearchView()
  }

  private fun setupSearchView() {
    binding.searchView.queryHint = "Search"

    if (Intent.ACTION_SEARCH == intent.action) {
      intent.getStringExtra(SearchManager.QUERY)?.also { query ->
        // doMySearch
        SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
          .saveRecentQuery(query, null)
      }
    }
  }


}