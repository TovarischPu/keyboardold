package com.example.keyboard.translator

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class DictionaryTranslator(private val context: Context) {

    private val dbFileName = "database.db"
    private val dbFile = context.getDatabasePath(dbFileName)
    private var database: SQLiteDatabase? = null

    init {
        Log.d("DB", "📍 Ожидаемый путь: ${dbFile.absolutePath}")
        copyDatabaseIfNeeded()
        openDatabase()
    }

    private fun copyDatabaseIfNeeded() {
        val minExpectedSize = 50_000_000L

        if (dbFile.exists()) {
            Log.d("DB", "Файл уже есть. Размер: ${dbFile.length()} байт")
            if (dbFile.length() >= minExpectedSize) {
                Log.d("DB", "✅ Размер в норме, пропускаем копирование")
                return
            } else {
                Log.w("DB", "⚠️ Файл битый/пустой (${dbFile.length()} байт). Удаляю и копирую заново...")
                dbFile.delete()
            }
        }

        try {
            dbFile.parentFile?.mkdirs()
            Log.d("DB", "📥 Начинаю копирование из assets/$dbFileName...")

            context.assets.open(dbFileName).use { input ->
                FileOutputStream(dbFile).use { output ->
                    val buffer = ByteArray(8192)
                    var totalCopied = 0L
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        totalCopied += read
                    }
                    output.flush()
                    output.fd.sync() // Гарантирует физическую запису на диск
                    Log.d("DB", "✅ Скопировано $totalCopied байт")
                }
            }
        } catch (e: Exception) {
            Log.e("DB", "❌ КРИТИЧЕСКАЯ ОШИБКА КОПИРОВАНИЯ", e)
            e.printStackTrace()
        }
    }

    private fun openDatabase() {
        try {
            if (!dbFile.exists()) {
                Log.e("DB", "❌ Файл БД отсутствует после попытки копирования!")
                return
            }
            database = SQLiteDatabase.openDatabase(
                dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY
            )
            Log.d("DB", "✅ БД успешно открыта")
        } catch (e: Exception) {
            Log.e("DB", "❌ Ошибка открытия БД", e)
            e.printStackTrace()
        }
    }

    suspend fun translate(word: String): String? = withContext(Dispatchers.IO) {
        if (database == null) {
            Log.e("DB", "❌ База не инициализирована")
            return@withContext null
        }
        try {
            val cursor = database?.rawQuery(
                "SELECT col1 FROM words WHERE LOWER(col2) = LOWER(?) LIMIT 1",
                arrayOf(word)
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val result = it.getString(0)
                    Log.d("DB_QUERY", "✓ $word → $result")
                    result
                } else {
                    Log.d("DB_QUERY", "✗ $word не найдено")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DB_QUERY", "❌ Ошибка запроса", e)
            null
        }
    }

    fun close() {
        try {
            database?.close()
            Log.d("DB", "🔒 БД закрыта")
        } catch (e: Exception) {
            Log.e("DB", "Ошибка при закрытии", e)
        }
    }
}