package dev.leonardpark.app.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.leonardpark.app.weatherapp.Utils
import dev.leonardpark.app.weatherapp.WeatherApplication
import dev.leonardpark.app.weatherapp.api.WeatherResponse
import dev.leonardpark.app.weatherapp.db.SearchEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class MainActivityViewModel(private val context: Context) : Observable() {
  private val mCompositeDisposable = CompositeDisposable()

  private val mApplication = WeatherApplication().create(context)
  private val mWeatherService = mApplication.getWeatherService()
  private val mSearchRepository = mApplication.getSearchRepository()

  var weatherResponse = MutableLiveData<WeatherResponse>()
  var searchList: LiveData<List<SearchEntity>> = mSearchRepository.getSearchListLive()

  fun querySearch(query: String) {
    insertEntity(query)
    val coordinateArray = query.split(",")
    if (Utils.numeric(query)) {
      searchByZipCode(query)
    } else if (
      coordinateArray.size == 2 &&
      Utils.numeric(coordinateArray[0]) && Utils.numeric(coordinateArray[1])
    ) {
      searchByCoordinate(coordinateArray[0].toFloat(), coordinateArray[1].toFloat())
    } else {
      searchByCityName(query)
    }
  }

  // region recent search
  private fun insertEntity(query: String) {
    mSearchRepository.insertSearches(SearchEntity(query, Date().time))
  }

  fun deleteEntity(searchEntity: SearchEntity) {
    mSearchRepository.deleteSearches(searchEntity)
  }

  fun deleteAllEntity() {
    mSearchRepository.deleteAll()
  }
  // endregion

  // region Request OpenWeather API
  private fun searchByCityName(cityName: String) {
    mCompositeDisposable.add(
      mWeatherService.getWeatherByCityName(cityName)
        .subscribeOn(mApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
        }, {
          weatherResponse.value = null
        })
    )
  }

  private fun searchByCoordinate(lat: Float, lon: Float) {
    mCompositeDisposable.add(
      mWeatherService.getWeatherByCoordinate(lat, lon)
        .subscribeOn(mApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
        }, {
          weatherResponse.value = null
        })
    )
  }

  private fun searchByZipCode(zipCode: String) {
    mCompositeDisposable.add(
      mWeatherService.getWeatherByZipCode(zipCode)
        .subscribeOn(mApplication.subscribeScheduler())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          weatherResponse.value = it
        }, {
          weatherResponse.value = null
        })
    )
  }
  // endregion
}