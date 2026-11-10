package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BoxArchitectureType
import com.example.data.CurriculumLevel
import com.example.data.DiplomaTrack
import com.example.ui.DailyStreakAndExamGoalBar
import com.example.ui.ExamGoalEditorDialog
import com.example.ui.LevelNodeState
import com.example.ui.QuestUiState
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive

@Composable
fun AdventureMapScreen(
    uiState: QuestUiState,
    onSelectTrack: (DiplomaTrack) -> Unit,
    onOpenLevel: (CurriculumLevel) -> Unit,
    onUnlockLevel: (CurriculumLevel, String, (Boolean) -> Unit) -> Unit,
    onUnlockDiploma: (DiplomaTrack, String, (Boolean) -> Unit) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onDismissUnlockedReveal: () -> Unit = {},
    onOpenProvisioning: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onUpdateExamGoal: (String, Int) -> Unit = { _, _ -> },
    onOpenOnboarding: () -> Unit = {}
) {
    var levelToUnlock by remember { mutableStateOf<CurriculumLevel?>(null) }
    var diplomaToUnlock by remember { mutableStateOf<DiplomaTrack?>(null) }
    var showTeacherCodeSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showExamGoalDialog by remember { mutableStateOf(false) }
    var lastRevealedNode by remember { mutableStateOf<LevelNodeState?>(null) }

    if (uiState.newlyUnlockedLevelNode != null) {
        lastRevealedNode = uiState.newlyUnlockedLevelNode
    }

    val activeTrack = uiState.selectedTrackFilter
    val isActiveTrackUnlocked = uiState.isDiplomaUnlocked(activeTrack)

    val trackLevels = remember(uiState.levels, activeTrack) {
        when (activeTrack) {
            DiplomaTrack.TOEFL_PREP -> uiState.levels
            DiplomaTrack.PTP_PROGRAM -> uiState.levels.filter {
                it.level.boxType != BoxArchitectureType.GENERAL_4_ROOMS
            }
            else -> uiState.levels.filter { it.level.track == activeTrack }
        }
    }

    val mapListState = rememberLazyListState()

    LaunchedEffect(uiState.newlyUnlockedLevelId, activeTrack) {
        val unlockedId = uiState.newlyUnlockedLevelId ?: return@LaunchedEffect
        val targetIndex = trackLevels.indexOfFirst { it.level.id == unlockedId }
        if (targetIndex >= 0) {
            // Item 0 is the diploma header banner, so offset by 1
            mapListState.animateScrollToItem((targetIndex + 1).coerceAtLeast(0))
        }
    }

    val nextDiplomaTrack = remember(activeTrack, uiState.isPtpRevealed) {
        val entries = uiState.visibleDiplomaTracks
        val idx = entries.indexOf(activeTrack)
        if (idx in 0 until entries.lastIndex) entries[idx + 1] else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        activeTrack.primaryColor.copy(alpha = 0.07f),
                        MaterialTheme.colorScheme.background,
                        activeTrack.primaryColor.copy(alpha = 0.04f)
                    )
                )
            )
    ) {
        // Sleek Branded Top HUD Bar (Student Edition with ETA + Colored Arch Logo)
        CompactBrandedTopHudBar(
            uiState = uiState,
            onEditProfile = { showProfileDialog = true },
            onOpenProvisioning = onOpenProvisioning,
            onOpenNotifications = onOpenNotifications
        )

        // Daily Study Streak & Exam Countdown Goal Bar + Notification Center Pill
        DailyStreakAndExamGoalBar(
            profile = uiState.profile,
            unreadNotificationsCount = uiState.unreadNotificationsCount,
            onEditExamGoal = { showExamGoalDialog = true },
            onOpenNotifications = onOpenNotifications
        )

        // Compact Diploma Selector Bar (Locked diplomas show lock icon and hide units)
        CompactDiplomaSelectorRow(
            uiState = uiState,
            selectedTrack = activeTrack,
            onSelectTrack = { track ->
                if (uiState.isDiplomaUnlocked(track)) {
                    onSelectTrack(track)
                } else {
                    diplomaToUnlock = track
                }
            }
        )

        // Smooth AnimatedVisibility reveal when a student enters a valid teacher code to unlock a module
        AnimatedVisibility(
            visible = uiState.newlyUnlockedLevelNode != null,
            enter = fadeIn(animationSpec = tween(420)) +
                expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) +
                scaleIn(
                    initialScale = 0.88f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
            exit = fadeOut(animationSpec = tween(250)) +
                shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(250)
                ) +
                scaleOut(targetScale = 0.92f, animationSpec = tween(250))
        ) {
            val revealedNode = uiState.newlyUnlockedLevelNode ?: lastRevealedNode
            if (revealedNode != null) {
                UnlockedModuleRevealBanner(
                    node = revealedNode,
                    onStartModule = { onOpenLevel(revealedNode.level) },
                    onDismiss = onDismissUnlockedReveal
                )
            }
        }

        AnimatedVisibility(
            visible = !isActiveTrackUnlocked,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.92f)
        ) {
            LockedDiplomaGateView(
                track = activeTrack,
                onEnterDiplomaCode = { diplomaToUnlock = activeTrack }
            )
        }

        AnimatedVisibility(
            visible = isActiveTrackUnlocked,
            enter = fadeIn(tween(450)) +
                expandVertically(expandFrom = Alignment.Top, animationSpec = tween(450)) +
                scaleIn(initialScale = 0.96f, animationSpec = tween(450)),
            exit = fadeOut(tween(250)) + shrinkVertically(tween(250))
        ) {
            // Scrollable Game-Like Winding Learning Map
            LazyColumn(
                state = mapListState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("adventure_map_list"),
                contentPadding = PaddingValues(top = 4.dp, bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    CompactDiplomaHeaderBanner(
                        track = activeTrack,
                        trackLevels = trackLevels
                    )
                }

                itemsIndexed(
                    items = trackLevels,
                    key = { _, item -> item.level.id }
                ) { index, nodeState ->
                    val previousStage = if (index > 0) trackLevels[index - 1].level.stageGroup else null
                    val isNewlyUnlocked = uiState.newlyUnlockedLevelId == nodeState.level.id

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 440.dp)
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (nodeState.level.stageGroup != previousStage) {
                            CompactStageDividerPill(
                                stageTitle = nodeState.level.stageGroup,
                                color = activeTrack.primaryColor
                            )
                        }

                        GameMapPathStep(
                            index = index,
                            node = nodeState,
                            isNewlyUnlocked = isNewlyUnlocked,
                            isLastInTrack = index == trackLevels.lastIndex,
                            onClickNode = {
                                if (nodeState.isUnlocked) {
                                    onOpenLevel(nodeState.level)
                                } else {
                                    levelToUnlock = nodeState.level
                                }
                            }
                        )
                    }
                }

                if (nextDiplomaTrack != null) {
                    item {
                        val nextUnlocked = uiState.isDiplomaUnlocked(nextDiplomaTrack)
                        NextDiplomaPortalCard(
                            nextTrack = nextDiplomaTrack,
                            isUnlocked = nextUnlocked,
                            onClick = {
                                if (nextUnlocked) {
                                    onSelectTrack(nextDiplomaTrack)
                                } else {
                                    diplomaToUnlock = nextDiplomaTrack
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (levelToUnlock != null) {
        val targetLevel = levelToUnlock!!
        TeacherUnlockDialog(
            level = targetLevel,
            onDismiss = { levelToUnlock = null },
            onSubmitCode = { enteredCode, onError ->
                onUnlockLevel(targetLevel, enteredCode) { success ->
                    if (success) {
                        levelToUnlock = null
                    } else {
                        onError()
                    }
                }
            }
        )
    }

    if (diplomaToUnlock != null) {
        val targetTrack = diplomaToUnlock!!
        LockedDiplomaUnlockDialog(
            track = targetTrack,
            onDismiss = { diplomaToUnlock = null },
            onSubmitCode = { code, onError ->
                onUnlockDiploma(targetTrack, code) { success ->
                    if (success) {
                        diplomaToUnlock = null
                    } else {
                        onError()
                    }
                }
            }
        )
    }

    if (showProfileDialog) {
        StudentProfileDialog(
            currentName = uiState.profile.studentName,
            currentAvatar = uiState.profile.avatarEmoji,
            boundMacAddress = uiState.boundMacAddress,
            onDismiss = { showProfileDialog = false },
            onSave = { name, avatar ->
                onUpdateProfile(name, avatar)
                showProfileDialog = false
            },
            onOpenProvisioning = {
                showProfileDialog = false
                onOpenProvisioning()
            },
            onOpenOnboarding = {
                showProfileDialog = false
                onOpenOnboarding()
            }
        )
    }

    if (showExamGoalDialog) {
        ExamGoalEditorDialog(
            currentTitle = uiState.profile.examGoalTitle,
            currentDaysLeft = uiState.profile.daysUntilExam,
            onDismiss = { showExamGoalDialog = false },
            onSaveGoal = { title, days ->
                onUpdateExamGoal(title, days)
                showExamGoalDialog = false
            }
        )
    }
}

@Composable
private fun CompactBrandedTopHudBar(
    uiState: QuestUiState,
    onEditProfile: () -> Unit,
    onOpenProvisioning: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Student Avatar & Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onEditProfile)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .testTag("student_profile_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(EtaNavy.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.profile.avatarEmoji, fontSize = 17.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = uiState.profile.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = "${uiState.totalHoursCompleted}h Completed",
                        style = MaterialTheme.typography.labelMedium,
                        color = EtaNavy
                    )
                }
            }

            // Sleek Student Counters + Notification Bell + Student Role Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Notification Bell with unread badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (uiState.unreadNotificationsCount > 0) EtaRed.copy(alpha = 0.14f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                        .clickable(onClick = onOpenNotifications)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("notification_bell_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.unreadNotificationsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (uiState.unreadNotificationsCount > 0) EtaRed else EtaNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        if (uiState.unreadNotificationsCount > 0) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${uiState.unreadNotificationsCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaRed,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = EtaBlue.copy(alpha = 0.14f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onOpenProvisioning)
                        .testTag("device_role_badge_btn")
                ) {
                    Text(
                        text = "🎓 STUDENT",
                        style = MaterialTheme.typography.labelMedium,
                        color = EtaBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                HudCounterChip(emoji = "⚡", text = "${uiState.profile.totalXp}", bgColor = Color(0xFFFEF3C7), textColor = Color(0xFFB45309))
                HudCounterChip(emoji = "⭐", text = "${uiState.totalStars}", bgColor = Color(0xFFE8EDF8), textColor = EtaNavy)
                HudCounterChip(emoji = "🏅", text = "${uiState.badgesEarnedCount}", bgColor = Color(0xFFFFEBEE), textColor = EtaRed)
            }
        }
    }
}

