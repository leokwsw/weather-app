package dev.leonardpark.app.weatherapp

import android.app.Application
import android.content.Context
import dev.leonardpark.app.weatherapp.data.WeatherFactor
import dev.leonardpark.app.weatherapp.data.WeatherService
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class WeatherApplication : Application() {
  private var weatherService: WeatherService? = null
  private var scheduler: Scheduler? = null

  private operator fun get(context: Context): WeatherApplication {
    return context.applicationContext as WeatherApplication
  }

  fun create(context: Context): WeatherApplication {
    return get(context)
  }

  fun getWeatherService(): WeatherService {
    if (weatherService == null) weatherService = WeatherFactor().create()
    return weatherService as WeatherService
  }

  fun subscribeScheduler(): Scheduler {
    if (scheduler == null) scheduler = Schedulers.io()
    return scheduler as Scheduler
  }

  fun setWeatherService(weatherService: WeatherService) {
    this.weatherService = weatherService
  }

  fun setScheduler(scheduler: Scheduler) {
    this.scheduler = scheduler
  }
}