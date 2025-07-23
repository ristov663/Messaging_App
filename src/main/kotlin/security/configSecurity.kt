package com.example.security

import com.example.config.jwtVerifier
import com.example.domain.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond

fun Application.configSecurity(userService: UserService) {

    val claimField = environment.config.property("jwt.claimField").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    val verifier = jwtVerifier()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(verifier)
            validate { credential ->
                val userId = credential.payload.getClaim(claimField).asInt()

                if (userId != null) {
                    val user = userService.getUserById(userId)
                    if (user != null) {
                        UserPrincipal(userId)
                    } else null
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
