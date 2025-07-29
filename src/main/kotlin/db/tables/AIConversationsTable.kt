package com.example.db.tables

import org.jetbrains.exposed.sql.Table

object AIConversationsTable : Table("ai_conversations") {
    val id = integer("id").autoIncrement()
    val chatRoomId = integer("chat_room_id")
    val userId = integer("user_id")
    val userMessage = text("user_message")
    val aiResponse = text("ai_response")
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}
