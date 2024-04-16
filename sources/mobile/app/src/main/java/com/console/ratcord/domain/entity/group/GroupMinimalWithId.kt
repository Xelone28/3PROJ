package com.console.ratcord.domain.entity.group

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class GroupMinimalWithId(
    @PrimaryKey val id: Int,
    val groupName: String,
    val groupDesc: String?
)
