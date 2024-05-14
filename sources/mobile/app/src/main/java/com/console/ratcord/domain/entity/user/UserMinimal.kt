package com.console.ratcord.domain.entity.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserMinimal(
    val username: String,
    val email: String,
    val password: String,
    val rib: String?,
    val paypalUsername: String?,
    @Transient
    val imagePath: String? = null
)
