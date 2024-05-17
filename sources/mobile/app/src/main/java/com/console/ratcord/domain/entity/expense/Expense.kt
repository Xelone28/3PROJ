package com.console.ratcord.domain.entity.expense

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import com.console.ratcord.domain.entity.user.UserMinimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "Expense",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userIdInvolved"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey val id: Int,
    val user: UserExtraMinimal,
    val groupId: Int,
    val usersInvolved: List<UserExtraMinimal>,
    val category: Category,
    val amount: Float,
    val date: Int,
    val place: String,
    val description: String,
    val image: String?
)