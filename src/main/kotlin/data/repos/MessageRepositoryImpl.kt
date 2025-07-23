package com.example.data.repos

import com.example.db.entities.*
import com.example.db.tables.MessageReactionsTable
import com.example.domain.models.Message
import com.example.db.tables.MessagesTable
import com.example.domain.repos.MessageRepository
import com.example.db.utils.dbQuery
import com.example.domain.models.MessageReaction
import org.jetbrains.exposed.sql.and

class MessageRepositoryImpl : MessageRepository {

    override suspend fun sendMessage(chatRoomId: Int, senderId: Int, content: String): Message? {
        return dbQuery {
            val chatRoom = ChatRoomEntity.findById(chatRoomId) ?: return@dbQuery null
            val sender = UserEntity.findById(senderId) ?: return@dbQuery null

            MessageEntity.new {
                this.chatRoom = chatRoom
                this.sender = sender
                this.content = content
            }.toDomain()
        }
    }

    override suspend fun getMessagesByRoom(chatRoomId: Int): List<Message>? {
        return dbQuery {
            MessageEntity.find { MessagesTable.chatRoom eq chatRoomId }
                .map { it.toDomain() }
        }
    }

    override suspend fun getMessageById(id: Int): Message? {
        return dbQuery {
            MessageEntity.findById(id)?.toDomain()
        }
    }

    override suspend fun editMessage(messageId: Int, newContent: String): Message? {
        return dbQuery {
            val message = MessageEntity.findById(messageId) ?: return@dbQuery null
            message.content = newContent
            message.toDomain()
        }
    }

    override suspend fun deleteMessage(id: Int): Boolean? {
        return dbQuery {
            val message = MessageEntity.findById(id) ?: return@dbQuery false
            message.delete()
            true
        }
    }

    override suspend fun addOrUpdateReaction(messageId: Int, userId: Int, emoji: String): MessageReaction? {
        return dbQuery {
            val existing = MessageReactionEntity.find {
                (MessageReactionsTable.message eq messageId) and (MessageReactionsTable.user eq userId)
            }.firstOrNull()

            if (existing != null) {
                existing.emoji = emoji
                MessageReaction(userId, emoji)
            } else {
                val newReaction = MessageReactionEntity.new {
                    message = MessageEntity[messageId]
                    user = UserEntity[userId]
                    this.emoji = emoji
                }
                MessageReaction(newReaction.user.id.value, newReaction.emoji)
            }
        }
    }

    override suspend fun deleteReaction(messageId: Int, userId: Int): Boolean? {
        return dbQuery {
            val reaction = MessageReactionEntity.find {
                (MessageReactionsTable.message eq messageId) and (MessageReactionsTable.user eq userId)
            }.firstOrNull()

            reaction?.delete() != null
        }
    }

    override suspend fun getReactions(messageId: Int): List<MessageReaction>? {
        return dbQuery {
            MessageReactionEntity.find { MessageReactionsTable.message eq messageId }
                .map { MessageReaction(it.user.id.value, it.emoji) }
        }
    }
}
