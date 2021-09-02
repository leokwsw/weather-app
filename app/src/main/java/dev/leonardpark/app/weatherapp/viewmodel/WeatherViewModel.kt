package dev.leonardpark.app.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dev.leonardpark.app.weatherapp.WeatherApplication
import dev.leonardpark.app.weatherapp.data.WeatherResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class WeatherViewModel(context: Context) : Observable() {
  private val mCompositeDisposable = CompositeDisposable()

  private val weatherApplication = WeatherApplication().create(context)
  private val weatherService = weatherApplication.getWeatherService()

  var isLoading = MutableLiveData<Boolean>()

  var weatherResponse = MutableLiveData<WeatherResponse?>()

  fun searchByCityName(keyword: String) {
    isLoading.value = true
    mCompositeDisposable.add(
      weatherService.getWeatherByCityName(keyword)
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

  fun searchByLatLong(lat: Float, lon: Float) {
    isLoading.value = true
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

  fun searchByZipCode(zipCode: String) {
    isLoading.value = true
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