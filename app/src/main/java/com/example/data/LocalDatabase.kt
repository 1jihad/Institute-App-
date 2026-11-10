package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class BoxPracticeRoomType {
    VOCAB_PRACTICE,
    GRAMMAR_PRACTICE,
    RW_SENTENCE_PRACTICE,
    LISTENING_DICTATION,
    SPEAKING_VOICE_QA
}

object DeviceMacSecurity {
    private const val ADMIN_SECRET_SALT = "EXCEL_ETA_ADMIN_2026"

    @SuppressLint("HardwareIds")
    fun getHardwareDeviceId(context: Context): String {
        val raw = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull()
        return if (!raw.isNullOrBlank()) raw.uppercase(Locale.US) else "ETA-HW-DEFAULT-01"
    }

    fun detectDeviceMacAddress(context: Context): String {
        val hwId = getHardwareDeviceId(context)
        val digest = MessageDigest.getInstance("SHA-256").digest(hwId.toByteArray(Charsets.UTF_8))
        val b0 = (digest[0].toInt() and 0xFC) or 0x02
        return String.format(
            Locale.US,
            "%02X:%02X:%02X:%02X:%02X:%02X",
            b0 and 0xFF,
            digest[1].toInt() and 0xFF,
            digest[2].toInt() and 0xFF,
            digest[3].toInt() and 0xFF,
            digest[4].toInt() and 0xFF,
            digest[5].toInt() and 0xFF
        )
    }

    fun normalizeMac(mac: String): String {
        val hexOnly = mac.uppercase(Locale.US).filter { it in '0'..'9' || it in 'A'..'F' }
        if (hexOnly.length != 12) return mac.trim().uppercase(Locale.US)
        return hexOnly.chunked(2).joinToString(":")
    }

    fun isValidMacFormat(mac: String): Boolean {
        val hexOnly = mac.uppercase(Locale.US).filter { it in '0'..'9' || it in 'A'..'F' }
        return hexOnly.length == 12
    }

    fun computeAdminActivationKey(macAddress: String, role: DeviceRole): String {
        val normalizedMac = normalizeMac(macAddress)
        val payload = "$normalizedMac|${role.name}|$ADMIN_SECRET_SALT"
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(payload.toByteArray(Charsets.UTF_8))
            .joinToString("") { String.format(Locale.US, "%02X", it) }
        val code12 = hash.take(12)
        return "${code12.substring(0, 4)}-${code12.substring(4, 8)}-${code12.substring(8, 12)}"
    }

    fun normalizeActivationKey(key: String): String {
        val cleaned = key.uppercase(Locale.US).filter { it in '0'..'9' || it in 'A'..'F' }
        if (cleaned.length != 12) return key.trim().uppercase(Locale.US)
        return "${cleaned.substring(0, 4)}-${cleaned.substring(4, 8)}-${cleaned.substring(8, 12)}"
    }

    fun verifyProvisioning(
        entity: DeviceProvisionEntity?,
        currentHardwareId: String
    ): Boolean {
        if (entity == null) return false
        val role = entity.role
        if (role != DeviceRole.STUDENT) return false
        if (!isValidMacFormat(entity.macAddress)) return false
        if (entity.hardwareId != currentHardwareId) return false
        val expectedKey = computeAdminActivationKey(entity.macAddress, DeviceRole.STUDENT)
        return normalizeActivationKey(entity.activationSignature) == expectedKey
    }
}

@Entity(tableName = "device_provision")
data class DeviceProvisionEntity(
    @PrimaryKey val id: Int = 1,
    val macAddress: String = "",
    val hardwareId: String = "",
    val roleName: String = DeviceRole.UNPROVISIONED.name,
    val activationSignature: String = "",
    val provisionedAt: Long = 0L
) {
    val role: DeviceRole
        get() = DeviceRole.entries.find { it.name == roleName } ?: DeviceRole.UNPROVISIONED
}

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
    @PrimaryKey val levelId: String,
    val isUnlocked: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadedSizeKb: Int = 0,
    val completedMaterialsCsv: String = "",
    val completedAssignmentsCsv: String = "",
    val quizBestScore: Int = 0, // 0..100 overall best practice score
    val vocabRoomCompleted: Boolean = false,
    val grammarRoomCompleted: Boolean = false,
    val vocabPracticeBestScore: Int = 0, // Room 3 in General Box
    val grammarPracticeBestScore: Int = 0, // Room 4 in General Box
    val rwPracticeBestScore: Int = 0, // Room 2 in Skills RW Box
    val listeningPracticeBestScore: Int = 0, // Room 1 in Skills L&S Box
    val speakingPracticeBestScore: Int = 0, // Room 2 in Skills L&S Box
    val inClassGrade: Int = 0, // 0..100 given locally in class
    val speakingScore: Int = 0,
    val listeningScore: Int = 0,
    val readingScore: Int = 0,
    val writingScore: Int = 0,
    val teacherNote: String = "",
    val mistakesSerialized: String = "",
    val quizMistakesCsv: String = "",
    val gradedByTeacher: Boolean = false,
    val isPassed: Boolean = false,
    val starsEarned: Int = 0, // 0..3 (2/3 unlocks next box)
    val badgeEarnedTimestamp: Long = 0L
) {
    val completedMaterialSet: Set<String>
        get() = completedMaterialsCsv.split(",").filter { it.isNotBlank() }.toSet()

    val completedAssignmentSet: Set<String>
        get() = completedAssignmentsCsv.split(",").filter { it.isNotBlank() }.toSet()

    val quizMistakeIdSet: Set<String>
        get() = quizMistakesCsv.split(",").filter { it.isNotBlank() }.toSet()

    val mistakeItems: List<StudentMistakeItem>
        get() {
            if (mistakesSerialized.isBlank()) return emptyList()
            return mistakesSerialized.split(";;;").mapNotNull { entry ->
                val parts = entry.split("|||")
                if (parts.size >= 4) {
                    StudentMistakeItem(
                        id = parts[0],
                        skillArea = parts[1],
                        mistakeText = parts[2],
                        correctionText = parts[3],
                        timestamp = parts.getOrNull(4)?.toLongOrNull() ?: 0L
                    )
                } else null
            }
        }
}

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val studentName: String = "Star Explorer",
    val avatarEmoji: String = "🦊",
    val academicStage: String = "University Student",
    val learningGoal: String = "Master All 21 Boxes & Fluency",
    val startingBoxNumber: Int = 1,
    val isOnboardingCompleted: Boolean = false,
    val totalXp: Int = 120,
    val streakDays: Int = 3,
    val lastStudyEpochDay: Long = System.currentTimeMillis() / 86_400_000L,
    val examGoalTitle: String = "Intermediate Unit 1 Assessment",
    val examTargetEpochDay: Long = (System.currentTimeMillis() / 86_400_000L) + 14L
) {
    val daysUntilExam: Int
        get() {
            val today = System.currentTimeMillis() / 86_400_000L
            return (examTargetEpochDay - today).toInt().coerceAtLeast(0)
        }
}

