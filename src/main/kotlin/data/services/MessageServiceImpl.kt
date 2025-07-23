package com.example.data.services

import com.example.domain.models.Message
import com.example.domain.models.MessageReaction
import com.example.domain.repos.MessageRepository
import com.example.domain.services.MessageService

class MessageServiceImpl(
    private val messageRepository: MessageRepository
) : MessageService {

    override suspend fun sendMessage(chatRoomId: Int, senderId: Int, content: String): Message? =
        messageRepository.sendMessage(chatRoomId, senderId, content)

    override suspend fun getMessagesByRoom(chatRoomId: Int): List<Message> =
        messageRepository.getMessagesByRoom(chatRoomId) ?: emptyList()

    override suspend fun getMessageById(id: Int): Message? =
        messageRepository.getMessageById(id)

    override suspend fun editMessage(messageId: Int, newContent: String): Message? =
        messageRepository.editMessage(messageId, newContent)

    override suspend fun deleteMessage(id: Int): Boolean =
        messageRepository.deleteMessage(id) ?: false

    override suspend fun addOrUpdateReaction(messageId: Int, userId: Int, emoji: String): MessageReaction? =
        messageRepository.addOrUpdateReaction(messageId, userId, emoji)

    override suspend fun deleteReaction(messageId: Int, userId: Int): Boolean? =
        messageRepository.deleteReaction(messageId, userId)

    override suspend fun getReactions(messageId: Int): List<MessageReaction> =
        messageRepository.getReactions(messageId) ?: emptyList()
}
