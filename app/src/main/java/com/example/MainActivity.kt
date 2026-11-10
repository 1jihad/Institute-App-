package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ui.AppTab
import com.example.ui.LevelNodeState
import com.example.ui.QuestViewModel
import com.example.ui.StudentNotificationCenterDialog
import com.example.ui.screens.AdminProvisioningScreen
import com.example.ui.screens.AdventureMapScreen
import com.example.ui.screens.LevelDetailScreen
import com.example.ui.screens.OfflineStorageScreen
import com.example.ui.screens.StudentAcademicOnboardingDialog
import com.example.ui.screens.UnitBadgesScreen
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.delay

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

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    val openNotificationsWithPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runCatching {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        questViewModel.openNotificationCenter()
    }

    if (uiState.openedLevelId == null && uiState.activeTab != AppTab.ADVENTURE_MAP) {
        BackHandler {
            questViewModel.selectTab(AppTab.ADVENTURE_MAP)
        }
    }

    val mainMapAlpha by animateFloatAsState(
        targetValue = if (uiState.isSplashShowing) 0f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "main_map_fade_in"
    )

    val showProvisioningGate = !uiState.isDeviceAuthorized || uiState.isProvisioningModalOpen

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .alpha(mainMapAlpha),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (uiState.openedLevelNode == null && !uiState.isSplashShowing && !showProvisioningGate) {
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
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
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

                if (showProvisioningGate) {
                    AdminProvisioningScreen(
                        uiState = uiState,
                        onActivateWithKey = { mac, key, cb ->
                            questViewModel.activateDeviceWithKey(mac, key, cb)
                        },
                        onActivateWithJson = { json, cb ->
                            questViewModel.activateDeviceFromJson(json, cb)
                        },
                        onResetProvisioning = {
                            questViewModel.resetDeviceProvisioning()
                        },
                        onCloseIfAuthorized = {
                            questViewModel.closeProvisioningModal()
                        }
                    )
                } else {
                    val openedNode = uiState.openedLevelNode
                    var cachedOpenedNode by remember { mutableStateOf<LevelNodeState?>(null) }
                    if (openedNode != null) {
                        cachedOpenedNode = openedNode
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = openedNode == null,
                            enter = fadeIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(220))
                        ) {
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
                                    onDismissUnlockedReveal = {
                                        questViewModel.dismissUnlockedLevelReveal()
                                    },
                                    onOpenProvisioning = {
                                        questViewModel.openProvisioningModal()
                                    },
                                    onOpenNotifications = openNotificationsWithPermission,
                                    onUpdateExamGoal = { title, days ->
                                        questViewModel.updateExamGoal(title, days)
                                    },
                                    onOpenOnboarding = {
                                        questViewModel.openOnboardingModal()
                                    }
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
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = openedNode != null,
                            enter = fadeIn(animationSpec = tween(350)) +
                                slideInVertically(
                                    initialOffsetY = { it / 8 },
                                    animationSpec = tween(350)
                                ) +
                                scaleIn(
                                    initialScale = 0.94f,
                                    animationSpec = tween(350)
                                ),
                            exit = fadeOut(animationSpec = tween(250)) +
                                slideOutVertically(
                                    targetOffsetY = { it / 8 },
                                    animationSpec = tween(250)
                                ) +
                                scaleOut(
                                    targetScale = 0.94f,
                                    animationSpec = tween(250)
                                )
                        ) {
                            val activeNode = openedNode ?: cachedOpenedNode
                            if (activeNode != null) {
                                LevelDetailScreen(
                                    node = activeNode,
                                    initialSelectedTab = uiState.openedLevelInitialTab,
                                    unreadNotificationsCount = uiState.unreadNotificationsCount,
                                    isDownloading = uiState.downloadingLevelId == activeNode.level.id,
                                    downloadProgress = uiState.downloadProgress,
                                    onBack = { questViewModel.closeLevelDetail() },
                                    onDownloadContent = { questViewModel.downloadLevelContent(activeNode.level) },
                                    onRemoveOfflinePack = { questViewModel.removeOfflinePack(activeNode.level) },
                                    onToggleMaterial = { mat ->
                                        questViewModel.toggleMaterialDone(activeNode.level, mat)
                                    },
                                    onToggleAssignment = { assign ->
                                        questViewModel.toggleAssignmentDone(activeNode.level, assign)
                                    },
                                    onSubmitQuizScore = { pct, wrongIds ->
                                        questViewModel.submitSkillQuizScore(activeNode.level, pct, wrongIds)
                                    },
                                    onSaveStudentNote = { existingId, title, category, summary, content, phrases ->
                                        questViewModel.saveStudentNote(
                                            existingId = existingId,
                                            levelId = activeNode.level.id,
                                            title = title,
                                            category = category,
                                            summary = summary,
                                            contentBody = content,
                                            keyPhrases = phrases
                                        )
                                    },
                                    onDeleteStudentNote = { noteId ->
                                        questViewModel.deleteStudentNote(noteId)
                                    },
                                    onAddStudyReminder = { reminderText, priority ->
                                        questViewModel.addStudyReminder(
                                            levelId = activeNode.level.id,
                                            reminderText = reminderText,
                                            priority = priority
                                        )
                                    },
                                    onToggleStudyReminder = { reminderId ->
                                        questViewModel.toggleStudyReminder(reminderId)
                                    },
                                    onDeleteStudyReminder = { reminderId ->
                                        questViewModel.deleteStudyReminder(reminderId)
                                    },
                                    onRecordFlashcardMastered = {
                                        questViewModel.recordFlashcardMastered()
                                    },
                                    onOpenNotifications = openNotificationsWithPermission,
                                    onImportTeacherEvaluationPacket = { json, cb ->
                                        questViewModel.importTeacherEvaluationPacket(
                                            jsonPacket = json,
                                            fallbackLevelId = activeNode.level.id,
                                            onResult = cb
                                        )
                                    },
                                    onSendMessageToTeacher = { text ->
                                        questViewModel.sendMessageToTeacher(
                                            levelId = activeNode.level.id,
                                            messageText = text
                                        )
                                    },
                                    onImportVocabStream = { stream, name, cb ->
                                        questViewModel.importVocabularyFileStream(
                                            inputStream = stream,
                                            fileName = name,
                                            levelId = activeNode.level.id,
                                            onResult = cb
                                        )
                                    },
                                    onImportVocabCsvText = { csv, name, cb ->
                                        questViewModel.importVocabularyCsvText(
                                            csvText = csv,
                                            levelId = activeNode.level.id,
                                            sourceName = name,
                                            onResult = cb
                                        )
                                    },
                                    onAddVocabWord = { word, meaning, arabic, ex ->
                                        questViewModel.addCustomVocabularyWord(
                                            levelId = activeNode.level.id,
                                            word = word,
                                            meaning = meaning,
                                            arabicHint = arabic,
                                            exampleSentence = ex
                                        )
                                    },
                                    onDeleteVocabWord = { id ->
                                        questViewModel.deleteVocabularyWord(id)
                                    },
                                    onMarkStudyRoomDone = { isVocabRoom ->
                                        questViewModel.markStudyRoomCompleted(activeNode.level, isVocabRoom)
                                    },
                                    onSubmitRoomPracticeScore = { roomType, pct, wrongIds ->
                                        questViewModel.submitRoomPracticeScore(
                                            level = activeNode.level,
                                            roomType = roomType,
                                            scorePercent = pct,
                                            wrongQuestionIds = wrongIds
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Excel ETA Opening Logo Splash Screen with fade-in & smooth exit to curriculum map
        AnimatedVisibility(
            visible = uiState.isSplashShowing,
            enter = fadeIn(animationSpec = tween(durationMillis = 400)),
            exit = fadeOut(animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 1.05f, animationSpec = tween(durationMillis = 550))
        ) {
            ExcelEtaOpeningSplashScreen(
                onDismiss = { questViewModel.dismissSplash() }
            )
        }
    }

    if (uiState.isDeviceAuthorized && uiState.isOnboardingModalOpen && !uiState.isSplashShowing && !showProvisioningGate) {
        StudentAcademicOnboardingDialog(
            profile = uiState.profile,
            onCompleteOnboarding = { name, avatar, academicStage, goal, adminCode ->
                questViewModel.completeStudentOnboarding(
                    studentName = name,
                    avatarEmoji = avatar,
                    academicStage = academicStage,
                    learningGoal = goal,
                    optionalAdminCode = adminCode
                )
            },
            onDismiss = {
                questViewModel.closeOnboardingModal()
            }
        )
    }

    if (uiState.isNotificationCenterOpen) {
        StudentNotificationCenterDialog(
            notifications = uiState.notifications,
            onDismiss = { questViewModel.closeNotificationCenter() },
            onOpenUnitGradesFromNotification = { notif ->
                questViewModel.openNotificationTarget(notif)
            },
            onMarkAllRead = { questViewModel.markAllNotificationsRead() },
            onClearRead = { questViewModel.clearReadNotifications() },
            onImportTeacherEvaluationPacket = { json, cb ->
                questViewModel.importTeacherEvaluationPacket(
                    jsonPacket = json,
                    fallbackLevelId = uiState.openedLevelId ?: "lvl_01_essence",
                    onResult = cb
                )
            }
        )
    }

    val badgeLevel = uiState.newlyEarnedBadgeLevel
    if (badgeLevel != null) {
        AlertDialog(
            onDismissRequest = { questViewModel.dismissBadgeCelebration() },
            shape = RoundedCornerShape(20.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EtaGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badgeLevel.badgeEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
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
                    colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Collect Badge")
                }
            }
        )
    }
}

@Composable
private fun ExcelEtaOpeningSplashScreen(
    onDismiss: () -> Unit
) {
    // Full Excel ETA For Training kinetic logo animation states
    var startNavyArc by remember { mutableStateOf(false) }
    var startBlueArc by remember { mutableStateOf(false) }
    var startRedArc by remember { mutableStateOf(false) }
    var startWordmark by remember { mutableStateOf(false) }
    var startSubtitle by remember { mutableStateOf(false) }
    var startStars by remember { mutableStateOf(false) }

    // 1. Three Crescent Swooshes slicing along their curved trajectories
    val navyArcProgress by animateFloatAsState(
        targetValue = if (startNavyArc) 1f else 0f,
        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
        label = "navy_crescent_sweep"
    )
    val blueArcProgress by animateFloatAsState(
        targetValue = if (startBlueArc) 1f else 0f,
        animationSpec = tween(durationMillis = 540, easing = FastOutSlowInEasing),
        label = "blue_crescent_sweep"
    )
    val redArcProgress by animateFloatAsState(
        targetValue = if (startRedArc) 1f else 0f,
        animationSpec = tween(durationMillis = 560, easing = FastOutSlowInEasing),
        label = "red_crescent_sweep"
    )

    // Subtle kinetic camera zoom-settle as the arcs sweep across
    val cameraScale by animateFloatAsState(
        targetValue = if (startWordmark) 1.0f else 1.12f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "kinetic_camera_scale"
    )

    // 2. "EXCEL ETA" wordmark kinetic snap
    val wordmarkProgress by animateFloatAsState(
        targetValue = if (startWordmark) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "wordmark_snap"
    )

    // 3. "F o r   T r a i n i n g" subtitle reveal
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startSubtitle) 1f else 0f,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "subtitle_fade"
    )

    // 4. Three 4-pointed stars popping in at the right tip of the arch
    val starsScale by animateFloatAsState(
        targetValue = if (startStars) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stars_pop"
    )

    LaunchedEffect(Unit) {
        startNavyArc = true
        delay(95)
        startBlueArc = true
        delay(95)
        startRedArc = true
        delay(200)
        startWordmark = true
        delay(140)
        startSubtitle = true
        startStars = true
        delay(1250)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(onClick = onDismiss)
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Original Full Excel ETA For Training Logo
        Box(
            modifier = Modifier
                .width(290.dp)
                .height(185.dp)
                .scale(cameraScale)
                .testTag("splash_logo_image"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Outer Deep Navy Crescent Arch (#1E246B)
                drawProgressiveCrescent(
                    p0 = Offset(w * 0.14f, h * 0.24f),
                    p1 = Offset(w * -0.01f, h * 0.48f),
                    p2 = Offset(w * 0.03f, h * 0.82f),
                    p3 = Offset(w * 0.30f, h * 0.94f),
                    maxHalfWidth = w * 0.026f,
                    progress = navyArcProgress,
                    color = Color(0xFF1E246B)
                )

                // 2. Middle Royal Blue Crescent Arch (#1863B8)
                drawProgressiveCrescent(
                    p0 = Offset(w * 0.25f, h * 0.87f),
                    p1 = Offset(w * 0.01f, h * 0.56f),
                    p2 = Offset(w * 0.15f, h * 0.02f),
                    p3 = Offset(w * 0.65f, h * 0.05f),
                    maxHalfWidth = w * 0.035f,
                    progress = blueArcProgress,
                    color = Color(0xFF1863B8)
                )

                // 3. Inner Crimson Red Crescent Arch (#B8181E)
                drawProgressiveCrescent(
                    p0 = Offset(w * 0.19f, h * 0.74f),
                    p1 = Offset(w * 0.12f, h * 0.16f),
                    p2 = Offset(w * 0.58f, h * -0.01f),
                    p3 = Offset(w * 0.93f, h * 0.26f),
                    maxHalfWidth = w * 0.031f,
                    progress = redArcProgress,
                    color = Color(0xFFB8181E)
                )

                // 4. Three 4-Pointed Stars on the upper right
                if (starsScale > 0.01f) {
                    drawFourPointStar(
                        center = Offset(w * 0.84f, h * 0.28f),
                        radius = w * 0.042f * starsScale,
                        color = Color(0xFFB8181E)
                    )
                    drawFourPointStar(
                        center = Offset(w * 0.93f, h * 0.12f),
                        radius = w * 0.028f * starsScale,
                        color = Color(0xFFB8181E)
                    )
                    drawFourPointStar(
                        center = Offset(w * 0.96f, h * 0.28f),
                        radius = w * 0.052f * starsScale,
                        color = Color(0xFF1E246B)
                    )
                }
            }

            // 5. Full "EXCEL ETA" Wordmark + "F o r   T r a i n i n g" Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(x = 18.dp, y = 16.dp)
                    .alpha(wordmarkProgress.coerceIn(0f, 1f))
                    .scale(0.86f + 0.14f * wordmarkProgress)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "EXCEL ",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E246B),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ETA",
                        fontSize = 33.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFB8181E),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "F o r   T r a i n i n g",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E246B),
                    letterSpacing = 2.5.sp,
                    modifier = Modifier.alpha(subtitleAlpha)
                )
            }
        }
    }
}

