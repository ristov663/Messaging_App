package com.example.domain.repos

import com.example.domain.models.LoginRequest
import com.example.domain.models.RegisterRequest
import com.example.domain.models.UpdateUserRequest
import com.example.domain.models.User

interface UserRepository {

    suspend fun createUser(request: RegisterRequest): User?
    suspend fun getUserById(id: Int): User?
    suspend fun authenticate(request: LoginRequest): User?
    suspend fun getAllUsers(): List<User>?
    suspend fun updateUser(id: Int, request: UpdateUserRequest): User?
    suspend fun deleteUserSafely(userId: Int): Boolean?
    fun hashPassword(password: String): String
    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean
}
