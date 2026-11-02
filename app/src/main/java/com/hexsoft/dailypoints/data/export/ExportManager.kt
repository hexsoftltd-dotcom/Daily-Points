package com.hexsoft.dailypoints.data.export

import android.content.Context
import android.os.Environment
import com.hexsoft.dailypoints.domain.model.TaskCompletionDetail
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExportManager @Inject constructor(
    private val context: Context,
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun exportCsv(baseName: String, completions: List<TaskCompletionDetail>): File {
        val file = createFile(baseName, ExportFormat.CSV.name.lowercase())
        val totalPoints = completions.sumOf { if (it.completed) it.points else 0 }
        val content = buildString {
            appendLine("Date,Task,Category,Points,Completed")
            completions.forEach { completion ->
                appendLine(
                    listOf(
                        completion.date.format(dateFormatter),
                        completion.taskName.csvEscape(),
                        completion.category.csvEscape(),
                        completion.points.toString(),
                        if (completion.completed) "Yes" else "No",
                    ).joinToString(","),
                )
            }
            appendLine("TOTAL,,,${totalPoints},")
        }
        file.writeText(content)
        return file
    }

    fun toRows(completions: List<TaskCompletionDetail>): List<List<String>> {
        return completions.map { completion ->
            listOf(
                completion.date.format(dateFormatter),
                completion.taskName,
                completion.category,
                completion.points.toString(),
                completion.completed.toString(),
            )
        }
    }

    private fun createFile(baseName: String, extension: String): File {
        val root = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!root.exists()) {
            root.mkdirs()
        }
        return File(root, "$baseName.$extension")
    }

    private fun String.csvEscape(): String {
        return if (contains(',') || contains('"') || contains('\n')) {
            "\"${replace("\"", "\"\"")}\""
        } else {
            this
        }
    }
}
