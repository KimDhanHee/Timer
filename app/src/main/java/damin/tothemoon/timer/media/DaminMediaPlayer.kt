package damin.tothemoon.timer.media

import android.media.MediaPlayer
import android.media.RingtoneManager
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger

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
      isLooping = true
    }
  }

  fun play() = synchronized(this) {
    release()
    init()

    DaminAudioManager.setTimerVolume()

    mediaPlayer!!.setOnPreparedListener { player ->
      EventLogger.logMedia(DaminEvent.MEDIA_PLAY)

      player.start()
    }
    mediaPlayer!!.prepareAsync()
    EventLogger.logMedia(DaminEvent.MEDIA_PREPARE)
  }

  fun release() {
    EventLogger.logMedia(DaminEvent.MEDIA_RELEASE)

    if (mediaPlayer?.isPlaying == true) {
      mediaPlayer?.stop()
    }

    mediaPlayer?.release()
    mediaPlayer = null

    DaminAudioManager.release()
  }
}