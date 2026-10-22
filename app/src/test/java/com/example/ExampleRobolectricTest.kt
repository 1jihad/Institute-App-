package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.CurriculumCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context and verify curriculum catalog`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Diploma Quest", appName)

        // Verify all 25 institute levels exist and have valid teacher unlock codes & materials
        assertEquals(25, CurriculumCatalog.allLevels.size)
        assertEquals("Essence", CurriculumCatalog.allLevels.first().title)
        assertEquals("Teaching Practice", CurriculumCatalog.allLevels.last().title)
        assertTrue(CurriculumCatalog.allLevels.all { it.teacherUnlockCode.isNotBlank() && it.materials.isNotEmpty() })
    }
}
