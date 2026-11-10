package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showProfileDialog by remember { mutableStateOf(false) }
    var showExamGoalDialog by remember { mutableStateOf(false) }
    var lastRevealedNode by remember { mutableStateOf<LevelNodeState?>(null) }

    // Folder Explorer navigation state:
    // true = at the root "All Diplomas" folder directory showing Diploma Folder boxes (Intermediate Diploma, Advanced Diploma, etc.)
    // false = inside the selected Diploma folder showing its Course Boxes
    var isShowingRootDiplomaFolders by remember { mutableStateOf(true) }
    // Toggle between 2-column Box Grid and 1-column Folder List
    var isGridBoxMode by remember { mutableStateOf(true) }

    if (!isShowingRootDiplomaFolders) {
        BackHandler {
            isShowingRootDiplomaFolders = true
        }
    }

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

    val folderListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        activeTrack.primaryColor.copy(alpha = 0.06f),
                        MaterialTheme.colorScheme.background,
                        activeTrack.primaryColor.copy(alpha = 0.03f)
                    )
                )
            )
    ) {
        // Top HUD Bar (Student Profile, Notifications, Role, XP, Stars, Badges)
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

        // Computer-Style Folder Path & Diploma Selector Bar
        FolderExplorerTopBar(
            uiState = uiState,
            selectedTrack = activeTrack,
            isShowingRootDiplomaFolders = isShowingRootDiplomaFolders,
            isGridBoxMode = isGridBoxMode,
            onOpenRootDiplomas = { isShowingRootDiplomaFolders = true },
            onSelectDiplomaFolder = { track ->
                if (uiState.isDiplomaUnlocked(track)) {
                    onSelectTrack(track)
                    isShowingRootDiplomaFolders = false
                } else {
                    diplomaToUnlock = track
                }
            },
            onToggleGridList = { isGridBoxMode = !isGridBoxMode }
        )

        // Banner reveal when a student unlocks a new course box via code or 2/3 stars
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

        if (isShowingRootDiplomaFolders) {
            // ROOT FOLDER LEVEL: Diploma Folder Boxes (Intermediate Diploma, Advanced Diploma, Skills Courses, All 21 Boxes)
            RootDiplomaFoldersDirectory(
                uiState = uiState,
                onOpenDiplomaFolder = { track ->
                    if (uiState.isDiplomaUnlocked(track)) {
                        onSelectTrack(track)
                        isShowingRootDiplomaFolders = false
                    } else {
                        diplomaToUnlock = track
                    }
                }
            )
        } else if (!isActiveTrackUnlocked) {
            LockedDiplomaGateView(
                track = activeTrack,
                onEnterDiplomaCode = { diplomaToUnlock = activeTrack }
            )
        } else {
            // SECOND FOLDER LEVEL: Courses (Boxes) inside the selected Diploma Folder
            LazyColumn(
                state = folderListState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("adventure_map_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    DiplomaFolderHeaderBanner(
                        track = activeTrack,
                        trackLevels = trackLevels,
                        onBackToDiplomas = { isShowingRootDiplomaFolders = true }
                    )
                }

                if (isGridBoxMode) {
                    val rows = trackLevels.chunked(2)
                    items(
                        items = rows,
                        key = { pair -> pair.joinToString("_") { it.level.id } }
                    ) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 640.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { nodeState ->
                                val isNewlyUnlocked = uiState.newlyUnlockedLevelId == nodeState.level.id
                                CourseBoxFolderGridCard(
                                    node = nodeState,
                                    isNewlyUnlocked = isNewlyUnlocked,
                                    modifier = Modifier.weight(1f),
                                    onClickCourseBox = {
                                        if (nodeState.isUnlocked) {
                                            onOpenLevel(nodeState.level)
                                        } else {
                                            levelToUnlock = nodeState.level
                                        }
                                    }
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    items(
                        items = trackLevels,
                        key = { it.level.id }
                    ) { nodeState ->
                        val isNewlyUnlocked = uiState.newlyUnlockedLevelId == nodeState.level.id
                        CourseBoxFolderListRowCard(
                            node = nodeState,
                            isNewlyUnlocked = isNewlyUnlocked,
                            onClickCourseBox = {
                                if (nodeState.isUnlocked) {
                                    onOpenLevel(nodeState.level)
                                } else {
                                    levelToUnlock = nodeState.level
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
                        isShowingRootDiplomaFolders = false
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
private fun FolderExplorerTopBar(
    uiState: QuestUiState,
    selectedTrack: DiplomaTrack,
    isShowingRootDiplomaFolders: Boolean,
    isGridBoxMode: Boolean,
    onOpenRootDiplomas: () -> Unit,
    onSelectDiplomaFolder: (DiplomaTrack) -> Unit,
    onToggleGridList: () -> Unit
) {
    val activeFolderName = when (selectedTrack) {
        DiplomaTrack.INTERMEDIATE_DIPLOMA -> "Intermediate Diploma (Boxes 1–13)"
        DiplomaTrack.ADVANCED_DIPLOMA -> "Advanced Diploma (Boxes 14–21)"
        DiplomaTrack.TOEFL_PREP -> "All 21 Curriculum Boxes"
        DiplomaTrack.PTP_PROGRAM -> "Skills Courses (Skills 1–6)"
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Breadcrumb Path Row + Grid/List Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isShowingRootDiplomaFolders) EtaNavy else EtaNavy.copy(alpha = 0.1f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onOpenRootDiplomas)
                            .testTag("root_diplomas_folder_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = if (isShowingRootDiplomaFolders) EtaGold else EtaNavy,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "All Diplomas",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isShowingRootDiplomaFolders) Color.White else EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!isShowingRootDiplomaFolders) {
                        Text(
                            text = "›",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = selectedTrack.primaryColor.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = selectedTrack.primaryColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = activeFolderName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = selectedTrack.primaryColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                if (!isShowingRootDiplomaFolders) {
                    IconButton(
                        onClick = onToggleGridList,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("toggle_box_view_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (isGridBoxMode) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridBoxMode) "Switch to Folder List" else "Switch to Box Grid",
                            tint = EtaNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Diploma Folder Quick-Switch Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val orderedTracks = listOf(
                    DiplomaTrack.INTERMEDIATE_DIPLOMA,
                    DiplomaTrack.ADVANCED_DIPLOMA,
                    DiplomaTrack.PTP_PROGRAM,
                    DiplomaTrack.TOEFL_PREP
                )
                orderedTracks.forEach { track ->
                    val unlocked = uiState.isDiplomaUnlocked(track)
                    val isSelected = !isShowingRootDiplomaFolders && selectedTrack == track
                    val folderLabel = when (track) {
                        DiplomaTrack.INTERMEDIATE_DIPLOMA -> "📂 Intermediate (1–13)"
                        DiplomaTrack.ADVANCED_DIPLOMA -> "📂 Advanced (14–21)"
                        DiplomaTrack.PTP_PROGRAM -> "📂 Skills (1–6)"
                        DiplomaTrack.TOEFL_PREP -> "📂 All 21 Boxes"
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            isSelected -> track.primaryColor
                            unlocked -> MaterialTheme.colorScheme.surface
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (unlocked) track.primaryColor.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectDiplomaFolder(track) }
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
                                text = folderLabel,
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
    }
}

/**
 * Root Directory of Diploma Folders ("Intermediate Diploma -> Courses -> Vocabulary, Listening, Tests, Results").
 */
@Composable
private fun RootDiplomaFoldersDirectory(
    uiState: QuestUiState,
    onOpenDiplomaFolder: (DiplomaTrack) -> Unit
) {
    val diplomaFolders = listOf(
        Triple(
            DiplomaTrack.INTERMEDIATE_DIPLOMA,
            "الدبلوم المتوسط • الصناديق 1 إلى 13",
            uiState.levels.filter { it.level.track == DiplomaTrack.INTERMEDIATE_DIPLOMA }
        ),
        Triple(
            DiplomaTrack.ADVANCED_DIPLOMA,
            "الدبلوم المتقدم • الصناديق 14 إلى 21",
            uiState.levels.filter { it.level.track == DiplomaTrack.ADVANCED_DIPLOMA }
        ),
        Triple(
            DiplomaTrack.PTP_PROGRAM,
            "دورات المهارات الخاصة (كتابة واستماع وتحدث • Skills 1–6)",
            uiState.levels.filter { it.level.boxType != BoxArchitectureType.GENERAL_4_ROOMS }
        ),
        Triple(
            DiplomaTrack.TOEFL_PREP,
            "جميع المستويات المعتمدة بالتتابع (21 صندوقاً)",
            uiState.levels
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diploma_root_folders_list"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EtaNavy)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📁 Excel ETA • Diploma Folders",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Select a Diploma Folder to open its Course Boxes (Vocabulary, Grammar, Listening, Speaking, Tests & Results):",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }
        }

        items(diplomaFolders) { (track, arabicDesc, courses) ->
            val unlockedCount = courses.count { it.isUnlocked }
            val passedCount = courses.count { it.starsEarned >= 2 }
            val earnedStars = courses.sumOf { it.starsEarned }
            val maxStars = courses.size * 3
            val sampleCourseNames = courses.take(4).joinToString(" • ") { it.level.title }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onOpenDiplomaFolder(track) }
                    .testTag("diploma_folder_card_${track.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Folder Top Header Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(track.primaryColor)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = EtaGold,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = arabicDesc,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${courses.size} Courses",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📦 Includes: $sampleCourseNames...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔓 $unlockedCount/${courses.size} Open • ✅ $passedCount Passed",
                                style = MaterialTheme.typography.labelMedium,
                                color = track.primaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "⭐ $earnedStars/$maxStars Stars",
                                style = MaterialTheme.typography.labelMedium,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = {
                                if (courses.isEmpty()) 0f else passedCount.toFloat() / courses.size.toFloat()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = track.primaryColor,
                            trackColor = track.primaryColor.copy(alpha = 0.14f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiplomaFolderHeaderBanner(
    track: DiplomaTrack,
    trackLevels: List<LevelNodeState>,
    onBackToDiplomas: () -> Unit
) {
    val passedCount = trackLevels.count { it.starsEarned >= 2 }
    val unlockedCount = trackLevels.count { it.isUnlocked }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 640.dp),
        shape = RoundedCornerShape(16.dp),
        color = track.primaryColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onBackToDiplomas,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "All Diplomas",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "📂 ${track.title}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${trackLevels.size} Course Boxes • $unlockedCount Open • Earn ⭐⭐ (2/3) to Unlock Next Box",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "$passedCount/${trackLevels.size} Passed",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 2-Column Computer Folder / Course Box Card for each of the 21 Courses.
 */
@Composable
private fun CourseBoxFolderGridCard(
    node: LevelNodeState,
    isNewlyUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClickCourseBox: () -> Unit
) {
    val level = node.level
    val boxColor = level.boxType.badgeColor
    val isUnlocked = node.isUnlocked
    val isPassed = node.starsEarned >= 2

    val subFolderPreview = when (level.boxType) {
        BoxArchitectureType.GENERAL_4_ROOMS -> "📁 Vocab • Grammar • Tests • Results"
        BoxArchitectureType.SKILLS_RW_2_ROOMS -> "📁 Grammar • Writing Tests • Results"
        BoxArchitectureType.SKILLS_LS_2_ROOMS -> "📁 Listening • Speaking • Results"
    }

    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isNewlyUnlocked) 2.dp else 1.dp,
                color = when {
                    isNewlyUnlocked -> EtaGold
                    isPassed -> Color(0xFF10B981).copy(alpha = 0.6f)
                    isUnlocked -> boxColor.copy(alpha = 0.45f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClickCourseBox)
            .testTag("level_node_${level.orderIndex}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isUnlocked) 3.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Folder Tab Header Bar: Box Number + 3-Star Rating
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isUnlocked) boxColor else Color(0xFF64748B)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BOX ${level.orderIndex}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { starIdx ->
                            val filled = starIdx < node.starsEarned
                            Icon(
                                imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (filled) EtaGold else Color.White.copy(alpha = 0.45f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isUnlocked) boxColor.copy(alpha = 0.14f)
                                else Color(0xFF94A3B8).copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = "📂", fontSize = 17.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Box",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = level.boxType.arabicSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) boxColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Sub-Folders Inside This Course Box
                Text(
                    text = subFolderPreview,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        isPassed -> Color(0xFF10B981).copy(alpha = 0.14f)
                        isUnlocked -> boxColor.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when {
                            isPassed -> "✅ Open Folder (${node.starsEarned}/3 ⭐)"
                            isUnlocked -> "📂 Open Course Folder ➔"
                            else -> "🔒 Need 2/3 ⭐ or Code"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            isPassed -> Color(0xFF047857)
                            isUnlocked -> boxColor
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * 1-Column Detailed Computer Folder Row Card for a Course Box.
 */
@Composable
private fun CourseBoxFolderListRowCard(
    node: LevelNodeState,
    isNewlyUnlocked: Boolean,
    onClickCourseBox: () -> Unit
) {
    val level = node.level
    val boxColor = level.boxType.badgeColor
    val isUnlocked = node.isUnlocked
    val isPassed = node.starsEarned >= 2

    val subFoldersDescription = when (level.boxType) {
        BoxArchitectureType.GENERAL_4_ROOMS -> "Folders: 1. Vocabulary • 2. Grammar • 3. Vocab Tests • 4. Grammar Tests • Flashcards • Results"
        BoxArchitectureType.SKILLS_RW_2_ROOMS -> "Folders: 1. Grammar Rules • 2. Writing & Sentence Tests • Vocabulary • Flashcards • Results"
        BoxArchitectureType.SKILLS_LS_2_ROOMS -> "Folders: 1. Listening & Dictation • 2. Speaking & Voice Tests • Vocabulary • Flashcards • Results"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 640.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isNewlyUnlocked) 2.dp else 1.dp,
                color = if (isUnlocked) boxColor.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClickCourseBox)
            .testTag("level_node_${level.orderIndex}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isUnlocked) boxColor else Color(0xFF94A3B8)),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = "📂", fontSize = 22.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Folder",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Box ${level.orderIndex}: ${level.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(3) { starIdx ->
                            val filled = starIdx < node.starsEarned
                            Icon(
                                imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (filled) EtaGold else Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${level.boxType.title} • ${level.boxType.arabicSubtitle}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isUnlocked) boxColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subFoldersDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
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
            .widthIn(max = 640.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("unlocked_module_reveal_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, trackColor)
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
                            .background(trackColor)
                            .border(2.dp, EtaGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📂", fontSize = 22.sp)
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
                                text = "Course Box Unlocked!",
                                style = MaterialTheme.typography.labelMedium,
                                color = trackColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Box ${level.orderIndex}: ${level.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = level.boxType.arabicSubtitle,
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
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = "Open Course Folder",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
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
                    text = "Courses in ${track.title} are protected. Complete your previous diploma or enter an Upgrading Code from Administration.",
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
                    text = "Enter the Upgrading Code from Administration for this diploma folder:",
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
                Text(text = "📦", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Unlock Box ${level.orderIndex}: ${level.title}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Earn 2/3 ⭐ in Box ${(level.orderIndex - 1).coerceAtLeast(1)} or enter Admin Code",
                        style = MaterialTheme.typography.labelMedium,
                        color = level.track.primaryColor
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "To open ${level.title} directly, enter the Activation Code from Administration (or earn at least 2/3 Stars in the previous course box):",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        showError = false
                    },
                    label = { Text("Admin Activation Code") },
                    placeholder = {
                        Text("Enter Admin Code")
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
                        text = "Incorrect Activation Code. Please check with Administration.",
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
                Text("Unlock Box")
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
