package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudioBeat
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HhdBorder
import com.example.ui.theme.HhdSurfaceDark
import com.example.ui.theme.HhdSurfaceElevated
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextSub

/**
 * Beat Store component inside HHD Studio with category filters:
 * Afrobeat, Hip Hop, Amapiano, Drill, and R&B.
 * Allows users to audition beats and instantly select/link them to their recorded vocals.
 */
@Composable
fun BeatStoreSection(
    availableBeats: List<StudioBeat>,
    selectedBeat: StudioBeat?,
    isAuditionPlaying: Boolean,
    onAuditionBeat: (StudioBeat) -> Unit,
    onSelectBeatForVocals: (StudioBeat) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Afrobeat", "Hip Hop", "Amapiano", "Drill", "R&B")
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredBeats = if (selectedCategory == "All") {
        availableBeats
    } else {
        availableBeats.filter { it.genre.equals(selectedCategory, ignoreCase = true) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("beat_store_card"),
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
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "HHD Studio Beat Store",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "Licensed Instrumentals for Recorded Vocals",
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
                        text = "ROYALTY FREE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Text(
                text = "Choose from exclusive industry beats categorized by genre. Audition instantly or mix directly with your vocal recordings.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSub,
                    lineHeight = 15.sp
                )
            )

            // Category Filter Pills (Afrobeat, Hip Hop, Amapiano, Drill, R&B)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isCatActive = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCatActive) CyanAccent.copy(alpha = 0.2f) else ChipBg)
                            .border(1.dp, if (isCatActive) CyanAccent else HhdBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp)
                            .testTag("beat_cat_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isCatActive) CyanAccent else TextSub,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.drop(3).forEach { cat ->
                    val isCatActive = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCatActive) CyanAccent.copy(alpha = 0.2f) else ChipBg)
                            .border(1.dp, if (isCatActive) CyanAccent else HhdBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 8.dp)
                            .testTag("beat_cat_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isCatActive) CyanAccent else TextSub,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // List of Beats in Selected Category
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredBeats.forEach { beat ->
                    val isThisSelected = selectedBeat?.id == beat.id
                    val isThisAuditioning = isThisSelected && isAuditionPlaying

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isThisSelected) CyanAccent.copy(alpha = 0.12f) else ChipBg)
                            .border(1.dp, if (isThisSelected) CyanAccent else HhdBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                            .testTag("store_beat_item_${beat.id}")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { onAuditionBeat(beat) },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isThisAuditioning) GoldAccent else HhdSurfaceElevated)
                                            .testTag("audition_beat_${beat.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (isThisAuditioning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Audition",
                                            tint = if (isThisAuditioning) Color.Black else CyanAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = beat.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = if (isThisSelected) CyanAccent else TextMain,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = "${beat.genre} • ${beat.bpm} BPM • ${beat.keyScale}",
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
                                        text = "FREE BEAT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = beat.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSub,
                                    fontSize = 10.sp
                                )
                            )

                            // Action buttons: Select to Mix with Vocals
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onSelectBeatForVocals(beat) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("select_beat_button_${beat.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isThisSelected) NeonGreen else CyanAccent,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isThisSelected) Icons.Default.CheckCircle else Icons.Default.Mic,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isThisSelected) "Selected for Vocal Mix" else "Mix with Recorded Vocals",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
