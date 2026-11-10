package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.EtaBlue
import com.example.ui.theme.EtaNavy
import com.example.ui.theme.EtaNavyDark
import com.example.ui.theme.EtaRed
import java.util.UUID

enum class DeviceRole(val displayTitle: String) {
    UNPROVISIONED("Unprovisioned Device"),
    STUDENT("Student Phone"),
    TEACHER("Teacher Phone")
}

data class StudentMistakeItem(
    val id: String = UUID.randomUUID().toString(),
    val skillArea: String,
    val mistakeText: String,
    val correctionText: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class BoxArchitectureType(
    val title: String,
    val arabicSubtitle: String,
    val roomCount: Int,
    val badgeColor: Color
) {
    GENERAL_4_ROOMS(
        title = "General Box (4 Rooms)",
        arabicSubtitle = "غرف عامة (مفردات + قواعد + تطبيقات)",
        roomCount = 4,
        badgeColor = EtaNavy
    ),
    SKILLS_RW_2_ROOMS(
        title = "Skills RW (Reading & Writing)",
        arabicSubtitle = "مهارات كتابة وريدينج (قواعد + تركيب جمل)",
        roomCount = 2,
        badgeColor = EtaRed
    ),
    SKILLS_LS_2_ROOMS(
        title = "Skills L&S (Listening & Speaking)",
        arabicSubtitle = "مهارات استماع وتحدث (استماع وكتابة + رد صوتي)",
        roomCount = 2,
        badgeColor = EtaBlue
    )
}

enum class DiplomaTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val totalHours: Int,
    val primaryColor: Color,
    val badgeTitle: String
) {
    INTERMEDIATE_DIPLOMA(
        id = "intermediate_diploma",
        title = "FOUNDATION TO INTERMEDIATE (BOXES 1–13)",
        subtitle = "Essence to Skills 4 • 13 Sequential Boxes",
        totalHours = 585,
        primaryColor = EtaNavy,
        badgeTitle = "Intermediate Path Champion"
    ),
    ADVANCED_DIPLOMA(
        id = "advanced_diploma",
        title = "UPPER-INTERMEDIATE & ADVANCE (BOXES 14–21)",
        subtitle = "Upper Intermediate A to Skills 6 • 8 Sequential Boxes",
        totalHours = 360,
        primaryColor = EtaRed,
        badgeTitle = "Advanced Mastery Champion"
    ),
    TOEFL_PREP(
        id = "toefl_prep",
        title = "ALL 21 APPROVED BOXES",
        subtitle = "Complete 21-Box Institute Path",
        totalHours = 945,
        primaryColor = EtaBlue,
        badgeTitle = "21-Box Graduate"
    ),
    PTP_PROGRAM(
        id = "ptp_program",
        title = "SKILLS COURSES ONLY",
        subtitle = "Skills 1–6 (RW & Listening/Speaking)",
        totalHours = 270,
        primaryColor = EtaNavyDark,
        badgeTitle = "Skills Specialist"
    )
}

enum class MaterialCategory(val label: String) {
    VOCABULARY("Vocabulary Cards"),
    GRAMMAR("Grammar Guide"),
    READING_WRITING("Reading & Writing"),
    LISTENING_SPEAKING("Listening & Speaking"),
    PRACTICE_DRILL("Interactive Drill"),
    NOTES_HANDOUT("Teacher Notes & Handout")
}

data class VocabWordItem(
    val id: String = UUID.randomUUID().toString(),
    val word: String,
    val meaning: String,
    val arabicHint: String = "",
    val exampleSentence: String,
    val partOfSpeech: String = "Phrase",
    val sourceLabel: String = "Core Bank"
)

data class GrammarRoomContent(
    val ruleTitle: String,
    val formulaPattern: String,
    val explanationBody: String,
    val affirmativeExamples: List<String>,
    val negativeExamples: List<String>,
    val questionExamples: List<String>,
    val nominalVsVerbalNotes: List<Pair<String, String>> = emptyList()
)

