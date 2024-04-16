package com.console.ratcord.domain.entity.user

import kotlinx.serialization.Serializable

@Serializable
data class UserExtraMinimal(
    val username: String,
    val email: String,
    val rib: String?,
    val paypalUsername: String?
)
