package com.console.ratcord.domain.entity.group

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Group")
data class Group(
    @SerialName("pk")
    @PrimaryKey val id: Int,
    val groupName: String,
    val groupDesc: String?
)
