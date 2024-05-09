package com.console.ratcord.domain.entity.category

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryMinimal(
    val groupId: Int,
    val name: String
)