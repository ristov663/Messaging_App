package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatReactionPayload(
    val type: String = "reaction",
    val chatRoomId: Int,
    val messageId: Int,
    val senderId: Int,
    val senderName: String? = null,
    val emoji: String,
    val action: String = "add"
)
