package com.example.domain.services

import com.example.domain.models.Message
import com.example.domain.models.MessageReaction

interface MessageService {

    suspend fun sendMessage(chatRoomId: Int, senderId: Int, content: String): Message?
    suspend fun getMessagesByRoom(chatRoomId: Int): List<Message>
    suspend fun getMessageById(id: Int): Message?
    suspend fun editMessage(messageId: Int, newContent: String) : Message?
    suspend fun deleteMessage(id: Int): Boolean
    suspend fun addOrUpdateReaction(messageId: Int, userId: Int, emoji: String): MessageReaction?
    suspend fun deleteReaction(messageId: Int, userId: Int): Boolean?
    suspend fun getReactions(messageId: Int): List<MessageReaction>
}
