data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val type: String = "text", // text | image | voice
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
