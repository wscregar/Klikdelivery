package com.example.luaslingkaranapp.chat

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ChatBubble(message: String, isSender: Boolean, isRead: Boolean) {
    Column(
        horizontalAlignment = if (isSender) Alignment.End else Alignment.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSender) Color(0xFFDBEAFE) else Color.White
            )
        ) {
            Text(message, modifier = Modifier.padding(12.dp))
        }

        if (isSender && isRead) {
            Icon(Icons.Filled.DoneAll, null, tint = Color.Blue, modifier = Modifier.size(14.dp))
        }
    }
}
