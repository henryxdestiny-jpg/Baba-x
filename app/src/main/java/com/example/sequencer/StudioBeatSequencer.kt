package com.example.sequencer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.BeatStep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

data class BeatSequencerState(
    val isPlaying: Boolean = false,
    val bpm: Int = 120,
    val currentStep: Int = 0,
    val steps: List<BeatStep> = List(8) { idx ->
        // Default groovy Afrobeat rhythm
        when (idx) {
            0 -> BeatStep(isKick = true, isHiHat = true)
            2 -> BeatStep(isHiHat = true)
            4 -> BeatStep(isSnare = true, isHiHat = true)
            6 -> BeatStep(isKick = true, isHiHat = true, isSynth = true)
            else -> BeatStep(isHiHat = (idx % 2 != 0))
        }
    }
)

class StudioBeatSequencer(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var loopJob: Job? = null
    private var audioTrack: AudioTrack? = null

    private val _sequencerState = MutableStateFlow(BeatSequencerState())
    val sequencerState: StateFlow<BeatSequencerState> = _sequencerState.asStateFlow()

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val sampleRate = 44100
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(minBufSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.play()
        } catch (e: Exception) {
            // ignore
        }
    }

    fun togglePlayback() {
        if (_sequencerState.value.isPlaying) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        loopJob?.cancel()
        _sequencerState.value = _sequencerState.value.copy(isPlaying = true)

        loopJob = scope.launch {
            while (isActive && _sequencerState.value.isPlaying) {
                val bpm = _sequencerState.value.bpm
                val stepDurationMs = ((60000L / bpm) / 2).coerceAtLeast(80L) // 8th notes
                val stepIdx = _sequencerState.value.currentStep
                val currentBeat = _sequencerState.value.steps[stepIdx]

                // Trigger audio synthesis for active drum hits
                triggerStepAudio(currentBeat)

                delay(stepDurationMs)

                val nextStep = (stepIdx + 1) % _sequencerState.value.steps.size
                _sequencerState.value = _sequencerState.value.copy(currentStep = nextStep)
            }
        }
    }

    fun stop() {
        _sequencerState.value = _sequencerState.value.copy(isPlaying = false, currentStep = 0)
        loopJob?.cancel()
    }

    fun setBpm(newBpm: Int) {
        _sequencerState.value = _sequencerState.value.copy(bpm = newBpm.coerceIn(60, 200))
    }

    fun toggleInstrument(stepIndex: Int, instrument: String) {
        val list = _sequencerState.value.steps.toMutableList()
        val current = list[stepIndex]
        val updated = when (instrument) {
            "kick" -> current.copy(isKick = !current.isKick)
            "snare" -> current.copy(isSnare = !current.isSnare)
            "hihat" -> current.copy(isHiHat = !current.isHiHat)
            "synth" -> current.copy(isSynth = !current.isSynth)
            else -> current
        }
        list[stepIndex] = updated
        _uiBeatUpdate(list)
    }

    private fun _uiBeatUpdate(list: List<BeatStep>) {
        _sequencerState.value = _sequencerState.value.copy(steps = list)
    }

    private fun triggerStepAudio(step: BeatStep) {
        if (!step.isKick && !step.isSnare && !step.isHiHat && !step.isSynth) return
        scope.launch {
            try {
                val sampleRate = 44100
                val samplesCount = 1800
                val buffer = ShortArray(samplesCount)
                for (i in 0 until samplesCount) {
                    val t = i.toDouble() / sampleRate
                    var sum = 0.0

                    if (step.isKick) {
                        // Pitch dropping sine wave
                        val pitch = 130.0 * exp(-15.0 * t) + 40.0
                        sum += 0.8 * sin(2.0 * Math.PI * pitch * t) * exp(-10.0 * t)
                    }
                    if (step.isSnare) {
                        // Noise + 180Hz punch
                        val noise = (Math.random() * 2.0 - 1.0) * 0.4
                        val tone = 0.4 * sin(2.0 * Math.PI * 180.0 * t)
                        sum += (noise + tone) * exp(-14.0 * t)
                    }
                    if (step.isHiHat) {
                        // High noise tick
                        val noise = (Math.random() * 2.0 - 1.0) * 0.35
                        sum += noise * exp(-45.0 * t)
                    }
                    if (step.isSynth) {
                        // Neo-soul Afro synth chord note
                        val tone = 0.5 * sin(2.0 * Math.PI * 349.23 * t) + 0.3 * sin(2.0 * Math.PI * 523.25 * t)
                        sum += tone * exp(-6.0 * t)
                    }

                    buffer[i] = (sum.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun release() {
        stop()
        try {
            audioTrack?.release()
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
    }
}
