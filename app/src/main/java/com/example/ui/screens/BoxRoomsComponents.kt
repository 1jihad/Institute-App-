package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.BoxArchitectureType
import com.example.data.BoxPracticeRoomType
import com.example.data.SkillQuizQuestion
import com.example.data.StudentProfileEntity
import com.example.data.VocabSpreadsheetImporter
import com.example.ui.AudioPronunciationButtons
import com.example.ui.LevelNodeState
import com.example.ui.OfflineSpeechController
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaGold
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaRed
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Accurate On-Device Voice & Pronunciation Evaluator for Skills 2, 4, 6 Speaking Rooms.
 * Evaluates multiple N-best speech recognition hypotheses using token similarity, phonetic normalization,
 * and required keyword coverage.
 */
object VoiceAnswerAccuracyEvaluator {

    data class VoiceEvaluationResult(
        val overallScorePercent: Int,
        val matchedKeywords: List<String>,
        val missingKeywords: List<String>,
        val bestTranscript: String,
        val feedbackMessage: String
    )

    fun evaluateSpokenCandidates(
        candidates: List<String>,
        sampleTargetAnswer: String,
        requiredKeywords: List<String>
    ): VoiceEvaluationResult {
        val cleanedCandidates = candidates.map { it.trim() }.filter { it.isNotBlank() }
        if (cleanedCandidates.isEmpty()) {
            return VoiceEvaluationResult(
                overallScorePercent = 0,
                matchedKeywords = emptyList(),
                missingKeywords = requiredKeywords,
                bestTranscript = "",
                feedbackMessage = "No spoken words detected. Tap the microphone and speak clearly in a full sentence."
            )
        }

        var bestResult: VoiceEvaluationResult? = null
        for (candidate in cleanedCandidates) {
            val evaluated = evaluateSingleTranscript(candidate, sampleTargetAnswer, requiredKeywords)
            if (bestResult == null || evaluated.overallScorePercent > bestResult.overallScorePercent) {
                bestResult = evaluated
            }
        }
        return bestResult!!
    }

    fun evaluateSingleTranscript(
        spokenText: String,
        sampleTargetAnswer: String,
        requiredKeywords: List<String>
    ): VoiceEvaluationResult {
        val spokenWords = tokenize(spokenText)
        val targetWords = tokenize(sampleTargetAnswer)

        if (spokenWords.isEmpty()) {
            return VoiceEvaluationResult(0, emptyList(), requiredKeywords, spokenText, "Please speak a complete sentence.")
        }

        val matchedKw = mutableListOf<String>()
        val missingKw = mutableListOf<String>()

        for (kw in requiredKeywords) {
            val kwTokens = tokenize(kw)
            val isMatched = kwTokens.all { kt ->
                spokenWords.any { sw -> wordSimilarity(sw, kt) >= 0.78f }
            }
            if (isMatched) matchedKw.add(kw) else missingKw.add(kw)
        }

        val keywordRatio = if (requiredKeywords.isEmpty()) 1f else matchedKw.size.toFloat() / requiredKeywords.size.toFloat()

        // Token overlap & fuzzy pronunciation similarity against sample target answer
        var fuzzyMatches = 0f
        for (tw in targetWords) {
            val bestWordMatch = spokenWords.maxOfOrNull { sw -> wordSimilarity(sw, tw) } ?: 0f
            if (bestWordMatch >= 0.75f) {
                fuzzyMatches += bestWordMatch
            }
        }
        val targetCoverage = if (targetWords.isEmpty()) 1f else (fuzzyMatches / targetWords.size.toFloat()).coerceIn(0f, 1f)
        val lengthCompleteness = (spokenWords.size.toFloat() / max(4, targetWords.size - 2).toFloat()).coerceIn(0f, 1f)

        val rawScore = ((keywordRatio * 55f) + (targetCoverage * 30f) + (lengthCompleteness * 15f)).toInt().coerceIn(0, 100)
        val finalScore = if (matchedKw.size == requiredKeywords.size && spokenWords.size >= 4) {
            max(rawScore, 88)
        } else {
            rawScore
        }

        val feedback = when {
            finalScore >= 85 -> "🌟 Excellent pronunciation & sentence structure! ($finalScore%)"
            finalScore >= 65 -> "✅ Good spoken response ($finalScore%)! You met the 2-star fluency standard."
            else -> "💡 Try speaking a full sentence including: ${missingKw.joinToString(", ")}."
        }

        return VoiceEvaluationResult(
            overallScorePercent = finalScore,
            matchedKeywords = matchedKw,
            missingKeywords = missingKw,
            bestTranscript = spokenText,
            feedbackMessage = feedback
        )
    }

    private fun tokenize(text: String): List<String> {
        return text.lowercase(Locale.US)
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
    }

    private fun phoneticNormalize(word: String): String {
        return word.lowercase(Locale.US)
            .replace("ph", "f")
            .replace("ght", "t")
            .replace("ck", "k")
            .replace("kn", "n")
            .replace("wr", "r")
    }

    fun wordSimilarity(a: String, b: String): Float {
        if (a.equals(b, ignoreCase = true)) return 1f
        val pa = phoneticNormalize(a)
        val pb = phoneticNormalize(b)
        if (pa == pb) return 0.95f
        if (pa.startsWith(pb) || pb.startsWith(pa)) {
            val minLen = min(pa.length, pb.length)
            if (minLen >= 4) return 0.86f
        }
        val dist = levenshtein(pa, pb)
        val maxLen = max(pa.length, pb.length).coerceAtLeast(1)
        return (1f - (dist.toFloat() / maxLen.toFloat())).coerceIn(0f, 1f)
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..s2.length) {
                val temp = dp[j]
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[j] = min(min(dp[j] + 1, dp[j - 1] + 1), prev + cost)
                prev = temp
            }
        }
        return dp[s2.length]
    }
}

/**
 * First-Time Student Personal & Academic Onboarding Dialog.
 * Guides the student upon first launch to enter their personal/academic details and start at Box 1 (or enter an Admin Code).
 */
