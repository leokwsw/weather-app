package dev.leonardpark.app.weatherapp.api

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
) {
  override fun toString(): String {
    var str = ""

    str += "Coordinate : lat : ${coord.lat} ; lon : ${coord.lon}\n"
    str += "Weather :\n"
    weather.forEachIndexed { index, obj ->
      str += "[$index] id : ${obj.id}\n"
      str += "[$index] description : ${obj.description}\n"
      str += "[$index] icon : ${obj.icon}\n"
      str += "[$index] main : ${obj.main}\n"
    }
    str += "base : ${base}\n"
    str += "main.temp : ${main.temp}\n"
    str += "main.feels_like : ${main.feelsLike}\n"
    str += "main.temp_min : ${main.tempMin}\n"
    str += "main.temp_max : ${main.tempMax}\n"
    str += "main.pressure : ${main.pressure}\n"
    str += "main.humidity : ${main.humidity}\n"
    str += "visibility : ${visibility}\n"
    str += "wind.deg : ${wind.deg}\n"
    str += "wind.speed : ${wind.speed}\n"
    str += "clouds.all : ${clouds.all}\n"
    str += "dt : ${clouds.all}\n"
    str += "sys.type : ${sys.type}\n"
    str += "sys.id : ${sys.id}\n"
    str += "sys.country : ${sys.country}\n"
    str += "sys.sunrise : ${sys.sunrise}\n"
    str += "sys.sunset : ${sys.sunset}\n"
    str += "timezone : ${timezone}\n"
    str += "id : ${id}\n"
    str += "name : ${name}\n"
    str += "cod : ${cod}\n"

    return str
  }
}