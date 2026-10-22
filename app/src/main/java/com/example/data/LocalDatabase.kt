package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: String,
    val isUnlocked: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadedSizeKb: Int = 0,
    val completedMaterialsCsv: String = "",
    val completedAssignmentsCsv: String = "",
    val quizBestScore: Int = 0, // 0..100
    val inClassGrade: Int = 0, // 0..100 given locally in class
    val speakingScore: Int = 0,
    val listeningScore: Int = 0,
    val readingScore: Int = 0,
    val writingScore: Int = 0,
    val teacherNote: String = "",
    val gradedByTeacher: Boolean = false,
    val isPassed: Boolean = false,
    val starsEarned: Int = 0, // 0..3
    val badgeEarnedTimestamp: Long = 0L
) {
    val completedMaterialSet: Set<String>
        get() = completedMaterialsCsv.split(",").filter { it.isNotBlank() }.toSet()

    val completedAssignmentSet: Set<String>
        get() = completedAssignmentsCsv.split(",").filter { it.isNotBlank() }.toSet()
}

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val studentName: String = "Young Explorer",
    val avatarEmoji: String = "🦊",
    val totalXp: Int = 120,
    val streakDays: Int = 3
)

@Dao
interface QuestDao {
    @Query("SELECT * FROM level_progress")
    fun observeAllLevelProgress(): Flow<List<LevelProgressEntity>>

    @Query("SELECT * FROM level_progress WHERE levelId = :levelId LIMIT 1")
    suspend fun getLevelProgress(levelId: String): LevelProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLevelProgress(progress: LevelProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllLevelProgress(list: List<LevelProgressEntity>)

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun observeStudentProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    suspend fun getStudentProfile(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudentProfile(profile: StudentProfileEntity)
}

@Database(
    entities = [LevelProgressEntity::class, StudentProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diploma_quest_offline.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

class QuestRepository(
    private val dao: QuestDao,
    private val appContext: Context
) {
    val allProgressFlow: Flow<List<LevelProgressEntity>> = dao.observeAllLevelProgress()
    val profileFlow: Flow<StudentProfileEntity?> = dao.observeStudentProfile()

    private val offlinePacksDir: File
        get() = File(appContext.filesDir, "offline_level_packs").apply { if (!exists()) mkdirs() }

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        if (dao.getStudentProfile() == null) {
            dao.upsertStudentProfile(
                StudentProfileEntity(
                    id = 1,
                    studentName = "Star Explorer",
                    avatarEmoji = "🦊",
                    totalXp = 150,
                    streakDays = 5
                )
            )
        }
        // Unlock Level 1 (Essence) by default so new students can immediately start their journey!
        val firstLevel = CurriculumCatalog.allLevels.first()
        val existingFirst = dao.getLevelProgress(firstLevel.id)
        if (existingFirst == null) {
            val initialEntries = CurriculumCatalog.allLevels.mapIndexed { index, level ->
                LevelProgressEntity(
                    levelId = level.id,
                    isUnlocked = index == 0, // Level 1 unlocked initially
                    isDownloaded = false
                )
            }
            dao.upsertAllLevelProgress(initialEntries)
        }
    }

    suspend fun updateStudentIdentity(name: String, avatarEmoji: String) {
        val current = dao.getStudentProfile() ?: StudentProfileEntity()
        dao.upsertStudentProfile(
            current.copy(
                studentName = name.trim().ifEmpty { "Star Explorer" },
                avatarEmoji = avatarEmoji
            )
        )
    }

    suspend fun addXp(points: Int) {
        val current = dao.getStudentProfile() ?: StudentProfileEntity()
        dao.upsertStudentProfile(current.copy(totalXp = current.totalXp + points))
    }

    suspend fun unlockLevelWithCode(level: CurriculumLevel, enteredCode: String): Boolean {
        val normalized = enteredCode.trim().uppercase()
        val expected = level.teacherUnlockCode.trim().uppercase()
        val expectedWithoutDash = expected.replace("-", "")
        val normalizedWithoutDash = normalized.replace("-", "")

        // Accept either the exact level code (with or without hyphen) OR master teacher key "TEACH-ALL"
        if (normalized == expected || normalizedWithoutDash == expectedWithoutDash || normalized == "TEACH-ALL") {
            val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id)
            if (!current.isUnlocked) {
                dao.upsertLevelProgress(current.copy(isUnlocked = true))
                addXp(50)
            }
            return true
        }
        return false
    }

    suspend fun downloadLevelPack(level: CurriculumLevel) = withContext(Dispatchers.IO) {
        // Write actual cached JSON/bundle payload to device storage so offline caching is real and measurable
        val packFile = File(offlinePacksDir, "${level.id}.pack")
        val bundleContent = buildString {
            appendLine("=== OFFLINE LEVEL PACK: ${level.title} (${level.hours} HRS) ===")
            appendLine("Track: ${level.track.title}")
            appendLine("Stage: ${level.stageGroup}")
            appendLine("Badge: ${level.badgeEmoji} ${level.badgeName}")
            level.materials.forEach { mat ->
                appendLine("--- Material: ${mat.title} ---")
                appendLine(mat.contentBody)
                mat.keyPhrases.forEach { (k, v) -> appendLine("• $k : $v") }
            }
            level.assignments.forEach { assign ->
                appendLine("--- Assignment: ${assign.title} ---")
                appendLine(assign.instructions)
                appendLine(assign.rubricCriteria)
            }
            level.quizQuestions.forEach { q ->
                appendLine("Q: ${q.prompt} | Ans: ${q.options[q.correctIndex]}")
            }
        }
        packFile.writeText(bundleContent)

        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(
            levelId = level.id,
            isUnlocked = true
        )
        val sizeKb = (level.packSizeMb * 1024).toInt()
        dao.upsertLevelProgress(
            current.copy(
                isUnlocked = true,
                isDownloaded = true,
                downloadedSizeKb = sizeKb
            )
        )
        addXp(20)
    }

    suspend fun removeOfflinePack(level: CurriculumLevel) = withContext(Dispatchers.IO) {
        val packFile = File(offlinePacksDir, "${level.id}.pack")
        if (packFile.exists()) {
            packFile.delete()
        }
        val current = dao.getLevelProgress(level.id) ?: return@withContext
        dao.upsertLevelProgress(
            current.copy(
                isDownloaded = false,
                downloadedSizeKb = 0
            )
        )
    }

    suspend fun clearAllCompletedOfflinePacks() = withContext(Dispatchers.IO) {
        CurriculumCatalog.allLevels.forEach { level ->
            val current = dao.getLevelProgress(level.id)
            if (current != null && current.isDownloaded && current.isPassed) {
                removeOfflinePack(level)
            }
        }
    }

    suspend fun toggleMaterialCompleted(level: CurriculumLevel, material: StudyMaterial) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val set = current.completedMaterialSet.toMutableSet()
        val newlyAdded = if (set.contains(material.id)) {
            set.remove(material.id)
            false
        } else {
            set.add(material.id)
            true
        }
        dao.upsertLevelProgress(current.copy(completedMaterialsCsv = set.joinToString(",")))
        if (newlyAdded) {
            addXp(material.xpReward)
        }
    }

    suspend fun toggleAssignmentSubmitted(level: CurriculumLevel, assignment: LevelAssignment) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val set = current.completedAssignmentSet.toMutableSet()
        val newlyAdded = if (set.contains(assignment.id)) {
            set.remove(assignment.id)
            false
        } else {
            set.add(assignment.id)
            true
        }
        dao.upsertLevelProgress(current.copy(completedAssignmentsCsv = set.joinToString(",")))
        if (newlyAdded) {
            addXp(assignment.xpReward)
        }
    }

