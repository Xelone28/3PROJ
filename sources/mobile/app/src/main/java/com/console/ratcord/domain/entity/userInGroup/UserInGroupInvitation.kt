package com.console.ratcord.domain.entity.userInGroup
import com.console.ratcord.domain.entity.group.Group
import kotlinx.serialization.Serializable
@Serializable
data class UserInGroupInvitation(
    val group: Group,
    val isGroupAdmin: Boolean,
)