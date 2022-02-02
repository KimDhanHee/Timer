package damin.tothemoon.timer.media

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import damin.tothemoon.damin.utils.AndroidUtils
import damin.tothemoon.timer.event.DaminEvent
import damin.tothemoon.timer.event.EventLogger

object DaminAudioManager {
  private val defaultVolume: Int
    get() = AndroidUtils.audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) / 2
  private var originalVolume: Int = 0

  val audioAttributes: AudioAttributes
    get() = AudioAttributes.Builder()
      .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
      .setUsage(AudioAttributes.USAGE_ALARM)
      .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
      .build()

  fun setTimerVolume() {
    EventLogger.logMedia(DaminEvent.AUDIO_PLAY)

    originalVolume = AndroidUtils.audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
    setVolume(defaultVolume)
  }

  fun release() {
    EventLogger.logMedia(DaminEvent.AUDIO_RELEASE)

    setVolume(originalVolume)
    abandonAudioFocus()
  }

  private fun setVolume(volume: Int) {
    EventLogger.logMedia(DaminEvent.AUDIO_VOLUME_SET, bundleOf(
      "volume" to volume
    ))

    AndroidUtils.audioManager.setStreamVolume(
      AudioManager.STREAM_ALARM,
      volume,
      AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
    )
  }

  private val audioFocusRequest: AudioFocusRequest
    @RequiresApi(Build.VERSION_CODES.O)
    get() = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
      .setAudioAttributes(audioAttributes)
      .build()

  fun requestAudioFocus() {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        AndroidUtils.audioManager.requestAudioFocus(audioFocusRequest)
      else ->
        AndroidUtils.audioManager.requestAudioFocus(
          null,
          AudioManager.STREAM_ALARM,
          AudioManager.AUDIOFOCUS_GAIN
        )
    }
  }

  private fun abandonAudioFocus() {
    when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
        AndroidUtils.audioManager.abandonAudioFocusRequest(audioFocusRequest)
      else ->
        AndroidUtils.audioManager.abandonAudioFocus(null)
    }
  }
}