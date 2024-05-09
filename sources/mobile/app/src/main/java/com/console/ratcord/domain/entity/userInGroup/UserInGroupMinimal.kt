package com.console.ratcord.domain.entity.userInGroup
import kotlinx.serialization.Serializable
@Serializable
data class UserInGroupMinimal(
    val groupId: Int,
    val userId: Int,
    val isGroupAdmin: Boolean,
    val isActive: Boolean
)
