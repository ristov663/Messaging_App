package com.example.data.services

import com.example.domain.models.LoginRequest
import com.example.domain.models.RegisterRequest
import com.example.domain.models.UpdateUserRequest
import com.example.domain.models.User
import com.example.domain.repos.UserRepository
import com.example.domain.services.UserService

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override suspend fun registerUser(registerRequest: RegisterRequest): User? {
        return userRepository.createUser(registerRequest)
    }

    override suspend fun loginUser(loginRequest: LoginRequest): User? {
        return userRepository.authenticate(loginRequest)
    }

    override suspend fun getUserById(id: Int): User? {
        return userRepository.getUserById(id)
    }

    override suspend fun getAllUsers(): List<User>? {
        return userRepository.getAllUsers()
    }

    override suspend fun updateUser(id: Int, request: UpdateUserRequest): User? {
        return userRepository.updateUser(id, request)
    }

    override suspend fun deleteUser(id: Int): Boolean? {
        return userRepository.deleteUser(id)
    }
}
