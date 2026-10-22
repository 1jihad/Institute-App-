package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.QuestEmerald
import com.example.ui.theme.QuestGold
import com.example.ui.theme.QuestIndigo
import com.example.ui.theme.QuestTeal

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
        title = "INTERMEDIATE DIPLOMA",
        subtitle = "Foundations to Intermediate Fluency • 13 Levels",
        totalHours = 585,
        primaryColor = QuestIndigo,
        badgeTitle = "Intermediate Diploma Champion"
    ),
    ADVANCED_DIPLOMA(
        id = "advanced_diploma",
        title = "ADVANCED DIPLOMA",
        subtitle = "Upper-Intermediate & Academic Mastery • 8 Levels",
        totalHours = 360,
        primaryColor = QuestEmerald,
        badgeTitle = "Advanced Diploma Master"
    ),
    TOEFL_PREP(
        id = "toefl_prep",
        title = "TOEFL TEST PREPARATION",
        subtitle = "Intensive Exam Strategies & Practice • 100 Hrs",
        totalHours = 100,
        primaryColor = QuestGold,
        badgeTitle = "TOEFL 100-Hour Conquerer"
    ),
    PTP_PROGRAM(
        id = "ptp_program",
        title = "PROFESSIONAL TEACHING PROGRAM - PTP",
        subtitle = "TKT, TESOL & Classroom Practice • 3 Levels",
        totalHours = 100,
        primaryColor = QuestTeal,
        badgeTitle = "Certified Master Educator"
    )
}

enum class MaterialCategory(val label: String) {
    VOCABULARY("Vocabulary Cards"),
    GRAMMAR("Grammar Guide"),
    READING_WRITING("Reading & Writing"),
    LISTENING_SPEAKING("Listening & Speaking"),
    PRACTICE_DRILL("Interactive Drill")
}

data class StudyMaterial(
    val id: String,
    val title: String,
    val category: MaterialCategory,
    val summary: String,
    val contentBody: String,
    val keyPhrases: List<Pair<String, String>>,
    val xpReward: Int = 25
)

data class LevelAssignment(
    val id: String,
    val title: String,
    val typeLabel: String, // e.g. "In-Class Activity", "Workbook Task", "Oral Presentation"
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
    val stageGroup: String, // e.g., "Stage 1 • Core Foundation", "Stage 2 • Intermediate Skills"
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
    val quizQuestions: List<SkillQuizQuestion>
)
