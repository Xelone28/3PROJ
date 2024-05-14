package com.console.ratcord.domain.entity.user

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class UserMinimalWithId(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val rib: String?,
    val paypalUsername: String?,
    val image: String?
)