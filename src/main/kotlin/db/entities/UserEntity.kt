package com.example.db.entities

import com.example.db.tables.UsersTable
import com.example.domain.models.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var username by UsersTable.username
    var email by UsersTable.email
    var password by UsersTable.password
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt

    fun toDomain() = User(
        id = id.value,
        username = username,
        email = email
    )
}
