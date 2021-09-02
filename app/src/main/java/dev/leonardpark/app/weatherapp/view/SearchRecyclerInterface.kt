package dev.leonardpark.app.weatherapp.view

import dev.leonardpark.app.weatherapp.db.SearchEntity

interface SearchRecyclerInterface {
  fun onSearchItemClicked(query: String)

  fun onSearchDeleteClicked(searchEntity: SearchEntity)
}