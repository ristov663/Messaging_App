package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AIHistoryPayload(
    val type: String = "ai_history",
    val chatRoomId: Int,
    val senderId: Int
)
