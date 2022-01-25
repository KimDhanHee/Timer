package damin.tothemoon.timer.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDAO {
  @Query("SELECT * FROM timer")
  fun getTimerInfos(): Flow<List<TimerInfo>>

  @Insert
  suspend fun addTimerInfo(timerInfo: TimerInfo): Long

  @Update
  suspend fun updateTimerInfo(timerInfo: TimerInfo)

  @Delete
  suspend fun deleteTimerInfo(timerInfo: TimerInfo)
}