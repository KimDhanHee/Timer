package damin.tothemoon.timer.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimerInfo::class], version = 1)
abstract class TimerDatabase : RoomDatabase() {
  abstract fun timerDao(): TimerDAO

  companion object {
    @Volatile
    private var INSTANCE: TimerDatabase? = null

    fun timerDao(context: Context) = getDatabase(context).timerDao()

    private fun getDatabase(
      context: Context,
    ): TimerDatabase = INSTANCE ?: synchronized(this) {
      val instance = Room.databaseBuilder(
        context.applicationContext,
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