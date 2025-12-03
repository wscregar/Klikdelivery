package com.example.luaslingkaranapp.chat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

fun sendText(chatId: String, senderId: String, message: String) {
    val firestore = FirebaseFirestore.getInstance()

    val data = hashMapOf(
        "senderId" to senderId,
        "content" to message,
        "timestamp" to FieldValue.serverTimestamp(),
        "isRead" to false
    )

    firestore.collection("chats")
        .document(chatId)
        .collection("messages")
        .add(data)
}
