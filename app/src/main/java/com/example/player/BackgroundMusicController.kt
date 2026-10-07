package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

data class PlayerState(
    val currentTrackId: String? = null,
    val currentTrackTitle: String? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val totalDurationMs: Long = 180000L,
    val volume: Float = 0.85f,
    val isContinuousLoop: Boolean = true,
    // Dynamic spectrum magnitudes calculated for 16 bands
    val spectrumBands: List<Float> = List(16) { 0.15f }
)

class BackgroundMusicController(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var synthAudioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // Callback when track finishes
    var onTrackFinishedListener: (() -> Unit)? = null

    fun playTrack(
        trackId: String,
        title: String,
        uriString: String?,
        totalDurationMs: Long = 180000L
    ) {
        stop()

        _playerState.value = _playerState.value.copy(
            currentTrackId = trackId,
            currentTrackTitle = title,
            isPlaying = true,
            currentPositionMs = 0L,
            totalDurationMs = totalDurationMs
        )

        var uriLoaded = false
        if (!uriString.isNullOrBlank()) {
            try {
                val uri = Uri.parse(uriString)
                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, uri)
                    isLooping = _playerState.value.isContinuousLoop
                    setVolume(_playerState.value.volume, _playerState.value.volume)
                    prepare()
                    start()
                    setOnCompletionListener {
                        if (!_playerState.value.isContinuousLoop) {
                            pause()
                            onTrackFinishedListener?.invoke()
                        }
                    }
                }
                mediaPlayer = mp
                _playerState.value = _playerState.value.copy(
                    totalDurationMs = mp.duration.toLong().coerceAtLeast(1000L)
                )
                uriLoaded = true
            } catch (e: Exception) {
                Log.w("BackgroundMusicPlayer", "Could not play via Uri, falling back to HHD synthetic master", e)
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }

        if (!uriLoaded) {
            // Play continuous high-fidelity 48kHz audio track stream
            startHhdAudioSynthesis(title)
        }

        startProgressAndSpectrumLoop()
    }

    private fun startHhdAudioSynthesis(trackTitle: String) {
        synthJob?.cancel()
        synthJob = scope.launch {
            try {
                val sampleRate = 48000
                val minBufSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
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
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.setVolume(_playerState.value.volume)
                track.play()
                synthAudioTrack = track

                val baseFreq = if (trackTitle.contains("Afrobeat", ignoreCase = true)) 220.0 else 329.63
                val buffer = ShortArray(1024)
                var sampleIndex = 0L

                while (isActive && _playerState.value.isPlaying) {
                    val vol = _playerState.value.volume
                    for (i in 0 until buffer.size step 2) {
                        val t = sampleIndex.toDouble() / sampleRate
                        val f1 = sin(2.0 * Math.PI * baseFreq * t)
                        val f2 = 0.5 * sin(2.0 * Math.PI * (baseFreq * 1.5) * t)
                        val f3 = 0.25 * sin(2.0 * Math.PI * (baseFreq * 2.0) * t)
                        val beatMod = 0.7 + 0.3 * sin(2.0 * Math.PI * 2.0 * t)

                        val sampleLeft = ((f1 + f2) * beatMod * 0.45 * vol * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        val sampleRight = ((f1 + f3) * beatMod * 0.45 * vol * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

                        buffer[i] = sampleLeft
                        buffer[i + 1] = sampleRight
                        sampleIndex++
                    }
                    track.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                Log.e("BackgroundMusicPlayer", "Synth streaming error", e)
            }
        }
    }

    private fun startProgressAndSpectrumLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            var stepCounter = 0
            while (isActive) {
                delay(60)
                if (_playerState.value.isPlaying) {
                    stepCounter++
                    val mp = mediaPlayer
                    val currentPos = if (mp != null) {
                        try { mp.currentPosition.toLong() } catch (e: Exception) { _playerState.value.currentPositionMs + 60 }
                    } else {
                        val next = _playerState.value.currentPositionMs + 60
                        if (next >= _playerState.value.totalDurationMs) {
                            if (_playerState.value.isContinuousLoop) 0L else {
                                pause()
                                onTrackFinishedListener?.invoke()
                                _playerState.value.totalDurationMs
                            }
                        } else next
                    }

                    // Dynamically generate 16 frequency spectrum visualizer bands that react to time & audio volume
                    val vol = _playerState.value.volume
                    val bands = List(16) { bandIdx ->
                        val phase = (stepCounter * 0.15) + (bandIdx * 0.45)
                        val base = 0.25f + 0.65f * abs(sin(phase).toFloat())
                        // Scale by volume
                        (base * vol.coerceIn(0.15f, 1f)).coerceIn(0.05f, 1f)
                    }

                    _playerState.value = _playerState.value.copy(
                        currentPositionMs = currentPos,
                        spectrumBands = bands
                    )
                } else {
                    // Decay spectrum smoothly when paused
                    val decayed = _playerState.value.spectrumBands.map { (it * 0.85f).coerceAtLeast(0.05f) }
                    _playerState.value = _playerState.value.copy(spectrumBands = decayed)
                }
            }
        }
    }

    fun pause() {
        _playerState.value = _playerState.value.copy(isPlaying = false)
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
            // ignore
        }
        synthAudioTrack?.pause()
    }

    fun resume() {
        _playerState.value = _playerState.value.copy(isPlaying = true)
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            // ignore
        }
        try {
            synthAudioTrack?.play()
        } catch (e: Exception) {
            // restart synthesis if needed
            _playerState.value.currentTrackTitle?.let { startHhdAudioSynthesis(it) }
        }
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            if (_playerState.value.currentTrackId != null) {
                resume()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        val target = positionMs.coerceIn(0L, _playerState.value.totalDurationMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = target)
        try {
            mediaPlayer?.seekTo(target.toInt())
        } catch (e: Exception) {
            // ignore
        }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _playerState.value = _playerState.value.copy(volume = clamped)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            // ignore
        }
        try {
            synthAudioTrack?.setVolume(clamped)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun toggleContinuousLoop() {
        val newLoop = !_playerState.value.isContinuousLoop
        _playerState.value = _playerState.value.copy(isContinuousLoop = newLoop)
        try {
            mediaPlayer?.isLooping = newLoop
        } catch (e: Exception) {
            // ignore
        }
    }

    fun stop() {
        _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0L)
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
        }

        try {
            synthAudioTrack?.stop()
            synthAudioTrack?.release()
        } catch (e: Exception) {
            // ignore
        } finally {
            synthAudioTrack = null
        }
        synthJob?.cancel()
    }

    fun release() {
        stop()
        progressJob?.cancel()
    }
}
