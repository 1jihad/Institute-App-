package com.example.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.CurriculumCatalog
import com.example.data.FlashcardReviewItem
import com.example.data.FlashcardSourceType
import com.example.data.NotificationCategory
import com.example.data.StudentNotificationEntity
import com.example.data.StudentProfileEntity
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Offline Android TextToSpeech controller for pronouncing English vocabulary phrases,
 * student flashcards, and teacher mistake corrections at Normal (1.0x) or Slow (0.75x) speed.
 */
class OfflineSpeechController(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    var lastSpokenText by mutableStateOf<String?>(null)
        private set

    init {
        runCatching {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.US)
                    isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                }
            }
        }
    }

    fun speak(text: String, speechRate: Float = 1.0f) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return
        lastSpokenText = "$cleanText (${if (speechRate < 0.9f) "0.75x Slow" else "1.0x Normal"})"
        runCatching {
            val engine = tts ?: return
            if (isReady) {
                engine.setSpeechRate(speechRate.coerceIn(0.5f, 1.5f))
                engine.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "eta_tts_${System.currentTimeMillis()}")
            }
        }
    }

    fun shutdown() {
        runCatching {
            tts?.stop()
            tts?.shutdown()
            tts = null
        }
    }
}

@Composable
fun rememberOfflineSpeechController(): OfflineSpeechController {
    val context = LocalContext.current
    val controller = remember(context) { OfflineSpeechController(context) }
    DisposableEffect(controller) {
        onDispose {
            controller.shutdown()
        }
    }
    return controller
}

/**
 * Compact 1.0x Normal and 0.75x Slow offline audio pronunciation pill buttons.
 */
@Composable
fun AudioPronunciationButtons(
    textToSpeak: String,
    speechController: OfflineSpeechController,
    accentColor: Color = EtaBlue,
    testTagPrefix: String = "tts_btn"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = accentColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { speechController.speak(textToSpeak, 1.0f) }
                .testTag("${testTagPrefix}_normal")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen Normal Speed",
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Listen",
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = EtaNavy.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, EtaNavy.copy(alpha = 0.22f)),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { speechController.speak(textToSpeak, 0.75f) }
                .testTag("${testTagPrefix}_slow")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Listen Slow Speed 0.75x",
                    tint = EtaNavy,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "0.75x",
                    style = MaterialTheme.typography.labelMedium,
                    color = EtaNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Helper to post Android system notifications when new grades or mistake corrections arrive.
 */
object StudentSystemNotificationDispatcher {
    private const val CHANNEL_ID = "eta_student_grade_updates"

    fun notifyStudent(context: Context, title: String, message: String) {
        runCatching {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "ETA Grades & Mistake Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for updated class grades, mistake corrections, and teacher replies"
                }
                manager.createNotificationChannel(channel)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!granted) return
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            manager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
        }
    }
}

/**
 * Compact Daily Study Streak & Exam Countdown Goal bar shown on the Student HUD.
 */
