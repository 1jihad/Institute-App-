package com.example.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Locale
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Offline Teacher Vocabulary Sheet Importer supporting both:
 * 1) Excel OpenXML (.xlsx) files (parsed natively via ZipInputStream + XML extraction)
 * 2) Comma/Semicolon/Tab-separated (.csv) files
 */
object VocabSpreadsheetImporter {

    data class ImportedVocabRow(
        val word: String,
        val meaning: String,
        val arabicHint: String = "",
        val exampleSentence: String = "",
        val partOfSpeech: String = "Vocabulary",
        val targetLevelId: String? = null
    )

    fun parseStream(
        inputStream: InputStream,
        fileNameOrMime: String = "",
        defaultLevelId: String
    ): List<ImportedVocabRow> {
        val rawBytes = inputStream.readBytes()
        if (rawBytes.isEmpty()) return emptyList()

        // Detect ZIP/XLSX magic header (PK\x03\x04 -> 0x50, 0x4B, 0x03, 0x04)
        val isZipXlsx = (rawBytes.size >= 4 &&
            rawBytes[0] == 0x50.toByte() &&
            rawBytes[1] == 0x4B.toByte() &&
            rawBytes[2] == 0x03.toByte() &&
            rawBytes[3] == 0x04.toByte()) ||
            fileNameOrMime.lowercase(Locale.US).endsWith(".xlsx")

        return if (isZipXlsx) {
            parseXlsxBytes(rawBytes, defaultLevelId)
        } else {
            val text = rawBytes.toString(Charsets.UTF_8)
            parseCsvText(text, defaultLevelId)
        }
    }

    /**
     * Parses RFC-4180 CSV or TSV text into [ImportedVocabRow] items.
     * Supports columns: Word, Meaning, Arabic Hint (or Example), Example Sentence, Part of Speech, Box/Level.
     */
    fun parseCsvText(
        csvContent: String,
        defaultLevelId: String
    ): List<ImportedVocabRow> {
        val lines = csvContent
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) return emptyList()

