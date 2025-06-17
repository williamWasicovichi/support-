package com.example.sup // Adjust to your package name

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatMessage(
    val id: String = "", // Message ID, can be Firestore document ID
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "", // Name of the sender
    @ServerTimestamp // Automatically sets the timestamp on the server
    val timestamp: Timestamp? = null
)