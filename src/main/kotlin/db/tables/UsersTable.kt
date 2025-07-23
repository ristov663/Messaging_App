package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object UsersTable : IntIdTable("users") {

    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100)
    val password = varchar("password", 255)
    val isDeleted = bool("is_deleted").default(false)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}
