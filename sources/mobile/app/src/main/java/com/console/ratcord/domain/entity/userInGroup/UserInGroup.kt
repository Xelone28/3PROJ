package com.console.ratcord.domain.entity.userInGroup

import androidx.room.Entity
import androidx.room.ForeignKey
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.user.User
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "UserInGroup",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserInGroup(
    val userId: Int,
    val groupId: Int,
    val isGroupAdmin: Boolean,
    val isActive: Boolean
)
