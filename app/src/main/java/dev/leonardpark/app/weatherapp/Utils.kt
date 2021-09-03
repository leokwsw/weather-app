package dev.leonardpark.app.weatherapp

import android.content.Context
import android.content.res.Configuration
import java.lang.Double

class Utils {
  companion object {
    fun isUsingNightModeResources(context: Context): Boolean {
      return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
      }
    }

    fun numeric(string: String): Boolean {
      var numeric = true

      try {
        Double.parseDouble(string)
      } catch (e: NumberFormatException) {
        numeric = false
      }
      return numeric
    }
  }
}