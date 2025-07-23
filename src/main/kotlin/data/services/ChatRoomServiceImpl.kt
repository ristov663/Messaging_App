package com.example.data.services

import com.example.domain.models.ChatRoom
import com.example.domain.repos.ChatRoomRepository
import com.example.domain.services.ChatRoomService

class ChatRoomServiceImpl(
    private val chatRoomRepository: ChatRoomRepository
) : ChatRoomService {

    override suspend fun createChatRoom(name: String?, isGroup: Boolean, memberIds: List<Int>) =
        chatRoomRepository.createChatRoom(name, isGroup, memberIds)

    override suspend fun getChatRoomById(id: Int) =
        chatRoomRepository.getChatRoomById(id)

    override suspend fun getAllRoomsForUser(userId: Int): List<ChatRoom> =
        chatRoomRepository.getAllRoomsForUser(userId) ?: emptyList()

    override suspend fun addMemberToRoom(chatRoomId: Int, userId: Int): Boolean =
        chatRoomRepository.addMemberToRoom(chatRoomId, userId) ?: false

    override suspend fun removeMemberFromRoom(chatRoomId: Int, userId: Int): Boolean =
        chatRoomRepository.removeMemberFromRoom(chatRoomId, userId) ?: false

    override suspend fun deleteChatRoom(id: Int): Boolean =
        chatRoomRepository.deleteChatRoom(id) ?: false
}