    suspend fun recordQuizScore(level: CurriculumLevel, scorePercent: Int) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val best = maxOf(current.quizBestScore, scorePercent)
        dao.upsertLevelProgress(current.copy(quizBestScore = best))
        if (scorePercent >= 66) {
            addXp(45)
        }
    }

    suspend fun saveLocalClassroomGrade(
        level: CurriculumLevel,
        speaking: Int,
        listening: Int,
        reading: Int,
        writing: Int,
        teacherNote: String,
        unlockNextAutomatically: Boolean
    ) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val average = ((speaking + listening + reading + writing) / 4.0).toInt().coerceIn(0, 100)
        val passed = average >= 60
        val stars = when {
            average >= 90 -> 3
            average >= 75 -> 2
            average >= 60 -> 1
            else -> 0
        }
        val wasAlreadyPassed = current.isPassed
        val badgeTimestamp = if (passed && current.badgeEarnedTimestamp == 0L) {
            System.currentTimeMillis()
        } else {
            current.badgeEarnedTimestamp
        }

        dao.upsertLevelProgress(
            current.copy(
                inClassGrade = average,
                speakingScore = speaking,
                listeningScore = listening,
                readingScore = reading,
                writingScore = writing,
                teacherNote = teacherNote.trim(),
                gradedByTeacher = true,
                isPassed = passed,
                starsEarned = stars,
                badgeEarnedTimestamp = badgeTimestamp
            )
        )

        if (passed && !wasAlreadyPassed) {
            addXp(150 + (stars * 30))
        }

        if (passed && unlockNextAutomatically) {
            val nextLevel = CurriculumCatalog.allLevels.find { it.orderIndex == level.orderIndex + 1 }
            if (nextLevel != null) {
                val nextProg = dao.getLevelProgress(nextLevel.id) ?: LevelProgressEntity(levelId = nextLevel.id)
                if (!nextProg.isUnlocked) {
                    dao.upsertLevelProgress(nextProg.copy(isUnlocked = true))
                }
            }
        }
    }
}
