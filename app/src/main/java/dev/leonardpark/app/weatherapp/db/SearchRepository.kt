package dev.leonardpark.app.weatherapp.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class SearchRepository(
  private val searchExecutors: SearchExecutors,
  private val database: SearchDatabase
) {
  companion object {
    private var instance: SearchRepository? = null

    fun getInstance(searchExecutors: SearchExecutors, database: SearchDatabase): SearchRepository {
      if (instance == null) {
        synchronized(SearchRepository::class.java) {
          if (instance == null) {
            instance = SearchRepository(searchExecutors, database)
          }
        }
      }
      return instance!!
    }
  }

  fun insertSearches(vararg searchEntities: SearchEntity?) {
    searchExecutors.diskIO()
      .execute { database.getSearchDao().insertSearches(*searchEntities) }
  }

  fun deleteSearches(vararg searchEntities: SearchEntity?) {
    searchExecutors.diskIO()
      .execute { database.getSearchDao().deleteSearches(*searchEntities) }
  }

  fun getSearchListLive(): LiveData<List<SearchEntity>> {
    return database.getSearchDao().getSearchListLive().asLiveData()
  }
}