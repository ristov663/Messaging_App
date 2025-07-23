package com.example.domain.repos

import com.example.domain.models.ChatRoom

interface ChatRoomRepository {

    suspend fun createChatRoom(name: String?, isGroup: Boolean, memberIds: List<Int>): ChatRoom?
    suspend fun getChatRoomById(id: Int): ChatRoom?
    suspend fun getAllRoomsForUser(userId: Int): List<ChatRoom>?
    suspend fun addMemberToRoom(chatRoomId: Int, userId: Int): Boolean?
    suspend fun removeMemberFromRoom(chatRoomId: Int, userId: Int): Boolean?
    suspend fun deleteChatRoom(id: Int): Boolean?
}
