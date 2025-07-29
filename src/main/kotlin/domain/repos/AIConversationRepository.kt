package com.example.domain.repos

import com.example.domain.models.AIConversation

interface AIConversationRepository {

    suspend fun saveConversation(
        chatRoomId: Int,
        userId: Int,
        userMessage: String,
        aiResponse: String
    ): AIConversation?

    suspend fun getConversationHistory(chatRoomId: Int, userId: Int, limit: Int = 10): List<AIConversation>
}
