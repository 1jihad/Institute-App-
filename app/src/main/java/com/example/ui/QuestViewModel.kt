package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BoxArchitectureType
import com.example.data.BoxPracticeRoomType
import com.example.data.CurriculumCatalog
import com.example.data.CurriculumLevel
import com.example.data.CustomMaterialEntity
import com.example.data.DeviceMacSecurity
import com.example.data.DeviceProvisionEntity
import com.example.data.DeviceRole
import com.example.data.DiplomaTrack
import com.example.data.FlashcardReviewItem
import com.example.data.FlashcardSourceType
import com.example.data.LevelAssignment
import com.example.data.LevelProgressEntity
import com.example.data.MaterialCategory
import com.example.data.QuestRepository
import com.example.data.ReminderPriority
import com.example.data.SkillQuizQuestion
import com.example.data.StudentNotificationEntity
import com.example.data.StudentProfileEntity
import com.example.data.StudyMaterial
import com.example.data.StudyReminderEntity
import com.example.data.TeacherMessageEntity
import com.example.data.VocabWordItem
import com.example.data.VocabularyEntryEntity
import java.io.InputStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    ADVENTURE_MAP,
    UNIT_BADGES,
    OFFLINE_STORAGE
}

data class LevelNodeState(
    val level: CurriculumLevel,
    val progress: LevelProgressEntity,
    val uploadedVocabulary: List<VocabWordItem> = emptyList(),
    val studentNotes: List<StudyMaterial> = emptyList(),
    val studyReminders: List<StudyReminderEntity> = emptyList(),
    val messages: List<TeacherMessageEntity> = emptyList()
) {
    val isUnlocked: Boolean get() = progress.isUnlocked
    val isDownloaded: Boolean get() = progress.isDownloaded
    val isPassed: Boolean get() = progress.isPassed
    val starsEarned: Int get() = progress.starsEarned
    val materialsDoneCount: Int get() = progress.completedMaterialSet.size
    val assignmentsDoneCount: Int get() = progress.completedAssignmentSet.size

    /**
     * Complete in-app Vocabulary Bank for this Box (combines uploaded .xlsx/.csv words + core box vocabulary).
     */
    val allVocabularyItems: List<VocabWordItem>
        get() = uploadedVocabulary + level.vocabBankItems

    /**
     * Dynamic Vocabulary Practice Questions (Room 3): includes questions generated from uploaded .xlsx/.csv sheets
     * alongside the box's built-in vocabulary practice questions.
     */
    val effectiveVocabPracticeQuestions: List<SkillQuizQuestion>
        get() {
            val allWords = allVocabularyItems
            val generatedFromUploads = uploadedVocabulary.mapIndexed { idx, item ->
                val distractors = allWords
                    .filter { it.word != item.word }
                    .map { it.meaning }
                    .distinct()
                    .take(3)
                    .toMutableList()
                while (distractors.size < 3) {
                    distractors.add("Unrelated definition #${distractors.size + 1}")
                }
                val options = (distractors + item.meaning).sortedBy { it.hashCode() xor idx }
                val correctIdx = options.indexOf(item.meaning).coerceAtLeast(0)
                SkillQuizQuestion(
                    id = "uploaded_vp_${item.id}",
                    prompt = "What is the meaning of '${item.word}'?" +
                        (if (item.arabicHint.isNotBlank()) " (${item.arabicHint})" else ""),
                    options = options,
                    correctIndex = correctIdx,
                    explanation = "'${item.word}' means: ${item.meaning}. Example: ${item.exampleSentence}"
                )
            }
            return generatedFromUploads + level.vocabPracticeQuestions
        }

    val completionFraction: Float
        get() {
            return when (level.boxType) {
                BoxArchitectureType.GENERAL_4_ROOMS -> {
                    var done = 0
                    if (progress.vocabRoomCompleted || progress.completedMaterialSet.isNotEmpty()) done++
                    if (progress.grammarRoomCompleted || progress.completedMaterialSet.size >= 2) done++
                    if (progress.vocabPracticeBestScore >= 60) done++
                    if (progress.grammarPracticeBestScore >= 60) done++
                    (done / 4f).coerceIn(0f, 1f)
                }
                BoxArchitectureType.SKILLS_RW_2_ROOMS -> {
                    var done = 0
                    if (progress.grammarRoomCompleted || progress.completedMaterialSet.isNotEmpty()) done++
                    if (progress.rwPracticeBestScore >= 60) done++
                    (done / 2f).coerceIn(0f, 1f)
                }
                BoxArchitectureType.SKILLS_LS_2_ROOMS -> {
                    var done = 0
                    if (progress.listeningPracticeBestScore >= 60) done++
                    if (progress.speakingPracticeBestScore >= 60) done++
                    (done / 2f).coerceIn(0f, 1f)
                }
            }
        }

    val reviewFlashcards: List<FlashcardReviewItem>
        get() {
            val list = mutableListOf<FlashcardReviewItem>()
            // 1. Uploaded .xlsx/.csv and built-in Vocabulary Bank items
            allVocabularyItems.forEach { v ->
                list.add(
                    FlashcardReviewItem(
                        id = "vocab_fc_${v.id}",
                        frontPrompt = v.word,
                        backAnswer = buildString {
                            append(v.meaning)
                            if (v.arabicHint.isNotBlank()) append("\n(${v.arabicHint})")
                            if (v.exampleSentence.isNotBlank()) append("\nExample: ${v.exampleSentence}")
                        },
                        spokenText = v.word,
                        sourceType = FlashcardSourceType.UNIT_PHRASE,
                        skillTag = v.sourceLabel
                    )
                )
            }
            // 2. Student personal note flashcards
            studentNotes.forEach { note ->
                note.keyPhrases.forEachIndexed { idx, (term, definition) ->
                    list.add(
                        FlashcardReviewItem(
                            id = "note_${note.id}_$idx",
                            frontPrompt = term,
                            backAnswer = definition,
                            spokenText = term,
                            sourceType = FlashcardSourceType.PERSONAL_NOTE,
                            skillTag = "My Note"
                        )
                    )
                }
            }
            // 3. Teacher-logged mistake corrections
            progress.mistakeItems.forEach { mistake ->
                list.add(
                    FlashcardReviewItem(
                        id = "mistake_${mistake.id}",
                        frontPrompt = mistake.mistakeText,
                        backAnswer = mistake.correctionText,
                        spokenText = mistake.correctionText,
                        sourceType = FlashcardSourceType.MISTAKE_CORRECTION,
                        skillTag = mistake.skillArea
                    )
                )
            }
            return list
        }
}

