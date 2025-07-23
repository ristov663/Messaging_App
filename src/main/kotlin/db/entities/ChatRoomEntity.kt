package com.example.db.entities

import com.example.db.tables.ChatRoomMembersTable
import com.example.db.tables.ChatRoomsTable
import com.example.db.tables.MessagesTable
import com.example.domain.models.ChatRoom
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ChatRoomEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatRoomEntity>(ChatRoomsTable)

    var name by ChatRoomsTable.name
    var isGroup by ChatRoomsTable.isGroup
    var createdAt by ChatRoomsTable.createdAt
    var updatedAt by ChatRoomsTable.updatedAt

    val members by UserEntity via ChatRoomMembersTable
    val messages by MessageEntity referrersOn MessagesTable.chatRoom
}

fun ChatRoomEntity.toDomain() = ChatRoom(
    id = id.value,
    name = name,
    isGroup = isGroup,
    members = members.map { it.toDomain() }
)
