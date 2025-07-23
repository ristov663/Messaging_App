package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object MessageReactionsTable : IntIdTable() {
    val message = reference("message_id", MessagesTable)
    val user = reference("user_id", UsersTable)
    val emoji = varchar("emoji", 10)
    init {
        index(true, message, user)
    }
}
