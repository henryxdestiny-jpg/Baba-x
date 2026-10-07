package com.example.model

data class StudioBeat(
    val id: String,
    val name: String,
    val genre: String, // "Afrobeat", "Hip Hop", "Amapiano", "Drill", "R&B"
    val bpm: Int,
    val keyScale: String,
    val description: String,
    val audioFreqBase: Double,
    val priceMoMo: String = "GH₵ 50.00",
    val tags: List<String> = emptyList()
)

data class VocalFxSettings(
    val autoTuneEnabled: Boolean = true,
    val autoTunePitchKey: String = "C Minor", // C Minor, G Minor, D Major, etc.
    val noiseGateReductionDb: Float = 28f, // Automatic noise removal
    val studioReverb: Float = 0.40f, // Warm studio plate reverb
    val tubeSaturation: Float = 0.55f, // Analog warmth
    val deEsserIntensity: Float = 0.70f, // Vocal harshness removal
    val vocalClarityBoost: Float = 0.65f, // Presence & shine
    val beatAlignmentSnap: Boolean = true, // Automatic tempo/quantization alignment
    val vocalMixLevel: Float = 0.85f,
    val beatMixLevel: Float = 0.80f
)

data class RecordingSessionState(
    val isRecording: Boolean = false,
    val isProcessingVocal: Boolean = false,
    val recordingDurationMs: Long = 0L,
    val selectedBeat: StudioBeat? = null,
    val userCustomBeatName: String? = null,
    val isBeatAuditionPlaying: Boolean = false,
    val vocalFx: VocalFxSettings = VocalFxSettings(),
    val inputMicLevel: Float = 0.0f,
    val recordedAudioFilePath: String? = null,
    val lastVocalCleanSummary: String? = null
)

/**
 * AI Stem Separation State: Isolate Vocals vs Instrumental Beat
 */
data class StemSeparationState(
    val isSeparating: Boolean = false,
    val separationProgress: Float = 0f,
    val activeTrackTitle: String? = null,
    val separatedVocalsTrack: ProcessedTrack? = null,
    val separatedBeatTrack: ProcessedTrack? = null,
    val separationMode: StemSeparationMode = StemSeparationMode.BOTH_STEMS
)

enum class StemSeparationMode(val label: String) {
    VOCALS_ONLY("Acappella (Vocals Only)"),
    BEAT_ONLY("Instrumental (Karaoke / Beat Only)"),
    BOTH_STEMS("Separate Both (Stems)")
}

data class ProcessedTrack(
    val id: String,
    val title: String,
    val format: String,
    val durationText: String,
    val durationMs: Long = 180000L,
    val originalBitrate: String,
    val targetQuality: String = "24-bit / 96kHz Apple Music HHD",
    val isAiGenerated: Boolean,
    val aiConfidencePercent: Int,
    val spectralPurity: String = "99.4%",
    val dynamicRange: String = "+14.2 LUFS",
    val processedTimestamp: String,
    val localUriString: String? = null,
    val isDownloaded: Boolean = false,
    val recordingBeatGenre: String? = null,
    val stemType: String? = null // "VOCAL_STEM", "BEAT_STEM", "FULL_MASTER"
)

data class StudioEqualizerState(
    val lowBass: Float = 0.5f,
    val midTone: Float = 0.65f,
    val highTreble: Float = 0.8f,
    val stereoSpread: Float = 0.75f,
    val hhdUpsamplingEnabled: Boolean = true,
    val aiArtifactFilter: Boolean = true
)

data class BeatStep(
    val isKick: Boolean = false,
    val isSnare: Boolean = false,
    val isHiHat: Boolean = false,
    val isSynth: Boolean = false
)

enum class StudioTab(val label: String, val testTag: String) {
    HOME("Home", "nav_tab_home"),
    VOCAL_BOOTH("Mic Booth", "nav_tab_booth"),
    HHD_STUDIO("HHD Studio", "nav_tab_studio"),
    STEM_SPLITTER("AI Stems", "nav_tab_stems"),
    SETTINGS("Settings", "nav_tab_settings")
}
