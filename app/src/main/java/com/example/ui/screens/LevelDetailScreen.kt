package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BoxPracticeRoomType
import com.example.data.LevelAssignment
import com.example.data.MaterialCategory
import com.example.data.ReminderPriority
import com.example.data.StudyMaterial
import com.example.ui.AudioPronunciationButtons
import com.example.ui.InteractiveFlashcardDeckCard
import com.example.ui.LevelNodeState
import com.example.ui.OfflineSpeechController
import com.example.ui.rememberOfflineSpeechController
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed
import java.io.InputStream

@Composable
fun LevelDetailScreen(
    node: LevelNodeState,
    initialSelectedTab: Int = 0,
    unreadNotificationsCount: Int = 0,
    isDownloading: Boolean,
    downloadProgress: Float,
    onBack: () -> Unit,
    onDownloadContent: () -> Unit,
    onRemoveOfflinePack: () -> Unit,
    onToggleMaterial: (StudyMaterial) -> Unit,
    onToggleAssignment: (LevelAssignment) -> Unit,
    onSubmitQuizScore: (Int, List<String>) -> Unit,
    onSaveStudentNote: (String?, String, MaterialCategory, String, String, List<Pair<String, String>>) -> Unit,
    onDeleteStudentNote: (String) -> Unit,
    onAddStudyReminder: (String, ReminderPriority) -> Unit = { _, _ -> },
    onToggleStudyReminder: (String) -> Unit = {},
    onDeleteStudyReminder: (String) -> Unit = {},
    onRecordFlashcardMastered: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onImportTeacherEvaluationPacket: (String, (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    onSendMessageToTeacher: (String) -> Unit,
    onImportVocabStream: (InputStream, String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onImportVocabCsvText: (String, String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onAddVocabWord: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteVocabWord: (String) -> Unit = {},
    onMarkStudyRoomDone: (Boolean) -> Unit = {},
    onSubmitRoomPracticeScore: (BoxPracticeRoomType, Int, List<String>) -> Unit = { _, _, _ -> }
) {
    BackHandler(onBack = onBack)

    val level = node.level
    val trackColor = level.track.primaryColor
    var selectedSection by remember(level.id) { mutableIntStateOf(initialSelectedTab.coerceIn(0, 2)) }

    LaunchedEffect(initialSelectedTab, level.id) {
        selectedSection = initialSelectedTab.coerceIn(0, 2)
    }

    val speechController = rememberOfflineSpeechController()

    // Dialog states for adding or editing student personal study notes
    var showNoteEditorDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<StudyMaterial?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("level_detail_screen")
    ) {
        // Compact Top Header Bar (Level Title, Notification Bell & 3 Stars)
        Surface(
            color = trackColor,
            contentColor = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("back_to_map_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Adventure Map",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "BOX ${level.orderIndex} OF 21 • ${level.boxType.title.uppercase()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${level.badgeEmoji} ${level.title}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Notification Bell inside unit header
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onOpenNotifications)
                            .testTag("unit_header_notifications_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (unreadNotificationsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Unit Notifications",
                                tint = if (unreadNotificationsCount > 0) EtaGold else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            if (unreadNotificationsCount > 0) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "$unreadNotificationsCount",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EtaGold,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { idx ->
                            Icon(
                                imageVector = if (idx < node.starsEarned) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (idx < node.starsEarned) EtaGold else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // Subtle Audio Pronunciation Banner when TTS speaks a phrase or mistake correction
        AnimatedVisibility(visible = speechController.lastSpokenText != null) {
            val spoken = speechController.lastSpokenText ?: ""
            Surface(
                color = EtaBlue.copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔊 Pronouncing: $spoken",
                        style = MaterialTheme.typography.labelMedium,
                        color = EtaNavy,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        val mistakeCount = node.progress.mistakeItems.size + node.progress.quizMistakeIdSet.size
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = {
                    Text(
                        text = "Rooms (${level.boxType.roomCount})",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_materials")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Flashcards & Notes", style = MaterialTheme.typography.labelLarge) },
                icon = { Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_assignments")
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = {
                    Text(
                        text = if (mistakeCount > 0) "Grades & Mistakes ($mistakeCount)" else "Grades & Mistakes",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(15.dp)) },
                modifier = Modifier.testTag("tab_class_grade")
            )
        }

        when (selectedSection) {
            0 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                BoxRoomsInteractiveView(
                    node = node,
                    speechController = speechController,
                    onImportVocabStream = onImportVocabStream,
                    onImportVocabCsvText = onImportVocabCsvText,
                    onAddVocabWord = onAddVocabWord,
                    onDeleteVocabWord = onDeleteVocabWord,
                    onMarkStudyRoomDone = onMarkStudyRoomDone,
                    onSubmitRoomPracticeScore = onSubmitRoomPracticeScore
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            1 -> StudyMaterialsSection(
                node = node,
                speechController = speechController,
                onToggleMaterial = onToggleMaterial,
                onEditStudentNoteClick = { note ->
                    editingNote = note
                    showNoteEditorDialog = true
                },
                onAddStudentNoteClick = {
                    editingNote = null
                    showNoteEditorDialog = true
                },
                onDeleteStudentNote = onDeleteStudentNote,
                onAddStudyReminder = onAddStudyReminder,
                onToggleStudyReminder = onToggleStudyReminder,
                onDeleteStudyReminder = onDeleteStudyReminder,
                onRecordFlashcardMastered = onRecordFlashcardMastered
            )
            2 -> StudentGradesAndMistakesSection(
                node = node,
                speechController = speechController,
                onSaveMistakeAsStudyReminder = { reminderText ->
                    onAddStudyReminder(reminderText, ReminderPriority.SPEAKING_PRACTICE)
                },
                onOpenNotifications = onOpenNotifications,
                onImportTeacherEvaluationPacket = onImportTeacherEvaluationPacket,
                onSendMessageToTeacher = onSendMessageToTeacher
            )
        }
    }

    if (showNoteEditorDialog) {
        StudentNoteEditorDialog(
            levelTitle = level.title,
            existingNote = editingNote,
            onDismiss = {
                showNoteEditorDialog = false
                editingNote = null
            },
            onSave = { existingId, title, category, summary, content, phrases ->
                onSaveStudentNote(existingId, title, category, summary, content, phrases)
                showNoteEditorDialog = false
                editingNote = null
            }
        )
    }
}

@Composable
private fun StudyMaterialsSection(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onToggleMaterial: (StudyMaterial) -> Unit,
    onEditStudentNoteClick: (StudyMaterial) -> Unit,
    onAddStudentNoteClick: () -> Unit,
    onDeleteStudentNote: (String) -> Unit,
    onAddStudyReminder: (String, ReminderPriority) -> Unit,
    onToggleStudyReminder: (String) -> Unit,
    onDeleteStudyReminder: (String) -> Unit,
    onRecordFlashcardMastered: () -> Unit
) {
    val level = node.level
    val completedSet = node.progress.completedMaterialSet
    val flippedCards = remember { mutableStateMapOf<String, Boolean>() }

    // State for adding a local study reminder inside this unit
    var reminderDraft by remember(level.id) { mutableStateOf("") }
    var selectedPriority by remember(level.id) { mutableStateOf(ReminderPriority.HIGH_PRIORITY) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Interactive Vocabulary & Mistake Review Flashcards Deck
        item {
            InteractiveFlashcardDeckCard(
                unitTitle = level.title,
                trackColor = level.track.primaryColor,
                flashcards = node.reviewFlashcards,
                speechController = speechController,
                onCompleteCardReview = onRecordFlashcardMastered
            )
        }

        // 2. Official Curriculum Materials with Offline Audio Pronunciation
        items(level.materials, key = { it.id }) { material ->
            val isCompleted = completedSet.contains(material.id)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_card_${material.id}"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = level.track.primaryColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${material.category.label} • +${material.xpReward} XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = level.track.primaryColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Mastered",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = material.contentBody,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (material.keyPhrases.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        material.keyPhrases.forEachIndexed { idx, (front, back) ->
                            val cardKey = "${material.id}_$idx"
                            val isFlipped = flippedCards[cardKey] == true
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (isFlipped) Color(0xFF10B981) else level.track.primaryColor.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { flippedCards[cardKey] = !isFlipped },
                                color = if (isFlipped) {
                                    Color(0xFF10B981).copy(alpha = 0.1f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                }
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isFlipped) "💡 Meaning:" else "🗣️ Phrase (Tap to Flip):",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isFlipped) Color(0xFF10B981) else level.track.primaryColor
                                            )
                                            Text(
                                                text = if (isFlipped) back else front,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Text(
                                            text = "🔄",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AudioPronunciationButtons(
                                        textToSpeak = front,
                                        speechController = speechController,
                                        accentColor = level.track.primaryColor,
                                        testTagPrefix = "phrase_tts_${material.id}_$idx"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { onToggleMaterial(material) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) Color(0xFF10B981) else level.track.primaryColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_material_btn_${material.id}")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCompleted) "Completed" else "Mark Mastered (+${material.xpReward} XP)")
                    }
                }
            }
        }

        // 3. Local Unit Study Reminders Checklist Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("unit_study_reminders_section"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AlarmOn,
                                contentDescription = null,
                                tint = EtaRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "⏰ My Study Reminders (${level.title})",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = EtaNavy,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Save local study tasks, pronunciation targets, or exam reminders for this unit",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Priority Tag Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ReminderPriority.entries.forEach { priority ->
                            val isSelected = selectedPriority == priority
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (isSelected) priority.badgeColor else priority.badgeColor.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, priority.badgeColor.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { selectedPriority = priority }
                                    .testTag("reminder_priority_${priority.name}")
                            ) {
                                Text(
                                    text = "${priority.emoji} ${priority.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) Color.White else priority.badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = reminderDraft,
                            onValueChange = { reminderDraft = it },
                            label = { Text("Add a study reminder for ${level.title}") },
                            placeholder = { Text("e.g. Review 5 vocabulary cards before Thursday's class") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("study_reminder_input")
                        )

                        Button(
                            onClick = {
                                if (reminderDraft.isNotBlank()) {
                                    onAddStudyReminder(reminderDraft, selectedPriority)
                                    reminderDraft = ""
                                }
                            },
                            enabled = reminderDraft.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                            modifier = Modifier.testTag("add_study_reminder_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Save Reminder", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Save")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (node.studyReminders.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No study reminders saved for ${level.title} yet. Add one above or pin a correction from your Grades & Mistakes tab!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        node.studyReminders.forEach { reminder ->
                            val priority = reminder.priority
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (reminder.isCompleted) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                } else {
                                    priority.badgeColor.copy(alpha = 0.06f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (reminder.isCompleted) MaterialTheme.colorScheme.outlineVariant
                                    else priority.badgeColor.copy(alpha = 0.28f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .testTag("study_reminder_item_${reminder.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = reminder.isCompleted,
                                        onCheckedChange = { onToggleStudyReminder(reminder.id) },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("toggle_reminder_checkbox_${reminder.id}")
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = priority.badgeColor.copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = "${priority.emoji} ${priority.label}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = priority.badgeColor,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = reminder.reminderText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (reminder.isCompleted) {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteStudyReminder(reminder.id) },
                                        modifier = Modifier
                                            .size(26.dp)
                                            .testTag("delete_reminder_btn_${reminder.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Study Reminder",
                                            tint = EtaRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Student Personal Study Notes Section (Students can add, edit, and delete their own detailed notes)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📝 My Personal Study Notes",
                    style = MaterialTheme.typography.titleLarge,
                    color = EtaNavy,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = EtaBlue.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable(onClick = onAddStudentNoteClick)
                        .testTag("add_student_note_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = EtaBlue,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add My Note",
                            style = MaterialTheme.typography.labelLarge,
                            color = EtaBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (node.studentNotes.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = "No personal notes yet. Tap \"+ Add My Note\" to write your own vocabulary summaries, grammar notes, or custom flashcards for this unit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            items(node.studentNotes, key = { it.id }) { note ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_note_card_${note.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = EtaBlue.copy(alpha = 0.06f)
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = EtaBlue.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "Personal Note • ${note.category.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EtaBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onEditStudentNoteClick(note) },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("edit_student_note_btn_${note.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Note",
                                        tint = EtaBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                IconButton(
                                    onClick = { onDeleteStudentNote(note.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Note",
                                        tint = EtaRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = note.contentBody,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (note.keyPhrases.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            note.keyPhrases.forEachIndexed { idx, (term, meaning) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• $term → $meaning",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = EtaNavy,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AudioPronunciationButtons(
                                        textToSpeak = term,
                                        speechController = speechController,
                                        accentColor = EtaBlue,
                                        testTagPrefix = "note_phrase_tts_${note.id}_$idx"
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentNoteEditorDialog(
    levelTitle: String,
    existingNote: StudyMaterial?,
    onDismiss: () -> Unit,
    onSave: (String?, String, MaterialCategory, String, String, List<Pair<String, String>>) -> Unit
) {
    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var selectedCategory by remember(existingNote) {
        mutableStateOf(existingNote?.category ?: MaterialCategory.VOCABULARY)
    }
    var content by remember(existingNote) { mutableStateOf(existingNote?.contentBody ?: "") }
    var flashcardsRaw by remember(existingNote) {
        mutableStateOf(
            existingNote?.keyPhrases?.joinToString("\n") { "${it.first} = ${it.second}" } ?: ""
        )
    }
    var expandedDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = if (existingNote != null) "Edit My Personal Note" else "Add Personal Note for $levelTitle",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(modifier = Modifier.height(340.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    placeholder = {
                        Text("e.g. My Grammar Reminders")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("material_editor_title_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        MaterialCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("My Notes & Study Reminders") },
                    placeholder = { Text("Write your personal vocabulary notes, grammar rules, or class reminders...") },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("material_editor_content_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = flashcardsRaw,
                    onValueChange = { flashcardsRaw = it },
                    label = { Text("Personal Flashcards (Optional: Term = Definition)") },
                    placeholder = { Text("Target Phrase = Meaning\nNext Word = Definition") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val phrases = flashcardsRaw.lines().mapNotNull { line ->
                        val parts = line.split("=")
                        if (parts.size >= 2) parts[0].trim() to parts[1].trim() else null
                    }
                    onSave(
                        existingNote?.id,
                        title.ifBlank { "My Study Note" },
                        selectedCategory,
                        "Personal student note for $levelTitle",
                        content.ifBlank { "Personal notes and study reminders." },
                        phrases
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                modifier = Modifier.testTag("save_material_or_note_btn")
            ) {
                Text(if (existingNote != null) "Save Changes" else "Save Note")
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
private fun AssignmentsAndQuizSection(
    node: LevelNodeState,
    onToggleAssignment: (LevelAssignment) -> Unit,
    onSubmitQuizScore: (Int, List<String>) -> Unit
) {
    val level = node.level
    val completedAssigns = node.progress.completedAssignmentSet
    val selectedAnswers = remember(level.id) { mutableStateMapOf<String, Int>() }
    var quizSubmitted by remember(level.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "📋 Class Assignments",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(level.assignments, key = { it.id }) { assignment ->
            val isDone = completedAssigns.contains(assignment.id)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("assignment_card_${assignment.id}"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${assignment.title} (+${assignment.xpReward} XP)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = isDone,
                            onCheckedChange = { onToggleAssignment(assignment) },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("assignment_checkbox_${assignment.id}")
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = assignment.instructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Quick Skill Quiz",
                    style = MaterialTheme.typography.titleLarge
                )
                if (node.progress.quizBestScore > 0) {
                    Text(
                        text = "Best: ${node.progress.quizBestScore}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        items(level.quizQuestions, key = { it.id }) { q ->
            val chosen = selectedAnswers[q.id]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = q.prompt,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    q.options.forEachIndexed { idx, optText ->
                        val isSelected = chosen == idx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedAnswers[q.id] = idx
                                    quizSubmitted = false
                                }
                                .padding(vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedAnswers[q.id] = idx
                                    quizSubmitted = false
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (quizSubmitted && chosen != null) {
                        val isCorrect = chosen == q.correctIndex
                        Text(
                            text = if (isCorrect) "✅ ${q.explanation}" else "💡 ${q.explanation}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isCorrect) Color(0xFF10B981) else EtaRed,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val correctCount = level.quizQuestions.count { q ->
                        selectedAnswers[q.id] == q.correctIndex
                    }
                    val wrongIds = level.quizQuestions
                        .filter { q -> selectedAnswers[q.id] != q.correctIndex }
                        .map { it.id }
                    val pct = ((correctCount.toFloat() / level.quizQuestions.size.coerceAtLeast(1)) * 100).toInt()
                    quizSubmitted = true
                    onSubmitQuizScore(pct, wrongIds)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = level.track.primaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_quiz_button")
            ) {
                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Check Quiz Answers")
            }
        }
    }
}

private enum class MistakeFilter {
    ALL,
    TEACHER_LOGGED,
    QUIZ_MISTAKES
}

/**
 * STUDENT PHONE ONLY: Read-only "Grades & Mistakes" Dashboard inside each individual unit screen.
 * - View Speaking, Listening, Reading, Writing scores & teacher feedback (NO grading sliders).
 * - View Mistakes (both teacher-logged in-class mistakes and missed quiz questions) with offline TTS pronunciation and one-tap "Pin to Study Reminders".
 * - Sync/Receive offline teacher evaluation updates and open Notification Center.
 * - Send a message to the teacher only (and see teacher replies).
 */
@Composable
private fun StudentGradesAndMistakesSection(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onSaveMistakeAsStudyReminder: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onImportTeacherEvaluationPacket: (String, (Boolean, String) -> Unit) -> Unit,
    onSendMessageToTeacher: (String) -> Unit
) {
    val level = node.level
    val prog = node.progress
    var messageDraft by remember(level.id) { mutableStateOf("") }
    var mistakeFilter by remember(level.id) { mutableStateOf(MistakeFilter.ALL) }
    var showSyncBox by remember(level.id) { mutableStateOf(false) }
    var syncJsonDraft by remember(level.id) { mutableStateOf("") }
    var syncStatus by remember(level.id) { mutableStateOf<String?>(null) }

    val missedQuizQuestions = remember(level.quizQuestions, prog.quizMistakesCsv) {
        val mistakeIds = prog.quizMistakeIdSet
        level.quizQuestions.filter { it.id in mistakeIds }
    }

    val inClassAvg = ((prog.speakingScore + prog.listeningScore + prog.readingScore + prog.writingScore) / 4.0).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_grades_section"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Unit Standing & Upgrading Code Requirement Banner
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (node.isPassed) EtaGold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (node.isPassed) EtaGold else level.track.primaryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = level.badgeEmoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (node.isPassed) "🏅 Unit Passed • Badge Earned!" else "🎯 Pass Goal: 60%+ Overall Score",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (node.isPassed) Color(0xFFB45309) else level.track.primaryColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${level.badgeName} (${level.title})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Read-Only Student Dashboard • Obtain your Upgrading Code from Administration to unlock the next class.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Summary KPI Chips Row (Class Average, Quiz Best, Mistakes Count, Notifications)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = level.track.primaryColor.copy(alpha = 0.10f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (prog.gradedByTeacher) "$inClassAvg%" else "Pending",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = level.track.primaryColor,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Class Avg",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.10f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (prog.quizBestScore > 0) "${prog.quizBestScore}%" else "Not Taken",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF047857),
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Skill Quiz",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val totalMistakes = prog.mistakeItems.size + missedQuizQuestions.size
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtaRed.copy(alpha = 0.10f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$totalMistakes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EtaRed,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Mistakes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Read-Only In-Class Grades & Teacher Evaluation Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_readonly_grades_card"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📊 My Class Grades (Read-Only)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "4-Skill Classroom Rubric Evaluation",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (prog.gradedByTeacher) level.track.primaryColor.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (prog.gradedByTeacher) "Class Avg: $inClassAvg%" else "Awaiting Teacher Grade",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (prog.gradedByTeacher) level.track.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ReadOnlySkillScoreRow("🗣️ Speaking", prog.speakingScore, prog.gradedByTeacher, level.track.primaryColor)
                    ReadOnlySkillScoreRow("🎧 Listening", prog.listeningScore, prog.gradedByTeacher, level.track.primaryColor)
                    ReadOnlySkillScoreRow("📖 Reading", prog.readingScore, prog.gradedByTeacher, level.track.primaryColor)
                    ReadOnlySkillScoreRow("✍️ Writing", prog.writingScore, prog.gradedByTeacher, level.track.primaryColor)

                    if (prog.teacherNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EtaNavy.copy(alpha = 0.06f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "👨‍🏫 Teacher Feedback:",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = EtaNavy,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AudioPronunciationButtons(
                                        textToSpeak = prog.teacherNote,
                                        speechController = speechController,
                                        accentColor = EtaNavy,
                                        testTagPrefix = "teacher_note_tts"
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = prog.teacherNote,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Row: View Notifications Inbox or Sync New Teacher Evaluation Packet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenNotifications,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_notifications_from_grades_btn")
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Notifications", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = { showSyncBox = !showSyncBox },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_unit_teacher_sync_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Teacher Update", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    AnimatedVisibility(visible = showSyncBox) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EtaBlue.copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, EtaBlue.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Receive Offline Grade & Mistake Update for ${level.title}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EtaNavy,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = syncJsonDraft,
                                    onValueChange = { syncJsonDraft = it },
                                    label = { Text("Paste Teacher Evaluation JSON") },
                                    minLines = 2,
                                    maxLines = 3,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            val sampleJson = """
                                                {
                                                  "levelId": "${level.id}",
                                                  "speaking": 89,
                                                  "listening": 92,
                                                  "reading": 87,
                                                  "writing": 85,
                                                  "teacherNote": "Strong progress in ${level.title}! Keep practicing the corrected phrases aloud.",
                                                  "mistakes": [
                                                    {
                                                      "skillArea": "Speaking & Grammar",
                                                      "mistake": "I have went to the institute yesterday.",
                                                      "correction": "I went to the institute yesterday."
                                                    }
                                                  ]
                                                }
                                            """.trimIndent()
                                            onImportTeacherEvaluationPacket(sampleJson) { _, msg ->
                                                syncStatus = msg
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("unit_receive_classroom_update_btn")
                                    ) {
                                        Text("Receive Latest Feedback", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            onImportTeacherEvaluationPacket(syncJsonDraft) { ok, msg ->
                                                syncStatus = msg
                                                if (ok) syncJsonDraft = ""
                                            }
                                        },
                                        enabled = syncJsonDraft.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Apply", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                                if (syncStatus != null) {
                                    Text(
                                        text = syncStatus!!,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF047857),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. My Mistakes & Corrections Review Hub (with Offline Audio Pronunciation & Pin to Study Reminders)
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_mistakes_card"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = EtaRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "🔍 My Mistakes & Corrections",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Listen to proper pronunciation or pin any correction to your unit Study Reminders",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Filter Chips: All | In-Class Mistakes | Quiz Mistakes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = mistakeFilter == MistakeFilter.ALL,
                            onClick = { mistakeFilter = MistakeFilter.ALL },
                            label = {
                                Text(
                                    "All (${prog.mistakeItems.size + missedQuizQuestions.size})",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                        FilterChip(
                            selected = mistakeFilter == MistakeFilter.TEACHER_LOGGED,
                            onClick = { mistakeFilter = MistakeFilter.TEACHER_LOGGED },
                            label = {
                                Text(
                                    "In-Class (${prog.mistakeItems.size})",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                        FilterChip(
                            selected = mistakeFilter == MistakeFilter.QUIZ_MISTAKES,
                            onClick = { mistakeFilter = MistakeFilter.QUIZ_MISTAKES },
                            label = {
                                Text(
                                    "Quiz (${missedQuizQuestions.size})",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val showInClass = mistakeFilter == MistakeFilter.ALL || mistakeFilter == MistakeFilter.TEACHER_LOGGED
                    val showQuiz = mistakeFilter == MistakeFilter.ALL || mistakeFilter == MistakeFilter.QUIZ_MISTAKES

                    if (prog.mistakeItems.isEmpty() && missedQuizQuestions.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎉 No mistakes logged for this level yet! Keep practicing your skills and complete the Skill Quiz.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        if (showInClass && prog.mistakeItems.isNotEmpty()) {
                            Text(
                                text = "In-Class Mistakes Logged by Teacher:",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            prog.mistakeItems.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = EtaRed.copy(alpha = 0.06f),
                                    border = BorderStroke(1.dp, EtaRed.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .testTag("mistake_correction_card_${item.id}")
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = EtaRed.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = item.skillArea,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = EtaRed,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            // Quick action to pin this mistake correction into unit Study Reminders
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = EtaNavy.copy(alpha = 0.09f),
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(50))
                                                    .clickable {
                                                        onSaveMistakeAsStudyReminder(
                                                            "Practice correction (${item.skillArea}): \"${item.correctionText}\" (instead of \"${item.mistakeText}\")"
                                                        )
                                                    }
                                                    .testTag("pin_mistake_reminder_${item.id}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.BookmarkAdd,
                                                        contentDescription = "Save to Study Reminders",
                                                        tint = EtaNavy,
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "Pin Reminder",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = EtaNavy,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "❌ Mistake: ${item.mistakeText}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = EtaRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "✅ Correction: ${item.correctionText}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF047857),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        AudioPronunciationButtons(
                                            textToSpeak = item.correctionText,
                                            speechController = speechController,
                                            accentColor = Color(0xFF059669),
                                            testTagPrefix = "mistake_tts_${item.id}"
                                        )
                                    }
                                }
                            }
                        }

                        if (showQuiz && missedQuizQuestions.isNotEmpty()) {
                            if (showInClass && prog.mistakeItems.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text(
                                text = "Skill Quiz Mistakes to Review:",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            missedQuizQuestions.forEach { q ->
                                val correctOption = q.options.getOrNull(q.correctIndex) ?: ""
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFEF3C7).copy(alpha = 0.65f),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "❓ ${q.prompt}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "✅ Correct Answer: $correctOption",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF047857),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "💡 ${q.explanation}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AudioPronunciationButtons(
                                                textToSpeak = correctOption,
                                                speechController = speechController,
                                                accentColor = Color(0xFF047857),
                                                testTagPrefix = "quiz_mistake_tts_${q.id}"
                                            )

                                            TextButton(
                                                onClick = {
                                                    onSaveMistakeAsStudyReminder("Review quiz question: \"${q.prompt}\" → $correctOption")
                                                },
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("+ Pin Reminder", style = MaterialTheme.typography.labelMedium)
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

        // 4. Send Message to Teacher Only Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_message_teacher_card"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = EtaBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "💬 Message My Teacher",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Have a question about your grades or mistakes? Send a direct message to your teacher only.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = messageDraft,
                        onValueChange = { messageDraft = it },
                        label = { Text("Write a message to your teacher") },
                        placeholder = { Text("e.g. Teacher, could we review my speaking pronunciation in class?") },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_message_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (messageDraft.isNotBlank()) {
                                onSendMessageToTeacher(messageDraft)
                                messageDraft = ""
                            }
                        },
                        enabled = messageDraft.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_teacher_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Message to Teacher")
                    }

                    if (node.messages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sent Messages & Teacher Replies:",
                            style = MaterialTheme.typography.labelLarge,
                            color = EtaNavy,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        node.messages.forEach { msg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "You: ${msg.messageText}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (msg.teacherReply.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "👨‍🏫 Teacher Reply: ${msg.teacherReply}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = EtaBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            text = "⏳ Sent to Teacher",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun ReadOnlySkillScoreRow(
    label: String,
    score: Int,
    gradedByTeacher: Boolean,
    color: Color
) {
    val standingLabel = when {
        !gradedByTeacher -> "Not graded yet"
        score >= 90 -> "$score% • Excellent"
        score >= 75 -> "$score% • Proficient"
        score >= 60 -> "$score% • Pass"
        else -> "$score% • Needs Review"
    }
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                text = standingLabel,
                style = MaterialTheme.typography.labelLarge,
                color = if (gradedByTeacher) color else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { if (gradedByTeacher) (score / 100f).coerceIn(0f, 1f) else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.14f)
        )
    }
}
