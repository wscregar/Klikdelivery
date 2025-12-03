package com.example.luaslingkaranapp.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatListScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val firestore = FirebaseFirestore.getInstance()

    var chats by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(Unit) {
        firestore.collection("chats")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) chats = snapshot.documents
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(chats) { chat ->
            ListItem(
                headlineContent = {
                    Text(text = chat.getString("lastMessage") ?: "Belum ada pesan")
                },
                supportingContent = {
                    Text(text = "Order ID: ${chat.getString("orderId") ?: "-"}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("chatRoom/${chat.id}")
                    }
                    .padding(vertical = 4.dp)
            )
            Divider()
        }
    }
}