data class QuestUiState(
    val profile: StudentProfileEntity = StudentProfileEntity(),
    val deviceProvision: DeviceProvisionEntity? = null,
    val detectedMacAddress: String = "02:00:00:00:00:00",
    val currentHardwareId: String = "",
    val isProvisioningModalOpen: Boolean = false,
    val isOnboardingModalOpen: Boolean = false,
    val isNotificationCenterOpen: Boolean = false,
    val notifications: List<StudentNotificationEntity> = emptyList(),
    val levels: List<LevelNodeState> = emptyList(),
    val selectedTrackFilter: DiplomaTrack = DiplomaTrack.TOEFL_PREP, // Default shows all 21 boxes in sequence!
    val activeTab: AppTab = AppTab.ADVENTURE_MAP,
    val openedLevelId: String? = null,
    val openedLevelInitialTab: Int = 0,
    val downloadingLevelId: String? = null,
    val downloadProgress: Float = 0f,
    val bannerMessage: String? = null,
    val newlyEarnedBadgeLevel: CurriculumLevel? = null,
    val newlyUnlockedLevelId: String? = null,
    val isSplashShowing: Boolean = true
) {
    val isDeviceAuthorized: Boolean
        get() = DeviceMacSecurity.verifyProvisioning(deviceProvision, currentHardwareId)

    val deviceRole: DeviceRole
        get() = if (isDeviceAuthorized) DeviceRole.STUDENT else DeviceRole.UNPROVISIONED

    val isStudentPhone: Boolean
        get() = isDeviceAuthorized

    val boundMacAddress: String
        get() = deviceProvision?.macAddress?.takeIf { it.isNotBlank() } ?: detectedMacAddress

    val unreadNotificationsCount: Int
        get() = notifications.count { !it.isRead }

    val openedLevelNode: LevelNodeState?
        get() = levels.find { it.level.id == openedLevelId }

    val newlyUnlockedLevelNode: LevelNodeState?
        get() = levels.find { it.level.id == newlyUnlockedLevelId }

    val unlockedCount: Int
        get() = levels.count { it.isUnlocked }

    val badgesEarnedCount: Int
        get() = levels.count { it.isPassed }

    val totalStars: Int
        get() = levels.sumOf { it.starsEarned }

    val maxTotalStars: Int
        get() = levels.size * 3

    val totalHoursCompleted: Int
        get() = levels.filter { it.isPassed }.sumOf { it.level.hours }

    val totalHoursInCurriculum: Int
        get() = levels.sumOf { it.level.hours }

    val downloadedPacksCount: Int
        get() = levels.count { it.isDownloaded }

    val totalCachedMb: Float
        get() = levels.filter { it.isDownloaded }.sumOf { it.level.packSizeMb.toDouble() }.toFloat()

    val isPtpRevealed: Boolean
        get() = true

    val visibleDiplomaTracks: List<DiplomaTrack>
        get() = listOf(
            DiplomaTrack.TOEFL_PREP, // All 21 Boxes
            DiplomaTrack.INTERMEDIATE_DIPLOMA, // Boxes 1-13
            DiplomaTrack.ADVANCED_DIPLOMA, // Boxes 14-21
            DiplomaTrack.PTP_PROGRAM // Skills 1-6 Only
        )

    fun isDiplomaUnlocked(track: DiplomaTrack): Boolean = true

    fun firstLevelOfDiploma(track: DiplomaTrack): CurriculumLevel? =
        levels.firstOrNull { it.level.track == track }?.level
}

