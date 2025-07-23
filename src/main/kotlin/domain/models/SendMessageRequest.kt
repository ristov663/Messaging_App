package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val chatRoomId: Int,
    val content: String
)
