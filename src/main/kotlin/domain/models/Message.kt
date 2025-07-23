package com.example.domain.models

import com.example.db.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Message(
    val id: Int,
    val content: String,
    val sender: User,
    val chatRoomId: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val reactions: List<MessageReaction> = emptyList()
)
