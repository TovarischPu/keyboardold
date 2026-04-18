package com.example.keyboard

import android.content.Context
import androidx.compose.runtime.Composable
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun Translatoric(context: Context, text: String): String?{
    //val file = File("app/src/main/res/words.txt")

    val dictionary = mutableListOf<List<String>>()
    var text2: String?=null
    val untranslated = mutableListOf<String>()

    try {
        // Читаем из assets с явным указанием потоков
        context.assets.open("words.txt").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).useLines { lines ->
                lines.forEach { line: String ->
                    if (line.isNotBlank()) {
                        val parts: List<String> = line.split(' ').filter { it.isNotEmpty() }
                        if (parts.size >= 2) {
                            dictionary.add(parts)
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {

        return "Ошибка"
    }

    for (word in text.split(' ')) {
        var found = false
        for (entry in dictionary) {

            if (entry.size > 1) {
                val key = if (entry[1].isNotEmpty()) entry[1].dropLast(1) else entry[1]
                if (key == word) {
                    text2+=entry[0]
                    found = true
                    break
                }
            }
        }
        if (!found) {
            text2+=word
            untranslated.add(word)
        }
        }
        return text2
//    if (untranslated.isNotEmpty()) {
//        println()
//        print("К сожалению некоторые слова не перевелись: ")
//        for (word in untranslated) {
//            print("$word ")
//        }
//    }
}
