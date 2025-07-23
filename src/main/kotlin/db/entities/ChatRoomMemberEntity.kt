package com.example.db.entities

import com.example.db.tables.ChatRoomMembersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ChatRoomMemberEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChatRoomMemberEntity>(ChatRoomMembersTable)

    var user by UserEntity referencedOn ChatRoomMembersTable.user
    var chatRoom by ChatRoomEntity referencedOn ChatRoomMembersTable.chatRoom
}
