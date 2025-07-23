package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomRequest(
    val name: String?,
    val isGroup: Boolean,
    val memberIds: List<Int>
)
