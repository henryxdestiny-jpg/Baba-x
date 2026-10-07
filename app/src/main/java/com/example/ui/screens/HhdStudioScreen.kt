package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.sequencer.BeatSequencerState
import com.example.ui.components.PersistentPlayerDeck
import com.example.ui.components.ReactiveSpectrumVisualizer
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HhdBorder
import com.example.ui.theme.HhdBorderGlow
import com.example.ui.theme.HhdSurfaceDark
import com.example.ui.theme.HhdSurfaceElevated
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextSub
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@Composable
fun HhdStudioScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val eq = uiState.equalizerState
    val playerState by viewModel.playerState.collectAsState()
    val seqState by viewModel.sequencerState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Player Controls Header
        PersistentPlayerDeck(
            playerState = playerState,
            onPlayPause = { viewModel.togglePlayPause() },
            onSeek = { viewModel.seekTo(it) },
            onVolumeChange = { viewModel.setVolume(it) },
            onLoopToggle = { viewModel.toggleContinuousLoop() },
            onNext = { viewModel.playNextTrack() },
            onPrevious = { viewModel.playPreviousTrack() }
        )

        // Reactive Audio Spectrum Visualizer (Reacts dynamically to current track & volume)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reactive_spectrum_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorderGlow)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HHD Reactive Spectrum Visualizer",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (playerState.isPlaying) NeonGreen.copy(alpha = 0.2f) else ChipBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (playerState.isPlaying) "LIVE 96kHz STREAM" else "IDLE / PAUSED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (playerState.isPlaying) NeonGreen else TextSub,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Text(
                    text = if (playerState.isPlaying)
                        "Currently analyzing frequency dynamics for: \"${playerState.currentTrackTitle}\""
                    else
                        "Press play on any track to observe real-time harmonic spectrum peaks.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSub,
                        fontSize = 11.sp
                    )
                )

                // Large Real-Time Reactive Spectrum Display
                ReactiveSpectrumVisualizer(
                    bands = playerState.spectrumBands,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ChipBg)
                        .border(1.dp, HhdBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    barBrush = Brush.verticalGradient(
                        listOf(CyanAccent, PurpleAccent, CyanAccentVariant)
                    )
                )

                // Frequency Band Calibration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("30Hz Sub", color = TextSub, fontSize = 9.sp)
                    Text("250Hz Low", color = TextSub, fontSize = 9.sp)
                    Text("1kHz Mid", color = TextSub, fontSize = 9.sp)
                    Text("8kHz High", color = TextSub, fontSize = 9.sp)
                    Text("48kHz Nyquist", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // HHD Studio Beat Store Section
        com.example.ui.components.BeatStoreSection(
            availableBeats = uiState.availableBeats,
            selectedBeat = uiState.recordingSession.selectedBeat,
            isAuditionPlaying = uiState.recordingSession.isBeatAuditionPlaying,
            onAuditionBeat = { beat ->
                viewModel.selectRecordingBeat(beat)
                viewModel.toggleBeatAudition()
            },
            onSelectBeatForVocals = { beat ->
                viewModel.selectRecordingBeat(beat)
                viewModel.selectTab(com.example.model.StudioTab.VOCAL_BOOTH)
            }
        )

        // HHD Studio Beat Sequencing Tool
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("beat_sequencer_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Afrobeat / HHD Drum Sequencer",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = PurpleAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${seqState.bpm} BPM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (seqState.isPlaying) DangerRedOrGold(seqState.isPlaying) else CyanAccent)
                                .clickable { viewModel.beatSequencer.togglePlayback() }
                                .testTag("sequencer_play_toggle"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (seqState.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (seqState.isPlaying) "Stop Beat" else "Play Beat",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Tap grid cells to sequence polyrhythms while listening to your background master.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSub,
                        fontSize = 11.sp
                    )
                )

                // 8-Step Drum Matrix
                SequencerRow(
                    label = "Kick",
                    color = CyanAccent,
                    currentStep = if (seqState.isPlaying) seqState.currentStep else -1,
                    steps = seqState.steps.map { it.isKick },
                    onStepToggle = { step -> viewModel.beatSequencer.toggleInstrument(step, "kick") }
                )
                SequencerRow(
                    label = "Snare",
                    color = PurpleAccent,
                    currentStep = if (seqState.isPlaying) seqState.currentStep else -1,
                    steps = seqState.steps.map { it.isSnare },
                    onStepToggle = { step -> viewModel.beatSequencer.toggleInstrument(step, "snare") }
                )
                SequencerRow(
                    label = "Hi-Hat",
                    color = NeonGreen,
                    currentStep = if (seqState.isPlaying) seqState.currentStep else -1,
                    steps = seqState.steps.map { it.isHiHat },
                    onStepToggle = { step -> viewModel.beatSequencer.toggleInstrument(step, "hihat") }
                )
                SequencerRow(
                    label = "Synth",
                    color = GoldAccent,
                    currentStep = if (seqState.isPlaying) seqState.currentStep else -1,
                    steps = seqState.steps.map { it.isSynth },
                    onStepToggle = { step -> viewModel.beatSequencer.toggleInstrument(step, "synth") }
                )

                // Tempo Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Tempo", color = TextSub, fontSize = 11.sp)
                    Slider(
                        value = seqState.bpm.toFloat(),
                        onValueChange = { viewModel.beatSequencer.setBpm(it.toInt()) },
                        valueRange = 80f..160f,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tempo_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent,
                            inactiveTrackColor = HhdBorder
                        )
                    )
                }
            }
        }

        // Parametric Equalizer Adjustments Deck
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mastering_eq_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Parametric Mastering EQ Adjustments",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Low Bass Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sub-Bass Warmth (50Hz)", color = TextMain, fontSize = 12.sp)
                        Text("${(eq.lowBass * 100).toInt()}%", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = eq.lowBass,
                        onValueChange = { viewModel.updateEqualizer(lowBass = it) },
                        colors = SliderDefaults.colors(
                            thumbColor = CyanAccent,
                            activeTrackColor = CyanAccent,
                            inactiveTrackColor = HhdBorder
                        ),
                        modifier = Modifier.testTag("slider_bass")
                    )
                }

                // Mid Tone Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vocal Presence & Clarity (2.5kHz)", color = TextMain, fontSize = 12.sp)
                        Text("${(eq.midTone * 100).toInt()}%", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = eq.midTone,
                        onValueChange = { viewModel.updateEqualizer(midTone = it) },
                        colors = SliderDefaults.colors(
                            thumbColor = PurpleAccent,
                            activeTrackColor = PurpleAccent,
                            inactiveTrackColor = HhdBorder
                        ),
                        modifier = Modifier.testTag("slider_mid")
                    )
                }

                // Treble Air Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ultra-High Air & Silk (16kHz)", color = TextMain, fontSize = 12.sp)
                        Text("${(eq.highTreble * 100).toInt()}%", color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = eq.highTreble,
                        onValueChange = { viewModel.updateEqualizer(highTreble = it) },
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreen,
                            activeTrackColor = NeonGreen,
                            inactiveTrackColor = HhdBorder
                        ),
                        modifier = Modifier.testTag("slider_treble")
                    )
                }

                // Stereo Spread Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("3D Spatial Stereo Dimension", color = TextMain, fontSize = 12.sp)
                        Text("${(eq.stereoSpread * 100).toInt()}%", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = eq.stereoSpread,
                        onValueChange = { viewModel.updateEqualizer(stereoSpread = it) },
                        colors = SliderDefaults.colors(
                            thumbColor = GoldAccent,
                            activeTrackColor = GoldAccent,
                            inactiveTrackColor = HhdBorder
                        ),
                        modifier = Modifier.testTag("slider_stereo")
                    )
                }

                Divider(color = HhdBorder)

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("24-bit / 96kHz Upsampler", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Apple Music Lossless dynamic headroom", color = TextSub, fontSize = 11.sp)
                    }
                    Switch(
                        checked = eq.hhdUpsamplingEnabled,
                        onCheckedChange = { viewModel.updateEqualizer(hhdUpsampling = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = CyanAccent
                        ),
                        modifier = Modifier.testTag("switch_upsampler")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AI Artifact De-Noise Filter", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Suppresses synthetic phase jitter & distortion", color = TextSub, fontSize = 11.sp)
                    }
                    Switch(
                        checked = eq.aiArtifactFilter,
                        onCheckedChange = { viewModel.updateEqualizer(aiArtifactFilter = it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PurpleAccent
                        ),
                        modifier = Modifier.testTag("switch_ai_filter")
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SequencerRow(
    label: String,
    color: Color,
    currentStep: Int,
    steps: List<Boolean>,
    onStepToggle: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(44.dp)
        )
        steps.forEachIndexed { index, isActive ->
            val isCurrentBeat = currentStep == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when {
                            isActive -> color
                            isCurrentBeat -> color.copy(alpha = 0.35f)
                            else -> ChipBg
                        }
                    )
                    .border(
                        1.dp,
                        if (isCurrentBeat) Color.White else HhdBorder,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onStepToggle(index) }
                    .testTag("seq_step_${label.lowercase()}_$index"),
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}

private fun DangerRedOrGold(isPlaying: Boolean): Color {
    return if (isPlaying) Color(0xFFFF4060) else CyanAccent
}
