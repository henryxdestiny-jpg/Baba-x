package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessedTrack
import com.example.model.StemSeparationMode
import com.example.ui.components.TrackItemCard
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
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
fun StemSplitterScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val stemState = uiState.stemState
    val playerState by viewModel.playerState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Vocal & Beat Separator Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stem_separator_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorderGlow)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallSplit,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Voice & Beat Separator",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "Split Song into Isolated Stems",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSub,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "KARAOKE / ACAPPELLA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "Remove the voice from any music track to extract the clean instrumental beat, or extract the isolated vocals (Acappella) to remix over any beat.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSub,
                            lineHeight = 16.sp
                        )
                    )

                    // Separation Mode Selector (Vocals, Beat, Both)
                    Text(
                        text = "Select Extraction Mode:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMain,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StemSeparationMode.values().forEach { mode ->
                            val isSelected = stemState.separationMode == mode
                            val shortLabel = when (mode) {
                                StemSeparationMode.VOCALS_ONLY -> "Vocals Only"
                                StemSeparationMode.BEAT_ONLY -> "Beat Only"
                                StemSeparationMode.BOTH_STEMS -> "Both Stems"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else ChipBg)
                                    .border(1.dp, if (isSelected) CyanAccent else HhdBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setStemSeparationMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = shortLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) CyanAccent else TextSub,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Progress indicator if separating
                    AnimatedVisibility(visible = stemState.isSeparating) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🧠 AI neural phase canceler isolating: \"${stemState.activeTrackTitle}\"...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            LinearProgressIndicator(
                                progress = { stemState.separationProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("stem_separation_progress"),
                                color = CyanAccent,
                                trackColor = ChipBg
                            )
                        }
                    }
                }
            }
        }

        // Available Tracks in Studio to Separate
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Track to Separate (${uiState.tracks.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Tap 'Separate' below",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSub,
                        fontSize = 11.sp
                    )
                )
            }
        }

        items(uiState.tracks) { track ->
            TrackStemCard(
                track = track,
                isPlaying = playerState.currentTrackId == track.id && playerState.isPlaying,
                onPlayClick = {
                    if (playerState.currentTrackId == track.id) viewModel.togglePlayPause() else viewModel.playTrack(track)
                },
                onSeparateClick = {
                    viewModel.separateStemsForTrack(track)
                },
                onUseAsBackingBeat = {
                    viewModel.attachUserCustomBeat(track)
                    viewModel.selectTab(com.example.model.StudioTab.VOCAL_BOOTH)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrackStemCard(
    track: ProcessedTrack,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onSeparateClick: () -> Unit,
    onUseAsBackingBeat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("track_stem_card_${track.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ChipBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlaying) CyanAccent else HhdBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) CyanAccent.copy(alpha = 0.2f) else HhdSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onPlayClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = if (isPlaying) CyanAccent else TextMain,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMain,
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.format} • ${track.durationText}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSub,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Stem label badge if already a stem
                if (track.stemType != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (track.stemType == "VOCAL_STEM") PurpleAccent.copy(alpha = 0.3f) else NeonGreen.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (track.stemType == "VOCAL_STEM") "🎤 VOCALS" else "🥁 BEAT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (track.stemType == "VOCAL_STEM") PurpleAccent else NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSeparateClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("separate_button_${track.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallSplit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Separate Stems", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onUseAsBackingBeat,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("use_as_beat_button_${track.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HhdSurfaceElevated,
                        contentColor = GoldAccent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sing Over Beat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
