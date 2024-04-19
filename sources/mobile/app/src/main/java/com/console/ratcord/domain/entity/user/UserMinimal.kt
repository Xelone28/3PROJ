package com.console.ratcord.domain.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class UserMinimal(
    val username: String,
    val email: String,
    val password: String,
    val rib: String?,
    val paypalUsername: String?
)
