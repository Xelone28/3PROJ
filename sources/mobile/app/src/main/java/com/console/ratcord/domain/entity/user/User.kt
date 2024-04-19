package com.console.ratcord.domain.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "User")
data class User(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val rib: String?,
    val paypalUsername: String?
)
