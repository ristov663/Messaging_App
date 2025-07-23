package com.example.security

import io.ktor.server.auth.Principal

data class UserPrincipal(val id: Int) : Principal
