package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ChatRoomMembersTable : IntIdTable("chat_room_members") {
    val user = reference("user_id", UsersTable)
    val chatRoom = reference("chat_room_id", ChatRoomsTable)

    init {
        uniqueIndex(user, chatRoom)
    }
}
