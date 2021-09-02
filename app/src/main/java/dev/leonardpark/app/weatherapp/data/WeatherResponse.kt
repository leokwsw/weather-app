package dev.leonardpark.app.weatherapp.data

import dev.leonardpark.app.weatherapp.model.*

data class WeatherResponse(
  var coord: Coordinate,
  var weather: List<Object>,
  var base: String,
  var main: Main,
  var visibility: Int,
  var wind: Wind,
  var clouds: Clouds,
  var dt: Int,
  var sys: Sys,
  var timezone: Int,
  var id: Int,
  var name: String,
  var cod: Int
)