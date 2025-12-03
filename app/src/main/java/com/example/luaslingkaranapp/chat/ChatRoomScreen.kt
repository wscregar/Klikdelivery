package com.example.luaslingkaranapp.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatRoomScreen(chatId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    var messages by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->
                if (snap != null) messages = snap.documents
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg.getString("content") ?: "",
                    isSender = msg.getString("senderId") == uid,
                    isRead = msg.getBoolean("isRead") == true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ketik pesan...") }
            )
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        sendText(chatId, uid, text)
                        text = ""
                    }
                }
            ) {
                Icon(Icons.Filled.Send, contentDescription = null)
            }
        }
    }
}
