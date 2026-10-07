package com.example.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PersistentPlayerDeck
import com.example.ui.components.TrackItemCard
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HhdBorder
import com.example.ui.theme.HhdBorderGlow
import com.example.ui.theme.HhdSurfaceDark
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextSub
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var quickInputName by remember { mutableStateOf("") }

    val playerState by viewModel.playerState.collectAsState()

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.processMediaUri(uri)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Studio Hero Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorderGlow),
                colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_studio_banner),
                        contentDescription = "Studio Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, HhdSurfaceDark.copy(alpha = 0.85f), HhdSurfaceDark)
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Universal HHD Engine",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "One-Tap Download Session & AI Auto-Update Engine",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMain,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Dedicated Background Music Player Deck
        item {
            PersistentPlayerDeck(
                playerState = playerState,
                onPlayPause = { viewModel.togglePlayPause() },
                onSeek = { viewModel.seekTo(it) },
                onVolumeChange = { viewModel.setVolume(it) },
                onLoopToggle = { viewModel.toggleContinuousLoop() },
                onNext = { viewModel.playNextTrack() },
                onPrevious = { viewModel.playPreviousTrack() }
            )
        }

        // Upload & Processing Zone with simple Download Session
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_processing_card"),
                shape = RoundedCornerShape(14.dp),
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
                        Text(
                            text = "Upload & Download Session",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ONE-TAP EXPORT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "Upload any MP3, WAV or Video. Once completed, download directly to your device with one tap.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSub,
                            lineHeight = 16.sp
                        )
                    )

                    // Drop Zone / Picker Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, CyanAccent, RoundedCornerShape(12.dp))
                            .background(CyanAccent.copy(alpha = 0.05f))
                            .clickable {
                                filePicker.launch("audio/*")
                            }
                            .padding(vertical = 18.dp, horizontal = 16.dp)
                            .testTag("drop_zone_picker"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Import",
                                tint = CyanAccent,
                                modifier = Modifier.size(34.dp)
                            )
                            Text(
                                text = "Tap to Import Music or Video (MP3, WAV)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Ready for immediate download & playback when finished",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSub,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Quick name processing button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = quickInputName,
                            onValueChange = { quickInputName = it },
                            placeholder = { Text("Or name track to master & download...", color = TextSub, fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_track_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = HhdBorder,
                                focusedTextColor = TextMain,
                                unfocusedTextColor = TextMain,
                                focusedContainerColor = ChipBg,
                                unfocusedContainerColor = ChipBg
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Button(
                            onClick = {
                                val name = if (quickInputName.isNotBlank()) quickInputName.trim() else "Accra_HHD_Release.wav"
                                viewModel.processMediaNamed(name)
                                quickInputName = ""
                            },
                            modifier = Modifier.testTag("quick_process_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Process", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Processing Indicator
                    AnimatedVisibility(visible = uiState.isProcessing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = { uiState.processingProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("mastering_progress_bar"),
                                color = CyanAccent,
                                trackColor = ChipBg
                            )
                            Text(
                                text = uiState.processingStatusText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    if (!uiState.isProcessing && uiState.processingStatusText.isNotBlank()) {
                        Text(
                            text = uiState.processingStatusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Processed Studio Tracks & Download Session List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Studio Masters & Downloads (${uiState.tracks.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "Tap 📥 to save to device",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSub,
                        fontSize = 11.sp
                    )
                )
            }
        }

        if (uiState.tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tracks processed yet. Import a file to download.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
                    )
                }
            }
        } else {
            items(uiState.tracks, key = { it.id }) { track ->
                val isThisTrackActive = playerState.currentTrackId == track.id
                val isDownloadingThis = uiState.isDownloadingTrackId == track.id
                TrackItemCard(
                    track = track,
                    isPlaying = isThisTrackActive && playerState.isPlaying,
                    isDownloading = isDownloadingThis,
                    onPlayClick = {
                        if (isThisTrackActive) {
                            viewModel.togglePlayPause()
                        } else {
                            viewModel.playTrack(track)
                        }
                    },
                    onDownloadClick = {
                        viewModel.downloadTrack(track)
                    },
                    onDeleteClick = { viewModel.deleteTrack(track.id) }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
