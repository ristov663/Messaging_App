package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessagePayload(
    val type: String = "message",
    val chatRoomId: Int,
    val senderId: Int,
    val senderName: String? = null,
    val receiverIds: List<Int>? = null,
    val content: String
)
