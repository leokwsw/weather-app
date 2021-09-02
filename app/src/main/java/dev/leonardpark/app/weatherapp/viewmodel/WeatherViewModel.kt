package dev.leonardpark.app.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.WeatherApplication
import dev.leonardpark.app.weatherapp.api.WeatherResponse
import dev.leonardpark.app.weatherapp.db.SearchEntity
import dev.leonardpark.app.weatherapp.db.SearchRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.lang.Double.parseDouble
import java.util.*

class WeatherViewModel(private val context: Context) : Observable() {
  private val mCompositeDisposable = CompositeDisposable()

  private val weatherApplication = WeatherApplication().create(context)
  private val weatherService = weatherApplication.getWeatherService()

  var isLoading = MutableLiveData<Boolean>()
  var weatherResponse = MutableLiveData<WeatherResponse?>()
  var searchMethod = MutableLiveData<String>()

  private val searchRepository: SearchRepository = weatherApplication.getSearchRepository()
  var getSearchListLive: LiveData<List<SearchEntity>> = searchRepository.getSearchListLive()

  fun search(query: String) {
    insertEntity(query)
    val coordinateArray = query.split(",")
    if (numeric(query)) {
      searchByZipCode(query)
    } else if (coordinateArray.size == 2 && numeric(coordinateArray[0]) && numeric(coordinateArray[1])) {
      searchByCoordinate(coordinateArray[0].toFloat(), coordinateArray[1].toFloat())
    } else {
      searchByCityName(query)
    }
  }

  private fun numeric(string: String): Boolean {
    var numeric = true

    try {
      parseDouble(string)
    } catch (e: NumberFormatException) {
      numeric = false
    }
    return numeric
  }

  private fun insertEntity(query: String) {
    searchRepository.insertSearches(SearchEntity(query, Date().time))
  }

  fun deleteEntity(searchEntity: SearchEntity) {
    searchRepository.deleteSearches(searchEntity)
  }

  private fun searchByCityName(cityName: String) {
    isLoading.value = true
    searchMethod.value = context.getString(R.string.method_city_name)
    mCompositeDisposable.add(
      weatherService.getWeatherByCityName(cityName)
        .subscribeOn(weatherApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
          isLoading.value = false
        }, {
          weatherResponse.value = null
          isLoading.value = false
        })
    )
  }

  private fun searchByCoordinate(lat: Float, lon: Float) {
    isLoading.value = true
    searchMethod.value = context.getString(R.string.method_coordinate)
    mCompositeDisposable.add(
      weatherService.getWeatherByCoordinate(lat, lon)
        .subscribeOn(weatherApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
          isLoading.value = false
        }, {
          weatherResponse.value = null
          isLoading.value = false
        })
    )
  }

  private fun searchByZipCode(zipCode: String) {
    isLoading.value = true
    searchMethod.value = context.getString(R.string.method_zip_code)
    mCompositeDisposable.add(
      weatherService.getWeatherByZipCode(zipCode)
        .subscribeOn(weatherApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
          isLoading.value = false
        }, {
          weatherResponse.value = null
          isLoading.value = false
        })
    )
  }
}