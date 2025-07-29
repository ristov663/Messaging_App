package com.example

import com.example.db.initDatabase
import com.example.di.configureKoin
import com.example.domain.services.ChatRoomService
import com.example.domain.services.MessageService
import com.example.domain.services.UserService
import com.example.routes.*
import com.example.security.configSecurity
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.get
import kotlin.time.Duration.Companion.seconds
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureContentNegotiation()
    initDatabase()

    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        anyHost()
    }

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val userService = get<UserService>()
    val messageService = get<MessageService>()
    val chatRoomService = get<ChatRoomService>()

    configSecurity(userService)

    chatWebSocketRoute()
    aiAgentWebSocketRoute()
    userRoutes(userService)
    messageRoutes(messageService)
    chatRoomRoutes(chatRoomService)
    messageReactionRoutes(messageService)
}
