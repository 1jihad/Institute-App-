package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CurriculumCatalog
import com.example.data.LevelAssignment
import com.example.data.StudyMaterial
import com.example.ui.LevelNodeState
import com.example.ui.theme.QuestCoral
import com.example.ui.theme.QuestEmerald
import com.example.ui.theme.QuestGold

@Composable
fun LevelDetailScreen(
    node: LevelNodeState,
    isDownloading: Boolean,
    downloadProgress: Float,
    onBack: () -> Unit,
    onDownloadContent: () -> Unit,
    onRemoveOfflinePack: () -> Unit,
    onToggleMaterial: (StudyMaterial) -> Unit,
    onToggleAssignment: (LevelAssignment) -> Unit,
    onSubmitQuizScore: (Int) -> Unit,
    onSaveInClassGrade: (Int, Int, Int, Int, String, Boolean) -> Unit
) {
    BackHandler(onBack = onBack)

    val level = node.level
    val trackColor = level.track.primaryColor
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("level_detail_screen")
    ) {
        // Compact Top Header Bar (Level Title & 3 Stars — no offline pack box below it)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("back_to_map_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Adventure Map",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LEVEL ${level.orderIndex} • ${level.hours} HRS",
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

                // Compact 3 Stars Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { idx ->
                        Icon(
                            imageVector = if (idx < node.starsEarned) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (idx < node.starsEarned) QuestGold else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        if (!node.isDownloaded) {
            // Clean, compact Download Content prompt before opening materials
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    modifier = Modifier.widthIn(max = 380.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = trackColor,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Download ${level.title}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Press Download Content (${level.packSizeMb} MB) to cache this unit's flashcards, assignments, and quiz for offline use.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        if (isDownloading) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = trackColor
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onDownloadContent,
                            enabled = !isDownloading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = trackColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("download_content_button")
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isDownloading) "Downloading..." else "Download Content (${level.packSizeMb} MB)")
                        }
                    }
                }
            }
        } else {
            // Compact 3 Level Tabs directly below the header
            TabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Materials (${node.materialsDoneCount}/${level.materials.size})", style = MaterialTheme.typography.labelLarge) },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_materials")
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Tasks & Quiz", style = MaterialTheme.typography.labelLarge) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_assignments")
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Class Grade", style = MaterialTheme.typography.labelLarge) },
                    icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_class_grade")
                )
            }

            when (selectedSection) {
                0 -> StudyMaterialsSection(
                    node = node,
                    onToggleMaterial = onToggleMaterial
                )
                1 -> AssignmentsAndQuizSection(
                    node = node,
                    onToggleAssignment = onToggleAssignment,
                    onSubmitQuizScore = onSubmitQuizScore
                )
                2 -> LocalClassroomGradingSection(
                    node = node,
                    onSaveInClassGrade = onSaveInClassGrade
                )
            }
        }
    }
}

@Composable
private fun StudyMaterialsSection(
    node: LevelNodeState,
    onToggleMaterial: (StudyMaterial) -> Unit
) {
    val level = node.level
    val completedSet = node.progress.completedMaterialSet
    val flippedCards = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(level.materials, key = { it.id }) { material ->
            val isCompleted = completedSet.contains(material.id)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("material_card_${material.id}"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = level.track.primaryColor.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = "${material.category.label} • +${material.xpReward} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = level.track.primaryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        if (isCompleted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Mastered",
                                    tint = QuestEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Done",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = QuestEmerald
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
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

                    Spacer(modifier = Modifier.height(8.dp))

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
                                    color = if (isFlipped) QuestEmerald else level.track.primaryColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { flippedCards[cardKey] = !isFlipped },
                            color = if (isFlipped) {
                                QuestEmerald.copy(alpha = 0.1f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isFlipped) "💡 Meaning:" else "🗣️ Phrase:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isFlipped) QuestEmerald else level.track.primaryColor
                                    )
                                    Text(
                                        text = if (isFlipped) back else front,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Text(
                                    text = "🔄 Flip",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onToggleMaterial(material) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) QuestEmerald else level.track.primaryColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_material_btn_${material.id}")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isCompleted) "Completed" else "Mark Mastered (+${material.xpReward} XP)")
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignmentsAndQuizSection(
    node: LevelNodeState,
    onToggleAssignment: (LevelAssignment) -> Unit,
    onSubmitQuizScore: (Int) -> Unit
) {
    val level = node.level
    val completedAssigns = node.progress.completedAssignmentSet
    val selectedAnswers = remember(level.id) { mutableStateMapOf<String, Int>() }
    var quizSubmitted by remember(level.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                .size(28.dp)
                                .testTag("assignment_checkbox_${assignment.id}")
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = assignment.instructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
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
                        color = QuestEmerald
                    )
                }
            }
        }

        items(level.quizQuestions, key = { it.id }) { q ->
            val chosen = selectedAnswers[q.id]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = q.prompt,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    q.options.forEachIndexed { idx, optText ->
                        val isSelected = chosen == idx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedAnswers[q.id] = idx
                                    quizSubmitted = false
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedAnswers[q.id] = idx
                                    quizSubmitted = false
                                },
                                modifier = Modifier.size(28.dp)
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
                            color = if (isCorrect) QuestEmerald else QuestCoral,
                            modifier = Modifier.padding(top = 4.dp)
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
                    val pct = ((correctCount.toFloat() / level.quizQuestions.size.coerceAtLeast(1)) * 100).toInt()
                    quizSubmitted = true
                    onSubmitQuizScore(pct)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = level.track.primaryColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("submit_quiz_button")
            ) {
                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Check Quiz Answers")
            }
        }
    }
}

