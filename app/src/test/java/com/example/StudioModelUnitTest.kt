package com.example

import com.example.ai.StudioAiUpdateManager
import com.example.model.BeatStep
import com.example.model.ProcessedTrack
import com.example.model.StemSeparationMode
import com.example.model.StudioBeat
import com.example.model.StudioEqualizerState
import com.example.model.StudioTab
import com.example.model.VocalFxSettings
import com.example.player.PlayerState
import com.example.sequencer.BeatSequencerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StudioModelUnitTest {

    @Test
    fun testBeatStoreCatalogAndCategories() {
        val afroBeat = StudioBeat(
            id = "afro_test",
            name = "Accra Golden Sunset",
            genre = "Afrobeat",
            bpm = 104,
            keyScale = "F# Minor",
            description = "Warm log drums",
            audioFreqBase = 185.0,
            priceMoMo = "GH₵ 40.00",
            tags = listOf("Afro-Fusion", "Groovy")
        )
        val hiphopBeat = StudioBeat(
            id = "hiphop_test",
            name = "808 Metro Midnight",
            genre = "Hip Hop",
            bpm = 140,
            keyScale = "C Minor",
            description = "Heavy 808s",
            audioFreqBase = 130.81,
            priceMoMo = "GH₵ 60.00",
            tags = listOf("Trap", "Hard 808")
        )
        val amapianoBeat = StudioBeat(
            id = "amapiano_test",
            name = "Soweto Private School",
            genre = "Amapiano",
            bpm = 113,
            keyScale = "G Minor",
            description = "Deep log taps",
            audioFreqBase = 196.0,
            priceMoMo = "GH₵ 50.00",
            tags = listOf("Private School")
        )

        assertEquals("Afrobeat", afroBeat.genre)
        assertEquals("Hip Hop", hiphopBeat.genre)
        assertEquals("Amapiano", amapianoBeat.genre)
        assertTrue(afroBeat.tags.contains("Afro-Fusion"))
    }

    @Test
    fun testProcessedTrackCreationAndDownload() {
        val track = ProcessedTrack(
            id = "test_track_1",
            title = "Afrobeat_Master.wav",
            format = "WAV Master",
            durationText = "3:30",
            durationMs = 210000L,
            originalBitrate = "16-bit / 44.1kHz",
            isAiGenerated = false,
            aiConfidencePercent = 10,
            spectralPurity = "99.9%",
            dynamicRange = "+14.2 LUFS",
            processedTimestamp = "11:00 AM",
            isDownloaded = false,
            recordingBeatGenre = "Afrobeat",
            stemType = "FULL_MASTER"
        )

        assertEquals("Afrobeat_Master.wav", track.title)
        assertFalse(track.isAiGenerated)
        assertFalse(track.isDownloaded)
        assertEquals("24-bit / 96kHz Apple Music HHD", track.targetQuality)
        assertEquals("Afrobeat", track.recordingBeatGenre)
        assertEquals("FULL_MASTER", track.stemType)

        val downloadedTrack = track.copy(isDownloaded = true)
        assertTrue(downloadedTrack.isDownloaded)
    }

    @Test
    fun testStemSeparationModes() {
        assertEquals("Acappella (Vocals Only)", StemSeparationMode.VOCALS_ONLY.label)
        assertEquals("Instrumental (Karaoke / Beat Only)", StemSeparationMode.BEAT_ONLY.label)
        assertEquals("Separate Both (Stems)", StemSeparationMode.BOTH_STEMS.label)
    }

    @Test
    fun testVocalBoothBeatsAndFxDefaults() {
        val fx = VocalFxSettings()
        assertTrue(fx.autoTuneEnabled)
        assertEquals("C Minor", fx.autoTunePitchKey)
        assertEquals(28f, fx.noiseGateReductionDb, 0.001f)
        assertTrue(fx.studioReverb > 0.2f)
        assertTrue(fx.beatAlignmentSnap)
    }

    @Test
    fun testStudioTabs() {
        assertEquals("Home", StudioTab.HOME.label)
        assertEquals("Mic Booth", StudioTab.VOCAL_BOOTH.label)
        assertEquals("HHD Studio", StudioTab.HHD_STUDIO.label)
        assertEquals("AI Stems", StudioTab.STEM_SPLITTER.label)
        assertEquals("Settings", StudioTab.SETTINGS.label)
    }
}
