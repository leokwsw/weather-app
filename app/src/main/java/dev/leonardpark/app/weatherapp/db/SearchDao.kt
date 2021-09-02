package dev.leonardpark.app.weatherapp.db

import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
abstract class SearchDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertSearches(vararg searchEntities: SearchEntity?)

  @Delete
  abstract fun deleteSearches(vararg searchEntities: SearchEntity?)

  @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
  abstract fun getSearchList(): List<SearchEntity>

  @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
  abstract fun getSearchListLive(): Flow<List<SearchEntity>>
}