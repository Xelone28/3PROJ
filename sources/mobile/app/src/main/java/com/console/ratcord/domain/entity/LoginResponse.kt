package com.console.ratcord.domain.entity

import kotlinx.serialization.Serializable
@Serializable
data class LoginResponse(
    val id: Int,
    val username: String,
    val token: String
)
