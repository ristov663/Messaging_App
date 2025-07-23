package com.example.db.entities

import com.example.db.tables.MessageReactionsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MessageReactionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageReactionEntity>(MessageReactionsTable)

    var user by UserEntity referencedOn MessageReactionsTable.user
    var message by MessageEntity referencedOn MessageReactionsTable.message
    var emoji by MessageReactionsTable.emoji
}
