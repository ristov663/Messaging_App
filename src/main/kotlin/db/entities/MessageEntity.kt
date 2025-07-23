package com.example.db.entities

import com.example.db.tables.MessageReactionsTable
import com.example.db.tables.MessagesTable
import com.example.domain.models.Message
import com.example.domain.models.MessageReaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MessageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(MessagesTable)

    var content by MessagesTable.content
    var sender by UserEntity referencedOn MessagesTable.sender
    var chatRoom by ChatRoomEntity referencedOn MessagesTable.chatRoom
    var createdAt by MessagesTable.createdAt

    val reactions: List<MessageReaction>
        get() = MessageReactionEntity.find {
            MessageReactionsTable.message eq this@MessageEntity.id
        }.map { reaction ->
            MessageReaction(userId = reaction.user.id.value, emoji = reaction.emoji)
        }
}

fun MessageEntity.toDomain() = Message(
    id = id.value,
    content = content,
    sender = sender.toDomain(),
    chatRoomId = chatRoom.id.value,
    createdAt = createdAt,
    reactions = reactions
)