@Composable
fun StudentAcademicOnboardingDialog(
    profile: StudentProfileEntity,
    onCompleteOnboarding: (String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember(profile.studentName) {
        mutableStateOf(if (profile.studentName == "Star Explorer") "" else profile.studentName)
    }
    var selectedAvatar by remember(profile.avatarEmoji) { mutableStateOf(profile.avatarEmoji) }
    var selectedAcademicStage by remember(profile.academicStage) { mutableStateOf(profile.academicStage) }
    var selectedGoal by remember(profile.learningGoal) { mutableStateOf(profile.learningGoal) }
    var adminCodeInput by remember { mutableStateOf("") }

    val avatars = listOf("🦊", "🦁", "🐼", "🦄", "🐯", "🦉", "🐬", "🚀")
    val academicStages = listOf(
        "High School Student",
        "University Student",
        "Fresh Graduate",
        "Working Professional"
    )
    val learningGoals = listOf(
        "Master All 21 Boxes & Fluency",
        "Academic Reading & Writing (RW)",
        "Listening & Speaking Confidence",
        "Career & International Exams"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = EtaNavy.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "🎓 EXCEL ETA • STUDENT ONBOARDING",
                        style = MaterialTheme.typography.labelLarge,
                        color = EtaNavy,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Welcome! Set Up Your Academic Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "التسجيل والتأسيس الأكاديمي للطالب (21 مستوياً)",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaRed,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Student Full Name (الاسم الكامل)") },
                    placeholder = { Text("e.g., Ahmed Al-Saady") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input")
                )

                Text(
                    text = "Choose Your Avatar:",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaNavy
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    avatars.forEach { emoji ->
                        val isSelected = emoji == selectedAvatar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EtaNavy else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedAvatar = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                Text(
                    text = "Academic Background (المرحلة الدراسية):",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaNavy
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    academicStages.forEach { stage ->
                        val selected = stage == selectedAcademicStage
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) EtaBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, if (selected) EtaBlue else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedAcademicStage = stage }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selected, onClick = { selectedAcademicStage = stage })
                                Text(text = stage, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                Text(
                    text = "Target Learning Goal (الهدف التعليمي):",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaNavy
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    learningGoals.forEach { goal ->
                        val selected = goal == selectedGoal
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) EtaRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, if (selected) EtaRed else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedGoal = goal }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selected, onClick = { selectedGoal = goal })
                                Text(text = goal, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = adminCodeInput,
                    onValueChange = { adminCodeInput = it.uppercase(Locale.US) },
                    label = { Text("Optional Admin Box Code (كود فتح مستوى مباشر)") },
                    placeholder = { Text("Leave blank to start at Box 1 (Essence)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_admin_code_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCompleteOnboarding(
                        nameInput.ifBlank { "Star Explorer" },
                        selectedAvatar,
                        selectedAcademicStage,
                        selectedGoal,
                        adminCodeInput
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complete_onboarding_button")
            ) {
                Text("Start My 21-Box Learning Path")
            }
        }
    )
}

/**
 * Interactive Rooms container for any of the 21 Boxes:
 * - General Boxes (4 Rooms): 1. Vocabulary (.xlsx/.csv) | 2. Grammar | 3. Vocab Practice | 4. Grammar Practice
 * - Skills RW Boxes (2 Rooms): 1. Grammar Explanation | 2. Writing & Sentence Construction
 * - Skills L&S Boxes (2 Rooms): 1. Listening & Dictation (.xlsx/.csv) | 2. Speaking & Voice Interaction
 */
@Composable
fun BoxRoomsInteractiveView(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onImportVocabStream: (InputStream, String, (Boolean, String) -> Unit) -> Unit,
    onImportVocabCsvText: (String, String, (Boolean, String) -> Unit) -> Unit,
    onAddVocabWord: (String, String, String, String) -> Unit,
    onDeleteVocabWord: (String) -> Unit,
    onMarkStudyRoomDone: (Boolean) -> Unit,
    onSubmitRoomPracticeScore: (BoxPracticeRoomType, Int, List<String>) -> Unit,
    initialRoomIndex: Int = 0
) {
    val level = node.level
    var activeRoomIndex by remember(level.id, initialRoomIndex) { mutableIntStateOf(initialRoomIndex) }

    val roomTabs = remember(level.boxType) {
        when (level.boxType) {
            BoxArchitectureType.GENERAL_4_ROOMS -> listOf(
                "1. Vocabulary" to "المفردات",
                "2. Grammar" to "شرح القواعد",
                "3. Vocab Tests" to "تطبيق المفردات",
                "4. Grammar Tests" to "تطبيقات القواعد"
            )
            BoxArchitectureType.SKILLS_RW_2_ROOMS -> listOf(
                "1. Grammar Rules" to "غرفة شرح القواعد",
                "2. Writing & Sentences" to "تطبيقات الكتابة والتركيب",
                "3. Vocabulary Bank" to "بنك المفردات"
            )
            BoxArchitectureType.SKILLS_LS_2_ROOMS -> listOf(
                "1. Listening & Dictation" to "الاستماع والمفردات",
                "2. Speaking & Voice" to "التحدث والتفاعل الصوتي",
                "3. Vocabulary Bank" to "بنك المفردات"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sub-Folder / Room Switcher Pills inside the Course Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            roomTabs.forEachIndexed { idx, (enTitle, arTitle) ->
                val isSelected = activeRoomIndex == idx
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) level.boxType.badgeColor else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) level.boxType.badgeColor else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { activeRoomIndex = idx }
                        .testTag("box_room_tab_$idx")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📁 $enTitle",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = arTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Render active room folder content based on BoxArchitectureType
        when (level.boxType) {
            BoxArchitectureType.GENERAL_4_ROOMS -> {
                when (activeRoomIndex.coerceIn(0, 3)) {
                    0 -> InAppVocabularyBankRoom(
                        node = node,
                        speechController = speechController,
                        onImportVocabStream = onImportVocabStream,
                        onImportVocabCsvText = onImportVocabCsvText,
                        onAddVocabWord = onAddVocabWord,
                        onDeleteVocabWord = onDeleteVocabWord,
                        onMarkRoomDone = { onMarkStudyRoomDone(true) },
                        onGoToPractice = { activeRoomIndex = 2 }
                    )
                    1 -> GrammarExplanationRoomView(
                        node = node,
                        speechController = speechController,
                        onMarkRoomDone = { onMarkStudyRoomDone(false) },
                        onGoToPractice = { activeRoomIndex = 3 }
                    )
                    2 -> InteractivePracticeQuizRoom(
                        roomTitle = "Folder • Vocabulary Practice & Tests (تطبيق المفردات)",
                        roomSubtitle = "Includes your Box Vocabulary Bank & any uploaded Teacher .xlsx / .csv words!",
                        questions = node.effectiveVocabPracticeQuestions,
                        bestScore = node.progress.vocabPracticeBestScore,
                        accentColor = EtaNavy,
                        onSubmitScore = { pct, wrongIds ->
                            onSubmitRoomPracticeScore(BoxPracticeRoomType.VOCAB_PRACTICE, pct, wrongIds)
                        }
                    )
                    3 -> InteractivePracticeQuizRoom(
                        roomTitle = "Folder • Grammar Practice & Tests (تطبيقات القواعد)",
                        roomSubtitle = "Apply ${level.title} grammar rules to earn up to 3 Stars (2/3 unlocks next Box)!",
                        questions = level.grammarPracticeQuestions,
                        bestScore = node.progress.grammarPracticeBestScore,
                        accentColor = EtaRed,
                        onSubmitScore = { pct, wrongIds ->
                            onSubmitRoomPracticeScore(BoxPracticeRoomType.GRAMMAR_PRACTICE, pct, wrongIds)
                        }
                    )
                }
            }

            BoxArchitectureType.SKILLS_RW_2_ROOMS -> {
                when (activeRoomIndex.coerceIn(0, 2)) {
                    0 -> GrammarExplanationRoomView(
                        node = node,
                        speechController = speechController,
                        onMarkRoomDone = { onMarkStudyRoomDone(false) },
                        onGoToPractice = { activeRoomIndex = 1 }
                    )
                    1 -> SkillsRwSentenceConstructionRoom(
                        node = node,
                        speechController = speechController,
                        onSubmitScore = { pct ->
                            onSubmitRoomPracticeScore(BoxPracticeRoomType.RW_SENTENCE_PRACTICE, pct, emptyList())
                        }
                    )
                    2 -> InAppVocabularyBankRoom(
                        node = node,
                        speechController = speechController,
                        onImportVocabStream = onImportVocabStream,
                        onImportVocabCsvText = onImportVocabCsvText,
                        onAddVocabWord = onAddVocabWord,
                        onDeleteVocabWord = onDeleteVocabWord,
                        onMarkRoomDone = { onMarkStudyRoomDone(true) },
                        onGoToPractice = { activeRoomIndex = 1 }
                    )
                }
            }

            BoxArchitectureType.SKILLS_LS_2_ROOMS -> {
                when (activeRoomIndex.coerceIn(0, 2)) {
                    0 -> SkillsListeningAndVocabDictationRoom(
                        node = node,
                        speechController = speechController,
                        onImportVocabStream = onImportVocabStream,
                        onImportVocabCsvText = onImportVocabCsvText,
                        onAddVocabWord = onAddVocabWord,
                        onSubmitListeningScore = { pct ->
                            onSubmitRoomPracticeScore(BoxPracticeRoomType.LISTENING_DICTATION, pct, emptyList())
                        }
                    )
                    1 -> SkillsSpeakingAndVoiceInteractionRoom(
                        node = node,
                        speechController = speechController,
                        onSubmitSpeakingScore = { pct ->
                            onSubmitRoomPracticeScore(BoxPracticeRoomType.SPEAKING_VOICE_QA, pct, emptyList())
                        }
                    )
                    2 -> InAppVocabularyBankRoom(
                        node = node,
                        speechController = speechController,
                        onImportVocabStream = onImportVocabStream,
                        onImportVocabCsvText = onImportVocabCsvText,
                        onAddVocabWord = onAddVocabWord,
                        onDeleteVocabWord = onDeleteVocabWord,
                        onMarkRoomDone = { onMarkStudyRoomDone(true) },
                        onGoToPractice = { activeRoomIndex = 0 }
                    )
                }
            }
        }
    }
}

/**
 * Room 1 (General Boxes): 100% In-App Vocabulary Bank with Teacher .xlsx & .csv Sheet Upload.
 */
@Composable
private fun InAppVocabularyBankRoom(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onImportVocabStream: (InputStream, String, (Boolean, String) -> Unit) -> Unit,
    onImportVocabCsvText: (String, String, (Boolean, String) -> Unit) -> Unit,
    onAddVocabWord: (String, String, String, String) -> Unit,
    onDeleteVocabWord: (String) -> Unit,
    onMarkRoomDone: () -> Unit,
    onGoToPractice: () -> Unit
) {
    val context = LocalContext.current
    val allWords = node.allVocabularyItems
    var searchQuery by remember { mutableStateOf("") }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showAddWordForm by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }
    var isUploadError by remember { mutableStateOf(false) }

    var newWord by remember { mutableStateOf("") }
    var newMeaning by remember { mutableStateOf("") }
    var newArabic by remember { mutableStateOf("") }
    var newExample by remember { mutableStateOf("") }

    val spreadsheetPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val mimeOrPath = context.contentResolver.getType(uri) ?: uri.lastPathSegment ?: "vocab.xlsx"
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()
            if (bytes != null && bytes.isNotEmpty()) {
                onImportVocabStream(ByteArrayInputStream(bytes), mimeOrPath) { ok, msg ->
                    isUploadError = !ok
                    uploadStatusMessage = msg
                }
            } else {
                isUploadError = true
                uploadStatusMessage = "Could not read selected .xlsx or .csv file."
            }
        }
    }

    val filteredWords = remember(allWords, searchQuery) {
        if (searchQuery.isBlank()) allWords
        else allWords.filter {
            it.word.contains(searchQuery, ignoreCase = true) ||
                it.meaning.contains(searchQuery, ignoreCase = true) ||
                it.arabicHint.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Header & Teacher .XLSX / .CSV Upload Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📚 Room 1 • In-App Vocabulary Bank (المفردات)",
                            style = MaterialTheme.typography.titleMedium,
                            color = EtaNavy,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${allWords.size} words in bank (${node.uploadedVocabulary.size} uploaded from .xlsx/.csv)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showUploadDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("open_vocab_sheet_upload_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload .XLSX / .CSV", style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (uploadStatusMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isUploadError) EtaRed.copy(alpha = 0.12f) else Color(0xFF10B981).copy(alpha = 0.14f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uploadStatusMessage ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isUploadError) EtaRed else Color(0xFF047857),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search vocabulary inside app...") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { showAddWordForm = !showAddWordForm },
                        modifier = Modifier.testTag("toggle_add_vocab_word_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Word")
                    }
                }

                AnimatedVisibility(visible = showAddWordForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newWord,
                            onValueChange = { newWord = it },
                            label = { Text("English Word / Phrase") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newMeaning,
                            onValueChange = { newMeaning = it },
                            label = { Text("English Definition") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newArabic,
                            onValueChange = { newArabic = it },
                            label = { Text("Arabic Meaning (المعنى بالعربية)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newExample,
                            onValueChange = { newExample = it },
                            label = { Text("Example Sentence") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (newWord.isNotBlank() && newMeaning.isNotBlank()) {
                                    onAddVocabWord(newWord, newMeaning, newArabic, newExample)
                                    newWord = ""
                                    newMeaning = ""
                                    newArabic = ""
                                    newExample = ""
                                    showAddWordForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Word to In-App Vocabulary Bank")
                        }
                    }
                }
            }
        }

        // Vocabulary Cards List
        filteredWords.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.word,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EtaNavy,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EtaBlue.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = item.sourceLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = EtaBlue,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (item.arabicHint.isNotBlank()) {
                                Text(
                                    text = item.arabicHint,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EtaRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AudioPronunciationButtons(
                                textToSpeak = "${item.word}. ${item.exampleSentence}",
                                speechController = speechController
                            )
                            if (item.id.startsWith("vocab_")) {
                                IconButton(
                                    onClick = { onDeleteVocabWord(item.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Word",
                                        tint = EtaRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.exampleSentence.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💬 \"${item.exampleSentence}\"",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Complete Room 1 & Move to Practice Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onMarkRoomDone,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (node.progress.vocabRoomCompleted) "Room 1 Completed" else "Complete Room 1 (+20 XP)")
            }
            Button(
                onClick = onGoToPractice,
                colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                modifier = Modifier.weight(1f)
            ) {
                Text("Go to Room 3: Vocab Practice")
            }
        }
    }

    if (showUploadDialog) {
        TeacherVocabSheetUploadDialog(
            boxTitle = node.level.title,
            onDismiss = { showUploadDialog = false },
            onPickSystemFile = {
                spreadsheetPickerLauncher.launch(
                    arrayOf(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.ms-excel",
                        "text/csv",
                        "text/comma-separated-values",
                        "text/plain",
                        "*/*"
                    )
                )
                showUploadDialog = false
            },
            onImportCsvText = { csvText, sourceLabel ->
                onImportVocabCsvText(csvText, sourceLabel) { ok, msg ->
                    isUploadError = !ok
                    uploadStatusMessage = msg
                    if (ok) showUploadDialog = false
                }
            },
            onImportSampleXlsx = {
                val sampleRows = listOf(
                    listOf("Word", "Meaning", "Arabic", "Example Sentence"),
                    listOf("Fluent speaker", "Able to speak smoothly and accurately", "متحدث طليق", "Daily practice makes you a fluent speaker."),
                    listOf("Mastery", "Comprehensive knowledge or skill in a subject", "إتقان تام", "Earning three stars shows complete mastery of the box."),
                    listOf("Context clue", "Hints in a sentence that help you understand a new word", "قرينة سياقية", "Use context clues when reading a new passage.")
                )
                val xlsxBytes = VocabSpreadsheetImporter.createSampleXlsxBytes(sampleRows)
                onImportVocabStream(ByteArrayInputStream(xlsxBytes), "Teacher_Vocab_${node.level.title}.xlsx") { ok, msg ->
                    isUploadError = !ok
                    uploadStatusMessage = msg
                    if (ok) showUploadDialog = false
                }
            }
        )
    }
}

@Composable
private fun TeacherVocabSheetUploadDialog(
    boxTitle: String,
    onDismiss: () -> Unit,
    onPickSystemFile: () -> Unit,
    onImportCsvText: (String, String) -> Unit,
    onImportSampleXlsx: () -> Unit
) {
    var csvPasteInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
                Text(
                    text = "📥 Upload Teacher Vocabulary (.XLSX / .CSV)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = EtaNavy
                )
                Text(
                    text = "Import into $boxTitle In-App Vocabulary Bank",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaRed
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select an Excel (.xlsx) or CSV (.csv) vocabulary sheet prepared by your teacher. All words remain 100% inside the app and automatically generate practice questions in Room 3.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = onPickSystemFile,
                    colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_xlsx_csv_file_button")
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select .XLSX or .CSV File from Phone")
                }

                OutlinedButton(
                    onClick = onImportSampleXlsx,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("load_sample_xlsx_button")
                ) {
                    Text("Test with Sample Teacher .XLSX Workbook")
                }

                HorizontalDivider()

                Text(
                    text = "Or Paste CSV Rows (Word, Meaning, Arabic, Example):",
                    style = MaterialTheme.typography.labelLarge,
                    color = EtaNavy
                )
                OutlinedTextField(
                    value = csvPasteInput,
                    onValueChange = { csvPasteInput = it },
                    placeholder = {
                        Text("Word,Meaning,Arabic,Example\nResilient,Able to recover quickly,مرن / صامد,Students are resilient learners.")
                    },
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paste_csv_vocab_input")
                )

                Button(
                    onClick = {
                        if (csvPasteInput.isNotBlank()) {
                            onImportCsvText(csvPasteInput, "Teacher CSV Sheet")
                        }
                    },
                    enabled = csvPasteInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_pasted_csv_button")
                ) {
                    Text("Import CSV Rows into Vocabulary Bank")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Room 2 (General Boxes) & Room 1 (Skills 1, 3, 5 RW): Grammar Explanation Room (غرفة شرح القواعد).
 */
@Composable
private fun GrammarExplanationRoomView(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onMarkRoomDone: () -> Unit,
    onGoToPractice: () -> Unit
) {
    val grammar = node.level.grammarContent

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📐 ${grammar.ruleTitle} (شرح القواعد)",
                            style = MaterialTheme.typography.titleMedium,
                            color = EtaNavy,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Study the rule and examples before entering the Practice Room",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AudioPronunciationButtons(
                        textToSpeak = "${grammar.ruleTitle}. ${grammar.explanationBody}",
                        speechController = speechController
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "GRAMMAR FORMULA",
                            style = MaterialTheme.typography.labelMedium,
                            color = EtaGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = grammar.formulaPattern,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = grammar.explanationBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Nominal vs. Verbal Sentence Breakdown (highlighted in Skills 1, 3, 5 RW)
        if (grammar.nominalVsVerbalNotes.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EtaRed.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, EtaRed.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "✍️ Nominal & Verbal Sentence Architecture (الجمل الاسمية والفعلية)",
                        style = MaterialTheme.typography.titleMedium,
                        color = EtaRed,
                        fontWeight = FontWeight.Bold
                    )
                    grammar.nominalVsVerbalNotes.forEach { (heading, detail) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = heading,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EtaNavy,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = detail,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Examples Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "✅ Affirmative Examples",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF047857),
                    fontWeight = FontWeight.Bold
                )
                grammar.affirmativeExamples.forEach { ex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "• $ex", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        AudioPronunciationButtons(textToSpeak = ex, speechController = speechController)
                    }
                }

                if (grammar.negativeExamples.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "🚫 Negative Examples",
                        style = MaterialTheme.typography.labelLarge,
                        color = EtaRed,
                        fontWeight = FontWeight.Bold
                    )
                    grammar.negativeExamples.forEach { ex ->
                        Text(text = "• $ex", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (grammar.questionExamples.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "❓ Question Examples",
                        style = MaterialTheme.typography.labelLarge,
                        color = EtaBlue,
                        fontWeight = FontWeight.Bold
                    )
                    grammar.questionExamples.forEach { ex ->
                        Text(text = "• $ex", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onMarkRoomDone,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (node.progress.grammarRoomCompleted) "Grammar Studied" else "Mark Studied (+20 XP)")
            }
            Button(
                onClick = onGoToPractice,
                colors = ButtonDefaults.buttonColors(containerColor = EtaRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Go to Grammar Practice")
            }
        }
    }
}

/**
 * Interactive Practice Quiz Room (Room 3 Vocab Practice & Room 4 Grammar Practice in General Boxes).
 */
@Composable
private fun InteractivePracticeQuizRoom(
    roomTitle: String,
    roomSubtitle: String,
    questions: List<SkillQuizQuestion>,
    bestScore: Int,
    accentColor: Color,
    onSubmitScore: (Int, List<String>) -> Unit
) {
    val selectedAnswers = remember(questions) { mutableStateMapOf<String, Int>() }
    var submittedScore by remember(questions) { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = roomTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = accentColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = roomSubtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accentColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Best: $bestScore%",
                            style = MaterialTheme.typography.labelLarge,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        questions.forEachIndexed { idx, q ->
            val chosen = selectedAnswers[q.id]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Q${idx + 1}. ${q.prompt}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    q.options.forEachIndexed { optIdx, optText ->
                        val isChosen = chosen == optIdx
                        val isCorrect = optIdx == q.correctIndex
                        val rowBg = when {
                            submittedScore != null && isCorrect -> Color(0xFF10B981).copy(alpha = 0.16f)
                            submittedScore != null && isChosen && !isCorrect -> EtaRed.copy(alpha = 0.15f)
                            isChosen -> accentColor.copy(alpha = 0.12f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = rowBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedAnswers[q.id] = optIdx
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isChosen,
                                    onClick = { selectedAnswers[q.id] = optIdx }
                                )
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    if (submittedScore != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 ${q.explanation}",
                            style = MaterialTheme.typography.labelMedium,
                            color = EtaNavy
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val total = questions.size.coerceAtLeast(1)
                var correct = 0
                val wrongIds = mutableListOf<String>()
                questions.forEach { q ->
                    if (selectedAnswers[q.id] == q.correctIndex) {
                        correct++
                    } else {
                        wrongIds.add(q.id)
                    }
                }
                val pct = ((correct * 100f) / total.toFloat()).toInt()
                submittedScore = pct
                onSubmitScore(pct, wrongIds)
            },
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_practice_room_button")
        ) {
            Text("Submit Practice & Calculate Box Stars (⭐⭐⭐)")
        }
    }
}

/**
 * Room 2 of Skills 1, 3, 5 RW: Writing & Sentence Construction Practice (غرفة تطبيقات الكتابة والتركيب).
 * Includes interactive word-tile sentence reordering for Nominal/Verbal sentences + Past Continuous & clause drills.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsRwSentenceConstructionRoom(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onSubmitScore: (Int) -> Unit
) {
    val exercises = node.level.rwSentenceExercises
    val grammarQuestions = node.level.grammarPracticeQuestions
    val selectedWordsByEx = remember(node.level.id) {
        mutableStateMapOf<String, List<String>>()
    }
    val selectedQuizAnswers = remember(node.level.id) {
        mutableStateMapOf<String, Int>()
    }
    var evaluationBanner by remember(node.level.id) { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "✍️ Room 2 • Writing & Sentence Construction (تطبيقات الكتابة والتركيب)",
                    style = MaterialTheme.typography.titleMedium,
                    color = EtaRed,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Tap the word tiles in order to form Nominal & Verbal sentences and apply ${node.level.title} grammar rules.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        exercises.forEachIndexed { idx, ex ->
            val currentChosen = selectedWordsByEx[ex.id] ?: emptyList()
            val remainingWords = remember(ex.scrambledWords, currentChosen) {
                val mutable = ex.scrambledWords.toMutableList()
                currentChosen.forEach { mutable.remove(it) }
                mutable
            }
            val builtSentence = currentChosen.joinToString(" ")
            val isExactMatch = builtSentence.equals(ex.targetSentence, ignoreCase = true)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtaRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Exercise ${idx + 1} • ${ex.sentenceTypeTag}",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Row {
                            AudioPronunciationButtons(
                                textToSpeak = ex.targetSentence,
                                speechController = speechController
                            )
                            IconButton(
                                onClick = { selectedWordsByEx[ex.id] = emptyList() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset Sentence", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Text(
                        text = ex.instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Constructed Sentence Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isExactMatch) Color(0xFF10B981).copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isExactMatch) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (currentChosen.isEmpty()) {
                                Text(
                                    text = "Tap word tiles below to build the sentence...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    currentChosen.forEachIndexed { wIdx, word ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EtaNavy,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    val updated = currentChosen.toMutableList()
                                                    updated.removeAt(wIdx)
                                                    selectedWordsByEx[ex.id] = updated
                                                }
                                        ) {
                                            Text(
                                                text = word,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Available Scrambled Word Tiles
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        remainingWords.forEach { word ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EtaBlue.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, EtaBlue.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedWordsByEx[ex.id] = currentChosen + word
                                    }
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = EtaNavy,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    if (isExactMatch) {
                        Text(
                            text = "✅ Correct Sentence: \"${ex.targetSentence}.\"",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "💡 Hint: ${ex.grammarHint}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Additional Grammar Check Questions for RW Box
        if (grammarQuestions.isNotEmpty()) {
            grammarQuestions.forEachIndexed { qIdx, q ->
                val chosen = selectedQuizAnswers[q.id]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Grammar Check ${qIdx + 1}: ${q.prompt}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        q.options.forEachIndexed { optIdx, opt ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedQuizAnswers[q.id] = optIdx }
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = chosen == optIdx,
                                    onClick = { selectedQuizAnswers[q.id] = optIdx }
                                )
                                Text(text = opt, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        if (evaluationBanner != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF10B981).copy(alpha = 0.16f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = evaluationBanner ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF047857),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Button(
            onClick = {
                val totalItems = (exercises.size + grammarQuestions.size).coerceAtLeast(1)
                var correctCount = 0
                exercises.forEach { ex ->
                    val built = (selectedWordsByEx[ex.id] ?: emptyList()).joinToString(" ")
                    if (built.equals(ex.targetSentence, ignoreCase = true)) {
                        correctCount++
                    }
                }
                grammarQuestions.forEach { q ->
                    if (selectedQuizAnswers[q.id] == q.correctIndex) {
                        correctCount++
                    }
                }
                val pct = ((correctCount * 100f) / totalItems.toFloat()).toInt()
                evaluationBanner = "Scored $pct% ($correctCount/$totalItems correct) in Writing & Sentence Construction!"
                onSubmitScore(pct)
            },
            colors = ButtonDefaults.buttonColors(containerColor = EtaRed),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_rw_sentence_room_button")
        ) {
            Text("Evaluate Sentences & Award Box Stars (⭐⭐⭐)")
        }
    }
}

/**
 * Room 1 of Skills 2, 4, 6 (Listening & Vocabulary Dictation Room - غرفة الاستماع والتطبيقي المفرداتي):
 * Real-time dictation typing as the student listens to audio texts + adding new vocabulary words to the box's bank.
 */
@Composable
private fun SkillsListeningAndVocabDictationRoom(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onImportVocabStream: (InputStream, String, (Boolean, String) -> Unit) -> Unit,
    onImportVocabCsvText: (String, String, (Boolean, String) -> Unit) -> Unit,
    onAddVocabWord: (String, String, String, String) -> Unit,
    onSubmitListeningScore: (Int) -> Unit
) {
    val exercises = node.level.listeningExercises
    val typedDictationMap = remember(node.level.id) { mutableStateMapOf<String, String>() }
    var quickWord by remember { mutableStateOf("") }
    var quickMeaning by remember { mutableStateOf("") }
    var quickArabic by remember { mutableStateOf("") }
    var showUploadDialog by remember { mutableStateOf(false) }
    var scoreMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val spreadsheetPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val mimeOrPath = context.contentResolver.getType(uri) ?: uri.lastPathSegment ?: "vocab.xlsx"
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()
            if (bytes != null && bytes.isNotEmpty()) {
                onImportVocabStream(ByteArrayInputStream(bytes), mimeOrPath) { _, msg ->
                    scoreMessage = msg
                }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🎧 Room 1 • Listening & Vocabulary Dictation",
                            style = MaterialTheme.typography.titleMedium,
                            color = EtaBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "غرفة الاستماع والتطبيقي المفرداتي: استمع واكتب فوراً ما تسمعه وأضف مفردات جديدة",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { showUploadDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(".XLSX/.CSV", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        exercises.forEachIndexed { idx, ex ->
            val typed = typedDictationMap[ex.id] ?: ""
            val evalResult = remember(typed, ex.audioTranscript) {
                if (typed.isBlank()) 0
                else VoiceAnswerAccuracyEvaluator.evaluateSingleTranscript(
                    spokenText = typed,
                    sampleTargetAnswer = ex.audioTranscript,
                    requiredKeywords = ex.keyWordsToExtract.map { it.first }
                ).overallScorePercent
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ex.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ex.hintText,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AudioPronunciationButtons(
                            textToSpeak = ex.audioTranscript,
                            speechController = speechController
                        )
                    }

                    OutlinedTextField(
                        value = typed,
                        onValueChange = { typedDictationMap[ex.id] = it },
                        label = { Text("Type what you hear in real time (اكتب ما تسمعه فوراً)...") },
                        singleLine = false,
                        minLines = 2,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("listening_dictation_input_$idx")
                    )

                    if (typed.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Live Dictation Accuracy: $evalResult%",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (evalResult >= 75) Color(0xFF047857) else EtaBlue,
                                fontWeight = FontWeight.Bold
                            )
                            LinearProgressIndicator(
                                progress = { evalResult / 100f },
                                color = if (evalResult >= 75) Color(0xFF10B981) else EtaBlue,
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }

                    // Key Vocabulary from this Listening Passage (1-tap Add to Vocabulary Bank)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Key Vocab:",
                            style = MaterialTheme.typography.labelMedium,
                            color = EtaNavy,
                            fontWeight = FontWeight.Bold
                        )
                        ex.keyWordsToExtract.forEach { (word, arMeaning) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EtaBlue.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onAddVocabWord(word, "Key listening vocabulary from ${ex.title}", arMeaning, ex.audioTranscript)
                                    }
                            ) {
                                Text(
                                    text = "+ $word ($arMeaning)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EtaNavy,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add New Vocabulary Discovered While Listening
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EtaNavy.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "➕ Add New Vocabulary From Listening (إضافة مفردات جديدة)",
                    style = MaterialTheme.typography.titleMedium,
                    color = EtaNavy,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = quickWord,
                        onValueChange = { quickWord = it },
                        label = { Text("New Word") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = quickArabic,
                        onValueChange = { quickArabic = it },
                        label = { Text("Arabic / Meaning") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = quickMeaning,
                    onValueChange = { quickMeaning = it },
                    label = { Text("Definition or Sentence Heard") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (quickWord.isNotBlank()) {
                            onAddVocabWord(
                                quickWord,
                                quickMeaning.ifBlank { "Captured during Listening Room" },
                                quickArabic,
                                ""
                            )
                            quickWord = ""
                            quickMeaning = ""
                            quickArabic = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to Box Vocabulary Bank (+15 XP)")
                }
            }
        }

        if (scoreMessage != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = scoreMessage ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF047857),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Button(
            onClick = {
                val scores = exercises.map { ex ->
                    val typed = typedDictationMap[ex.id] ?: ""
                    VoiceAnswerAccuracyEvaluator.evaluateSingleTranscript(
                        spokenText = typed,
                        sampleTargetAnswer = ex.audioTranscript,
                        requiredKeywords = ex.keyWordsToExtract.map { it.first }
                    ).overallScorePercent
                }
                val avg = if (scores.isEmpty()) 0 else scores.sum() / scores.size
                scoreMessage = "Listening Dictation Score: $avg%!"
                onSubmitListeningScore(avg)
            },
            colors = ButtonDefaults.buttonColors(containerColor = EtaBlue),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_listening_dictation_button")
        ) {
            Text("Submit Dictation & Update Box Stars (⭐⭐⭐)")
        }
    }

    if (showUploadDialog) {
        TeacherVocabSheetUploadDialog(
            boxTitle = node.level.title,
            onDismiss = { showUploadDialog = false },
            onPickSystemFile = {
                spreadsheetPickerLauncher.launch(arrayOf("*/*"))
                showUploadDialog = false
            },
            onImportCsvText = { csv, label ->
                onImportVocabCsvText(csv, label) { _, msg ->
                    scoreMessage = msg
                    showUploadDialog = false
                }
            },
            onImportSampleXlsx = {
                val sampleRows = listOf(
                    listOf("Word", "Meaning", "Arabic", "Example Sentence"),
                    listOf("Pronunciation", "The way in which a word is spoken", "النطق الصوتي", "Clear pronunciation helps listeners understand you."),
                    listOf("Intonation", "The rise and fall of the voice in speaking", "نبرة الصوت", "Use rising intonation for yes/no questions.")
                )
                val xlsxBytes = VocabSpreadsheetImporter.createSampleXlsxBytes(sampleRows)
                onImportVocabStream(ByteArrayInputStream(xlsxBytes), "Listening_Vocab_${node.level.title}.xlsx") { _, msg ->
                    scoreMessage = msg
                    showUploadDialog = false
                }
            }
        )
    }
}

/**
 * Room 2 of Skills 2, 4, 6 (Speaking & Voice Interaction Room - غرفة التحدث والتفاعل الصوتي):
 * Plays spoken questions, records student voice answers via On-Device Android SpeechRecognizer,
 * and runs multi-pass accuracy & pronunciation evaluation.
 */
@Composable
private fun SkillsSpeakingAndVoiceInteractionRoom(
    node: LevelNodeState,
    speechController: OfflineSpeechController,
    onSubmitSpeakingScore: (Int) -> Unit
) {
    val context = LocalContext.current
    val prompts = node.level.speakingPrompts
    val spokenTranscripts = remember(node.level.id) { mutableStateMapOf<String, String>() }
    val evaluationResults = remember(node.level.id) {
        mutableStateMapOf<String, VoiceAnswerAccuracyEvaluator.VoiceEvaluationResult>()
    }
    var activeListeningPromptId by remember { mutableStateOf<String?>(null) }
    var recognizerStatus by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember(context) {
        runCatching {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                SpeechRecognizer.createSpeechRecognizer(context)
            } else null
        }.getOrNull()
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            runCatching { speechRecognizer?.destroy() }
        }
    }

    val startListeningForPrompt: (String, String, List<String>) -> Unit = { promptId, sampleTarget, requiredKw ->
        if (speechRecognizer == null) {
            recognizerStatus = "On-device microphone recognizer unavailable in this environment. Use the Voice Answer field below to test accuracy grading."
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    activeListeningPromptId = promptId
                    recognizerStatus = "🎙️ Listening now... Speak your English answer clearly!"
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    activeListeningPromptId = null
                }
                override fun onError(error: Int) {
                    activeListeningPromptId = null
                    recognizerStatus = "Microphone finished. You can tap 'Speak Answer' again or refine your spoken transcript below."
                }
                override fun onResults(results: Bundle?) {
                    activeListeningPromptId = null
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.toList().orEmpty()
                    if (matches.isNotEmpty()) {
                        val eval = VoiceAnswerAccuracyEvaluator.evaluateSpokenCandidates(
                            candidates = matches,
                            sampleTargetAnswer = sampleTarget,
                            requiredKeywords = requiredKw
                        )
                        spokenTranscripts[promptId] = eval.bestTranscript
                        evaluationResults[promptId] = eval
                        recognizerStatus = eval.feedbackMessage
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (!partial.isNullOrBlank()) {
                        spokenTranscripts[promptId] = partial
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            runCatching {
                speechRecognizer.startListening(intent)
            }.onFailure {
                activeListeningPromptId = null
                recognizerStatus = "Could not start microphone. Type or tap 'Simulate Spoken Response' to verify scoring."
            }
        }
    }

    var pendingPromptForPermission by remember { mutableStateOf<Triple<String, String, List<String>>?>(null) }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val pending = pendingPromptForPermission
        if (granted && pending != null) {
            startListeningForPrompt(pending.first, pending.second, pending.third)
        } else if (!granted) {
            recognizerStatus = "Microphone permission is needed for live voice capture, or test your spoken phrasing in the transcript box."
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🎙️ Room 2 • Speaking & Voice Interaction (غرفة التحدث والتفاعل الصوتي)",
                    style = MaterialTheme.typography.titleMedium,
                    color = EtaNavy,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Listen to each audio question, then answer aloud using On-Device Speech Recognition for instant pronunciation & grammar verification.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (recognizerStatus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EtaBlue.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = recognizerStatus ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            color = EtaNavy,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        prompts.forEachIndexed { idx, prompt ->
            val currentTranscript = spokenTranscripts[prompt.id] ?: ""
            val eval = evaluationResults[prompt.id]
            val isListeningThis = activeListeningPromptId == prompt.id

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtaNavy.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Voice Prompt ${idx + 1} • ${prompt.grammarFocusLabel}",
                                style = MaterialTheme.typography.labelLarge,
                                color = EtaNavy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        AudioPronunciationButtons(
                            textToSpeak = prompt.questionAudioText,
                            speechController = speechController
                        )
                    }

                    Text(
                        text = "🔊 \"${prompt.questionAudioText}\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = prompt.arabicPromptHint,
                        style = MaterialTheme.typography.labelLarge,
                        color = EtaRed
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    startListeningForPrompt(prompt.id, prompt.sampleTargetAnswer, prompt.requiredKeywords)
                                } else {
                                    pendingPromptForPermission = Triple(prompt.id, prompt.sampleTargetAnswer, prompt.requiredKeywords)
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListeningThis) EtaRed else EtaNavy
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("speak_voice_answer_btn_$idx")
                        ) {
                            Icon(
                                imageVector = if (isListeningThis) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isListeningThis) "Listening..." else "Speak Answer by Voice")
                        }

                        OutlinedButton(
                            onClick = {
                                spokenTranscripts[prompt.id] = prompt.sampleTargetAnswer
                                val res = VoiceAnswerAccuracyEvaluator.evaluateSingleTranscript(
                                    spokenText = prompt.sampleTargetAnswer,
                                    sampleTargetAnswer = prompt.sampleTargetAnswer,
                                    requiredKeywords = prompt.requiredKeywords
                                )
                                evaluationResults[prompt.id] = res
                            }
                        ) {
                            Text("Sample Voice", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    OutlinedTextField(
                        value = currentTranscript,
                        onValueChange = { updated ->
                            spokenTranscripts[prompt.id] = updated
                            evaluationResults[prompt.id] = VoiceAnswerAccuracyEvaluator.evaluateSingleTranscript(
                                spokenText = updated,
                                sampleTargetAnswer = prompt.sampleTargetAnswer,
                                requiredKeywords = prompt.requiredKeywords
                            )
                        },
                        label = { Text("Recognized Voice Transcript (النص الصوتي الملتقط)") },
                        placeholder = { Text("Target keywords: ${prompt.requiredKeywords.joinToString(", ")}") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (eval != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (eval.overallScorePercent >= 65) Color(0xFF10B981).copy(alpha = 0.14f) else EtaGold.copy(alpha = 0.16f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Pronunciation & Answer Accuracy: ${eval.overallScorePercent}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (eval.overallScorePercent >= 65) Color(0xFF047857) else EtaNavy,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = eval.feedbackMessage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val scores = prompts.map { p ->
                    val currentEval = evaluationResults[p.id]
                        ?: VoiceAnswerAccuracyEvaluator.evaluateSingleTranscript(
                            spokenText = spokenTranscripts[p.id] ?: "",
                            sampleTargetAnswer = p.sampleTargetAnswer,
                            requiredKeywords = p.requiredKeywords
                        )
                    evaluationResults[p.id] = currentEval
                    currentEval.overallScorePercent
                }
                val avg = if (scores.isEmpty()) 0 else scores.sum() / scores.size
                recognizerStatus = "Speaking Room Score: $avg%! (Earn 65%+ for 2/3 Stars to unlock next Box)"
                onSubmitSpeakingScore(avg)
            },
            colors = ButtonDefaults.buttonColors(containerColor = EtaNavy),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_speaking_voice_room_button")
        ) {
            Text("Complete Voice Evaluation & Award Stars (⭐⭐⭐)")
        }
    }
}
