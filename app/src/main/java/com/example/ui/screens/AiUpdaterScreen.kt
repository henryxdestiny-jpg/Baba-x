package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.StudioUpdateRelease
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
fun AiUpdaterScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Auto-Update Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_updater_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorderGlow)
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Studio Auto-Update Tool",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "Engine v${uiState.appCurrentEngineVersion}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GoldAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.checkForAiStudioUpdates(isAutomatic = false) },
                            modifier = Modifier.testTag("check_updates_button")
                        ) {
                            if (uiState.isCheckingForAiUpdates) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = CyanAccent,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Check for updates",
                                    tint = CyanAccent
                                )
                            }
                        }
                    }

                    Text(
                        text = "When new studio mastering models, audio plugins, or features release, the AI Tool automatically updates the app in real time and installs them directly into your audio engine.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSub,
                            lineHeight = 16.sp
                        )
                    )

                    Divider(color = HhdBorder)

                    // Auto-Update Switch Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Automatic AI Live Patching",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMain,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Auto-install studio algorithm updates silently",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSub,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Switch(
                            checked = uiState.autoUpdateEnabled,
                            onCheckedChange = { viewModel.toggleAutoUpdate(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyanAccent
                            ),
                            modifier = Modifier.testTag("switch_auto_update")
                        )
                    }

                    // Progress bar if applying update
                    AnimatedVisibility(visible = uiState.isApplyingAiUpdate) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "⚡ AI Deploying Studio Update to App...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            LinearProgressIndicator(
                                progress = { uiState.aiUpdateProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanAccent,
                                trackColor = ChipBg
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Studio Releases & AI Patches",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Verified Cloud Sync",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // List of Releases
        items(uiState.updatesList) { update ->
            UpdateReleaseCard(
                release = update,
                onApplyClick = { viewModel.triggerAutoDeployUpdate(update) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UpdateReleaseCard(
    release: StudioUpdateRelease,
    onApplyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("release_card_${release.version}"),
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
                Column {
                    Text(
                        text = release.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${release.version} • ${release.date}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSub,
                            fontSize = 11.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "INSTALLED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Text(
                text = release.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSub,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            // Features added bullet list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ChipBg)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Added to Studio by AI:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                release.featuresAdded.forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feat,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMain,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Button(
                onClick = onApplyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_update_button_${release.version}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HhdSurfaceElevated,
                    contentColor = CyanAccent
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-verify & Re-apply Studio Patch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
