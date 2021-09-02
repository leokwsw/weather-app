package dev.leonardpark.app.weatherapp.data

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
  @GET("weather")
  fun getWeatherByCityName(
    @Query("q") cityName: String,
    @Query("appid") appId: String = "95d190a434083879a6398aafd54d9e73"
  ): Observable<WeatherResponse>

  @GET("weather")
  fun getWeatherByCoordinate(
    @Query("lat") lat: Float,
    @Query("lon") lon: Float,
    @Query("appid") appId: String = "95d190a434083879a6398aafd54d9e73"
  ): Observable<WeatherResponse>

  @GET("weather")
  fun getWeatherByZipCode(
    @Query("zip") zip: String,
    @Query("appid") appId: String = "95d190a434083879a6398aafd54d9e73"
  ): Observable<WeatherResponse>
}