package com.example.routes

import com.example.domain.models.CreateChatRoomRequest
import com.example.domain.services.ChatRoomService
import com.example.security.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.chatRoomRoutes(chatRoomService: ChatRoomService) {
    routing {
        authenticate("auth-jwt") {
            route("/api/chat-rooms") {

                post {
                    val principal = call.principal<UserPrincipal>()!!
                    val request = call.receive<CreateChatRoomRequest>()
                    val result =
                        chatRoomService.createChatRoom(request.name, request.isGroup, request.memberIds + principal.id)
                    call.respond(result!!)
                }

                get {
                    val principal = call.principal<UserPrincipal>()!!
                    val rooms = chatRoomService.getAllRoomsForUser(principal.id)
                    call.respond(rooms)
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                        "Invalid ID",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val room = chatRoomService.getChatRoomById(id) ?: return@get call.respondText(
                        "Room not found",
                        status = io.ktor.http.HttpStatusCode.NotFound
                    )
                    call.respond(room)
                }

                delete("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
                        "Invalid ID",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val success = chatRoomService.deleteChatRoom(id)
                    call.respond(mapOf("deleted" to success))
                }

                post("/{id}/add-member") {
                    val chatRoomId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText(
                        "Invalid ID",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val userId = call.receive<Map<String, Int>>()["userId"] ?: return@post call.respondText(
                        "Missing userId",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val success = chatRoomService.addMemberToRoom(chatRoomId, userId)
                    call.respond(mapOf("added" to success))
                }

                post("/{id}/remove-member") {
                    val chatRoomId = call.parameters["id"]?.toIntOrNull() ?: return@post call.respondText(
                        "Invalid ID",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val userId = call.receive<Map<String, Int>>()["userId"] ?: return@post call.respondText(
                        "Missing userId",
                        status = io.ktor.http.HttpStatusCode.BadRequest
                    )
                    val success = chatRoomService.removeMemberFromRoom(chatRoomId, userId)
                    call.respond(mapOf("removed" to success))
                }
            }
        }
    }
}
