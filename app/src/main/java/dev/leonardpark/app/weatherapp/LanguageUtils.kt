package dev.leonardpark.app.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import androidx.preference.PreferenceManager
import java.util.*

object LanguageUtils {
  var lang: String?
    get() = prefs.getString(langKey, getDefaultLang())
    set(lang) = prefs.edit().putString(langKey, lang).apply()

  enum class LangList(val value: String) {
    ZT("zt"),
    EN("en"),
    ZS("zs")
  }

  private lateinit var prefs: SharedPreferences

  private const val langKey = "LANGUAGE_CODE"

  fun init(context: Context) {
    prefs = PreferenceManager.getDefaultSharedPreferences(context)
  }

  fun setCurrentLocal(context: Context): Context {
    val resources = context.resources
    val config = resources.configuration
    val locale = getLocal()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      LocaleList.setDefault(LocaleList(locale))
      config.setLocales(LocaleList(locale))
    } else {
      Locale.setDefault(locale)
      config.setLocale(locale)
    }
    resources.updateConfiguration(config, resources.displayMetrics)
//    context.createConfigurationContext(config)
    return context.createConfigurationContext(config)
  }

  private fun getLocal() = when (lang) {
    "zt" -> Locale("zh", "HK")
    "en" -> Locale("en", "US")
    "zs" -> Locale("zh", "CN")
    else -> Locale(lang ?: "")
  }

  private fun getDefaultLang(): String {
    val language = when (
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        LocaleList.getDefault().get(0).language
      } else {
        Locale.getDefault().language
      }
    ) {
      LangList.ZT.name -> LangList.ZT.value
      LangList.ZS.name -> LangList.ZS.value
      else -> LangList.EN.value
    }

    setDefaultLang(language)
    return language
  }

  private fun setDefaultLang(lang: String) {
    if (prefs.getString(langKey, "") == "") {
      prefs.edit().putString(langKey, lang).apply()
    }
  }


}