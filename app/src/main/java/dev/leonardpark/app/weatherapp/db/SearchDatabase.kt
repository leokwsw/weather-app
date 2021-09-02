package dev.leonardpark.app.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchEntity::class], version = 1)
abstract class SearchDatabase : RoomDatabase() {
  companion object {
    private const val DATABASE_NAME = "weather-search-db"

    private var instance: SearchDatabase? = null

    fun getInstance(context: Context): SearchDatabase {
      if (instance == null) {
        synchronized(SearchDatabase::class.java) {
          if (instance == null) {
            instance = buildDatabase(context.applicationContext)
          }
        }
      }
      return instance!!
    }

    private fun buildDatabase(context: Context): SearchDatabase {
      return Room.databaseBuilder(context, SearchDatabase::class.java, DATABASE_NAME).build()
    }
  }

  abstract fun getSearchDao(): SearchDao
}