@Composable
fun DailyStreakAndExamGoalBar(
    profile: StudentProfileEntity,
    unreadNotificationsCount: Int,
    onEditExamGoal: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .testTag("streak_and_exam_goal_bar"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, EtaNavy.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Daily Study Streak Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFFFF7ED))
                    .border(1.dp, Color(0xFFF97316).copy(alpha = 0.35f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Daily Study Streak",
                    tint = Color(0xFFEA580C),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${profile.streakDays}d Streak",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFC2410C),
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. Exam Countdown Goal Pill (Clickable to edit goal & target days)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(EtaNavy.copy(alpha = 0.06f))
                    .clickable(onClick = onEditExamGoal)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .testTag("edit_exam_goal_button")
            ) {
                Text(
                    text = "🎯 ${profile.examGoalTitle}: ",
                    style = MaterialTheme.typography.labelMedium,
                    color = EtaNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = "${profile.daysUntilExam}d left",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaRed,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Exam Goal",
                    tint = EtaNavy.copy(alpha = 0.65f),
                    modifier = Modifier.size(11.dp)
                )
            }

            // 3. Notification Center Quick Pill
            Surface(
                shape = RoundedCornerShape(50),
                color = if (unreadNotificationsCount > 0) EtaRed.copy(alpha = 0.12f) else EtaBlue.copy(alpha = 0.10f),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onOpenNotifications)
                    .testTag("hud_notifications_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (unreadNotificationsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Student Notifications",
                        tint = if (unreadNotificationsCount > 0) EtaRed else EtaBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (unreadNotificationsCount > 0) "$unreadNotificationsCount New" else "Inbox",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (unreadNotificationsCount > 0) EtaRed else EtaBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ExamGoalEditorDialog(
    currentTitle: String,
    currentDaysLeft: Int,
    onDismiss: () -> Unit,
    onSaveGoal: (String, Int) -> Unit
) {
    var goalTitle by remember(currentTitle) { mutableStateOf(currentTitle) }
    var daysText by remember(currentDaysLeft) { mutableStateOf(currentDaysLeft.coerceAtLeast(1).toString()) }
    val presetGoals = listOf(
        "Unit 1 Speaking & Grammar Exam" to 7,
        "Intermediate Midterm Assessment" to 14,
        "TOEFL Practice Milestone" to 30
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "🎯 Set Exam Countdown Goal",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Track your target exam date and maintain your daily study streak by reviewing flashcards, notes, and quizzes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = goalTitle,
                    onValueChange = { goalTitle = it },
                    label = { Text("Target Exam or Milestone") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_goal_title_input")
                )

                OutlinedTextField(
                    value = daysText,
                    onValueChange = { input ->
                        daysText = input.filter { it.isDigit() }.take(3)
                    },
                    label = { Text("Days Until Exam (1 - 365)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exam_goal_days_input")
                )

                Text(
                    text = "Quick Presets:",
                    style = MaterialTheme.typography.labelMedium,
                    color = EtaNavy,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetGoals.forEach { (presetName, presetDays) ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = EtaBlue.copy(alpha = 0.1f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    goalTitle = presetName
                                    daysText = presetDays.toString()
                                }
                        ) {
                            Text(
                                text = "$presetName (${presetDays}d)",
                                style = MaterialTheme.typography.labelMedium,
                                color = EtaBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedDays = daysText.toIntOrNull()?.coerceIn(1, 365) ?: 14
                    onSaveGoal(goalTitle.ifBlank { "Next Unit Assessment" }, parsedDays)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                modifier = Modifier.testTag("save_exam_goal_button")
            ) {
                Text("Save Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Dedicated Student Notification Center ("place for notifications") where students receive
 * alerts for updated grades, teacher-logged mistake corrections, teacher replies, and study reminders,
 * and can tap any notification to jump directly to that unit's Read-Only Grades & Mistakes dashboard.
 */
@Composable
fun StudentNotificationCenterDialog(
    notifications: List<StudentNotificationEntity>,
    onDismiss: () -> Unit,
    onOpenUnitGradesFromNotification: (StudentNotificationEntity) -> Unit,
    onMarkAllRead: () -> Unit,
    onClearRead: () -> Unit,
    onImportTeacherEvaluationPacket: (String, (Boolean, String) -> Unit) -> Unit
) {
    var showUnreadOnly by remember { mutableStateOf(false) }
    var showSyncForm by remember { mutableStateOf(false) }
    var packetInput by remember { mutableStateOf("") }
    var syncStatusMessage by remember { mutableStateOf<String?>(null) }
    var syncStatusSuccess by remember { mutableStateOf(true) }

    val filteredList = remember(notifications, showUnreadOnly) {
        if (showUnreadOnly) notifications.filter { !it.isRead } else notifications
    }
    val unreadCount = notifications.count { !it.isRead }
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.US) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
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
                                .background(EtaRed.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = EtaRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Student Notifications",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = if (unreadCount > 0) "$unreadCount unread grade & mistake update(s)" else "All caught up!",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (unreadCount > 0) EtaRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Notifications")
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp, max = 440.dp)
                    .testTag("notification_center_dialog"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Filter & Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = !showUnreadOnly,
                            onClick = { showUnreadOnly = false },
                            label = { Text("All (${notifications.size})", style = MaterialTheme.typography.labelMedium) }
                        )
                        FilterChip(
                            selected = showUnreadOnly,
                            onClick = { showUnreadOnly = true },
                            label = { Text("Unread ($unreadCount)", style = MaterialTheme.typography.labelMedium) }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (unreadCount > 0) {
                            TextButton(
                                onClick = onMarkAllRead,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("mark_all_notifications_read_btn")
                            ) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Mark Read", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        TextButton(
                            onClick = { showSyncForm = !showSyncForm },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("toggle_sync_teacher_packet_btn")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Sync Update", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // Optional Offline Teacher Grade / Mistake Packet Import Box
                AnimatedVisibility(visible = showSyncForm) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EtaBlue.copy(alpha = 0.07f),
                        border = BorderStroke(1.dp, EtaBlue.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "📥 Receive Offline Teacher Evaluation Packet",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Paste a teacher grade/mistake JSON packet or tap 'Receive Latest Classroom Feedback' to get notified immediately.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = packetInput,
                                onValueChange = { packetInput = it },
                                label = { Text("Teacher Evaluation JSON") },
                                placeholder = { Text("{\"levelId\":\"lvl_1_essence\",\"speaking\":90,...}") },
                                minLines = 2,
                                maxLines = 3,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("teacher_packet_json_input")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val samplePacket = """
                                            {
                                              "levelId": "lvl_1_essence",
                                              "speaking": 91,
                                              "listening": 94,
                                              "reading": 89,
                                              "writing": 86,
                                              "teacherNote": "Excellent improvement on your speaking fluency today! Keep reviewing your unit flashcards.",
                                              "mistakes": [
                                                {
                                                  "skillArea": "Speaking",
                                                  "mistake": "He don't know the classroom number.",
                                                  "correction": "He doesn't know the classroom number."
                                                }
                                              ]
                                            }
                                        """.trimIndent()
                                        onImportTeacherEvaluationPacket(samplePacket) { ok, msg ->
                                            syncStatusSuccess = ok
                                            syncStatusMessage = msg
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("receive_sample_teacher_update_btn")
                                ) {
                                    Text("Receive Classroom Update", style = MaterialTheme.typography.labelMedium)
                                }

                                Button(
                                    onClick = {
                                        onImportTeacherEvaluationPacket(packetInput) { ok, msg ->
                                            syncStatusSuccess = ok
                                            syncStatusMessage = msg
                                            if (ok) packetInput = ""
                                        }
                                    },
                                    enabled = packetInput.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("apply_teacher_packet_btn")
                                ) {
                                    Text("Apply", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            if (syncStatusMessage != null) {
                                Text(
                                    text = syncStatusMessage!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (syncStatusSuccess) Color(0xFF047857) else EtaRed,
                                    modifier = Modifier.padding(top = 3.dp)
                                )
                            }
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔔", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (showUnreadOnly) "No unread notifications!" else "No notifications yet.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "When your teacher updates your grades or logs mistake corrections, they will appear right here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredList, key = { it.id }) { notif ->
                            val levelTitle = CurriculumCatalog.allLevels.find { it.id == notif.levelId }?.title ?: "Unit"
                            val cat = notif.category
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenUnitGradesFromNotification(notif) }
                                    .testTag("notification_item_${notif.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (!notif.isRead) {
                                        cat.accentColor.copy(alpha = 0.08f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
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
                                                color = cat.accentColor.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "${cat.emoji} ${cat.label} • $levelTitle",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = cat.accentColor,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            if (!notif.isRead) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(EtaRed)
                                                )
                                            }
                                        }
                                        Text(
                                            text = dateFormat.format(Date(notif.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = "View in $levelTitle Grades & Mistakes →",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = cat.accentColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EtaNavy)
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            if (notifications.any { it.isRead }) {
                TextButton(onClick = onClearRead) {
                    Text("Clear Read")
                }
            }
        }
    )
}

/**
 * Interactive Vocabulary & Mistake Review Flashcards Deck inside each Unit.
 * Combines:
 * 1) Official Unit Key Phrases
 * 2) Student Personal Note Flashcards
 * 3) Teacher-Logged Mistake Corrections (Mistake on Front -> Correction on Back)
 */
@Composable
fun InteractiveFlashcardDeckCard(
    unitTitle: String,
    trackColor: Color,
    flashcards: List<FlashcardReviewItem>,
    speechController: OfflineSpeechController,
    onCompleteCardReview: () -> Unit
) {
    if (flashcards.isEmpty()) return

    var selectedSourceFilter by remember { mutableStateOf<FlashcardSourceType?>(null) }
    val activeDeck = remember(flashcards, selectedSourceFilter) {
        if (selectedSourceFilter == null) flashcards
        else flashcards.filter { it.sourceType == selectedSourceFilter }
    }.ifEmpty { flashcards }

    var currentIndex by remember(activeDeck.size, selectedSourceFilter) { mutableIntStateOf(0) }
    var isFlipped by remember(currentIndex, selectedSourceFilter) { mutableStateOf(false) }
    val masteredIds = remember { mutableStateMapOf<String, Boolean>() }

    val safeIndex = currentIndex.coerceIn(0, (activeDeck.size - 1).coerceAtLeast(0))
    val currentCard = activeDeck[safeIndex]
    val masteredCount = activeDeck.count { masteredIds[it.id] == true }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("unit_flashcard_deck_card"),
        shape = RoundedCornerShape(16.dp),
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
                        imageVector = Icons.Default.Style,
                        contentDescription = null,
                        tint = trackColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "🃏 Interactive Review Flashcards",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Practice $unitTitle phrases, personal cards & mistake corrections",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF10B981).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$masteredCount/${activeDeck.size} Mastered",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Source filter chips (All, Unit Phrases, My Note Cards, Mistake Corrections)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedSourceFilter == null,
                    onClick = { selectedSourceFilter = null },
                    label = { Text("All (${flashcards.size})", style = MaterialTheme.typography.labelMedium) }
                )
                FlashcardSourceType.entries.forEach { source ->
                    val count = flashcards.count { it.sourceType == source }
                    if (count > 0) {
                        FilterChip(
                            selected = selectedSourceFilter == source,
                            onClick = { selectedSourceFilter = source },
                            label = { Text("${source.label} ($count)", style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Interactive Flip Card Surface
            val cardAccent = when (currentCard.sourceType) {
                FlashcardSourceType.MISTAKE_CORRECTION -> if (isFlipped) Color(0xFF059669) else EtaRed
                FlashcardSourceType.PERSONAL_NOTE -> EtaBlue
                FlashcardSourceType.UNIT_PHRASE -> if (isFlipped) Color(0xFF059669) else trackColor
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 1.5.dp,
                        color = cardAccent.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { isFlipped = !isFlipped }
                    .testTag("flashcard_flip_surface"),
                color = cardAccent.copy(alpha = 0.07f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = cardAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${currentCard.sourceType.label} • ${currentCard.skillTag}",
                                style = MaterialTheme.typography.labelMedium,
                                color = cardAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "Card ${safeIndex + 1} of ${activeDeck.size} • Tap to Flip 🔄",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when {
                            currentCard.sourceType == FlashcardSourceType.MISTAKE_CORRECTION && !isFlipped ->
                                "❌ Spot & Fix This Mistake:"
                            currentCard.sourceType == FlashcardSourceType.MISTAKE_CORRECTION && isFlipped ->
                                "✅ Teacher Correction:"
                            !isFlipped -> "🗣️ Prompt / Phrase:"
                            else -> "💡 Meaning / Explanation:"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = cardAccent,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isFlipped) currentCard.backAnswer else currentCard.frontPrompt,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AudioPronunciationButtons(
                            textToSpeak = currentCard.spokenText,
                            speechController = speechController,
                            accentColor = cardAccent,
                            testTagPrefix = "flashcard_tts"
                        )

                        if (masteredIds[currentCard.id] == true) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Mastered",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Mastered",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF059669),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Controls: Previous / Practice Again / Got It (+XP) / Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        currentIndex = if (safeIndex > 0) safeIndex - 1 else activeDeck.lastIndex
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("← Prev", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = {
                        val wasAlreadyMastered = masteredIds[currentCard.id] == true
                        masteredIds[currentCard.id] = true
                        if (!wasAlreadyMastered) {
                            onCompleteCardReview()
                        }
                        isFlipped = false
                        currentIndex = (safeIndex + 1) % activeDeck.size
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1.4f)
                        .testTag("flashcard_got_it_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Got It!", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        currentIndex = (safeIndex + 1) % activeDeck.size
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("flashcard_next_button")
                ) {
                    Text("Next →", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
