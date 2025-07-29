package com.example.routes

import com.example.domain.models.AIAgentPayload
import com.example.domain.models.AIAgentResponse
import com.example.domain.models.AIHistoryPayload
import com.example.domain.services.AIAgentService
import com.example.websocket.WebSocketSessionManager
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Application.aiAgentWebSocketRoute() {
    val aiAgentService by inject<AIAgentService>()

    routing {
        webSocket("/ai-agent/{chatRoomId}") {
            val chatRoomId = call.parameters["chatRoomId"]?.toIntOrNull()
            val token = call.request.queryParameters["token"]

            if (chatRoomId == null) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing chatRoomId"))
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
                            // Handle AI message processing
                            "ai_agent" -> {
                                try {
                                    val payload = Json.decodeFromString<AIAgentPayload>(jsonText)

                                    if (payload.userMessage.isBlank()) {
                                        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Empty message"))
                                        return@consumeEach
                                    }

                                    // Generate AI response
                                    val aiResponseText = aiAgentService.processUserMessage(
                                        chatRoomId = payload.chatRoomId,
                                        userId = payload.senderId,
                                        userMessage = payload.userMessage
                                    )

                                    val enrichedPayload = AIAgentResponse(
                                        chatRoomId = payload.chatRoomId,
                                        content = aiResponseText,
                                        timestamp = System.currentTimeMillis().toString()
                                    )

                                    val responseJson = Json.encodeToString(enrichedPayload)

                                    // Broadcast AI response to all clients in the room
                                    WebSocketSessionManager.broadcastMessage(payload.chatRoomId, responseJson)

                                } catch (e: Exception) {
                                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid AI message format"))
                                }
                            }

                            // Handle history request
                            "ai_history" -> {
                                try {
                                    val historyPayload = Json.decodeFromString<AIHistoryPayload>(jsonText)
                                    val history = aiAgentService.getConversationHistory(
                                        chatRoomId = historyPayload.chatRoomId,
                                        userId = historyPayload.senderId
                                    )

                                    // Convert history to JSON
                                    val historyJson = buildJsonObject {
                                        put("type", JsonPrimitive("ai_history"))
                                        put("chatRoomId", JsonPrimitive(historyPayload.chatRoomId))
                                        putJsonArray("history") {
                                            history.forEach { conv ->
                                                add(buildJsonObject {
                                                    put("id", JsonPrimitive(conv.id))
                                                    put("userMessage", JsonPrimitive(conv.userMessage))
                                                    put("aiResponse", JsonPrimitive(conv.aiResponse))
                                                    put("createdAt", JsonPrimitive(conv.createdAt))
                                                })
                                            }
                                        }
                                    }

                                    // Send history only to the requesting client
                                    outgoing.send(Frame.Text(historyJson.toString()))

                                } catch (e: Exception) {
                                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid history request"))
                                }
                            }

                            else -> {
                                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unsupported type"))
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
