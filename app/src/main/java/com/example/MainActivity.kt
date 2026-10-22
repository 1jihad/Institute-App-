package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.QuestViewModel
import com.example.ui.screens.AdventureMapScreen
import com.example.ui.screens.LevelDetailScreen
import com.example.ui.screens.OfflineStorageScreen
import com.example.ui.screens.OfficialInstitutePosterScreen
import com.example.ui.screens.UnitBadgesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.QuestGold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DiplomaQuestApp()
            }
        }
    }
}

@Composable
fun DiplomaQuestApp(
    questViewModel: QuestViewModel = viewModel()
) {
    val uiState by questViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.openedLevelId == null && uiState.activeTab != AppTab.ADVENTURE_MAP) {
        BackHandler {
            questViewModel.selectTab(AppTab.ADVENTURE_MAP)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (uiState.openedLevelNode == null) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = uiState.activeTab == AppTab.ADVENTURE_MAP,
                        onClick = { questViewModel.selectTab(AppTab.ADVENTURE_MAP) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == AppTab.ADVENTURE_MAP) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = stringResource(R.string.map_tab),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(stringResource(R.string.map_tab), style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("nav_adventure_map")
                    )
                    NavigationBarItem(
                        selected = uiState.activeTab == AppTab.UNIT_BADGES,
                        onClick = { questViewModel.selectTab(AppTab.UNIT_BADGES) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == AppTab.UNIT_BADGES) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = stringResource(R.string.badges_tab),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text("${stringResource(R.string.badges_tab)} (${uiState.badgesEarnedCount})", style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("nav_unit_badges")
                    )
                    NavigationBarItem(
                        selected = uiState.activeTab == AppTab.OFFLINE_STORAGE,
                        onClick = { questViewModel.selectTab(AppTab.OFFLINE_STORAGE) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == AppTab.OFFLINE_STORAGE) Icons.Filled.CloudDownload else Icons.Outlined.CloudDownload,
                                contentDescription = stringResource(R.string.storage_tab),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(stringResource(R.string.storage_tab), style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("nav_offline_storage")
                    )
                    NavigationBarItem(
                        selected = uiState.activeTab == AppTab.INSTITUTE_POSTER,
                        onClick = { questViewModel.selectTab(AppTab.INSTITUTE_POSTER) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == AppTab.INSTITUTE_POSTER) Icons.Filled.Map else Icons.Outlined.Map,
                                contentDescription = stringResource(R.string.poster_tab),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(stringResource(R.string.poster_tab), style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.testTag("nav_institute_poster")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(visible = uiState.bannerMessage != null) {
                val msg = uiState.bannerMessage ?: ""
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { questViewModel.dismissBanner() }) {
                            Text("OK", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            val openedNode = uiState.openedLevelNode
            if (openedNode != null) {
                LevelDetailScreen(
                    node = openedNode,
                    isDownloading = uiState.downloadingLevelId == openedNode.level.id,
                    downloadProgress = uiState.downloadProgress,
                    onBack = { questViewModel.closeLevelDetail() },
                    onDownloadContent = { questViewModel.downloadLevelContent(openedNode.level) },
                    onRemoveOfflinePack = { questViewModel.removeOfflinePack(openedNode.level) },
                    onToggleMaterial = { mat ->
                        questViewModel.toggleMaterialDone(openedNode.level, mat)
                    },
                    onToggleAssignment = { assign ->
                        questViewModel.toggleAssignmentDone(openedNode.level, assign)
                    },
                    onSubmitQuizScore = { pct ->
                        questViewModel.submitSkillQuizScore(openedNode.level, pct)
                    },
                    onSaveInClassGrade = { spk, lst, rd, wr, note, unlockNext ->
                        questViewModel.saveInClassGrade(
                            level = openedNode.level,
                            speaking = spk,
                            listening = lst,
                            reading = rd,
                            writing = wr,
                            teacherNote = note,
                            unlockNextAutomatically = unlockNext
                        )
                    }
                )
            } else {
                when (uiState.activeTab) {
                    AppTab.ADVENTURE_MAP -> AdventureMapScreen(
                        uiState = uiState,
                        onSelectTrack = { questViewModel.selectTrackFilter(it) },
                        onOpenLevel = { questViewModel.openLevelDetail(it.id) },
                        onUnlockLevel = { lvl, code, cb ->
                            questViewModel.attemptUnlockLevel(lvl, code, cb)
                        },
                        onUnlockDiploma = { track, code, cb ->
                            questViewModel.attemptUnlockDiploma(track, code, cb)
                        },
                        onUpdateProfile = { name, avatar ->
                            questViewModel.updateProfile(name, avatar)
                        },
                        onOpenPosterTab = { questViewModel.selectTab(AppTab.INSTITUTE_POSTER) }
                    )

                    AppTab.UNIT_BADGES -> UnitBadgesScreen(
                        uiState = uiState,
                        onOpenLevel = { questViewModel.openLevelDetail(it.id) }
                    )

                    AppTab.OFFLINE_STORAGE -> OfflineStorageScreen(
                        uiState = uiState,
                        onDownloadLevel = { questViewModel.downloadLevelContent(it) },
                        onRemoveLevelPack = { questViewModel.removeOfflinePack(it) },
                        onClearCompletedPacks = { questViewModel.clearCompletedPacks() }
                    )

                    AppTab.INSTITUTE_POSTER -> OfficialInstitutePosterScreen()
                }
            }
        }
    }

    val badgeLevel = uiState.newlyEarnedBadgeLevel
    if (badgeLevel != null) {
        AlertDialog(
            onDismissRequest = { questViewModel.dismissBadgeCelebration() },
            shape = RoundedCornerShape(22.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(QuestGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badgeLevel.badgeEmoji, fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Unit Badge Unlocked!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = badgeLevel.badgeName,
                        style = MaterialTheme.typography.titleLarge,
                        color = badgeLevel.track.primaryColor,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "You passed ${badgeLevel.title} (${badgeLevel.hours} Hrs)! ${badgeLevel.badgeDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { questViewModel.dismissBadgeCelebration() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Collect Badge")
                }
            }
        )
    }
}
