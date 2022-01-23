package damin.tothemoon.timer.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import damin.tothemoon.damin.utils.AndroidUtils

@Database(entities = [TimerInfo::class], version = 1)
abstract class TimerDatabase : RoomDatabase() {
  abstract fun timerDao(): TimerDAO

  companion object {
    @Volatile
    private var INSTANCE: TimerDatabase? = null

    val timerDao: TimerDAO
      get() = database.timerDao()

    private val database: TimerDatabase
      get() = INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          AndroidUtils.application,
          TimerDatabase::class.java,
          "timer_database"
        )
          .fallbackToDestructiveMigration()
          .build()

        INSTANCE = instance

        instance
      }
  }
}