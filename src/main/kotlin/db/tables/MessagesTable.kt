package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MessagesTable : IntIdTable("messages") {
    val content = text("content")
    val sender = reference("sender_id", UsersTable)
    val chatRoom = reference("chat_room_id", ChatRoomsTable)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
}
