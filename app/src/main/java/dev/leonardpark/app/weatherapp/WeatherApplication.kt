package dev.leonardpark.app.weatherapp

import android.app.Application
import android.content.Context
import dev.leonardpark.app.weatherapp.api.WeatherFactor
import dev.leonardpark.app.weatherapp.api.WeatherService
import dev.leonardpark.app.weatherapp.db.SearchDatabase
import dev.leonardpark.app.weatherapp.db.SearchExecutors
import dev.leonardpark.app.weatherapp.db.SearchRepository
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class WeatherApplication : Application() {
  private var weatherService: WeatherService? = null
  private var scheduler: Scheduler? = null
  private lateinit var searchExecutors: SearchExecutors

  override fun onCreate() {
    super.onCreate()
    searchExecutors = SearchExecutors()
  }

  fun create(context: Context): WeatherApplication {
    return get(context)
  }

  private operator fun get(context: Context): WeatherApplication {
    return context.applicationContext as WeatherApplication
  }

  private fun getDatabase(): SearchDatabase = SearchDatabase.getInstance(this)

  fun getSearchRepository(): SearchRepository {
    return SearchRepository.getInstance(searchExecutors, getDatabase())
  }

  fun getWeatherService(): WeatherService {
    if (weatherService == null) weatherService = WeatherFactor().create()
    return weatherService as WeatherService
  }

  fun subscribeScheduler(): Scheduler {
    if (scheduler == null) scheduler = Schedulers.io()
    return scheduler as Scheduler
  }
}