@Composable
private fun HudCounterChip(
    emoji: String,
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 10.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor
        )
    }
}

@Composable
private fun CompactDiplomaSelectorRow(
    uiState: QuestUiState,
    selectedTrack: DiplomaTrack,
    onSelectTrack: (DiplomaTrack) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        uiState.visibleDiplomaTracks.forEach { track ->
            val unlocked = uiState.isDiplomaUnlocked(track)
            val isSelected = selectedTrack == track
            val shortName = when (track) {
                DiplomaTrack.INTERMEDIATE_DIPLOMA -> "Boxes 1–13"
                DiplomaTrack.ADVANCED_DIPLOMA -> "Boxes 14–21"
                DiplomaTrack.TOEFL_PREP -> "All 21 Boxes"
                DiplomaTrack.PTP_PROGRAM -> "Skills 1–6"
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = when {
                    isSelected -> track.primaryColor
                    unlocked -> MaterialTheme.colorScheme.surface
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (unlocked) track.primaryColor.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelectTrack(track) }
                    .testTag("filter_${track.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!unlocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Diploma",
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = shortName,
                        style = MaterialTheme.typography.labelLarge,
                        color = when {
                            isSelected -> Color.White
                            unlocked -> track.primaryColor
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactDiplomaHeaderBanner(
    track: DiplomaTrack,
    trackLevels: List<LevelNodeState>
) {
    val passedCount = trackLevels.count { it.isPassed }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = track.primaryColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "${trackLevels.size} Units • ${track.totalHours} Hrs Total",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "$passedCount/${trackLevels.size} Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactStageDividerPill(
    stageTitle: String,
    color: Color
) {
    Surface(
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = stageTitle,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

private fun nodeOffsetX(index: Int): Dp {
    return when (index % 4) {
        0 -> 0.dp
        1 -> (-50).dp
        2 -> 0.dp
        else -> 50.dp
    }
}

@Composable
private fun UnlockedModuleRevealBanner(
    node: LevelNodeState,
    onStartModule: () -> Unit,
    onDismiss: () -> Unit
) {
    val level = node.level
    val trackColor = level.track.primaryColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 460.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("unlocked_module_reveal_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, trackColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            trackColor.copy(alpha = 0.12f),
                            MaterialTheme.colorScheme.surface,
                            EtaGold.copy(alpha = 0.10f)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(trackColor, trackColor.copy(alpha = 0.8f))
                                )
                            )
                            .border(2.dp, EtaGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = level.badgeEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EtaGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Upgrading Code Verified • Module Unlocked!",
                                style = MaterialTheme.typography.labelMedium,
                                color = trackColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Level ${level.orderIndex}: ${level.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${level.hours} Hours • ${level.materials.size} Materials • ${level.assignments.size} Assignments",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Reveal",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    level.focusSkills.take(3).forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = trackColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.labelMedium,
                                color = trackColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onStartModule,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = trackColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("open_unlocked_module_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Open Module",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun GameMapPathStep(
    index: Int,
    node: LevelNodeState,
    isNewlyUnlocked: Boolean = false,
    isLastInTrack: Boolean,
    onClickNode: () -> Unit
) {
    val level = node.level
    val trackColor = level.track.primaryColor
    val currentOffset = nodeOffsetX(index)
    val nextOffset = nodeOffsetX(index + 1)

    val nodeScale by animateFloatAsState(
        targetValue = when {
            isNewlyUnlocked -> 1.08f
            node.isUnlocked -> 1.0f
            else -> 0.95f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "node_unlock_scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(x = currentOffset)
                    .scale(nodeScale)
                    .clickable(onClick = onClickNode)
                    .testTag("level_node_${level.orderIndex}")
            ) {
                // Smooth AnimatedVisibility reveal for the 3-Star rating bar when unlocked
                AnimatedVisibility(
                    visible = node.isUnlocked,
                    enter = fadeIn(animationSpec = tween(420)) +
                        expandVertically(animationSpec = tween(420)) +
                        scaleIn(
                            initialScale = 0.7f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        ),
                    exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        repeat(3) { starIdx ->
                            val filled = starIdx < node.starsEarned
                            Icon(
                                imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (filled) EtaGold else Color(0xFF94A3B8),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = if (isNewlyUnlocked) 8.dp else if (node.isUnlocked) 4.dp else 1.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(
                            when {
                                node.isPassed -> Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
                                node.isUnlocked -> Brush.verticalGradient(listOf(trackColor, trackColor.copy(alpha = 0.85f)))
                                else -> Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)))
                            }
                        )
                        .border(
                            width = if (isNewlyUnlocked) 3.dp else 2.5.dp,
                            color = when {
                                isNewlyUnlocked -> EtaGold
                                node.isPassed -> Color(0xFFFEF3C7)
                                node.isUnlocked -> Color.White
                                else -> Color(0xFFE2E8F0)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (node.isUnlocked) {
                        Text(
                            text = level.badgeEmoji,
                            fontSize = 22.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Unit",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (node.isPassed) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(1.2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Passed",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = if (isNewlyUnlocked) 4.dp else 1.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isNewlyUnlocked) 1.5.dp else 1.dp,
                        color = when {
                            isNewlyUnlocked -> EtaGold
                            node.isUnlocked -> trackColor.copy(alpha = 0.35f)
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Box ${level.orderIndex}: ${level.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val boxStructureText = when (level.boxType) {
                            BoxArchitectureType.GENERAL_4_ROOMS -> "4 Rooms (Vocab + Grammar)"
                            BoxArchitectureType.SKILLS_RW_2_ROOMS -> "Skills RW (Grammar + Writing)"
                            BoxArchitectureType.SKILLS_LS_2_ROOMS -> "Skills L&S (Listen + Speak)"
                        }
                        Text(
                            text = if (node.isUnlocked) boxStructureText else "$boxStructureText • Tap for Code",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (node.isUnlocked) trackColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Smooth AnimatedVisibility reveal of the unlocked curriculum module summary pill
                        AnimatedVisibility(
                            visible = node.isUnlocked,
                            enter = expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) + fadeIn(animationSpec = tween(420)),
                            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
                        ) {
                            Surface(
                                modifier = Modifier.padding(top = 3.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = if (isNewlyUnlocked) EtaGold.copy(alpha = 0.22f) else trackColor.copy(alpha = 0.10f)
                            ) {
                                Text(
                                    text = when {
                                        isNewlyUnlocked -> "✨ Newly Unlocked • Tap to Enter Box"
                                        node.starsEarned >= 2 -> "⭐ ${node.starsEarned}/3 Stars • Next Box Open ✓"
                                        else -> "Earn ⭐⭐ (2/3) to Unlock Box ${level.orderIndex + 1}"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isNewlyUnlocked) EtaNavy else trackColor,
                                    fontWeight = if (isNewlyUnlocked) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!isLastInTrack) {
            Canvas(
                modifier = Modifier
                    .width(200.dp)
                    .height(28.dp)
            ) {
                val centerX = size.width / 2f
                val startX = centerX + currentOffset.toPx()
                val endX = centerX + nextOffset.toPx()

                val path = Path().apply {
                    moveTo(startX, 0f)
                    cubicTo(
                        startX, size.height * 0.5f,
                        endX, size.height * 0.5f,
                        endX, size.height
                    )
                }

                drawPath(
                    path = path,
                    color = if (node.isPassed) EtaGold.copy(alpha = 0.35f) else trackColor.copy(alpha = 0.16f),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )

                drawPath(
                    path = path,
                    color = if (node.isPassed) EtaGold else trackColor.copy(alpha = 0.55f),
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )
            }
        }
    }
}

@Composable
private fun NextDiplomaPortalCard(
    nextTrack: DiplomaTrack,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 340.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("next_diploma_gate_${nextTrack.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isUnlocked) nextTrack.primaryColor else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isUnlocked) nextTrack.primaryColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isUnlocked) "Enter ${nextTrack.title}" else "${nextTrack.title} (Locked)",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isUnlocked) "Tap to explore units" else "Complete previous diploma or enter Upgrading Code",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LockedDiplomaGateView(
    track: DiplomaTrack,
    onEnterDiplomaCode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.widthIn(max = 360.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(track.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = track.primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${track.title} is Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Units in ${track.title} are protected. Graduate from your current diploma or enter an Upgrading Code from Administration.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEnterDiplomaCode,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = track.primaryColor)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enter Upgrading Code")
                }
            }
        }
    }
}

@Composable
private fun LockedDiplomaUnlockDialog(
    track: DiplomaTrack,
    onDismiss: () -> Unit,
    onSubmitCode: (String, () -> Unit) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = track.primaryColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Unlock ${track.title}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Enter the Upgrading Code from Administration for this diploma level:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        showError = false
                    },
                    label = { Text("Upgrading Code") },
                    placeholder = { Text("Enter Upgrading Code") },
                    singleLine = true,
                    isError = showError,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = "Invalid Upgrading Code for ${track.title}. Please check with Administration.",
                        style = MaterialTheme.typography.labelMedium,
                        color = EtaRed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitCode(codeInput) { showError = true } },
                colors = ButtonDefaults.buttonColors(containerColor = track.primaryColor)
            ) {
                Text("Unlock Diploma")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TeacherUnlockDialog(
    level: CurriculumLevel,
    onDismiss: () -> Unit,
    onSubmitCode: (String, () -> Unit) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = level.badgeEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Unlock ${level.title}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Level ${level.orderIndex} • ${level.hours} Hrs",
                        style = MaterialTheme.typography.labelMedium,
                        color = level.track.primaryColor
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Enter the Upgrading Code from Administration to access ${level.title}:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        showError = false
                    },
                    label = { Text("Upgrading Code") },
                    placeholder = {
                        Text("Enter Upgrading Code from Administration")
                    },
                    singleLine = true,
                    isError = showError,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_code_input")
                )
                if (showError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Incorrect Upgrading Code. Please obtain your Upgrading Code from Administration.",
                        style = MaterialTheme.typography.labelMedium,
                        color = EtaRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitCode(codeInput) {
                        showError = true
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = level.track.primaryColor),
                modifier = Modifier.testTag("confirm_unlock_button")
            ) {
                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Unlock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun StudentProfileDialog(
    currentName: String,
    currentAvatar: String,
    boundMacAddress: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onOpenProvisioning: () -> Unit,
    onOpenOnboarding: () -> Unit = {}
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }
    val avatars = listOf("🦊", "🦁", "🐼", "🦄", "🐯", "🦉", "🐸", "🚀")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Student Profile", style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    avatars.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedAvatar == emoji) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (selectedAvatar == emoji) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Role: Student Phone",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "MAC: ${boundMacAddress.ifBlank { "Bound" }}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row {
                            TextButton(onClick = onOpenOnboarding) {
                                Text("Academic Profile", style = MaterialTheme.typography.labelLarge)
                            }
                            TextButton(onClick = onOpenProvisioning) {
                                Text("MAC Lock", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, selectedAvatar) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