data class SentenceBuildExercise(
    val id: String,
    val instruction: String,
    val sentenceTypeTag: String, // e.g., "Nominal Sentence (جملة اسمية)", "Verbal Sentence (جملة فعلية)", "Past Continuous"
    val scrambledWords: List<String>,
    val targetSentence: String,
    val grammarHint: String
)

data class ListeningDictationExercise(
    val id: String,
    val title: String,
    val audioTranscript: String,
    val hintText: String,
    val keyWordsToExtract: List<Pair<String, String>>
)

data class SpeakingVoicePrompt(
    val id: String,
    val questionAudioText: String,
    val arabicPromptHint: String,
    val sampleTargetAnswer: String,
    val requiredKeywords: List<String>,
    val grammarFocusLabel: String
)

data class StudyMaterial(
    val id: String,
    val title: String,
    val category: MaterialCategory,
    val summary: String,
    val contentBody: String,
    val keyPhrases: List<Pair<String, String>>,
    val xpReward: Int = 25,
    val isCustom: Boolean = false,
    val authorRole: DeviceRole = DeviceRole.TEACHER
)

data class LevelAssignment(
    val id: String,
    val title: String,
    val typeLabel: String,
    val instructions: String,
    val rubricCriteria: String,
    val maxPoints: Int = 100,
    val xpReward: Int = 40
)

data class SkillQuizQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class CurriculumLevel(
    val id: String,
    val orderIndex: Int,
    val track: DiplomaTrack,
    val stageGroup: String,
    val title: String,
    val hours: Int,
    val teacherUnlockCode: String,
    val packSizeMb: Float,
    val badgeName: String,
    val badgeEmoji: String,
    val badgeDescription: String,
    val focusSkills: List<String>,
    val materials: List<StudyMaterial>,
    val assignments: List<LevelAssignment>,
    val quizQuestions: List<SkillQuizQuestion>,
    val boxType: BoxArchitectureType = BoxArchitectureType.GENERAL_4_ROOMS,
    val vocabBankItems: List<VocabWordItem> = emptyList(),
    val grammarContent: GrammarRoomContent = GrammarRoomContent(
        ruleTitle = "Core Grammar Structure",
        formulaPattern = "Subject + Verb + Object",
        explanationBody = "",
        affirmativeExamples = emptyList(),
        negativeExamples = emptyList(),
        questionExamples = emptyList()
    ),
    val vocabPracticeQuestions: List<SkillQuizQuestion> = emptyList(),
    val grammarPracticeQuestions: List<SkillQuizQuestion> = emptyList(),
    val rwSentenceExercises: List<SentenceBuildExercise> = emptyList(),
    val listeningExercises: List<ListeningDictationExercise> = emptyList(),
    val speakingPrompts: List<SpeakingVoicePrompt> = emptyList()
)

enum class ReminderPriority(
    val label: String,
    val emoji: String,
    val badgeColor: Color
) {
    HIGH_PRIORITY("High Priority", "🔥", EtaRed),
    EXAM_PREP("Exam Prep", "🎯", EtaNavy),
    SPEAKING_PRACTICE("Speaking Practice", "🗣️", EtaBlue),
    GRAMMAR_REVIEW("Grammar & Vocab", "📘", Color(0xFF059669))
}

enum class NotificationCategory(
    val label: String,
    val emoji: String,
    val accentColor: Color
) {
    GRADE_POSTED("Grades Updated", "📊", EtaBlue),
    MISTAKE_LOGGED("Mistake Correction", "🔍", EtaRed),
    TEACHER_REPLY("Teacher Reply", "👨‍🏫", EtaNavy),
    STUDY_REMINDER("Study Reminder", "⏰", Color(0xFFD97706))
}

enum class FlashcardSourceType(val label: String) {
    UNIT_PHRASE("Vocabulary Bank"),
    PERSONAL_NOTE("My Note Card"),
    MISTAKE_CORRECTION("Mistake Correction")
}

data class FlashcardReviewItem(
    val id: String,
    val frontPrompt: String,
    val backAnswer: String,
    val spokenText: String,
    val sourceType: FlashcardSourceType,
    val skillTag: String
)
