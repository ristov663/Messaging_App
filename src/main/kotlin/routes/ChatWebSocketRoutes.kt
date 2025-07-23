package com.example.routes

import com.example.domain.models.ChatMessagePayload
import com.example.domain.models.ChatReactionPayload
import com.example.domain.repos.MessageRepository
import com.example.domain.services.UserService
import com.example.websocket.WebSocketSessionManager
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.*
import org.koin.ktor.ext.inject

fun Application.chatWebSocketRoute() {
    val messageRepository by inject<MessageRepository>()
    val userService by inject<UserService>()

    routing {
        webSocket("/chat/{chatRoomId}") {
            val chatRoomId = call.parameters["chatRoomId"]?.toIntOrNull()
            val token = call.request.queryParameters["token"]

            if (chatRoomId == null || token == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing parameters"))
                return@webSocket
            }

            WebSocketSessionManager.addSession(chatRoomId, this)

            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val jsonText = frame.readText()

                        val jsonElement = Json.parseToJsonElement(jsonText)
                        val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.contentOrNull

                        when (type) {
                            "message" -> {
                                try {
                                    val message = Json.decodeFromString<ChatMessagePayload>(jsonText)

                                    if (message.content.isBlank()) {
                                        close(
                                            CloseReason(
                                                CloseReason.Codes.CANNOT_ACCEPT,
                                                "Empty message"
                                            )
                                        )
                                        return@consumeEach
                                    }

                                    val savedMessage = messageRepository.sendMessage(
                                        message.chatRoomId,
                                        message.senderId,
                                        message.content
                                    )

                                    val senderUser = userService.getUserById(message.senderId)

                                    val enrichedPayload = buildJsonObject {
                                        put("type", JsonPrimitive("message"))
                                        put("chatRoomId", JsonPrimitive(message.chatRoomId))
                                        put("senderId", JsonPrimitive(message.senderId))
                                        put(
                                            "senderName",
                                            JsonPrimitive(senderUser?.username ?: "User #${message.senderId}")
                                        )
                                        put("content", JsonPrimitive(savedMessage?.content ?: message.content))
                                        put(
                                            "timestamp",
                                            JsonPrimitive(savedMessage?.createdAt.toString())
                                        )
                                        put("id", JsonPrimitive(savedMessage?.id ?: 0))
                                    }

                                    WebSocketSessionManager.broadcastMessage(
                                        message.chatRoomId,
                                        enrichedPayload.toString()
                                    )

                                } catch (e: Exception) {
                                    close(
                                        CloseReason(
                                            CloseReason.Codes.CANNOT_ACCEPT,
                                            "Invalid message format"
                                        )
                                    )
                                }
                            }

                            "reaction" -> {
                                try {
                                    val reaction = Json.decodeFromString<ChatReactionPayload>(jsonText)

                                    if (reaction.action == "remove") {
                                        messageRepository.deleteReaction(
                                            messageId = reaction.messageId,
                                            userId = reaction.senderId
                                        )
                                    } else {
                                        messageRepository.addOrUpdateReaction(
                                            messageId = reaction.messageId,
                                            userId = reaction.senderId,
                                            emoji = reaction.emoji
                                        )
                                    }

                                    // Notify all clients
                                    WebSocketSessionManager.broadcastMessage(reaction.chatRoomId, jsonText)
                                } catch (e: Exception) {
                                    close(
                                        CloseReason(
                                            CloseReason.Codes.CANNOT_ACCEPT,
                                            "Invalid reaction format"
                                        )
                                    )
                                }
                            }

                            else -> {
                                close(
                                    CloseReason(
                                        CloseReason.Codes.CANNOT_ACCEPT,
                                        "Unsupported type"
                                    )
                                )
                            }
                        }
                    }
                }
            } finally {
                WebSocketSessionManager.removeSession(chatRoomId, this)
            }
        }
    }
}
