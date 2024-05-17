package com.console.ratcord.domain.entity.expense

import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseMinimal(
    @PrimaryKey val id: Int,
    val user: UserMinimalWithId,
    val groupId: Int,
    val userIdsInvolved: List<Int>,
    val categoryId: Int,
    val amount: Float,
    val date: Int,
    val place: String,
    val description: String,
)