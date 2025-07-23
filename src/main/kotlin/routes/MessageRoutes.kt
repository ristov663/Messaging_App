package com.example.routes

import com.example.domain.models.SendMessageRequest
import com.example.domain.services.MessageService
import com.example.security.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.messageRoutes(messageService: MessageService) {
    routing {
        authenticate("auth-jwt") {

            route("/api/messages") {

                post {
                    val principal = call.principal<UserPrincipal>()!!
                    val request = call.receive<SendMessageRequest>()
                    val message = messageService.sendMessage(request.chatRoomId, principal.id, request.content)
                    call.respond(message!!)
                }

                get("/{chatRoomId}") {
                    val chatRoomId = call.parameters["chatRoomId"]?.toIntOrNull() ?: return@get call.respondText(
                        "Invalid chatRoomId",
                        status = HttpStatusCode.BadRequest
                    )
                    val messages = messageService.getMessagesByRoom(chatRoomId)
                    call.respond(messages)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
                        "Invalid ID",
                        status = HttpStatusCode.BadRequest
                    )
                    val success = messageService.deleteMessage(id)
                    call.respond(mapOf("deleted" to success))
                }

                put("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
                        "Invalid ID", status = HttpStatusCode.BadRequest
                    )
                    val body = call.receive<Map<String, String>>()
                    val newContent = body["content"] ?: return@put call.respondText(
                        "Missing content", status = HttpStatusCode.BadRequest
                    )
                    val updated = messageService.editMessage(id, newContent)
                    call.respond(updated ?: HttpStatusCode.NotFound)
                }
            }
        }
    }
}
