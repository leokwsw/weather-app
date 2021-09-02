package dev.leonardpark.app.weatherapp

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider : SearchRecentSuggestionsProvider() {
  init {
    setupSuggestions(AUTHORITY, MODE)
  }

  companion object {
    const val AUTHORITY = "dev.leonardpark.app.weatherapp.MySuggestionProvider"
    const val MODE: Int = DATABASE_MODE_QUERIES
  }


}