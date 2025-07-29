package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AIAgentResponse(
    val type: String = "ai_response",
    val chatRoomId: Int,
    val content: String,
    val senderId: Int = -1,
    val senderName: String = "Sales Agent",
    val timestamp: String,
    val id: Int = 0
)
