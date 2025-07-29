package com.example.domain.models

data class AIConversation(
    val id: Int = 0,
    val chatRoomId: Int,
    val userId: Int,
    val userMessage: String,
    val aiResponse: String,
    val createdAt: String
)