@Entity(tableName = "vocabulary_entries")
data class VocabularyEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val levelId: String,
    val word: String,
    val meaning: String,
    val arabicHint: String = "",
    val exampleSentence: String = "",
    val partOfSpeech: String = "Vocabulary",
    val sourceLabel: String = "Teacher Sheet (.xlsx/.csv)",
    val isMastered: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toVocabWordItem(): VocabWordItem = VocabWordItem(
        id = id,
        word = word,
        meaning = meaning,
        arabicHint = arabicHint,
        exampleSentence = exampleSentence,
        partOfSpeech = partOfSpeech,
        sourceLabel = sourceLabel
    )
}

@Entity(tableName = "study_reminders")
data class StudyReminderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val levelId: String,
    val reminderText: String,
    val priorityName: String = ReminderPriority.HIGH_PRIORITY.name,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val priority: ReminderPriority
        get() = ReminderPriority.entries.find { it.name == priorityName } ?: ReminderPriority.HIGH_PRIORITY
}

@Entity(tableName = "student_notifications")
data class StudentNotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val levelId: String,
    val categoryName: String = NotificationCategory.GRADE_POSTED.name,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val category: NotificationCategory
        get() = NotificationCategory.entries.find { it.name == categoryName } ?: NotificationCategory.GRADE_POSTED
}

@Entity(tableName = "custom_materials")
data class CustomMaterialEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val levelId: String,
    val title: String,
    val categoryName: String,
    val summary: String,
    val contentBody: String,
    val keyPhrasesSerialized: String = "",
    val authorRoleName: String = DeviceRole.TEACHER.name,
    val timestamp: Long = System.currentTimeMillis()
) {
    val authorRole: DeviceRole
        get() = DeviceRole.entries.find { it.name == authorRoleName } ?: DeviceRole.TEACHER

    fun toStudyMaterial(): StudyMaterial {
        val cat = MaterialCategory.entries.find { it.name == categoryName } ?: MaterialCategory.NOTES_HANDOUT
        val phrases = if (keyPhrasesSerialized.isBlank()) {
            emptyList()
        } else {
            keyPhrasesSerialized.split(";;").mapNotNull { pairStr ->
                val parts = pairStr.split("||")
                if (parts.size >= 2) parts[0] to parts[1] else null
            }
        }
        return StudyMaterial(
            id = id,
            title = title,
            category = cat,
            summary = summary,
            contentBody = contentBody,
            keyPhrases = phrases,
            xpReward = 30,
            isCustom = true,
            authorRole = authorRole
        )
    }
}

