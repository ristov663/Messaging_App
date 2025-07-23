package com.example.data.repos

import com.example.domain.models.LoginRequest
import com.example.domain.models.RegisterRequest
import com.example.domain.models.User
import com.example.domain.repos.UserRepository
import com.example.db.entities.UserEntity
import com.example.db.utils.dbQuery
import com.example.db.tables.UsersTable
import com.example.domain.models.UpdateUserRequest
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

class UserRepositoryImpl : UserRepository {

    override suspend fun createUser(request: RegisterRequest): User? {
        return dbQuery {
            val user = UserEntity.find { UsersTable.username eq request.username }.firstOrNull()
            if (user != null) {
                return@dbQuery null
            }

            return@dbQuery UserEntity.new {
                this.username = request.username
                this.email = request.email
                this.createdAt = LocalDateTime.now()
                this.updatedAt = LocalDateTime.now()
                this.password = hashPassword(request.password)
            }.let {
                User(it.id.value, it.username, it.email, it.isDeleted)
            }
        }
    }

    override suspend fun getUserById(id: Int): User? {
        return dbQuery {
            UserEntity.findById(id)?.toDomain()
        }
    }

    override suspend fun authenticate(request: LoginRequest): User? {
        return dbQuery {
            val user = UserEntity.find { UsersTable.username eq request.username }.firstOrNull()

            user?.takeIf {
                verifyPassword(request.password, user.password)
            }?.let {
                User(user.id.value, user.username, it.email, it.isDeleted)
            }
        }
    }

    override suspend fun getAllUsers(): List<User>? {
        return dbQuery {
            UserEntity.find { UsersTable.isDeleted eq false }
                .map { it.toDomain() }
        }
    }

    override suspend fun updateUser(id: Int, request: UpdateUserRequest): User? {
        return dbQuery {
            val user = UserEntity.findById(id) ?: return@dbQuery null

            request.username?.let {
                val existingUser = UserEntity.find { UsersTable.username eq it }.firstOrNull()
                if (existingUser != null && existingUser.id.value != id) {
                    return@dbQuery null
                }
                user.username = it
            }

            request.email?.let { user.email = it }

            request.password?.let {
                user.password = hashPassword(it)
            }
            user.updatedAt = LocalDateTime.now()
            user.toDomain()
        }
    }

    override suspend fun deleteUser(userId: Int): Boolean? {
        return dbQuery {
            val user = UserEntity.findById(userId) ?: return@dbQuery false

            user.isDeleted = true
            true
        }
    }

    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}
