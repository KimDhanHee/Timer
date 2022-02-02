package damin.tothemoon.timer.media

import android.media.MediaPlayer
import android.media.RingtoneManager
import damin.tothemoon.damin.extensions.ioScope
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger
import kotlinx.coroutines.launch

object DaminMediaPlayer {
  private var mediaPlayer: MediaPlayer? = null

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

      DaminAudioManager.release()

      mediaPlayer?.release()
      mediaPlayer = null
    }
  }
}