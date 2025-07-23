package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageReaction(
    val userId: Int,
    val emoji: String
)
