package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CurriculumCatalog
import com.example.data.CurriculumLevel
import com.example.data.DiplomaTrack
import com.example.data.LevelAssignment
import com.example.data.LevelProgressEntity
import com.example.data.QuestRepository
import com.example.data.StudentProfileEntity
import com.example.data.StudyMaterial
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
    OFFLINE_STORAGE,
    INSTITUTE_POSTER
}

data class LevelNodeState(
    val level: CurriculumLevel,
    val progress: LevelProgressEntity
) {
    val isUnlocked: Boolean get() = progress.isUnlocked
    val isDownloaded: Boolean get() = progress.isDownloaded
    val isPassed: Boolean get() = progress.isPassed
    val starsEarned: Int get() = progress.starsEarned
    val materialsDoneCount: Int get() = progress.completedMaterialSet.size
    val assignmentsDoneCount: Int get() = progress.completedAssignmentSet.size

    val completionFraction: Float
        get() {
            val totalItems = level.materials.size + level.assignments.size + 2
            var done = materialsDoneCount + assignmentsDoneCount
            if (progress.quizBestScore >= 66) done += 1
            if (progress.isPassed) done += 1
            return (done.toFloat() / totalItems.coerceAtLeast(1)).coerceIn(0f, 1f)
        }
}

data class QuestUiState(
    val profile: StudentProfileEntity = StudentProfileEntity(),
    val levels: List<LevelNodeState> = emptyList(),
    val selectedTrackFilter: DiplomaTrack = DiplomaTrack.INTERMEDIATE_DIPLOMA,
    val activeTab: AppTab = AppTab.ADVENTURE_MAP,
    val openedLevelId: String? = null,
    val downloadingLevelId: String? = null,
    val downloadProgress: Float = 0f,
    val bannerMessage: String? = null,
    val newlyEarnedBadgeLevel: CurriculumLevel? = null
) {
    val openedLevelNode: LevelNodeState?
        get() = levels.find { it.level.id == openedLevelId }

    val unlockedCount: Int
        get() = levels.count { it.isUnlocked }

    val badgesEarnedCount: Int
        get() = levels.count { it.isPassed }

    val totalStars: Int
        get() = levels.sumOf { it.starsEarned }

    val totalHoursCompleted: Int
        get() = levels.filter { it.isPassed }.sumOf { it.level.hours }

    val totalHoursInCurriculum: Int
        get() = levels.sumOf { it.level.hours }

    val downloadedPacksCount: Int
        get() = levels.count { it.isDownloaded }

    val totalCachedMb: Float
        get() = levels.filter { it.isDownloaded }.sumOf { it.level.packSizeMb.toDouble() }.toFloat()

    /**
     * Checks if a Diploma Track is unlocked.
     * - INTERMEDIATE_DIPLOMA is unlocked by default.
     * - Any subsequent Diploma is locked (its levels are hidden) until either:
     *   1) All levels of the preceding Diploma are passed, OR
     *   2) A teacher unlock code has unlocked at least one level in that Diploma (or a later one).
     */
    fun isDiplomaUnlocked(track: DiplomaTrack): Boolean {
        val tracks = DiplomaTrack.entries
        val idx = tracks.indexOf(track)
        if (idx <= 0) return true

        // Unlocked if any level in this track or a higher track is already unlocked
        val unlockedInThisOrLater = levels.any {
            tracks.indexOf(it.level.track) >= idx && it.isUnlocked
        }
        if (unlockedInThisOrLater) return true

        // Or unlocked if all levels in the immediately preceding track are passed
        val prevTrack = tracks[idx - 1]
        val prevLevels = levels.filter { it.level.track == prevTrack }
        return prevLevels.isNotEmpty() && prevLevels.all { it.isPassed }
    }

    fun firstLevelOfDiploma(track: DiplomaTrack): CurriculumLevel? =
        levels.firstOrNull { it.level.track == track }?.level
}

private data class LocalActionState(
    val selectedTrackFilter: DiplomaTrack = DiplomaTrack.INTERMEDIATE_DIPLOMA,
    val activeTab: AppTab = AppTab.ADVENTURE_MAP,
    val openedLevelId: String? = null,
    val downloadingLevelId: String? = null,
    val downloadProgress: Float = 0f,
    val bannerMessage: String? = null,
    val newlyEarnedBadgeLevel: CurriculumLevel? = null
)

class QuestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuestRepository = QuestRepository(
        dao = AppDatabase.getInstance(application).questDao(),
        appContext = application.applicationContext
    )

    private val actionState = MutableStateFlow(LocalActionState())

    val uiState: StateFlow<QuestUiState> = combine(
        repository.profileFlow,
        repository.allProgressFlow,
        actionState
    ) { profile, progressList, local ->
        val progressMap = progressList.associateBy { it.levelId }
        val levelNodes = CurriculumCatalog.allLevels.mapIndexed { idx, level ->
            val entity = progressMap[level.id] ?: LevelProgressEntity(
                levelId = level.id,
                isUnlocked = idx == 0
            )
            LevelNodeState(level = level, progress = entity)
        }
        QuestUiState(
            profile = profile ?: StudentProfileEntity(),
            levels = levelNodes,
            selectedTrackFilter = local.selectedTrackFilter,
            activeTab = local.activeTab,
            openedLevelId = local.openedLevelId,
            downloadingLevelId = local.downloadingLevelId,
            downloadProgress = local.downloadProgress,
            bannerMessage = local.bannerMessage,
            newlyEarnedBadgeLevel = local.newlyEarnedBadgeLevel
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuestUiState(
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

    fun selectTab(tab: AppTab) {
        actionState.value = actionState.value.copy(
            activeTab = tab,
            openedLevelId = null
        )
    }

    fun selectTrackFilter(track: DiplomaTrack) {
        actionState.value = actionState.value.copy(selectedTrackFilter = track)
    }

    fun openLevelDetail(levelId: String) {
        actionState.value = actionState.value.copy(openedLevelId = levelId)
    }

    fun closeLevelDetail() {
        actionState.value = actionState.value.copy(openedLevelId = null)
    }

    fun dismissBanner() {
        actionState.value = actionState.value.copy(bannerMessage = null)
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

    fun attemptUnlockLevel(
        level: CurriculumLevel,
        code: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val unlocked = repository.unlockLevelWithCode(level, code)
            if (unlocked) {
                actionState.value = actionState.value.copy(
                    selectedTrackFilter = level.track,
                    openedLevelId = level.id,
                    bannerMessage = "🎉 Unlocked ${level.title}!"
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
            val trackLevels = CurriculumCatalog.allLevels.filter { it.track == track }
            val matchedLevel = trackLevels.firstOrNull { lvl ->
                val norm = code.trim().uppercase().replace("-", "")
                val exp = lvl.teacherUnlockCode.trim().uppercase().replace("-", "")
                norm == exp || code.trim().uppercase() == "TEACH-ALL"
            }
            if (matchedLevel != null) {
                repository.unlockLevelWithCode(matchedLevel, code)
                actionState.value = actionState.value.copy(
                    selectedTrackFilter = track,
                    bannerMessage = "🔓 ${track.title} Unlocked!"
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
                bannerMessage = "✅ ${level.title} downloaded for offline use!"
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

    fun submitSkillQuizScore(level: CurriculumLevel, scorePercent: Int) {
        viewModelScope.launch {
            repository.recordQuizScore(level, scorePercent)
            actionState.value = actionState.value.copy(
                bannerMessage = "⚡ Quiz Score: $scorePercent%!"
            )
        }
    }

    fun saveInClassGrade(
        level: CurriculumLevel,
        speaking: Int,
        listening: Int,
        reading: Int,
        writing: Int,
        teacherNote: String,
        unlockNextAutomatically: Boolean
    ) {
        viewModelScope.launch {
            val wasPassedBefore = uiState.value.levels.find { it.level.id == level.id }?.isPassed == true
            repository.saveLocalClassroomGrade(
                level = level,
                speaking = speaking,
                listening = listening,
                reading = reading,
                writing = writing,
                teacherNote = teacherNote,
                unlockNextAutomatically = unlockNextAutomatically
            )
            val average = ((speaking + listening + reading + writing) / 4.0).toInt()
            if (average >= 60 && !wasPassedBefore) {
                actionState.value = actionState.value.copy(
                    newlyEarnedBadgeLevel = level,
                    bannerMessage = "🏅 Badge Earned: ${level.badgeEmoji} ${level.badgeName}!"
                )
            } else {
                actionState.value = actionState.value.copy(
                    bannerMessage = "📋 Grade ($average%) saved for ${level.title}."
                )
            }
        }
    }
}