@Entity(tableName = "teacher_messages")
data class TeacherMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val levelId: String,
    val studentName: String,
    val messageText: String,
    val teacherReply: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface QuestDao {
    @Query("SELECT * FROM device_provision WHERE id = 1 LIMIT 1")
    fun observeDeviceProvision(): Flow<DeviceProvisionEntity?>

    @Query("SELECT * FROM device_provision WHERE id = 1 LIMIT 1")
    suspend fun getDeviceProvision(): DeviceProvisionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeviceProvision(entity: DeviceProvisionEntity)

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

    @Query("SELECT * FROM vocabulary_entries ORDER BY timestamp ASC")
    fun observeAllVocabularyEntries(): Flow<List<VocabularyEntryEntity>>

    @Query("SELECT * FROM vocabulary_entries WHERE levelId = :levelId ORDER BY timestamp ASC")
    suspend fun getVocabularyEntriesForLevel(levelId: String): List<VocabularyEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularyEntries(entries: List<VocabularyEntryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularyEntry(entry: VocabularyEntryEntity)

    @Query("DELETE FROM vocabulary_entries WHERE id = :id")
    suspend fun deleteVocabularyEntry(id: String)

    @Query("SELECT * FROM custom_materials ORDER BY timestamp ASC")
    fun observeAllCustomMaterials(): Flow<List<CustomMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomMaterial(material: CustomMaterialEntity)

    @Query("DELETE FROM custom_materials WHERE id = :id")
    suspend fun deleteCustomMaterial(id: String)

    @Query("SELECT * FROM study_reminders ORDER BY isCompleted ASC, createdAt DESC")
    fun observeAllStudyReminders(): Flow<List<StudyReminderEntity>>

    @Query("SELECT * FROM study_reminders WHERE id = :id LIMIT 1")
    suspend fun getStudyReminder(id: String): StudyReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudyReminder(reminder: StudyReminderEntity)

    @Query("DELETE FROM study_reminders WHERE id = :id")
    suspend fun deleteStudyReminder(id: String)

    @Query("SELECT * FROM student_notifications ORDER BY timestamp DESC")
    fun observeAllNotifications(): Flow<List<StudentNotificationEntity>>

    @Query("SELECT * FROM student_notifications WHERE id = :id LIMIT 1")
    suspend fun getNotification(id: String): StudentNotificationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotification(notification: StudentNotificationEntity)

    @Query("UPDATE student_notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM student_notifications WHERE isRead = 1")
    suspend fun clearReadNotifications()

    @Query("SELECT * FROM teacher_messages ORDER BY timestamp DESC")
    fun observeAllTeacherMessages(): Flow<List<TeacherMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTeacherMessage(message: TeacherMessageEntity)
}

@Database(
    entities = [
        DeviceProvisionEntity::class,
        LevelProgressEntity::class,
        StudentProfileEntity::class,
        VocabularyEntryEntity::class,
        CustomMaterialEntity::class,
        TeacherMessageEntity::class,
        StudyReminderEntity::class,
        StudentNotificationEntity::class
    ],
    version = 5,
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
                    "excel_eta_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}

class QuestRepository(
    private val dao: QuestDao,
    private val appContext: Context
) {
    val deviceProvisionFlow: Flow<DeviceProvisionEntity?> = dao.observeDeviceProvision()
    val allProgressFlow: Flow<List<LevelProgressEntity>> = dao.observeAllLevelProgress()
    val profileFlow: Flow<StudentProfileEntity?> = dao.observeStudentProfile()
    val allVocabularyEntriesFlow: Flow<List<VocabularyEntryEntity>> = dao.observeAllVocabularyEntries()
    val allCustomMaterialsFlow: Flow<List<CustomMaterialEntity>> = dao.observeAllCustomMaterials()
    val allStudyRemindersFlow: Flow<List<StudyReminderEntity>> = dao.observeAllStudyReminders()
    val allNotificationsFlow: Flow<List<StudentNotificationEntity>> = dao.observeAllNotifications()
    val allTeacherMessagesFlow: Flow<List<TeacherMessageEntity>> = dao.observeAllTeacherMessages()

    val detectedMacAddress: String by lazy {
        DeviceMacSecurity.detectDeviceMacAddress(appContext)
    }

    val currentHardwareId: String by lazy {
        DeviceMacSecurity.getHardwareDeviceId(appContext)
    }

    private val offlinePacksDir: File
        get() = File(appContext.filesDir, "offline_level_packs").apply { if (!exists()) mkdirs() }

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val todayEpoch = System.currentTimeMillis() / 86_400_000L
        if (dao.getStudentProfile() == null) {
            dao.upsertStudentProfile(
                StudentProfileEntity(
                    id = 1,
                    studentName = "Star Explorer",
                    avatarEmoji = "🦊",
                    academicStage = "University Student",
                    learningGoal = "Master All 21 Boxes & Fluency",
                    startingBoxNumber = 1,
                    isOnboardingCompleted = false,
                    totalXp = 150,
                    streakDays = 5,
                    lastStudyEpochDay = todayEpoch,
                    examGoalTitle = "Intermediate Unit 1 Assessment",
                    examTargetEpochDay = todayEpoch + 12L
                )
            )
        }
        val firstLevel = CurriculumCatalog.allLevels.first()
        val existingFirst = dao.getLevelProgress(firstLevel.id)
        if (existingFirst == null) {
            val initialMistakes = listOf(
                StudentMistakeItem(
                    id = "seed_mistake_1",
                    skillArea = "Speaking & Grammar",
                    mistakeText = "I am agree with your opinion about the schedule.",
                    correctionText = "I agree with your opinion about the schedule."
                ),
                StudentMistakeItem(
                    id = "seed_mistake_2",
                    skillArea = "Pronunciation & Fluency",
                    mistakeText = "She have three classes on Wednesday afternoon.",
                    correctionText = "She has three classes on Wednesday afternoon."
                )
            )
            val initialEntries = CurriculumCatalog.allLevels.mapIndexed { index, level ->
                if (index == 0) {
                    LevelProgressEntity(
                        levelId = level.id,
                        isUnlocked = true,
                        isDownloaded = false,
                        inClassGrade = 84,
                        speakingScore = 82,
                        listeningScore = 88,
                        readingScore = 85,
                        writingScore = 80,
                        teacherNote = "Great participation in Unit 1! Earn 2/3 Stars in your practice rooms to unlock Box 2 (Beginner A).",
                        mistakesSerialized = serializeMistakes(initialMistakes),
                        gradedByTeacher = true,
                        isPassed = false,
                        starsEarned = 1,
                        badgeEarnedTimestamp = 0L
                    )
                } else {
                    LevelProgressEntity(
                        levelId = level.id,
                        isUnlocked = false,
                        isDownloaded = false
                    )
                }
            }
            dao.upsertAllLevelProgress(initialEntries)

            dao.upsertStudyReminder(
                StudyReminderEntity(
                    id = "seed_reminder_1",
                    levelId = firstLevel.id,
                    reminderText = "Practice pronouncing 'I agree with you' and 'She has three classes' aloud 5 times before next class.",
                    priorityName = ReminderPriority.SPEAKING_PRACTICE.name,
                    isCompleted = false
                )
            )

            dao.upsertNotification(
                StudentNotificationEntity(
                    id = "seed_notif_grade_1",
                    levelId = firstLevel.id,
                    categoryName = NotificationCategory.GRADE_POSTED.name,
                    title = "📊 New Class Grades Posted: ${firstLevel.title}",
                    message = "Your teacher recorded your 4-skill scores (84% avg: Speaking 82%, Listening 88%, Reading 85%, Writing 80%). Tap to view your Grades & Mistakes.",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 120_000L
                )
            )
            dao.upsertNotification(
                StudentNotificationEntity(
                    id = "seed_notif_mistake_1",
                    levelId = firstLevel.id,
                    categoryName = NotificationCategory.MISTAKE_LOGGED.name,
                    title = "🔍 2 Mistake Corrections Added in ${firstLevel.title}",
                    message = "Teacher logged corrections for 'I agree with your opinion' and 'She has three classes'. Tap to listen and review.",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - 60_000L
                )
            )
        }
    }

    /**
     * Completes first-time Student Personal & Academic Onboarding.
     * Starts at Box 1 (Essence) by default, or unlocks a specific Box if a valid Admin Activation Code is provided.
     */
    suspend fun completeStudentOnboarding(
        studentName: String,
        avatarEmoji: String,
        academicStage: String,
        learningGoal: String,
        optionalAdminCode: String = ""
    ): Pair<Int, String?> = withContext(Dispatchers.IO) {
        val current = dao.getStudentProfile() ?: StudentProfileEntity()
        var unlockedBoxOrder = 1
        var unlockedBoxTitle: String? = null

        val cleanedCode = optionalAdminCode.trim().uppercase(Locale.US)
        if (cleanedCode.isNotBlank()) {
            val matchedLevel = CurriculumCatalog.allLevels.firstOrNull { lvl ->
                val expected = lvl.teacherUnlockCode.trim().uppercase(Locale.US)
                cleanedCode == expected || cleanedCode.replace("-", "") == expected.replace("-", "")
            }
            if (matchedLevel != null) {
                val prog = dao.getLevelProgress(matchedLevel.id) ?: LevelProgressEntity(levelId = matchedLevel.id)
                dao.upsertLevelProgress(prog.copy(isUnlocked = true))
                unlockedBoxOrder = matchedLevel.orderIndex
                unlockedBoxTitle = matchedLevel.title
            }
        }

        dao.upsertStudentProfile(
            current.copy(
                studentName = studentName.trim().ifBlank { "Star Explorer" },
                avatarEmoji = avatarEmoji.ifBlank { "🦊" },
                academicStage = academicStage.trim().ifBlank { "University Student" },
                learningGoal = learningGoal.trim().ifBlank { "Master All 21 Boxes & Fluency" },
                startingBoxNumber = unlockedBoxOrder,
                isOnboardingCompleted = true,
                totalXp = current.totalXp + 30
            )
        )
        unlockedBoxOrder to unlockedBoxTitle
    }

    /**
     * Imports a Teacher Vocabulary Sheet (.xlsx or .csv) from an InputStream and persists all words in Room.
     */
    suspend fun importVocabularySpreadsheetStream(
        inputStream: InputStream,
        fileNameOrMime: String,
        defaultLevelId: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        runCatching {
            val rows = VocabSpreadsheetImporter.parseStream(
                inputStream = inputStream,
                fileNameOrMime = fileNameOrMime,
                defaultLevelId = defaultLevelId
            )
            saveImportedVocabRows(rows, fileNameOrMime.ifBlank { "Teacher Sheet (.xlsx/.csv)" })
        }.getOrElse { e ->
            false to "Could not read vocabulary file (${e.localizedMessage ?: "invalid format"})."
        }
    }

    /**
     * Imports CSV text or raw rows into the in-app Vocabulary Bank for the target box(es).
     */
    suspend fun importVocabularyCsvText(
        csvText: String,
        defaultLevelId: String,
        sourceName: String = "Teacher Sheet (.csv)"
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val rows = VocabSpreadsheetImporter.parseCsvText(csvText, defaultLevelId)
        saveImportedVocabRows(rows, sourceName)
    }

    suspend fun importVocabularyXlsxBytes(
        xlsxBytes: ByteArray,
        defaultLevelId: String,
        sourceName: String = "Teacher Workbook (.xlsx)"
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val rows = VocabSpreadsheetImporter.parseXlsxBytes(xlsxBytes, defaultLevelId)
        saveImportedVocabRows(rows, sourceName)
    }

    private suspend fun saveImportedVocabRows(
        rows: List<VocabSpreadsheetImporter.ImportedVocabRow>,
        sourceLabel: String
    ): Pair<Boolean, String> {
        if (rows.isEmpty()) {
            return false to "No valid vocabulary rows found. Ensure columns include Word and Meaning."
        }
        val entities = rows.map { row ->
            val targetId = row.targetLevelId ?: CurriculumCatalog.allLevels.first().id
            VocabularyEntryEntity(
                id = "vocab_${targetId}_${row.word.lowercase(Locale.US).replace(Regex("[^a-z0-9]"), "_")}",
                levelId = targetId,
                word = row.word,
                meaning = row.meaning,
                arabicHint = row.arabicHint,
                exampleSentence = row.exampleSentence,
                partOfSpeech = row.partOfSpeech,
                sourceLabel = sourceLabel
            )
        }
        dao.insertVocabularyEntries(entities)
        recordDailyStudyActivity(xpBonus = 25)
        val distinctLevels = entities.map { it.levelId }.distinct().size
        return true to "Imported ${entities.size} vocabulary word(s) across $distinctLevels box(es) into the in-app Vocabulary Bank!"
    }

    suspend fun addSingleVocabularyEntry(
        levelId: String,
        word: String,
        meaning: String,
        arabicHint: String = "",
        exampleSentence: String = "",
        sourceLabel: String = "Student Added"
    ) = withContext(Dispatchers.IO) {
        if (word.isBlank() || meaning.isBlank()) return@withContext
        val entry = VocabularyEntryEntity(
            id = "vocab_${levelId}_${UUID.randomUUID()}",
            levelId = levelId,
            word = word.trim(),
            meaning = meaning.trim(),
            arabicHint = arabicHint.trim(),
            exampleSentence = exampleSentence.trim().ifBlank { "Example: ${word.trim()}." },
            partOfSpeech = "Custom Word",
            sourceLabel = sourceLabel
        )
        dao.insertVocabularyEntry(entry)
        recordDailyStudyActivity(xpBonus = 15)
    }

    suspend fun deleteVocabularyEntry(entryId: String) = withContext(Dispatchers.IO) {
        dao.deleteVocabularyEntry(entryId)
    }

    /**
     * Marks Room 1 (Vocabulary) or Room 2 (Grammar Explanation) as studied and awards XP.
     */
    suspend fun markStudyRoomCompleted(
        level: CurriculumLevel,
        isVocabRoom: Boolean
    ) = withContext(Dispatchers.IO) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val updated = if (isVocabRoom) {
            current.copy(vocabRoomCompleted = true)
        } else {
            current.copy(grammarRoomCompleted = true)
        }
        dao.upsertLevelProgress(updated)
        recordDailyStudyActivity(xpBonus = 20)
    }

    /**
     * Records a practice score (0..100) for one of a Box's interactive practice rooms,
     * recalculates the Box's 3-Star rating (0..3 stars), and automatically unlocks the
     * next sequential Box (orderIndex + 1) when the student reaches >= 2 Stars (2/3)!
     */
    suspend fun recordRoomPracticeScore(
        level: CurriculumLevel,
        roomType: BoxPracticeRoomType,
        scorePercent: Int,
        wrongQuestionIds: List<String> = emptyList()
    ): Pair<Int, CurriculumLevel?> = withContext(Dispatchers.IO) {
        val clamped = scorePercent.coerceIn(0, 100)
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)

        val updated = when (roomType) {
            BoxPracticeRoomType.VOCAB_PRACTICE -> current.copy(
                vocabPracticeBestScore = maxOf(current.vocabPracticeBestScore, clamped)
            )
            BoxPracticeRoomType.GRAMMAR_PRACTICE -> current.copy(
                grammarPracticeBestScore = maxOf(current.grammarPracticeBestScore, clamped)
            )
            BoxPracticeRoomType.RW_SENTENCE_PRACTICE -> current.copy(
                rwPracticeBestScore = maxOf(current.rwPracticeBestScore, clamped)
            )
            BoxPracticeRoomType.LISTENING_DICTATION -> current.copy(
                listeningPracticeBestScore = maxOf(current.listeningPracticeBestScore, clamped)
            )
            BoxPracticeRoomType.SPEAKING_VOICE_QA -> current.copy(
                speakingPracticeBestScore = maxOf(current.speakingPracticeBestScore, clamped)
            )
        }

        val combinedScore = when (level.boxType) {
            BoxArchitectureType.GENERAL_4_ROOMS -> {
                val v = updated.vocabPracticeBestScore
                val g = updated.grammarPracticeBestScore
                if (v > 0 && g > 0) (v + g) / 2 else maxOf(v, g)
            }
            BoxArchitectureType.SKILLS_RW_2_ROOMS -> {
                updated.rwPracticeBestScore
            }
            BoxArchitectureType.SKILLS_LS_2_ROOMS -> {
                val l = updated.listeningPracticeBestScore
                val s = updated.speakingPracticeBestScore
                if (l > 0 && s > 0) (l + s) / 2 else maxOf(l, s)
            }
        }

        val computedStars = when {
            combinedScore >= 85 -> 3
            combinedScore >= 65 -> 2
            combinedScore >= 40 -> 1
            else -> 0
        }

        val newStars = maxOf(updated.starsEarned, computedStars)
        val nowPassed = updated.isPassed || newStars >= 2 // 2/3 Stars minimum required to advance!
        val badgeTime = if (nowPassed && updated.badgeEarnedTimestamp == 0L) {
            System.currentTimeMillis()
        } else {
            updated.badgeEarnedTimestamp
        }

        val finalProgress = updated.copy(
            quizBestScore = maxOf(updated.quizBestScore, combinedScore),
            quizMistakesCsv = if (wrongQuestionIds.isNotEmpty()) wrongQuestionIds.joinToString(",") else updated.quizMistakesCsv,
            starsEarned = newStars,
            isPassed = nowPassed,
            badgeEarnedTimestamp = badgeTime
        )
        dao.upsertLevelProgress(finalProgress)

        recordDailyStudyActivity(xpBonus = 30 + (newStars * 15))

        // Automatically unlock the next box in the 21-level sequence if the student earned >= 2/3 stars!
        var newlyUnlockedNextBox: CurriculumLevel? = null
        if (newStars >= 2) {
            val nextBox = CurriculumCatalog.allLevels.find { it.orderIndex == level.orderIndex + 1 }
            if (nextBox != null) {
                val nextProg = dao.getLevelProgress(nextBox.id) ?: LevelProgressEntity(levelId = nextBox.id)
                if (!nextProg.isUnlocked) {
                    dao.upsertLevelProgress(nextProg.copy(isUnlocked = true))
                    newlyUnlockedNextBox = nextBox
                }
            }
        }

        newStars to newlyUnlockedNextBox
    }

    suspend fun recordDailyStudyActivity(xpBonus: Int = 0) = withContext(Dispatchers.IO) {
        val current = dao.getStudentProfile() ?: StudentProfileEntity()
        val today = System.currentTimeMillis() / 86_400_000L
        val newStreak = when {
            current.lastStudyEpochDay == today - 1L -> current.streakDays + 1
            current.lastStudyEpochDay < today - 1L -> 1
            else -> current.streakDays.coerceAtLeast(1)
        }
        dao.upsertStudentProfile(
            current.copy(
                totalXp = current.totalXp + xpBonus,
                streakDays = newStreak,
                lastStudyEpochDay = today
            )
        )
    }

    suspend fun updateExamGoal(goalTitle: String, daysFromToday: Int) = withContext(Dispatchers.IO) {
        val current = dao.getStudentProfile() ?: StudentProfileEntity()
        val today = System.currentTimeMillis() / 86_400_000L
        val clampedDays = daysFromToday.coerceIn(1, 365)
        dao.upsertStudentProfile(
            current.copy(
                examGoalTitle = goalTitle.trim().ifBlank { "Next Unit Assessment" },
                examTargetEpochDay = today + clampedDays
            )
        )
    }

    suspend fun addStudyReminder(
        levelId: String,
        reminderText: String,
        priority: ReminderPriority
    ) = withContext(Dispatchers.IO) {
        if (reminderText.isBlank()) return@withContext
        val entity = StudyReminderEntity(
            levelId = levelId,
            reminderText = reminderText.trim(),
            priorityName = priority.name,
            isCompleted = false
        )
        dao.upsertStudyReminder(entity)
        recordDailyStudyActivity(xpBonus = 15)
    }

    suspend fun toggleStudyReminderCompleted(reminderId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getStudyReminder(reminderId) ?: return@withContext
        val nowCompleted = !existing.isCompleted
        dao.upsertStudyReminder(existing.copy(isCompleted = nowCompleted))
        if (nowCompleted) {
            recordDailyStudyActivity(xpBonus = 10)
        }
    }

    suspend fun deleteStudyReminder(reminderId: String) = withContext(Dispatchers.IO) {
        dao.deleteStudyReminder(reminderId)
    }

    suspend fun postStudentNotification(
        levelId: String,
        category: NotificationCategory,
        title: String,
        message: String
    ): StudentNotificationEntity = withContext(Dispatchers.IO) {
        val notif = StudentNotificationEntity(
            levelId = levelId,
            categoryName = category.name,
            title = title,
            message = message,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )
        dao.upsertNotification(notif)
        notif
    }

    suspend fun markNotificationRead(notificationId: String) = withContext(Dispatchers.IO) {
        val existing = dao.getNotification(notificationId) ?: return@withContext
        if (!existing.isRead) {
            dao.upsertNotification(existing.copy(isRead = true))
        }
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsRead()
    }

    suspend fun clearReadNotifications() = withContext(Dispatchers.IO) {
        dao.clearReadNotifications()
    }

    suspend fun importTeacherEvaluationPacket(rawInput: String, fallbackLevelId: String? = null): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val trimmed = rawInput.trim()
        if (trimmed.isBlank()) {
            return@withContext false to "Please paste a Teacher Evaluation Packet or Sync Code."
        }
        runCatching {
            val obj = JSONObject(trimmed)
            val levelId = obj.optString("levelId", fallbackLevelId ?: "lvl_01_essence")
            val level = CurriculumCatalog.allLevels.find { it.id == levelId }
                ?: CurriculumCatalog.allLevels.first()

            val speaking = obj.optInt("speaking", 85).coerceIn(0, 100)
            val listening = obj.optInt("listening", 88).coerceIn(0, 100)
            val reading = obj.optInt("reading", 84).coerceIn(0, 100)
            val writing = obj.optInt("writing", 82).coerceIn(0, 100)
            val teacherNote = obj.optString("teacherNote", "Updated evaluation from your ETA instructor.")

            saveLocalClassroomGrade(
                level = level,
                speaking = speaking,
                listening = listening,
                reading = reading,
                writing = writing,
                teacherNote = teacherNote,
                unlockNextAutomatically = true
            )

            val mistakesArray = obj.optJSONArray("mistakes")
            var addedMistakes = 0
            if (mistakesArray != null) {
                for (i in 0 until mistakesArray.length()) {
                    val mObj = mistakesArray.optJSONObject(i) ?: continue
                    val skill = mObj.optString("skillArea", "Speaking")
                    val mistake = mObj.optString("mistake", "")
                    val correction = mObj.optString("correction", "")
                    if (mistake.isNotBlank() && correction.isNotBlank()) {
                        addStudentMistake(level, skill, mistake, correction)
                        addedMistakes++
                    }
                }
            }

            val avg = ((speaking + listening + reading + writing) / 4.0).toInt()
            true to "Synced ${level.title}: $avg% Avg" + (if (addedMistakes > 0) " & $addedMistakes correction(s)!" else "!")
        }.getOrElse {
            false to "Invalid Teacher Evaluation JSON format. Expected keys: levelId, speaking, listening, reading, writing, teacherNote."
        }
    }

    suspend fun provisionDeviceWithKey(
        macAddress: String,
        enteredActivationKey: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (!DeviceMacSecurity.isValidMacFormat(macAddress)) {
            return@withContext false to "Invalid MAC address format. Expected XX:XX:XX:XX:XX:XX."
        }
        val normalizedMac = DeviceMacSecurity.normalizeMac(macAddress)
        val expectedKey = DeviceMacSecurity.computeAdminActivationKey(normalizedMac, DeviceRole.STUDENT)
        val normalizedEntered = DeviceMacSecurity.normalizeActivationKey(enteredActivationKey)

        if (normalizedEntered != expectedKey) {
            return@withContext false to "MAC-bound signature mismatch. This key is not valid for Student Phone MAC $normalizedMac."
        }

        val entity = DeviceProvisionEntity(
            id = 1,
            macAddress = normalizedMac,
            hardwareId = currentHardwareId,
            roleName = DeviceRole.STUDENT.name,
            activationSignature = expectedKey,
            provisionedAt = System.currentTimeMillis()
        )
        dao.upsertDeviceProvision(entity)
        true to "Student Phone bound to MAC $normalizedMac!"
    }

    suspend fun provisionDeviceFromJson(jsonText: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        runCatching {
            val obj = JSONObject(jsonText.trim())
            val mac = obj.optString("macAddress", "")
            val roleStr = obj.optString("role", "STUDENT").uppercase(Locale.US)
            if (roleStr.isNotBlank() && roleStr != DeviceRole.STUDENT.name) {
                return@withContext false to "This is the ETA Student App. Only STUDENT provisioning bundles are accepted."
            }
            val key = obj.optString("activationKey", "")
            provisionDeviceWithKey(mac, key)
        }.getOrElse {
            false to "Invalid Desktop Administration JSON format."
        }
    }

    suspend fun clearDeviceProvisioning() = withContext(Dispatchers.IO) {
        dao.upsertDeviceProvision(
            DeviceProvisionEntity(
                id = 1,
                macAddress = detectedMacAddress,
                hardwareId = currentHardwareId,
                roleName = DeviceRole.UNPROVISIONED.name,
                activationSignature = "",
                provisionedAt = 0L
            )
        )
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
        recordDailyStudyActivity(xpBonus = points)
    }

    suspend fun saveCustomMaterialOrNote(
        existingId: String?,
        levelId: String,
        title: String,
        category: MaterialCategory,
        summary: String,
        contentBody: String,
        keyPhrases: List<Pair<String, String>>,
        authorRole: DeviceRole
    ) {
        val serialized = keyPhrases.joinToString(";;") { "${it.first}||${it.second}" }
        val entity = CustomMaterialEntity(
            id = existingId ?: UUID.randomUUID().toString(),
            levelId = levelId,
            title = title.trim(),
            categoryName = category.name,
            summary = summary.trim(),
            contentBody = contentBody.trim(),
            keyPhrasesSerialized = serialized,
            authorRoleName = authorRole.name
        )
        dao.insertCustomMaterial(entity)
        if (existingId == null) {
            addXp(25)
        }
    }

    suspend fun deleteCustomMaterial(materialId: String) {
        dao.deleteCustomMaterial(materialId)
    }

    suspend fun unlockLevelWithCode(
        level: CurriculumLevel,
        enteredCode: String
    ): Boolean {
        val normalized = enteredCode.trim().uppercase(Locale.US)
        val expected = level.teacherUnlockCode.trim().uppercase(Locale.US)
        val expectedWithoutDash = expected.replace("-", "")
        val normalizedWithoutDash = normalized.replace("-", "")

        val matchesCode = normalized == expected ||
            normalizedWithoutDash == expectedWithoutDash

        if (matchesCode) {
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
        val packFile = File(offlinePacksDir, "${level.id}.pack")
        val bundleContent = buildString {
            appendLine("=== OFFLINE LEVEL PACK: ${level.title} (${level.hours} HRS) ===")
            appendLine("Box #${level.orderIndex}: ${level.boxType.title}")
            appendLine("Stage: ${level.stageGroup}")
            appendLine("Badge: ${level.badgeEmoji} ${level.badgeName}")
            level.vocabBankItems.forEach { v ->
                appendLine("Vocab: ${v.word} -> ${v.meaning} (${v.arabicHint})")
            }
            appendLine("Grammar: ${level.grammarContent.ruleTitle} | ${level.grammarContent.formulaPattern}")
            appendLine(level.grammarContent.explanationBody)
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

    suspend fun recordQuizScoreAndMistakes(
        level: CurriculumLevel,
        scorePercent: Int,
        wrongQuestionIds: List<String>
    ) {
        recordRoomPracticeScore(
            level = level,
            roomType = BoxPracticeRoomType.GRAMMAR_PRACTICE,
            scorePercent = scorePercent,
            wrongQuestionIds = wrongQuestionIds
        )
        if (wrongQuestionIds.isNotEmpty()) {
            postStudentNotification(
                levelId = level.id,
                category = NotificationCategory.MISTAKE_LOGGED,
                title = "⚡ ${wrongQuestionIds.size} Practice Mistake(s) Logged in ${level.title}",
                message = "You scored $scorePercent%. Open ${level.title} → Grades & Mistakes to review explanations and flashcards."
            )
        }
    }

    suspend fun addStudentMistake(
        level: CurriculumLevel,
        skillArea: String,
        mistakeText: String,
        correctionText: String
    ) {
        val current = dao.getLevelProgress(level.id) ?: LevelProgressEntity(levelId = level.id, isUnlocked = true)
        val updatedList = current.mistakeItems + StudentMistakeItem(
            skillArea = skillArea.trim(),
            mistakeText = mistakeText.trim(),
            correctionText = correctionText.trim()
        )
        val serialized = serializeMistakes(updatedList)
        dao.upsertLevelProgress(current.copy(mistakesSerialized = serialized))
        postStudentNotification(
            levelId = level.id,
            category = NotificationCategory.MISTAKE_LOGGED,
            title = "🔍 New Mistake Correction (${skillArea.trim()}) — ${level.title}",
            message = "Mistake: \"${mistakeText.trim()}\" → Correction: \"${correctionText.trim()}\". Tap to listen and review."
        )
    }

    suspend fun deleteStudentMistake(level: CurriculumLevel, mistakeId: String) {
        val current = dao.getLevelProgress(level.id) ?: return
        val updatedList = current.mistakeItems.filterNot { it.id == mistakeId }
        dao.upsertLevelProgress(current.copy(mistakesSerialized = serializeMistakes(updatedList)))
    }

    private fun serializeMistakes(list: List<StudentMistakeItem>): String {
        return list.joinToString(";;;") { item ->
            "${item.id}|||${item.skillArea.replace("|", "/")}|||${item.mistakeText.replace("|", "/")}|||${item.correctionText.replace("|", "/")}|||${item.timestamp}"
        }
    }

    suspend fun sendMessageToTeacher(levelId: String, messageText: String) {
        val profile = dao.getStudentProfile() ?: StudentProfileEntity()
        val msg = TeacherMessageEntity(
            levelId = levelId,
            studentName = profile.studentName,
            messageText = messageText.trim()
        )
        dao.upsertTeacherMessage(msg)
        recordDailyStudyActivity(xpBonus = 5)
    }

    suspend fun replyToStudentMessage(message: TeacherMessageEntity, replyText: String) {
        dao.upsertTeacherMessage(message.copy(teacherReply = replyText.trim()))
        val levelTitle = CurriculumCatalog.allLevels.find { it.id == message.levelId }?.title ?: "Unit"
        postStudentNotification(
            levelId = message.levelId,
            category = NotificationCategory.TEACHER_REPLY,
            title = "👨‍🏫 Teacher Replied in $levelTitle",
            message = "\"${replyText.trim()}\""
        )
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
        val passed = average >= 65
        val stars = when {
            average >= 85 -> 3
            average >= 65 -> 2
            average >= 40 -> 1
            else -> 0
        }
        val wasAlreadyPassed = current.isPassed
        val mergedStars = maxOf(current.starsEarned, stars)
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
                isPassed = passed || current.isPassed,
                starsEarned = mergedStars,
                badgeEarnedTimestamp = badgeTimestamp
            )
        )

        postStudentNotification(
            levelId = level.id,
            category = NotificationCategory.GRADE_POSTED,
            title = "📊 Grades Updated for ${level.title}: $average%",
            message = "Speaking $speaking% • Listening $listening% • Reading $reading% • Writing $writing%." +
                (if (teacherNote.isNotBlank()) " Note: \"${teacherNote.trim()}\"" else "")
        )

        if (passed && !wasAlreadyPassed) {
            addXp(150 + (mergedStars * 30))
        }

        if ((mergedStars >= 2 || passed) && unlockNextAutomatically) {
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
