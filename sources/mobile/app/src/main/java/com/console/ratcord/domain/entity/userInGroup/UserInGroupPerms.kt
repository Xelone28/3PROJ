package com.console.ratcord.domain.entity.userInGroup
import kotlinx.serialization.Serializable

@Serializable
data class UserInGroupPerms(
    val isGroupAdmin: Boolean,
    val isActive: Boolean
)
