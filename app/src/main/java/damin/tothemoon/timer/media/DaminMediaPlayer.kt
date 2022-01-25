package damin.tothemoon.timer.media

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import damin.tothemoon.damin.utils.AndroidUtils

object DaminMediaPlayer {
  private var mediaPlayer: MediaPlayer? = null

  private fun init() {
    val uri =
      RingtoneManager.getActualDefaultRingtoneUri(AndroidUtils.context, RingtoneManager.TYPE_ALARM)

    mediaPlayer = MediaPlayer().apply {
      setAudioAttributes(
        AudioAttributes.Builder()
          .setLegacyStreamType(AudioManager.STREAM_MUSIC)
          .build()
      )
      setDataSource(AndroidUtils.context, uri)
      isLooping = true
    }
  }

  fun play() {
    release()
    init()
    mediaPlayer!!.setOnPreparedListener { player ->
      player.start()
    }
    mediaPlayer!!.prepareAsync()
  }

  fun release() {
    mediaPlayer?.release()
    mediaPlayer = null
  }
}