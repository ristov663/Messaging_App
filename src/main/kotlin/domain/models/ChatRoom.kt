package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val id: Int,
    val name: String?,
    val isGroup: Boolean,
    val members: List<User>
)
