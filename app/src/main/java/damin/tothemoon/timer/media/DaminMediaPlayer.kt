package damin.tothemoon.timer.media

import android.media.MediaPlayer
import android.media.RingtoneManager
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

object DaminMediaPlayer {
  private var mediaPlayer: MediaPlayer? = null
  private var periodicPlayingChecker: Timer? = null

  private const val CHECK_PERIOID = 1000L

  private fun init() {
    val mediaUri = RingtoneManager.getActualDefaultRingtoneUri(
      AndroidUtils.context,
      RingtoneManager.TYPE_ALARM
    )

    mediaPlayer = MediaPlayer().apply {
      setAudioAttributes(DaminAudioManager.audioAttributes)
      setDataSource(AndroidUtils.context, mediaUri)
      setVolume(1f, 1f)
      isLooping = true
    }
  }

  fun play() {
    ioScope.launch {
      EventLogger.logMedia(DaminEvent.MEDIA_PREPARE)

      release()
      init()

      periodicPlayingChecker =
        fixedRateTimer(initialDelay = CHECK_PERIOID, period = CHECK_PERIOID) {
          val needToPlay =
            mediaPlayer == null || (mediaPlayer != null && mediaPlayer?.isPlaying == false)

          if (needToPlay) play()
        }

      DaminAudioManager.setTimerVolume()

      mediaPlayer!!.setOnPreparedListener { player ->
        EventLogger.logMedia(DaminEvent.MEDIA_PLAY)

        player.start()

        DaminAudioManager.requestAudioFocus()
      }
      mediaPlayer!!.prepareAsync()
    }
  }

  fun release() {
    ioScope.launch {
      EventLogger.logMedia(DaminEvent.MEDIA_RELEASE)

      periodicPlayingChecker?.cancel()
      periodicPlayingChecker = null

      DaminAudioManager.release()

      mediaPlayer?.release()
      mediaPlayer = null
    }
  }
}