package com.example.routes

import com.example.domain.services.MessageService
import com.example.security.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.messageReactionRoutes(messageService: MessageService) {
    routing {
        authenticate("auth-jwt") {

            route("/api/messages") {

                post("{id}/reactions") {
                    val messageId = call.parameters["id"]?.toIntOrNull()
                        ?: return@post call.respondText(
                            "Invalid message ID",
                            status = HttpStatusCode.BadRequest
                        )

                    val principal = call.principal<UserPrincipal>()!!
                    val body = call.receive<Map<String, String>>()
                    val emoji = body["emoji"]
                        ?: return@post call.respondText("Missing emoji", status = HttpStatusCode.BadRequest)

                    messageService.addOrUpdateReaction(messageId, principal.id, emoji)
                    call.respond(HttpStatusCode.OK)
                }

                delete("{id}/reactions") {
                    val messageId = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respondText(
                            "Invalid message ID",
                            status = HttpStatusCode.BadRequest
                        )

                    val principal = call.principal<UserPrincipal>()!!
                    val deleted = messageService.deleteReaction(messageId, principal.id)
                    call.respond(mapOf("deleted" to deleted))
                }

                get("{id}/reactions") {
                    val messageId = call.parameters["id"]?.toIntOrNull()
                        ?: return@get call.respondText(
                            "Invalid message ID",
                            status = HttpStatusCode.BadRequest
                        )

                    val reactions = messageService.getReactions(messageId)
                    call.respond(reactions)
                }
            }
        }
    }
}
