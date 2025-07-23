package com.example.domain.services

import com.example.domain.models.LoginRequest
import com.example.domain.models.RegisterRequest
import com.example.domain.models.UpdateUserRequest
import com.example.domain.models.User

interface UserService {
    suspend fun registerUser(registerRequest: RegisterRequest): User?
    suspend fun loginUser(loginRequest: LoginRequest): User?
    suspend fun getUserById(id: Int): User?
    suspend fun getAllUsers(): List<User>?
    suspend fun updateUser(id: Int, request: UpdateUserRequest): User?
    suspend fun deleteUser(id: Int): Boolean?
}
