package damin.tothemoon.timer.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.damin.utils.AndroidUtils
import kotlinx.coroutines.launch

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
          .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              ioScope.launch {
                arrayOf(
                  TimerInfo(title = "🍜라면", time = 3 * TimerInfo.MINUTE_UNIT, color = TimerColor.Green),
                  TimerInfo(title = "🥚계란 반숙", time = 6 * TimerInfo.MINUTE_UNIT, color = TimerColor.Black),
                  TimerInfo(title = "🥚계란 완숙", time = 12 * TimerInfo.MINUTE_UNIT, color = TimerColor.Red),
                  TimerInfo(title = "💪플랭크", time = 1 * TimerInfo.MINUTE_UNIT, color = TimerColor.Purple)
                ).forEach {
                  timerDao.addTimerInfo(it)
                }
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()

        INSTANCE = instance

        instance
      }
  }
}