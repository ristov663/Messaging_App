package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Int,
    val username: String,
    val email: String,
    val token: String?
)
