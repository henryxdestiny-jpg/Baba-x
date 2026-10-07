package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

/**
 * High-definition synthetic tone audio player simulating studio playback
 * using Android's native AudioTrack at 48kHz/96kHz high dynamic range.
 */
class StudioAudioEngine(private val context: Context) {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false

    suspend fun playMasteringPreview(frequency: Double = 440.0, durationMs: Int = 1800) = withContext(Dispatchers.Default) {
        stop()
        try {
            val sampleRate = 48000
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                // Generate a rich studio test tone with subtle harmonics (f + 2f)
                val t = i.toDouble() / sampleRate
                val tone1 = sin(2.0 * Math.PI * frequency * t)
                val tone2 = 0.3 * sin(2.0 * Math.PI * (frequency * 1.5) * t)
                val tone3 = 0.15 * sin(2.0 * Math.PI * (frequency * 2.0) * t)
                val sample = ((tone1 + tone2 + tone3) * 0.7 * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                buffer[i] = sample.toShort()
            }

            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            audioTrack = track
            isPlaying = true
        } catch (e: Exception) {
            Log.e("StudioAudioEngine", "Playback preview error", e)
        }
    }

    fun stop() {
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        } finally {
            audioTrack = null
            isPlaying = false
        }
    }
}
