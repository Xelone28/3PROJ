package com.console.ratcord.domain.entity.category

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Category",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Category(
    @PrimaryKey val id: Int,
    val groupId: Int,
    val name: String
)