private fun DrawScope.drawFourPointStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val inner = radius * 0.22f
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + inner, center.y - inner)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + inner, center.y + inner)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - inner, center.y + inner)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - inner, center.y - inner)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawProgressiveCrescent(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    maxHalfWidth: Float,
    progress: Float,
    color: Color
) {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0.01f) return

    val steps = 48
    val upperPoints = ArrayList<Offset>(steps + 1)
    val lowerPoints = ArrayList<Offset>(steps + 1)

    for (i in 0..steps) {
        val u = i.toFloat() / steps.toFloat()
        val t = u * clamped

        val pt = cubicBezierPoint(p0, p1, p2, p3, t)
        val tan = cubicBezierTangent(p0, p1, p2, p3, t)
        val len = sqrt(tan.x * tan.x + tan.y * tan.y).coerceAtLeast(0.001f)
        val nx = -tan.y / len
        val ny = tan.x / len

        // Taper to needle-sharp 0 width at both tail (u=0) and moving tip (u=1)
        val activeEnvelope = sin(PI * u.toDouble()).toFloat()
        val fullArcEnvelope = sin(PI * t.toDouble()).pow(0.85).toFloat()
        val halfW = maxHalfWidth * activeEnvelope * fullArcEnvelope

        upperPoints.add(Offset(pt.x + nx * halfW, pt.y + ny * halfW))
        lowerPoints.add(Offset(pt.x - nx * halfW, pt.y - ny * halfW))
    }

    val path = Path().apply {
        val first = upperPoints.first()
        moveTo(first.x, first.y)
        for (i in 1 until upperPoints.size) {
            val p = upperPoints[i]
            lineTo(p.x, p.y)
        }
        for (i in lowerPoints.size - 1 downTo 0) {
            val p = lowerPoints[i]
            lineTo(p.x, p.y)
        }
        close()
    }

    drawPath(path = path, color = color)
}

private fun cubicBezierPoint(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    t: Float
): Offset {
    val inv = 1f - t
    val b0 = inv * inv * inv
    val b1 = 3f * inv * inv * t
    val b2 = 3f * inv * t * t
    val b3 = t * t * t
    return Offset(
        x = b0 * p0.x + b1 * p1.x + b2 * p2.x + b3 * p3.x,
        y = b0 * p0.y + b1 * p1.y + b2 * p2.y + b3 * p3.y
    )
}

private fun cubicBezierTangent(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    t: Float
): Offset {
    val inv = 1f - t
    val d0 = 3f * inv * inv
    val d1 = 6f * inv * t
    val d2 = 3f * t * t
    return Offset(
        x = d0 * (p1.x - p0.x) + d1 * (p2.x - p1.x) + d2 * (p3.x - p2.x),
        y = d0 * (p1.y - p0.y) + d1 * (p2.y - p1.y) + d2 * (p3.y - p2.y)
    )
}
