package com.example.data.repos

import com.example.db.tables.AIConversationsTable
import com.example.domain.models.AIConversation
import com.example.domain.repos.AIConversationRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class AIConversationRepositoryImpl : AIConversationRepository {

    override suspend fun saveConversation(
        chatRoomId: Int,
        userId: Int,
        userMessage: String,
        aiResponse: String
    ): AIConversation? {
        return transaction {
            val now = LocalDateTime.now().toString()

            val insertResult = AIConversationsTable.insert {
                it[AIConversationsTable.chatRoomId] = chatRoomId
                it[AIConversationsTable.userId] = userId
                it[AIConversationsTable.userMessage] = userMessage
                it[AIConversationsTable.aiResponse] = aiResponse
                it[createdAt] = now
            }

            insertResult.resultedValues?.firstOrNull()?.let { row ->
                AIConversation(
                    id = row[AIConversationsTable.id],
                    chatRoomId = row[AIConversationsTable.chatRoomId],
                    userId = row[AIConversationsTable.userId],
                    userMessage = row[AIConversationsTable.userMessage],
                    aiResponse = row[AIConversationsTable.aiResponse],
                    createdAt = row[AIConversationsTable.createdAt]
                )
            }
        }
    }

    override suspend fun getConversationHistory(chatRoomId: Int, userId: Int, limit: Int): List<AIConversation> {
        return transaction {
            AIConversationsTable
                .selectAll()
                .where {
                    (AIConversationsTable.chatRoomId eq chatRoomId) and
                            (AIConversationsTable.userId eq userId)
                }
                .orderBy(AIConversationsTable.id, SortOrder.DESC)
                .limit(limit)
                .map { row ->
                    AIConversation(
                        id = row[AIConversationsTable.id],
                        chatRoomId = row[AIConversationsTable.chatRoomId],
                        userId = row[AIConversationsTable.userId],
                        userMessage = row[AIConversationsTable.userMessage],
                        aiResponse = row[AIConversationsTable.aiResponse],
                        createdAt = row[AIConversationsTable.createdAt]
                    )
                }
                .reversed()
        }
    }
}
