package com.example.websocket

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

object WebSocketSessionManager {

    private val sessions = ConcurrentHashMap<Int, MutableSet<DefaultWebSocketServerSession>>()

    fun addSession(chatRoomId: Int, session: DefaultWebSocketServerSession) {
        sessions.computeIfAbsent(chatRoomId) { mutableSetOf() }.add(session)
    }

    fun removeSession(chatRoomId: Int, session: DefaultWebSocketServerSession) {
        sessions[chatRoomId]?.remove(session)
        if (sessions[chatRoomId]?.isEmpty() == true) {
            sessions.remove(chatRoomId)
        }
    }

    suspend fun broadcastMessage(chatRoomId: Int, message: String) {
        sessions[chatRoomId]?.forEach { session ->
            session.send(message)
        }
    }
}