@Composable
private fun LocalClassroomGradingSection(
    node: LevelNodeState,
    onSaveInClassGrade: (Int, Int, Int, Int, String, Boolean) -> Unit
) {
    val level = node.level
    val prog = node.progress

    var speaking by remember(level.id) { mutableIntStateOf(if (prog.gradedByTeacher) prog.speakingScore else 85) }
    var listening by remember(level.id) { mutableIntStateOf(if (prog.gradedByTeacher) prog.listeningScore else 85) }
    var reading by remember(level.id) { mutableIntStateOf(if (prog.gradedByTeacher) prog.readingScore else 85) }
    var writing by remember(level.id) { mutableIntStateOf(if (prog.gradedByTeacher) prog.writingScore else 85) }
    var teacherNote by remember(level.id) { mutableStateOf(prog.teacherNote) }
    var autoUnlockNext by remember(level.id) { mutableStateOf(true) }

    val averageScore = ((speaking + listening + reading + writing) / 4.0).toInt()
    val nextLevel = remember(level.orderIndex) {
        CurriculumCatalog.allLevels.find { it.orderIndex == level.orderIndex + 1 }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Compact Unit Badge Banner
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (node.isPassed) QuestGold.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (node.isPassed) QuestGold else level.track.primaryColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = level.badgeEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (node.isPassed) "🏅 Badge Earned!" else "🎯 Badge Goal: 60%+ in Class",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (node.isPassed) Color(0xFFB45309) else level.track.primaryColor
                        )
                        Text(
                            text = level.badgeName,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

        if (node.isPassed && nextLevel != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = QuestEmerald.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Next Level Code (${nextLevel.title}):",
                                style = MaterialTheme.typography.titleMedium,
                                color = QuestEmerald
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = QuestEmerald
                        ) {
                            Text(
                                text = "🔑 ${nextLevel.teacherUnlockCode}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Compact Local Classroom Assessment Sliders
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏫 In-Class Teacher Grade",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Avg: $averageScore%",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    SkillSliderRow("🗣️ Speaking", speaking) { speaking = it }
                    SkillSliderRow("🎧 Listening", listening) { listening = it }
                    SkillSliderRow("📖 Reading", reading) { reading = it }
                    SkillSliderRow("✍️ Writing", writing) { writing = it }

                    OutlinedTextField(
                        value = teacherNote,
                        onValueChange = { teacherNote = it },
                        label = { Text("Teacher Feedback Note") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_feedback_input")
                    )

                    if (nextLevel != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Checkbox(
                                checked = autoUnlockNext,
                                onCheckedChange = { autoUnlockNext = it },
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Auto-unlock ${nextLevel.title} on pass",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onSaveInClassGrade(
                                speaking,
                                listening,
                                reading,
                                writing,
                                teacherNote,
                                autoUnlockNext
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = QuestEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_class_grade_button")
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Grade & Award Badge")
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillSliderRow(
    label: String,
    score: Int,
    onScoreChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$score%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = score.toFloat(),
            onValueChange = { onScoreChange(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.height(26.dp)
        )
    }
}
