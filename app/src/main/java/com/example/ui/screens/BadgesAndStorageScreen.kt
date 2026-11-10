package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumLevel
import com.example.data.DiplomaTrack
import com.example.ui.LevelNodeState
import com.example.ui.QuestUiState
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed

@Composable
fun UnitBadgesScreen(
    uiState: QuestUiState,
    onOpenLevel: (CurriculumLevel) -> Unit
) {
    var inspectedNode by remember { mutableStateOf<LevelNodeState?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("unit_badges_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = EtaNavy.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = EtaGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Unit Badges (${uiState.badgesEarnedCount}/25)",
                            style = MaterialTheme.typography.titleLarge,
                            color = EtaNavy
                        )
                        Text(
                            text = "${uiState.totalHoursCompleted} Hrs Completed • ${uiState.totalStars} Stars Earned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(uiState.visibleDiplomaTracks) { track ->
            val isDiplomaUnlocked = uiState.isDiplomaUnlocked(track)
            val trackLevels = uiState.levels.filter { it.level.track == track }
            val earnedCount = trackLevels.count { it.isPassed }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            if (!isDiplomaUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked Diploma",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                            }
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isDiplomaUnlocked) track.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDiplomaUnlocked) track.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isDiplomaUnlocked) "$earnedCount/${trackLevels.size}" else "Locked",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isDiplomaUnlocked) track.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (!isDiplomaUnlocked) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Complete your current diploma to reveal badges in ${track.title}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            trackLevels.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    rowItems.forEach { node ->
                                        BadgeTileCard(
                                            modifier = Modifier.weight(1f),
                                            node = node,
                                            onClick = { inspectedNode = node }
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (inspectedNode != null) {
        val node = inspectedNode!!
        AlertDialog(
            onDismissRequest = { inspectedNode = null },
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = node.level.badgeEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(node.level.badgeName, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = "${node.level.title} (${node.level.hours} Hrs)",
                            style = MaterialTheme.typography.labelMedium,
                            color = node.level.track.primaryColor
                        )
                    }
                }
            },
            text = {
                Column {
                    Text(node.level.badgeDescription, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (node.isPassed) "✅ Earned! Grade: ${node.progress.inClassGrade}% (${node.starsEarned} Stars)"
                        else "🔒 Pass ${node.level.title} in class (60%+) to earn this badge!",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            confirmButton = {
                if (node.isUnlocked) {
                    Button(
                        onClick = {
                            inspectedNode = null
                            onOpenLevel(node.level)
                        }
                    ) {
                        Text("Open Level")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectedNode = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun BadgeTileCard(
    modifier: Modifier = Modifier,
    node: LevelNodeState,
    onClick: () -> Unit
) {
    val earned = node.isPassed
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = if (earned) EtaGold else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        color = if (earned) EtaGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (earned) EtaGold else Color(0xFF94A3B8)),
                contentAlignment = Alignment.Center
            ) {
                if (earned) {
                    Text(text = node.level.badgeEmoji, fontSize = 14.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.level.badgeName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
                Text(
                    text = "${node.level.title} (${node.level.hours}h)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun OfflineStorageScreen(
    uiState: QuestUiState,
    onDownloadLevel: (CurriculumLevel) -> Unit,
    onRemoveLevelPack: (CurriculumLevel) -> Unit,
    onClearCompletedPacks: () -> Unit
) {
    val visibleLevels = remember(uiState.levels) {
        uiState.levels.filter { uiState.isDiplomaUnlocked(it.level.track) }
    }
    val totalCurriculumMb = remember(uiState.levels) {
        uiState.levels.sumOf { it.level.packSizeMb.toDouble() }.toFloat()
    }
    val usedFraction = (uiState.totalCachedMb / totalCurriculumMb.coerceAtLeast(1f)).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("offline_storage_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = EtaNavy,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Offline Pack Storage",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = String.format("Cached: %.1f MB (%d packs)", uiState.totalCachedMb, uiState.downloadedPacksCount),
                                style = MaterialTheme.typography.labelMedium,
                                color = EtaNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { usedFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF10B981)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onClearCompletedPacks,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_completed_packs_btn")
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Free Space from Passed Levels")
                    }
                }
            }
        }

        items(visibleLevels, key = { it.level.id }) { node ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${node.level.orderIndex}. ${node.level.title} (${node.level.hours}h)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${node.level.packSizeMb} MB",
                            style = MaterialTheme.typography.labelMedium,
                            color = node.level.track.primaryColor
                        )
                    }

                    if (node.isDownloaded) {
                        OutlinedButton(
                            onClick = { onRemoveLevelPack(node.level) },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Remove")
                        }
                    } else if (node.isUnlocked) {
                        Button(
                            onClick = { onDownloadLevel(node.level) },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = node.level.track.primaryColor)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Download")
                        }
                    } else {
                        Text(
                            text = "🔒 Locked",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
