package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.BoxArchitectureType
import com.example.data.BoxPracticeRoomType
import com.example.data.CurriculumCatalog
import com.example.data.DeviceMacSecurity
import com.example.data.DeviceProvisionEntity
import com.example.data.DeviceRole
import com.example.data.QuestRepository
import com.example.data.ReminderPriority
import com.example.data.VocabSpreadsheetImporter
import com.example.ui.screens.VoiceAnswerAccuracyEvaluator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context and verify 21 approved curriculum boxes`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Excel ETA", appName)

        // Verify all 21 official institute boxes exist in exact order
        assertEquals(21, CurriculumCatalog.allLevels.size)
        assertEquals("Essence", CurriculumCatalog.allLevels.first().title)
        assertEquals("Skills 6", CurriculumCatalog.allLevels.last().title)

        // Verify Skills 1, 3, 5 RW boxes (indices 4, 10, 17 -> 0-based 3, 9, 16)
        assertEquals(BoxArchitectureType.SKILLS_RW_2_ROOMS, CurriculumCatalog.allLevels[3].boxType)
        assertEquals("Skills 1 RW", CurriculumCatalog.allLevels[3].title)
        assertEquals(BoxArchitectureType.SKILLS_RW_2_ROOMS, CurriculumCatalog.allLevels[9].boxType)
        assertEquals("Skills 3 RW", CurriculumCatalog.allLevels[9].title)
        assertEquals(BoxArchitectureType.SKILLS_RW_2_ROOMS, CurriculumCatalog.allLevels[16].boxType)
        assertEquals("Skills 5 RW", CurriculumCatalog.allLevels[16].title)

        // Verify Skills 2, 4, 6 L&S boxes (indices 7, 13, 21 -> 0-based 6, 12, 20)
        assertEquals(BoxArchitectureType.SKILLS_LS_2_ROOMS, CurriculumCatalog.allLevels[6].boxType)
        assertEquals("Skills 2", CurriculumCatalog.allLevels[6].title)
        assertEquals(BoxArchitectureType.SKILLS_LS_2_ROOMS, CurriculumCatalog.allLevels[12].boxType)
        assertEquals("Skills 4", CurriculumCatalog.allLevels[12].title)
        assertEquals(BoxArchitectureType.SKILLS_LS_2_ROOMS, CurriculumCatalog.allLevels[20].boxType)
        assertEquals("Skills 6", CurriculumCatalog.allLevels[20].title)

        assertTrue(CurriculumCatalog.allLevels.all { it.teacherUnlockCode.isNotBlank() && it.materials.isNotEmpty() })
    }

    @Test
    fun `verify mac address binding and admin activation key formula`() {
        val rawMac = "aa-bb-cc-11-22-33"
        val normalizedMac = DeviceMacSecurity.normalizeMac(rawMac)
        assertEquals("AA:BB:CC:11:22:33", normalizedMac)
        assertTrue(DeviceMacSecurity.isValidMacFormat(normalizedMac))

        val studentKey = DeviceMacSecurity.computeAdminActivationKey(
            normalizedMac,
            DeviceRole.STUDENT
        )
        val teacherKey = DeviceMacSecurity.computeAdminActivationKey(
            normalizedMac,
            DeviceRole.TEACHER
        )
        assertTrue(studentKey != teacherKey)

        val validProvision = DeviceProvisionEntity(
            macAddress = normalizedMac,
            roleName = DeviceRole.STUDENT.name,
            activationSignature = studentKey,
            hardwareId = "hw_device_1"
        )
        assertTrue(DeviceMacSecurity.verifyProvisioning(validProvision, "hw_device_1"))
        assertFalse(DeviceMacSecurity.verifyProvisioning(validProvision, "different_phone_hw"))
    }

    @Test
    fun `verify teacher xlsx and csv vocabulary upload and 2 of 3 star progression unlock`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = QuestRepository(db.questDao(), context)

        repo.ensureInitialized()

        val box1 = CurriculumCatalog.allLevels[0] // Essence
        val box2 = CurriculumCatalog.allLevels[1] // Beginner A

        // Box 2 starts locked
        val initialBox2Progress = db.questDao().getLevelProgress(box2.id)!!
        assertFalse(initialBox2Progress.isUnlocked)

        // 1. Test Teacher .CSV upload into Box 1 Vocabulary Room
        val csvContent = """
            Word,Meaning,Arabic,Example,PartOfSpeech
            Diligent,Showing care and effort in work,مجتهد,She is a diligent student.,Adjective
            Articulate,Able to express ideas clearly,فصيح,He gave an articulate answer.,Adjective
        """.trimIndent()
        val (csvOk, _) = repo.importVocabularyCsvText(csvContent, box1.id, "unit1_teacher_vocab.csv")
        assertTrue(csvOk)

        // 2. Test Teacher .XLSX (OpenXML ZIP) upload into Box 1 Vocabulary Room
        val xlsxBytes = VocabSpreadsheetImporter.createSampleXlsxBytes(
            listOf(
                listOf("Word", "Meaning", "Arabic", "Example", "PartOfSpeech"),
                listOf("Resilient", "Able to recover quickly from difficulties", "مرن / صامد", "Students are resilient learners.", "Adjective"),
                listOf("Collaborate", "Work jointly on an activity or project", "يتعاون", "We collaborate in speaking rooms.", "Verb")
            )
        )
        val (xlsxOk, _) = repo.importVocabularyXlsxBytes(xlsxBytes, box1.id, "box1_vocab.xlsx")
        assertTrue(xlsxOk)

        val uploadedWords = db.questDao().getVocabularyEntriesForLevel(box1.id)
        assertTrue(uploadedWords.any { it.word == "Diligent" && it.arabicHint == "مجتهد" })
        assertTrue(uploadedWords.any { it.word == "Resilient" })

        // 3. Scoring 50% (1/3 star) in Room 3 (Vocab Practice) is below 2/3 stars, so Box 2 stays locked
        val (starsAfterLowScore, unlockedAfterLow) = repo.recordRoomPracticeScore(
            level = box1,
            roomType = BoxPracticeRoomType.VOCAB_PRACTICE,
            scorePercent = 50,
            wrongQuestionIds = emptyList()
        )
        assertEquals(1, starsAfterLowScore)
        assertEquals(null, unlockedAfterLow)
        assertFalse(db.questDao().getLevelProgress(box2.id)!!.isUnlocked)

        // 4. Scoring 90% in Room 4 (Grammar Practice) brings combined score to (50+90)/2 = 70% (2/3 stars) -> unlocks Box 2!
        val (starsEarned, newlyUnlockedNextLevel) = repo.recordRoomPracticeScore(
            level = box1,
            roomType = BoxPracticeRoomType.GRAMMAR_PRACTICE,
            scorePercent = 90,
            wrongQuestionIds = emptyList()
        )

        assertTrue(starsEarned >= 2)
        assertNotNull(newlyUnlockedNextLevel)
        assertEquals(box2.id, newlyUnlockedNextLevel?.id)

        // Verify Box 2 is now unlocked in Room database
        val updatedBox2Progress = db.questDao().getLevelProgress(box2.id)!!
        assertTrue(updatedBox2Progress.isUnlocked)

        db.close()
    }

    @Test
    fun `verify on-device speaking voice answer accuracy evaluator`() {
        val evaluation = VoiceAnswerAccuracyEvaluator.evaluateSpokenCandidates(
            candidates = listOf("I live in Cairo and I study English every day"),
            sampleTargetAnswer = "I live in Baghdad and I study English at Excel ETA.",
            requiredKeywords = listOf("live", "study", "english")
        )
        assertTrue(evaluation.overallScorePercent >= 70)
        assertEquals(3, evaluation.matchedKeywords.size)
    }

    @Test
    fun `verify unit study reminders, read-only grades and mistakes sync, notifications, and exam goal`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val repo = QuestRepository(db.questDao(), context)

        repo.ensureInitialized()

        // 1. Verify Unit 1 is seeded with initial read-only grades, mistake corrections, study reminder, and notifications
        val firstLevel = CurriculumCatalog.allLevels.first()
        val unit1Progress = db.questDao().getLevelProgress(firstLevel.id)!!
        assertTrue(unit1Progress.gradedByTeacher)
        assertEquals(84, unit1Progress.inClassGrade)
        assertEquals(2, unit1Progress.mistakeItems.size)

        val initialNotifications = repo.allNotificationsFlow.first()
        assertTrue(initialNotifications.size >= 2)
        assertTrue(initialNotifications.any { !it.isRead })

        // 2. Add a local student study reminder for Unit 1 and toggle completion
        repo.addStudyReminder(
            levelId = firstLevel.id,
            reminderText = "Review past simple irregular verbs before Friday",
            priority = ReminderPriority.EXAM_PREP
        )
        val remindersAfterAdd = repo.allStudyRemindersFlow.first().filter { it.levelId == firstLevel.id }
        assertTrue(remindersAfterAdd.any { it.reminderText.contains("Review past simple") && it.priority == ReminderPriority.EXAM_PREP })

        val addedReminder = remindersAfterAdd.first { it.reminderText.contains("Review past simple") }
        repo.toggleStudyReminderCompleted(addedReminder.id)
        val updatedReminder = db.questDao().getStudyReminder(addedReminder.id)!!
        assertTrue(updatedReminder.isCompleted)

        // 3. Import an offline Teacher Evaluation JSON packet and verify grades, mistakes, and new notifications
        val packetJson = """
            {
              "levelId": "${firstLevel.id}",
              "speaking": 92,
              "listening": 96,
              "reading": 90,
              "writing": 88,
              "teacherNote": "Outstanding speaking clarity!",
              "mistakes": [
                {
                  "skillArea": "Speaking",
                  "mistake": "I did not went there.",
                  "correction": "I did not go there."
                }
              ]
            }
        """.trimIndent()

        val (syncOk, syncMsg) = repo.importTeacherEvaluationPacket(packetJson)
        assertTrue(syncOk)
        assertTrue(syncMsg.contains("91%"))

        val updatedProgress = db.questDao().getLevelProgress(firstLevel.id)!!
        assertEquals(91, updatedProgress.inClassGrade)
        assertEquals(3, updatedProgress.mistakeItems.size)
        assertTrue(updatedProgress.mistakeItems.any { it.correctionText == "I did not go there." })

        // 4. Verify exam countdown goal update
        repo.updateExamGoal("TOEFL Mock Exam", 21)
        val profile = db.questDao().getStudentProfile()!!
        assertEquals("TOEFL Mock Exam", profile.examGoalTitle)
        assertEquals(21, profile.daysUntilExam)

        db.close()
    }
}