private data class LocalActionState(
    val selectedTrackFilter: DiplomaTrack = DiplomaTrack.INTERMEDIATE_DIPLOMA,
    val activeTab: AppTab = AppTab.ADVENTURE_MAP,
    val openedLevelId: String? = null,
    val openedLevelInitialTab: Int = 0,
    val downloadingLevelId: String? = null,
    val downloadProgress: Float = 0f,
    val bannerMessage: String? = null,
    val newlyEarnedBadgeLevel: CurriculumLevel? = null,
    val newlyUnlockedLevelId: String? = null,
    val isSplashShowing: Boolean = true,
    val isProvisioningModalOpen: Boolean = false,
    val isOnboardingModalOpen: Boolean = false,
    val isNotificationCenterOpen: Boolean = false
)

private data class CombinedRepoData(
    val profile: StudentProfileEntity?,
    val progressList: List<LevelProgressEntity>,
    val customMaterials: List<CustomMaterialEntity>,
    val studyReminders: List<StudyReminderEntity>,
    val notifications: List<StudentNotificationEntity>,
    val vocabEntries: List<VocabularyEntryEntity>,
    val deviceProvision: DeviceProvisionEntity?,
    val messages: List<TeacherMessageEntity>
)

class QuestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuestRepository = QuestRepository(
        dao = AppDatabase.getInstance(application).questDao(),
        appContext = application.applicationContext
    )

    private val actionState = MutableStateFlow(LocalActionState())

    private val primaryRepoFlow = combine(
        repository.profileFlow,
        repository.allProgressFlow,
        repository.allCustomMaterialsFlow,
        repository.allStudyRemindersFlow,
        repository.allNotificationsFlow
    ) { profile, progressList, customMaterials, studyReminders, notifications ->
        CombinedRepoData(
            profile = profile,
            progressList = progressList,
            customMaterials = customMaterials,
            studyReminders = studyReminders,
            notifications = notifications,
            vocabEntries = emptyList(),
            deviceProvision = null,
            messages = emptyList()
        )
    }

    private val repoDataFlow = combine(
        primaryRepoFlow,
        repository.allVocabularyEntriesFlow,
        repository.deviceProvisionFlow,
        repository.allTeacherMessagesFlow
    ) { base, vocabEntries, deviceProvision, messages ->
        base.copy(
            vocabEntries = vocabEntries,
            deviceProvision = deviceProvision,
            messages = messages
        )
    }

    val uiState: StateFlow<QuestUiState> = combine(
        repoDataFlow,
        actionState
    ) { repo, local ->
        val progressMap = repo.progressList.associateBy { it.levelId }
        val customByLevel = repo.customMaterials.groupBy { it.levelId }
        val vocabByLevel = repo.vocabEntries.groupBy { it.levelId }
        val remindersByLevel = repo.studyReminders.groupBy { it.levelId }
        val messagesByLevel = repo.messages.groupBy { it.levelId }

        val levelNodes = CurriculumCatalog.allLevels.mapIndexed { idx, level ->
            val allCustomForLevel = customByLevel[level.id]?.map { it.toStudyMaterial() } ?: emptyList()
            val teacherMaterials = allCustomForLevel.filter { it.authorRole == DeviceRole.TEACHER }
            val studentPersonalNotes = allCustomForLevel.filter { it.authorRole == DeviceRole.STUDENT }
            val uploadedVocabForLevel = vocabByLevel[level.id]?.map { it.toVocabWordItem() } ?: emptyList()

            val mergedLevel = level.copy(
                materials = level.materials + teacherMaterials
            )
            val entity = progressMap[level.id] ?: LevelProgressEntity(
                levelId = level.id,
                isUnlocked = idx == 0
            )
            LevelNodeState(
                level = mergedLevel,
                progress = entity,
                uploadedVocabulary = uploadedVocabForLevel,
                studentNotes = studentPersonalNotes,
                studyReminders = remindersByLevel[level.id] ?: emptyList(),
                messages = messagesByLevel[level.id] ?: emptyList()
            )
        }
        val profile = repo.profile ?: StudentProfileEntity()
        QuestUiState(
            profile = profile,
            deviceProvision = repo.deviceProvision,
            detectedMacAddress = repository.detectedMacAddress,
            currentHardwareId = repository.currentHardwareId,
            isProvisioningModalOpen = local.isProvisioningModalOpen,
            isOnboardingModalOpen = local.isOnboardingModalOpen || !profile.isOnboardingCompleted,
            isNotificationCenterOpen = local.isNotificationCenterOpen,
            notifications = repo.notifications,
            levels = levelNodes,
            selectedTrackFilter = local.selectedTrackFilter,
            activeTab = local.activeTab,
            openedLevelId = local.openedLevelId,
            openedLevelInitialTab = local.openedLevelInitialTab,
            downloadingLevelId = local.downloadingLevelId,
            downloadProgress = local.downloadProgress,
            bannerMessage = local.bannerMessage,
            newlyEarnedBadgeLevel = local.newlyEarnedBadgeLevel,
            newlyUnlockedLevelId = local.newlyUnlockedLevelId,
            isSplashShowing = local.isSplashShowing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuestUiState(
            detectedMacAddress = repository.detectedMacAddress,
            currentHardwareId = repository.currentHardwareId,
            levels = CurriculumCatalog.allLevels.mapIndexed { idx, level ->
                LevelNodeState(
                    level = level,
                    progress = LevelProgressEntity(levelId = level.id, isUnlocked = idx == 0)
                )
            }
        )
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun dismissSplash() {
        actionState.value = actionState.value.copy(isSplashShowing = false)
    }

    fun openProvisioningModal() {
        actionState.value = actionState.value.copy(isProvisioningModalOpen = true)
    }

    fun closeProvisioningModal() {
        actionState.value = actionState.value.copy(isProvisioningModalOpen = false)
    }

    fun openOnboardingModal() {
        actionState.value = actionState.value.copy(isOnboardingModalOpen = true)
    }

    fun closeOnboardingModal() {
        actionState.value = actionState.value.copy(isOnboardingModalOpen = false)
    }

    fun completeStudentOnboarding(
        studentName: String,
        avatarEmoji: String,
        academicStage: String,
        learningGoal: String,
        optionalAdminCode: String
    ) {
        viewModelScope.launch {
            val (boxOrder, unlockedTitle) = repository.completeStudentOnboarding(
                studentName = studentName,
                avatarEmoji = avatarEmoji,
                academicStage = academicStage,
                learningGoal = learningGoal,
                optionalAdminCode = optionalAdminCode
            )
            val unlockedId = CurriculumCatalog.allLevels.find { it.orderIndex == boxOrder }?.id
            val welcomeMsg = if (unlockedTitle != null && boxOrder > 1) {
                "🎓 Welcome $studentName! Admin Code accepted — Box $boxOrder ($unlockedTitle) is unlocked!"
            } else {
                "🎓 Welcome $studentName! Your 21-Box journey begins at Box 1 (Essence)!"
            }
            actionState.value = actionState.value.copy(
                isOnboardingModalOpen = false,
                newlyUnlockedLevelId = unlockedId,
                bannerMessage = welcomeMsg
            )
        }
    }

    fun importVocabularyFileStream(
        inputStream: InputStream,
        fileName: String,
        levelId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.importVocabularySpreadsheetStream(
                inputStream = inputStream,
                fileNameOrMime = fileName,
                defaultLevelId = levelId
            )
            if (ok) {
                actionState.value = actionState.value.copy(bannerMessage = "📚 $msg")
            }
            onResult(ok, msg)
        }
    }

    fun importVocabularyCsvText(
        csvText: String,
        levelId: String,
        sourceName: String = "Teacher Sheet (.csv)",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.importVocabularyCsvText(csvText, levelId, sourceName)
            if (ok) {
                actionState.value = actionState.value.copy(bannerMessage = "📚 $msg")
            }
            onResult(ok, msg)
        }
    }

    fun addCustomVocabularyWord(
        levelId: String,
        word: String,
        meaning: String,
        arabicHint: String,
        exampleSentence: String
    ) {
        viewModelScope.launch {
            repository.addSingleVocabularyEntry(
                levelId = levelId,
                word = word,
                meaning = meaning,
                arabicHint = arabicHint,
                exampleSentence = exampleSentence,
                sourceLabel = "My Vocabulary"
            )
            actionState.value = actionState.value.copy(
                bannerMessage = "✨ Added '$word' to your in-app Vocabulary Bank (+15 XP)!"
            )
        }
    }

    fun deleteVocabularyWord(entryId: String) {
        viewModelScope.launch {
            repository.deleteVocabularyEntry(entryId)
            actionState.value = actionState.value.copy(
                bannerMessage = "Removed word from Vocabulary Bank."
            )
        }
    }

    fun markStudyRoomCompleted(level: CurriculumLevel, isVocabRoom: Boolean) {
        viewModelScope.launch {
            repository.markStudyRoomCompleted(level, isVocabRoom)
            val roomName = if (isVocabRoom) "Room 1 (Vocabulary)" else "Grammar Explanation Room"
            actionState.value = actionState.value.copy(
                bannerMessage = "✅ $roomName completed (+20 XP)!"
            )
        }
    }

    fun submitRoomPracticeScore(
        level: CurriculumLevel,
        roomType: BoxPracticeRoomType,
        scorePercent: Int,
        wrongQuestionIds: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val (stars, newlyUnlockedNext) = repository.recordRoomPracticeScore(
                level = level,
                roomType = roomType,
                scorePercent = scorePercent,
                wrongQuestionIds = wrongQuestionIds
            )
            val starDisplay = "⭐".repeat(stars.coerceAtLeast(1))
            val msg = if (newlyUnlockedNext != null) {
                "🎉 You earned $stars/3 Stars ($starDisplay) in ${level.title} and UNLOCKED Box ${newlyUnlockedNext.orderIndex}: ${newlyUnlockedNext.title}!"
            } else if (stars >= 2) {
                "🌟 Great job! $scorePercent% ($stars/3 Stars $starDisplay) in ${level.title}!"
            } else {
                "Score: $scorePercent% ($stars/3 Stars). Earn at least 2/3 Stars (65%+) to unlock the next Box!"
            }
            actionState.value = actionState.value.copy(
                newlyUnlockedLevelId = newlyUnlockedNext?.id ?: actionState.value.newlyUnlockedLevelId,
                newlyEarnedBadgeLevel = if (newlyUnlockedNext != null) level else actionState.value.newlyEarnedBadgeLevel,
                bannerMessage = msg
            )
        }
    }

    fun openNotificationCenter() {
        actionState.value = actionState.value.copy(isNotificationCenterOpen = true)
    }

    fun closeNotificationCenter() {
        actionState.value = actionState.value.copy(isNotificationCenterOpen = false)
    }

    fun openNotificationTarget(notification: StudentNotificationEntity) {
        viewModelScope.launch {
            repository.markNotificationRead(notification.id)
            val targetLevel = CurriculumCatalog.allLevels.find { it.id == notification.levelId }
            if (targetLevel != null) {
                val currentProg = uiState.value.levels.find { it.level.id == targetLevel.id }?.progress
                if (currentProg?.isDownloaded != true) {
                    repository.downloadLevelPack(targetLevel)
                }
                actionState.value = actionState.value.copy(
                    isNotificationCenterOpen = false,
                    openedLevelId = targetLevel.id,
                    openedLevelInitialTab = 2
                )
            } else {
                actionState.value = actionState.value.copy(isNotificationCenterOpen = false)
            }
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun clearReadNotifications() {
        viewModelScope.launch {
            repository.clearReadNotifications()
        }
    }

    fun importTeacherEvaluationPacket(
        jsonPacket: String,
        fallbackLevelId: String? = null,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.importTeacherEvaluationPacket(jsonPacket, fallbackLevelId)
            if (ok) {
                StudentSystemNotificationDispatcher.notifyStudent(
                    context = getApplication(),
                    title = "📊 New Teacher Grades & Corrections!",
                    message = msg
                )
                actionState.value = actionState.value.copy(
                    bannerMessage = "🔔 $msg"
                )
            }
            onResult(ok, msg)
        }
    }

    fun updateExamGoal(goalTitle: String, daysFromToday: Int) {
        viewModelScope.launch {
            repository.updateExamGoal(goalTitle, daysFromToday)
            actionState.value = actionState.value.copy(
                bannerMessage = "🎯 Exam Countdown set: $goalTitle ($daysFromToday days left)!"
            )
        }
    }

    fun addStudyReminder(
        levelId: String,
        reminderText: String,
        priority: ReminderPriority
    ) {
        viewModelScope.launch {
            repository.addStudyReminder(levelId, reminderText, priority)
            actionState.value = actionState.value.copy(
                bannerMessage = "⏰ Study reminder saved (+15 XP)!"
            )
        }
    }

    fun toggleStudyReminder(reminderId: String) {
        viewModelScope.launch {
            repository.toggleStudyReminderCompleted(reminderId)
        }
    }

    fun deleteStudyReminder(reminderId: String) {
        viewModelScope.launch {
            repository.deleteStudyReminder(reminderId)
            actionState.value = actionState.value.copy(
                bannerMessage = "Removed study reminder."
            )
        }
    }

    fun recordFlashcardMastered() {
        viewModelScope.launch {
            repository.recordDailyStudyActivity(xpBonus = 10)
        }
    }

    fun activateDeviceWithKey(
        macAddress: String,
        activationKey: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.provisionDeviceWithKey(macAddress, activationKey)
            if (ok) {
                actionState.value = actionState.value.copy(
                    isProvisioningModalOpen = false,
                    bannerMessage = "🔐 $msg"
                )
            }
            onResult(ok, msg)
        }
    }

    fun activateDeviceFromJson(
        jsonContent: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val (ok, msg) = repository.provisionDeviceFromJson(jsonContent)
            if (ok) {
                actionState.value = actionState.value.copy(
                    isProvisioningModalOpen = false,
                    bannerMessage = "🔐 $msg"
                )
            }
            onResult(ok, msg)
        }
    }

    fun resetDeviceProvisioning() {
        viewModelScope.launch {
            repository.clearDeviceProvisioning()
            actionState.value = actionState.value.copy(
                isProvisioningModalOpen = true,
                openedLevelId = null,
                bannerMessage = "Device unbound. Ready for Administration Desk provisioning."
            )
        }
    }

    fun selectTab(tab: AppTab) {
        actionState.value = actionState.value.copy(
            activeTab = tab,
            openedLevelId = null
        )
    }

    fun selectTrackFilter(track: DiplomaTrack) {
        actionState.value = actionState.value.copy(selectedTrackFilter = track)
    }

    fun openLevelDetail(levelId: String, initialTab: Int = 0) {
        actionState.value = actionState.value.copy(
            openedLevelId = levelId,
            openedLevelInitialTab = initialTab,
            newlyUnlockedLevelId = null
        )
    }

    fun closeLevelDetail() {
        actionState.value = actionState.value.copy(
            openedLevelId = null,
            openedLevelInitialTab = 0
        )
    }

    fun dismissBanner() {
        actionState.value = actionState.value.copy(bannerMessage = null)
    }

    fun dismissUnlockedLevelReveal() {
        actionState.value = actionState.value.copy(newlyUnlockedLevelId = null)
    }

    fun dismissBadgeCelebration() {
        actionState.value = actionState.value.copy(newlyEarnedBadgeLevel = null)
    }

    fun updateProfile(name: String, avatarEmoji: String) {
        viewModelScope.launch {
            repository.updateStudentIdentity(name, avatarEmoji)
            actionState.value = actionState.value.copy(
                bannerMessage = "Profile updated to $avatarEmoji $name!"
            )
        }
    }

    fun saveStudentNote(
        existingId: String?,
        levelId: String,
        title: String,
        category: MaterialCategory,
        summary: String,
        contentBody: String,
        keyPhrases: List<Pair<String, String>>
    ) {
        viewModelScope.launch {
            repository.saveCustomMaterialOrNote(
                existingId = existingId,
                levelId = levelId,
                title = title,
                category = category,
                summary = summary,
                contentBody = contentBody,
                keyPhrases = keyPhrases,
                authorRole = DeviceRole.STUDENT
            )
            val verb = if (existingId == null) "Added" else "Updated"
            actionState.value = actionState.value.copy(
                bannerMessage = "✨ $verb study note: $title!"
            )
        }
    }

    fun deleteStudentNote(materialId: String) {
        viewModelScope.launch {
            repository.deleteCustomMaterial(materialId)
            actionState.value = actionState.value.copy(
                bannerMessage = "Removed personal note."
            )
        }
    }

    fun attemptUnlockLevel(
        level: CurriculumLevel,
        code: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val unlocked = repository.unlockLevelWithCode(
                level = level,
                enteredCode = code
            )
            if (unlocked) {
                actionState.value = actionState.value.copy(
                    newlyUnlockedLevelId = level.id,
                    bannerMessage = "🎉 Admin Code accepted! Unlocked Box ${level.orderIndex}: ${level.title}!"
                )
            }
            onResult(unlocked)
        }
    }

    fun attemptUnlockDiploma(
        track: DiplomaTrack,
        code: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val matchedLevel = CurriculumCatalog.allLevels.firstOrNull { lvl ->
                val norm = code.trim().uppercase().replace("-", "")
                val exp = lvl.teacherUnlockCode.trim().uppercase().replace("-", "")
                norm == exp
            }
            if (matchedLevel != null) {
                repository.unlockLevelWithCode(matchedLevel, code)
                actionState.value = actionState.value.copy(
                    newlyUnlockedLevelId = matchedLevel.id,
                    bannerMessage = "🔓 Admin Code verified! Unlocked Box ${matchedLevel.orderIndex}: ${matchedLevel.title}!"
                )
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun downloadLevelContent(level: CurriculumLevel) {
        if (actionState.value.downloadingLevelId != null) return
        viewModelScope.launch {
            actionState.value = actionState.value.copy(
                downloadingLevelId = level.id,
                downloadProgress = 0.1f
            )
            val steps = listOf(0.3f, 0.65f, 1.0f)
            for (step in steps) {
                delay(160)
                actionState.value = actionState.value.copy(downloadProgress = step)
            }
            repository.downloadLevelPack(level)
            actionState.value = actionState.value.copy(
                downloadingLevelId = null,
                downloadProgress = 0f,
                bannerMessage = "✅ Box ${level.orderIndex} (${level.title}) ready for offline study!"
            )
        }
    }

    fun removeOfflinePack(level: CurriculumLevel) {
        viewModelScope.launch {
            repository.removeOfflinePack(level)
            actionState.value = actionState.value.copy(
                bannerMessage = "🧹 Freed ${level.packSizeMb} MB from ${level.title}."
            )
        }
    }

    fun clearCompletedPacks() {
        viewModelScope.launch {
            repository.clearAllCompletedOfflinePacks()
            actionState.value = actionState.value.copy(
                bannerMessage = "✨ Cleared cached packs for passed levels!"
            )
        }
    }

    fun toggleMaterialDone(level: CurriculumLevel, material: StudyMaterial) {
        viewModelScope.launch {
            repository.toggleMaterialCompleted(level, material)
        }
    }

    fun toggleAssignmentDone(level: CurriculumLevel, assignment: LevelAssignment) {
        viewModelScope.launch {
            repository.toggleAssignmentSubmitted(level, assignment)
        }
    }

    fun submitSkillQuizScore(
        level: CurriculumLevel,
        scorePercent: Int,
        wrongQuestionIds: List<String>
    ) {
        submitRoomPracticeScore(
            level = level,
            roomType = BoxPracticeRoomType.GRAMMAR_PRACTICE,
            scorePercent = scorePercent,
            wrongQuestionIds = wrongQuestionIds
        )
    }

    fun sendMessageToTeacher(levelId: String, messageText: String) {
        viewModelScope.launch {
            repository.sendMessageToTeacher(levelId, messageText)
            actionState.value = actionState.value.copy(
                bannerMessage = "✉️ Message sent to Teacher!"
            )
        }
    }
}