        val rows = lines.map { parseSingleCsvLine(it) }.filter { it.isNotEmpty() }
        return convertRawTableRowsToVocab(rows, defaultLevelId)
    }

    /**
     * Parses an Excel .xlsx archive from bytes by reading `xl/sharedStrings.xml` and `xl/worksheets/sheet*.xml`.
     */
    fun parseXlsxBytes(
        xlsxBytes: ByteArray,
        defaultLevelId: String
    ): List<ImportedVocabRow> {
        val sharedStrings = mutableListOf<String>()
        val worksheetXmls = mutableListOf<Pair<String, String>>()

        runCatching {
            ZipInputStream(ByteArrayInputStream(xlsxBytes)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val name = entry.name
                    if (!entry.isDirectory) {
                        if (name.equals("xl/sharedStrings.xml", ignoreCase = true)) {
                            val xml = zis.readBytes().toString(Charsets.UTF_8)
                            sharedStrings.addAll(extractSharedStringsFromXml(xml))
                        } else if (name.startsWith("xl/worksheets/sheet", ignoreCase = true) && name.endsWith(".xml", ignoreCase = true)) {
                            val xml = zis.readBytes().toString(Charsets.UTF_8)
                            worksheetXmls.add(name to xml)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        }

        val allImported = mutableListOf<ImportedVocabRow>()
        worksheetXmls.sortBy { it.first }
        for ((_, sheetXml) in worksheetXmls) {
            val tableRows = extractRowsFromWorksheetXml(sheetXml, sharedStrings)
            allImported.addAll(convertRawTableRowsToVocab(tableRows, defaultLevelId))
        }
        return allImported
    }

    private fun extractSharedStringsFromXml(xml: String): List<String> {
        val results = mutableListOf<String>()
        val siRegex = Regex("<si[^>]*>(.*?)</si>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        val tRegex = Regex("<t[^>]*>(.*?)</t>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        for (siMatch in siRegex.findAll(xml)) {
            val siContent = siMatch.groupValues[1]
            val combinedText = tRegex.findAll(siContent).joinToString("") { decodeXmlEntities(it.groupValues[1]) }
            results.add(combinedText.trim())
        }
        return results
    }

    private fun extractRowsFromWorksheetXml(sheetXml: String, sharedStrings: List<String>): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val rowRegex = Regex("<row[^>]*>(.*?)</row>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        val cellRegex = Regex("<c([^>]*)>(.*?)</c>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        val typeAttrRegex = Regex("""\bt\s*=\s*["']([^"']+)["']""")
        val refAttrRegex = Regex("""\br\s*=\s*["']([A-Za-z]+)\d+["']""")
        val vRegex = Regex("<v[^>]*>(.*?)</v>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        val tRegex = Regex("<t[^>]*>(.*?)</t>", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))

        for (rowMatch in rowRegex.findAll(sheetXml)) {
            val rowBody = rowMatch.groupValues[1]
            val indexedCells = mutableMapOf<Int, String>()
            var nextColIndex = 0

            for (cellMatch in cellRegex.findAll(rowBody)) {
                val attrs = cellMatch.groupValues[1]
                val cellBody = cellMatch.groupValues[2]
                val cellType = typeAttrRegex.find(attrs)?.groupValues?.get(1) ?: ""
                val colLetters = refAttrRegex.find(attrs)?.groupValues?.get(1)
                val colIndex = if (colLetters != null) columnLettersToIndex(colLetters) else nextColIndex

                val rawValue = when (cellType) {
                    "s" -> {
                        val idx = vRegex.find(cellBody)?.groupValues?.get(1)?.trim()?.toIntOrNull()
                        if (idx != null && idx in sharedStrings.indices) sharedStrings[idx] else ""
                    }
                    "inlineStr", "str" -> {
                        tRegex.findAll(cellBody).joinToString("") { decodeXmlEntities(it.groupValues[1]) }.trim()
                    }
                    else -> {
                        val vText = vRegex.find(cellBody)?.groupValues?.get(1)
                            ?: tRegex.find(cellBody)?.groupValues?.get(1)
                            ?: ""
                        decodeXmlEntities(vText).trim()
                    }
                }
                indexedCells[colIndex] = rawValue
                nextColIndex = colIndex + 1
            }

            if (indexedCells.isNotEmpty()) {
                val maxCol = indexedCells.keys.maxOrNull() ?: 0
                val rowList = (0..maxCol.coerceAtMost(10)).map { indexedCells[it]?.trim() ?: "" }
                if (rowList.any { it.isNotBlank() }) {
                    rows.add(rowList)
                }
            }
        }
        return rows
    }

    private fun columnLettersToIndex(letters: String): Int {
        var result = 0
        for (ch in letters.uppercase(Locale.US)) {
            if (ch in 'A'..'Z') {
                result = result * 26 + (ch - 'A' + 1)
            }
        }
        return (result - 1).coerceAtLeast(0)
    }

    private fun decodeXmlEntities(input: String): String {
        return input
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
    }

    private fun parseSingleCsvLine(line: String): List<String> {
        val delimiter = when {
            line.contains("\t") && !line.contains(",") -> '\t'
            line.count { it == ';' } > line.count { it == ',' } -> ';'
            else -> ','
        }
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                        current.append('"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                c == delimiter && !inQuotes -> {
                    fields.add(current.toString().trim())
                    current.clear()
                }
                else -> current.append(c)
            }
            i++
        }
        fields.add(current.toString().trim())
        return fields
    }

    private fun convertRawTableRowsToVocab(
        rows: List<List<String>>,
        defaultLevelId: String
    ): List<ImportedVocabRow> {
        if (rows.isEmpty()) return emptyList()

        val firstRowLower = rows.first().map { it.lowercase(Locale.US).trim() }
        val hasHeader = firstRowLower.any {
            it in setOf("word", "vocabulary", "term", "phrase", "english", "meaning", "definition", "arabic", "المفردة", "الكلمة", "المعنى")
        }

        var wordCol = 0
        var meaningCol = 1
        var arabicCol = -1
        var exampleCol = -1
        var posCol = -1
        var levelCol = -1

        if (hasHeader) {
            firstRowLower.forEachIndexed { idx, col ->
                when {
                    col.contains("word") || col.contains("vocab") || col.contains("term") || col.contains("phrase") || col.contains("كلمة") || col.contains("مفرد") -> wordCol = idx
                    col.contains("meaning") || col.contains("definition") || col.contains("شرح") -> meaningCol = idx
                    col.contains("arabic") || col.contains("translation") || col.contains("عربي") || col.contains("معنى") -> arabicCol = idx
                    col.contains("example") || col.contains("sentence") || col.contains("مثال") || col.contains("جملة") -> exampleCol = idx
                    col.contains("part") || col.contains("pos") || col.contains("type") || col.contains("نوع") -> posCol = idx
                    col.contains("level") || col.contains("box") || col.contains("unit") || col.contains("مستوى") -> levelCol = idx
                }
            }
            if (meaningCol == wordCol) {
                meaningCol = if (firstRowLower.size > 1) 1 else 0
            }
        } else {
            // Positional convention: Col 0 = Word, Col 1 = Meaning/Arabic, Col 2 = Arabic/Example, Col 3 = Example
            if ((rows.first().size) >= 3) {
                arabicCol = 2
            }
            if ((rows.first().size) >= 4) {
                exampleCol = 3
            }
        }

        val dataRows = if (hasHeader) rows.drop(1) else rows
        val imported = mutableListOf<ImportedVocabRow>()

        for (cols in dataRows) {
            val word = cols.getOrNull(wordCol)?.trim().orEmpty()
            val meaning = cols.getOrNull(meaningCol)?.trim().orEmpty()
            if (word.isBlank() || meaning.isBlank()) continue

            val arabicHint = if (arabicCol >= 0 && arabicCol != meaningCol) {
                cols.getOrNull(arabicCol)?.trim().orEmpty()
            } else ""

            val example = if (exampleCol >= 0) {
                cols.getOrNull(exampleCol)?.trim().orEmpty()
            } else if (arabicCol == -1 && cols.size >= 3) {
                cols.getOrNull(2)?.trim().orEmpty()
            } else ""

            val pos = if (posCol >= 0) {
                cols.getOrNull(posCol)?.trim().orEmpty().ifBlank { "Vocabulary" }
            } else "Vocabulary"

            val rawLevel = if (levelCol >= 0) cols.getOrNull(levelCol)?.trim().orEmpty() else ""
            val resolvedLevelId = resolveLevelId(rawLevel, defaultLevelId)

            imported.add(
                ImportedVocabRow(
                    word = word,
                    meaning = meaning,
                    arabicHint = arabicHint,
                    exampleSentence = example.ifBlank { "Practice using '$word' in a complete sentence." },
                    partOfSpeech = pos,
                    targetLevelId = resolvedLevelId
                )
            )
        }
        return imported
    }

    private fun resolveLevelId(rawLevelCell: String, defaultLevelId: String): String {
        if (rawLevelCell.isBlank()) return defaultLevelId
        val cleaned = rawLevelCell.trim().lowercase(Locale.US)
        val byNumber = cleaned.filter { it.isDigit() }.toIntOrNull()
        if (byNumber != null) {
            CurriculumCatalog.allLevels.find { it.orderIndex == byNumber }?.let { return it.id }
        }
        CurriculumCatalog.allLevels.find {
            it.id.equals(cleaned, ignoreCase = true) ||
                it.title.equals(cleaned, ignoreCase = true) ||
                cleaned.contains(it.title.lowercase(Locale.US))
        }?.let { return it.id }
        return defaultLevelId
    }

    /**
     * Generates a valid in-memory .xlsx Excel workbook ByteArray from rows (useful for testing and sample export).
     */
    fun createSampleXlsxBytes(rows: List<List<String>>): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            // 1. xl/sharedStrings.xml
            val flatStrings = rows.flatten().distinct()
            val sharedXml = buildString {
                append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
                append("""<sst xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" count="${flatStrings.size}" uniqueCount="${flatStrings.size}">""")
                for (s in flatStrings) {
                    val escaped = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    append("<si><t>$escaped</t></si>")
                }
                append("</sst>")
            }
            zos.putNextEntry(ZipEntry("xl/sharedStrings.xml"))
            zos.write(sharedXml.toByteArray(Charsets.UTF_8))
            zos.closeEntry()

            // 2. xl/worksheets/sheet1.xml
            val sheetXml = buildString {
                append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
                append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>""")
                rows.forEachIndexed { rIdx, row ->
                    val rowNum = rIdx + 1
                    append("""<row r="$rowNum">""")
                    row.forEachIndexed { cIdx, cellValue ->
                        val colLetter = ('A' + cIdx)
                        val sIdx = flatStrings.indexOf(cellValue).coerceAtLeast(0)
                        append("""<c r="$colLetter$rowNum" t="s"><v>$sIdx</v></c>""")
                    }
                    append("</row>")
                }
                append("</sheetData></worksheet>")
            }
            zos.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
            zos.write(sheetXml.toByteArray(Charsets.UTF_8))
            zos.closeEntry()
        }
        return baos.toByteArray()
    }
}
