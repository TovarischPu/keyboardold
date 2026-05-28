package com.example.keyboard

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object ProgressSync {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun saveProgress(courseKey: String, completed: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .collection("progress")
            .document(courseKey)
            .set(mapOf(
                "completed" to completed,
                "updatedAt" to FieldValue.serverTimestamp()
            ))
    }

    fun loadProgress(onResult: (Map<String, Boolean>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(emptyMap())
        db.collection("users").document(uid)
            .collection("progress")
            .get()
            .addOnSuccessListener { snapshot ->
                val map = mutableMapOf<String, Boolean>()
                for (doc in snapshot) {
                    map[doc.id] = doc.getBoolean("completed") ?: false
                }
                onResult(map)
            }
            .addOnFailureListener { onResult(emptyMap()) }
    }
}