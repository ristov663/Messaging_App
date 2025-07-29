package com.example.domain.services

import com.example.domain.models.AIConversation

interface AIAgentService {

    suspend fun processUserMessage(chatRoomId: Int, userId: Int, userMessage: String): String
    suspend fun getConversationHistory(chatRoomId: Int, userId: Int): List<AIConversation>
}
