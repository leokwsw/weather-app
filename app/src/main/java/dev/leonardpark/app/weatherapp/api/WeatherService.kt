package dev.leonardpark.app.weatherapp.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
  @GET("weather")
  fun getWeatherByCityName(
    @Query("q") cityName: String,
    @Query("appid") appId: String = WeatherFactor.APP_KEY
  ): Observable<WeatherResponse>

  @GET("weather")
  fun getWeatherByCoordinate(
    @Query("lat") lat: Float,
    @Query("lon") lon: Float,
    @Query("appid") appId: String = WeatherFactor.APP_KEY
  ): Observable<WeatherResponse>

  @GET("weather")
  fun getWeatherByZipCode(
    @Query("zip") zip: String,
    @Query("appid") appId: String = WeatherFactor.APP_KEY
  ): Observable<WeatherResponse>
}