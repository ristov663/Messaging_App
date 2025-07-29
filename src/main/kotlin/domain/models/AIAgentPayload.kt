package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AIAgentPayload(
    val type: String = "ai_agent",
    val chatRoomId: Int,
    val userMessage: String,
    val senderId: Int
)
