package dev.leonardpark.app.weatherapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_table")
class SearchEntity(
  @PrimaryKey
  private var text: String,
  private var timestamp: Long
) {
  fun getText() = text
  fun setText(text: String) {
    this.text = text
  }

  fun getTimestamp() = timestamp
  fun setTimestamp(timestamp: Long) {
    this.timestamp = timestamp
  }
}