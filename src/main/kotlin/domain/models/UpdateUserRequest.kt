package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null
)
