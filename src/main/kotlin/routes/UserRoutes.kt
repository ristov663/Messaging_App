package com.example.routes

import com.example.config.generateToken
import com.example.domain.models.LoginRequest
import com.example.domain.models.RegisterRequest
import com.example.domain.models.LoginResponse
import com.example.domain.models.UpdateUserRequest
import com.example.domain.services.UserService
import io.ktor.http.HttpStatusCode
import com.example.security.UserPrincipal
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.userRoutes(userService: UserService) {
    routing {
        authenticate("auth-jwt") {

            route("/api/users") {

                get {
                    val users = userService.getAllUsers() ?: return@get call.respond(HttpStatusCode.NoContent)
                    call.respond(HttpStatusCode.OK, users)
                }

                get("/me") {
                    val principal = call.principal<UserPrincipal>()!!
                    val userId = principal.id

                    val user = userService.getUserById(userId)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                }

                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                        return@get
                    }

                    val user = userService.getUserById(id)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else call.respond(HttpStatusCode.NotFound, "User not found")
                }

                put {
                    val principal = call.principal<UserPrincipal>()!!
                    val userId = principal.id

                    val updateRequest = call.receive<UpdateUserRequest>()
                    val updatedUser = userService.updateUser(userId, updateRequest)

                    if (updatedUser != null) {
                        call.respond(HttpStatusCode.OK, updatedUser)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                }

                delete {
                    val principal = call.principal<UserPrincipal>()!!
                    val userId = principal.id

                    userService.deleteUser(userId)
                    call.respond(HttpStatusCode.NoContent)
                }

                post("/logout") {
                    call.respond(HttpStatusCode.OK, "Logged out")
                }
            }
        }

        route("/api/users") {

            post("/register") {
                val request = call.receive<RegisterRequest>()
                val user = userService.registerUser(request)
                if (user != null) {
                    call.respond(HttpStatusCode.Created, user)
                } else call.respond(HttpStatusCode.Conflict, "Username already exists")
            }

            post("/login") {
                val request = call.receive<LoginRequest>()
                val user = userService.loginUser(request)
                if (user != null) {
                    val token = generateToken(user)
                    val response = LoginResponse(
                        id = user.id,
                        username = user.username,
                        email = user.email,
                        token = token
                    )
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")
                }
            }
        }
    }
}
