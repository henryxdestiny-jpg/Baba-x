package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.StudioBeat
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentVariant
import com.example.ui.theme.DangerRed
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
import kotlin.math.sin

@Composable
fun VocalBoothScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val session = uiState.recordingSession
    val fx = session.vocalFx
    val micLevel by viewModel.micLevel.collectAsState()
    val isBeatPlaying by viewModel.isBeatAuditioning.collectAsState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.toggleVocalRecording()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Vocal Recording Master Deck
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vocal_recorder_deck_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (session.isRecording) DangerRed else HhdBorderGlow)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (session.isRecording) DangerRed else CyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pro Vocal Recording Booth",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (session.isRecording) DangerRed.copy(alpha = 0.25f) else ChipBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (session.isRecording) "LIVE RECORDING" else "BOOTH READY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (session.isRecording) DangerRed else NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Recording timer
                    val totalSec = (session.recordingDurationMs / 1000).toInt()
                    val timeFormatted = String.format("%02d:%02d", totalSec / 60, totalSec % 60)

                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = if (session.isRecording) DangerRed else TextMain,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp
                        )
                    )

                    // Live Vocal Wave & Noise Reduction Meter
                    VocalLevelMeter(
                        level = if (session.isRecording) micLevel.coerceAtLeast(0.12f) else 0.05f,
                        isRecording = session.isRecording,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ChipBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    // Big Recording Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (session.isRecording) DangerRed else CyanAccent)
                                .clickable {
                                    if (hasMicPermission) {
                                        viewModel.toggleVocalRecording()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .testTag("record_mic_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (session.isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                                contentDescription = if (session.isRecording) "Stop Recording" else "Start Recording",
                                tint = if (session.isRecording) Color.White else Color.Black,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = if (session.isRecording)
                            "Recording vocal with ${session.selectedBeat?.name ?: "backing beat"}. Tap ⏹ to finish & polish voice."
                        else
                            "Select a beat below and tap ⏺ to record. Automatic noise removal & Auto-Tune will polish your voice.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSub,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )

                    // Auto-cleanup notice
                    session.lastVocalCleanSummary?.let { summary ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen.copy(alpha = 0.12f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Beat Selection Session (Afrobeat, Hip Hop, Amapiano, Drill, R&B)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("beat_selection_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
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
                        Column {
                            Text(
                                text = "Select Studio Beat Session",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Afrobeat, Hip Hop, Amapiano, Drill & R&B",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSub,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleBeatAudition() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBeatPlaying) GoldAccent else HhdSurfaceElevated,
                                contentColor = if (isBeatPlaying) Color.Black else CyanAccent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("audition_beat_button")
                        ) {
                            Icon(
                                imageVector = if (isBeatPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBeatPlaying) "Auditioning" else "Audition",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Beat Cards List
                    uiState.availableBeats.forEach { beat ->
                        val isSelected = session.selectedBeat?.id == beat.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.15f) else ChipBg)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanAccent else HhdBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.selectRecordingBeat(beat)
                                    if (isBeatPlaying) {
                                        viewModel.toggleBeatAudition()
                                    }
                                }
                                .padding(12.dp)
                                .testTag("beat_item_${beat.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = beat.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = if (isSelected) CyanAccent else TextMain,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(PurpleAccent)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = beat.genre,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${beat.bpm} BPM • Key: ${beat.keyScale} • ${beat.description}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSub,
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected Beat",
                                        tint = CyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Automatic Noise Removal & Vocal Polish Settings (Auto-Tune, Gate, Reverb)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vocal_fx_settings_card"),
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Auto Voice Polish & Noise Removal",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    // Auto-Tune Toggle & Key Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Real-Time Pitch Auto-Tune", color = TextMain, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Harmonizes voice pitch to musical scale", color = TextSub, fontSize = 11.sp)
                        }
                        Switch(
                            checked = fx.autoTuneEnabled,
                            onCheckedChange = { viewModel.updateVocalFx(autoTune = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyanAccent
                            ),
                            modifier = Modifier.testTag("switch_autotune")
                        )
                    }

                    // Key scale pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("C Minor", "F# Minor", "G Minor", "A Major").forEach { key ->
                            val isSelected = fx.autoTunePitchKey == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else ChipBg)
                                    .border(1.dp, if (isSelected) CyanAccent else HhdBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.updateVocalFx(key = key) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) CyanAccent else TextSub,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Divider(color = HhdBorder)

                    // Noise Gate Reduction
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Automatic Noise Gate (Hiss/Fan/Hum Removal)", color = TextMain, fontSize = 12.sp)
                            Text("-${fx.noiseGateReductionDb.toInt()} dB", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = fx.noiseGateReductionDb,
                            onValueChange = { viewModel.updateVocalFx(noiseGateDb = it) },
                            valueRange = 10f..45f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonGreen,
                                activeTrackColor = NeonGreen,
                                inactiveTrackColor = HhdBorder
                            ),
                            modifier = Modifier.testTag("slider_noise_gate")
                        )
                    }

                    // Studio Reverb
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Warm Studio Plate Reverb", color = TextMain, fontSize = 12.sp)
                            Text("${(fx.studioReverb * 100).toInt()}%", color = PurpleAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = fx.studioReverb,
                            onValueChange = { viewModel.updateVocalFx(reverb = it) },
                            colors = SliderDefaults.colors(
                                thumbColor = PurpleAccent,
                                activeTrackColor = PurpleAccent,
                                inactiveTrackColor = HhdBorder
                            ),
                            modifier = Modifier.testTag("slider_reverb")
                        )
                    }

                    // Tube Saturation & Analog Warmth
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Analog Tube Warmth & Vocal Presence", color = TextMain, fontSize = 12.sp)
                            Text("${(fx.tubeSaturation * 100).toInt()}%", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = fx.tubeSaturation,
                            onValueChange = { viewModel.updateVocalFx(saturation = it) },
                            colors = SliderDefaults.colors(
                                thumbColor = GoldAccent,
                                activeTrackColor = GoldAccent,
                                inactiveTrackColor = HhdBorder
                            ),
                            modifier = Modifier.testTag("slider_tube")
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VocalLevelMeter(
    level: Float,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 24
    Canvas(modifier = modifier) {
        val totalWidth = size.width
        val barWidth = (totalWidth / barCount) * 0.7f
        val gap = (totalWidth - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

        for (i in 0 until barCount) {
            val threshold = i.toFloat() / barCount
            val isActive = level >= threshold
            val barHeight = size.height * (0.3f + 0.7f * threshold)
            val left = i * (barWidth + gap)
            val top = (size.height - barHeight) / 2f

            val color = when {
                !isActive -> Color(0xFF1E2638)
                threshold < 0.65f -> Color(0xFF00FF87)
                threshold < 0.85f -> Color(0xFFFFD700)
                else -> Color(0xFFFF4060)
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}
