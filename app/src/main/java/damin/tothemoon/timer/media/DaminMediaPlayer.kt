package damin.tothemoon.timer.media

import android.media.MediaPlayer
import android.media.RingtoneManager
import damin.tothemoon.damin.utils.AndroidUtils

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

  fun play() {
    release()
    init()

    DaminAudioManager.setTimerVolume()

    mediaPlayer!!.setOnPreparedListener { player ->
      player.start()
    }
    mediaPlayer!!.prepareAsync()
  }

  fun release() {
    DaminAudioManager.release()

    mediaPlayer?.release()
    mediaPlayer = null
  }
}