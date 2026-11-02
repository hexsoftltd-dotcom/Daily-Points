package com.hexsoft.dailypoints.data.export

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import com.hexsoft.dailypoints.domain.model.YearlyReport
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ExportManagerTest {
    @Test
    fun generatesCsvFile() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val exportManager = ExportManager(context)
        val report = YearlyReport(
            year = 2026,
            totalPoints = 20,
            averagePoints = 0.1f,
            completions = listOf(
                TaskCompletionDetail(1, 1, "Exercise", "Health", 20, LocalDate.of(2026, 10, 29), true, 0L),
            ),
        )
        val file = exportManager.exportYearlyReport(report, ExportFormat.CSV)
        assertTrue(file.exists())
        assertTrue(file.readText().contains("Exercise"))
    }
}
