package dev.leonardpark.app.weatherapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherFactor {
  companion object {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val APP_KEY = "95d190a434083879a6398aafd54d9e73"
  }

  fun create(): WeatherService {
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    })

    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(httpClient.build())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
    return retrofit.create(WeatherService::class.java)
  }
}