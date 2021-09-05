package dev.leonardpark.app.weatherapp.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insert(vararg searchEntities: SearchEntity?)

  @Delete
  abstract fun delete(vararg searchEntities: SearchEntity?)

  @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
  abstract fun getList(): List<SearchEntity>

  @Query("SELECT * FROM search_table ORDER BY timestamp DESC LIMIT 5")
  abstract fun getListLive(): Flow<List<SearchEntity>>

  @Query("DELETE FROM search_table")
  abstract fun deleteAll()
}