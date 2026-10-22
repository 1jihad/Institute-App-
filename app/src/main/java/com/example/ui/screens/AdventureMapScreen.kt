package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumLevel
import com.example.data.DiplomaTrack
import com.example.ui.LevelNodeState
import com.example.ui.QuestUiState
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestEmerald
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestIndigo

@Composable
fun AdventureMapScreen(
    uiState: QuestUiState,
    onSelectTrack: (DiplomaTrack) -> Unit,
    onOpenLevel: (CurriculumLevel) -> Unit,
    onUnlockLevel: (CurriculumLevel, String, (Boolean) -> Unit) -> Unit,
    onUnlockDiploma: (DiplomaTrack, String, (Boolean) -> Unit) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onOpenPosterTab: () -> Unit
) {
    var levelToUnlock by remember { mutableStateOf<CurriculumLevel?>(null) }
    var diplomaToUnlock by remember { mutableStateOf<DiplomaTrack?>(null) }
    var showTeacherCodeSheet by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val activeTrack = uiState.selectedTrackFilter
    val isActiveTrackUnlocked = uiState.isDiplomaUnlocked(activeTrack)

    val trackLevels = remember(uiState.levels, activeTrack) {
        uiState.levels.filter { it.level.track == activeTrack }
    }

    val nextDiplomaTrack = remember(activeTrack) {
        val entries = DiplomaTrack.entries
        val idx = entries.indexOf(activeTrack)
        if (idx in 0 until entries.lastIndex) entries[idx + 1] else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        activeTrack.primaryColor.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background,
                        activeTrack.primaryColor.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        // Compact Top Game HUD Bar
        CompactTopHudBar(
            uiState = uiState,
            onEditProfile = { showProfileDialog = true },
            onShowTeacherCodes = { showTeacherCodeSheet = true },
            onOpenPosterTab = onOpenPosterTab
        )

        // Compact Diploma Selector Bar (Locked diplomas show a lock & hide their contents)
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

        if (!isActiveTrackUnlocked) {
            LockedDiplomaGateView(
                track = activeTrack,
                onEnterDiplomaCode = { diplomaToUnlock = activeTrack }
            )
        } else {
            // Scrollable Game-Like Winding Learning Map
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("adventure_map_list"),
                contentPadding = PaddingValues(top = 6.dp, bottom = 72.dp),
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 460.dp)
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

                // Bottom Gate for Next Diploma (Locked until current diploma is finished or code entered)
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
        val firstLevel = uiState.firstLevelOfDiploma(targetTrack)
        LockedDiplomaUnlockDialog(
            track = targetTrack,
            firstLevelHintCode = firstLevel?.teacherUnlockCode ?: "TEACH-ALL",
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

    if (showTeacherCodeSheet) {
        TeacherCodeGuideDialog(
            uiState = uiState,
            onDismiss = { showTeacherCodeSheet = false },
            onQuickUnlock = { lvl ->
                showTeacherCodeSheet = false
                levelToUnlock = lvl
            }
        )
    }

    if (showProfileDialog) {
        StudentProfileDialog(
            currentName = uiState.profile.studentName,
            currentAvatar = uiState.profile.avatarEmoji,
            onDismiss = { showProfileDialog = false },
            onSave = { name, avatar ->
                onUpdateProfile(name, avatar)
                showProfileDialog = false
            }
        )
    }
}

@Composable
private fun CompactTopHudBar(
    uiState: QuestUiState,
    onEditProfile: () -> Unit,
    onShowTeacherCodes: () -> Unit,
    onOpenPosterTab: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Compact Avatar & Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onEditProfile)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .testTag("student_profile_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.profile.avatarEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = uiState.profile.studentName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Text(
                        text = "${uiState.totalHoursCompleted}h Done",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Compact Game Stat Chips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HudCounterChip(emoji = "⚡", text = "${uiState.profile.totalXp}", bgColor = Color(0xFFFEF3C7), textColor = Color(0xFFB45309))
                HudCounterChip(emoji = "⭐", text = "${uiState.totalStars}", bgColor = Color(0xFFE0E7FF), textColor = Color(0xFF1E40AF))
                HudCounterChip(emoji = "🏅", text = "${uiState.badgesEarnedCount}", bgColor = Color(0xFFD1FAE5), textColor = Color(0xFF047857))

                IconButton(
                    onClick = onOpenPosterTab,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .testTag("open_institute_poster_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Official Institute Learning Map",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onShowTeacherCodes,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .testTag("teacher_codes_guide_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Teacher Unlock Codes Guide",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
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
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 11.sp)
        Spacer(modifier = Modifier.width(3.dp))
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        DiplomaTrack.entries.forEach { track ->
            val unlocked = uiState.isDiplomaUnlocked(track)
            val isSelected = selectedTrack == track
            val shortName = when (track) {
                DiplomaTrack.INTERMEDIATE_DIPLOMA -> "Intermediate Diploma"
                DiplomaTrack.ADVANCED_DIPLOMA -> "Advanced Diploma"
                DiplomaTrack.TOEFL_PREP -> "TOEFL (100h)"
                DiplomaTrack.PTP_PROGRAM -> "PTP Teaching"
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
                    color = if (unlocked) track.primaryColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onSelectTrack(track) }
                    .testTag("filter_${track.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (!unlocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Diploma",
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
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
            .widthIn(max = 440.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = track.primaryColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
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
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "$passedCount/${trackLevels.size} Done",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = stageTitle,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * Computes the horizontal x-offset (in dp) for a level node index to create a winding S-curve game path.
 */
private fun nodeOffsetX(index: Int): Dp {
    return when (index % 4) {
        0 -> 0.dp
        1 -> (-58).dp
        2 -> 0.dp
        else -> 58.dp
    }
}

@Composable
private fun GameMapPathStep(
    index: Int,
    node: LevelNodeState,
    isLastInTrack: Boolean,
    onClickNode: () -> Unit
) {
    val level = node.level
    val trackColor = level.track.primaryColor
    val currentOffset = nodeOffsetX(index)
    val nextOffset = nodeOffsetX(index + 1)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game Island Node + Label
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(x = currentOffset)
                    .clickable(onClick = onClickNode)
                    .testTag("level_node_${level.orderIndex}")
            ) {
                // 3 Mini Stars above unlocked nodes
                if (node.isUnlocked) {
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
                                tint = if (filled) QuestGold else Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Circular 3D-styled Game Island Node
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .shadow(
                            elevation = if (node.isUnlocked) 5.dp else 1.dp,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(
                            when {
                                node.isPassed -> Brush.verticalGradient(listOf( Color(0xFFFBBF24), Color(0xFFD97706)))
                                node.isUnlocked -> Brush.verticalGradient(listOf(trackColor, trackColor.copy(alpha = 0.8f)))
                                else -> Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)))
                            }
                        )
                        .border(
                            width = 3.dp,
                            color = when {
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
                            fontSize = 24.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked Unit",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Small checkmark badge in corner if passed
                    if (node.isPassed) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(QuestEmerald)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Passed",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Compact Title Pill below the circular island
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (node.isUnlocked) trackColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${level.orderIndex}. ${level.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (node.isUnlocked) "${level.hours} Hrs" else "${level.hours} Hrs • Tap for Code",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (node.isUnlocked) trackColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Winding S-Curve Bezier Trail to Next Node
        if (!isLastInTrack) {
            Canvas(
                modifier = Modifier
                    .width(220.dp)
                    .height(32.dp)
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

                // Outer road shadow
                drawPath(
                    path = path,
                    color = if (node.isPassed) QuestGold.copy(alpha = 0.35f) else trackColor.copy(alpha = 0.16f),
                    style = Stroke(width = 18f, cap = StrokeCap.Round)
                )

                // Inner dashed trail
                drawPath(
                    path = path,
                    color = if (node.isPassed) QuestGold else trackColor.copy(alpha = 0.55f),
                    style = Stroke(
                        width = 7f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
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
            .widthIn(max = 360.dp)
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("next_diploma_gate_${nextTrack.id}"),
        shape = RoundedCornerShape(18.dp),
        color = if (isUnlocked) nextTrack.primaryColor else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (isUnlocked) nextTrack.primaryColor else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (isUnlocked) "Enter ${nextTrack.title}" else "${nextTrack.title} (Locked)",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isUnlocked) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isUnlocked) "Tap to explore next diploma map" else "Complete current diploma or enter Teacher Code",
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
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.widthIn(max = 380.dp),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(track.primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = track.primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${track.title} is Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Levels in ${track.title} are hidden until you graduate from the previous diploma or enter a Teacher Code for this diploma.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onEnterDiplomaCode,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = track.primaryColor)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enter Teacher Code")
                }
            }
        }
    }
}

@Composable
private fun LockedDiplomaUnlockDialog(
    track: DiplomaTrack,
    firstLevelHintCode: String,
    onDismiss: () -> Unit,
    onSubmitCode: (String, () -> Unit) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = track.primaryColor, modifier = Modifier.size(22.dp))
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
                    text = "Students in earlier diplomas cannot view ${track.title} until they finish their current diploma or receive a placement code from their teacher.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        showError = false
                    },
                    label = { Text("Teacher Diploma Code") },
                    singleLine = true,
                    isError = showError,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = "Invalid code for ${track.title}.",
                        style = MaterialTheme.typography.labelMedium,
                        color = QuestCoral,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                TextButton(
                    onClick = { showHint = !showHint },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (showHint) "Hide Teacher Demo Code" else "Teacher / Tester: Show Demo Code", style = MaterialTheme.typography.labelLarge)
                }
                AnimatedVisibility(visible = showHint) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Code: $firstLevelHintCode",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        AssistChip(
                            onClick = {
                                codeInput = firstLevelHintCode
                                showError = false
                            },
                            label = { Text("Fill") }
                        )
                    }
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
    var showTeacherHint by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = level.badgeEmoji, fontSize = 22.sp)
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
                    text = "Enter the teacher code for ${level.title} to unlock its materials & assignments:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        showError = false
                    },
                    label = { Text("Teacher Unlock Code") },
                    placeholder = { Text("e.g. ${level.teacherUnlockCode}") },
                    singleLine = true,
                    isError = showError,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_code_input")
                )
                if (showError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Incorrect code. Ask your teacher or tap below for demo hint.",
                        style = MaterialTheme.typography.labelMedium,
                        color = QuestCoral
                    )
                }
                TextButton(
                    onClick = { showTeacherHint = !showTeacherHint },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.testTag("toggle_code_hint_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showTeacherHint) "Hide Classroom Code" else "Teacher / Tester: Show Code",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                AnimatedVisibility(visible = showTeacherHint) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code: ${level.teacherUnlockCode}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            AssistChip(
                                onClick = {
                                    codeInput = level.teacherUnlockCode
                                    showError = false
                                },
                                label = { Text("Fill Code") },
                                modifier = Modifier.testTag("fill_teacher_code_chip")
                            )
                        }
                    }
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = level.track.primaryColor),
                modifier = Modifier.testTag("confirm_unlock_button")
            ) {
                Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
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
private fun TeacherCodeGuideDialog(
    uiState: QuestUiState,
    onDismiss: () -> Unit,
    onQuickUnlock: (CurriculumLevel) -> Unit
) {
    var showLockedDiplomasForTeacher by remember { mutableStateOf(false) }
    val visibleLevels = remember(uiState.levels, showLockedDiplomasForTeacher) {
        if (showLockedDiplomasForTeacher) {
            uiState.levels
        } else {
            uiState.levels.filter { uiState.isDiplomaUnlocked(it.level.track) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "🔑 Teacher Unlock Codes",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(modifier = Modifier.height(340.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap a level to enter its code:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = { showLockedDiplomasForTeacher = !showLockedDiplomasForTeacher },
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text(
                            text = if (showLockedDiplomasForTeacher) "Hide Locked Diplomas" else "Teacher: Show All Diplomas",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    itemsIndexed(visibleLevels) { _, node ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onQuickUnlock(node.level) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${node.level.orderIndex}. ${node.level.title} (${node.level.hours}h)",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = node.level.track.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = node.level.track.primaryColor
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (node.isUnlocked) QuestEmerald.copy(alpha = 0.2f) else node.level.track.primaryColor
                                ) {
                                    Text(
                                        text = node.level.teacherUnlockCode,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (node.isUnlocked) QuestEmerald else Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun StudentProfileDialog(
    currentName: String,
    currentAvatar: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }
    val avatars = listOf("🦊", "🦁", "🐼", "🦄", "🐯", "🦉", "🐸", "🚀")

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text("Explorer Profile", style = MaterialTheme.typography.headlineMedium)
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
                                .size(40.dp)
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
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Nickname") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
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
