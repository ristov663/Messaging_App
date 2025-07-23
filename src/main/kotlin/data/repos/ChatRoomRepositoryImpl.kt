package com.example.data.repos

import com.example.db.entities.ChatRoomEntity
import com.example.db.entities.ChatRoomMemberEntity
import com.example.db.entities.UserEntity
import com.example.db.entities.toDomain
import com.example.db.tables.ChatRoomMembersTable
import com.example.db.utils.dbQuery
import com.example.domain.models.ChatRoom
import com.example.domain.repos.ChatRoomRepository

class ChatRoomRepositoryImpl : ChatRoomRepository {

    override suspend fun createChatRoom(name: String?, isGroup: Boolean, memberIds: List<Int>): ChatRoom? {
        return dbQuery {
            val chatRoom = ChatRoomEntity.new {
                this.name = name
                this.isGroup = isGroup
            }

            memberIds.forEach { userId ->
                ChatRoomMemberEntity.new {
                    this.chatRoom = chatRoom
                    this.user = UserEntity[userId]
                }
            }
            chatRoom.toDomain()
        }
    }

    override suspend fun getChatRoomById(id: Int): ChatRoom? {
        return dbQuery {
            ChatRoomEntity.findById(id)?.toDomain()
        }
    }

    override suspend fun getAllRoomsForUser(userId: Int): List<ChatRoom>? {
        return dbQuery {
            ChatRoomMemberEntity.find { ChatRoomMembersTable.user eq userId }
                .map { it.chatRoom }
                .distinctBy { it.id.value }
                .map { it.toDomain() }
        }
    }

    override suspend fun addMemberToRoom(chatRoomId: Int, userId: Int): Boolean? {
        return dbQuery {
            val exists = ChatRoomMemberEntity.find {
                (ChatRoomMembersTable.chatRoom eq chatRoomId)
                (ChatRoomMembersTable.user eq userId)
            }.any()

            if (exists) return@dbQuery false

            ChatRoomMemberEntity.new {
                this.chatRoom = ChatRoomEntity[chatRoomId]
                this.user = UserEntity[userId]
            }
            true
        }
    }

    override suspend fun removeMemberFromRoom(chatRoomId: Int, userId: Int): Boolean? {
        return dbQuery {
            val relation = ChatRoomMemberEntity.find {
                (ChatRoomMembersTable.chatRoom eq chatRoomId)
                (ChatRoomMembersTable.user eq userId)
            }.firstOrNull() ?: return@dbQuery false

            relation.delete()
            true
        }
    }

    override suspend fun deleteChatRoom(id: Int): Boolean? {
        return dbQuery {
            val room = ChatRoomEntity.findById(id) ?: return@dbQuery false

            ChatRoomMemberEntity.find { ChatRoomMembersTable.chatRoom eq id }
                .forEach { it.delete() }

            room.delete()
            true
        }
    }
}
