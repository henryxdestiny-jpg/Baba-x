package com.example.ui.screens

import android.os.Build
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChipBg
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.HhdBorder
import com.example.ui.theme.HhdSurfaceDark
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMain
import com.example.ui.theme.TextSub
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@Composable
fun SettingsScreen(
    uiState: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Studio Specifications Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("specs_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Studio Engine Specifications",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )

                SettingRow(title = "Audio Engine", value = "HX Universal HHD Native")
                SettingRow(title = "Bit Depth", value = "24-bit Integer / 32-bit Float")
                SettingRow(title = "Sample Rate", value = "96,000 Hz (Apple Music HHD)")
                SettingRow(title = "Dynamic Range", value = "144 dB Theoretical")
                SettingRow(title = "AI Detection Model", value = "Neural Frequency Artifact v4")
                SettingRow(title = "Stem Separation DSP", value = "Harmonic Phase Isolation Engine")
            }
        }

        // About & License Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("about_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = HhdSurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, HhdBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "About Henry X Destiny Studio",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )

                Text(
                    text = "Engineered for music producers, recording artists, and content creators. Provides high-fidelity audio mastering, lossy-to-lossless acoustic upsampling, and machine learning spectral checks for AI-synthesized tracks.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSub,
                        lineHeight = 16.sp
                    )
                )

                Divider(color = HhdBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Build Architecture",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
                    )
                    Text(
                        text = "Android Compose • SDK ${Build.VERSION.SDK_INT}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Version",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSub)
                    )
                    Text(
                        text = "Universal HHD 2.4.0",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SettingRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ChipBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSub, fontSize = 12.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextMain,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}
