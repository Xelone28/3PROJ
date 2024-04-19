package com.console.ratcord.domain.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.group.Group
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Category",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Category(
    @SerialName("pk")
    @PrimaryKey val categoryId: Int,
    val groupId: String,
    val name: String
)
