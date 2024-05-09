package com.console.ratcord.domain.entity.user

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class UserMinimalWithUserId(
    @PrimaryKey val userId: Int,
    val username: String,
    val email: String
)
