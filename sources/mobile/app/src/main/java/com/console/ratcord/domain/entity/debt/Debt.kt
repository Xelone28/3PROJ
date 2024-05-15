package com.console.ratcord.domain.entity.debt

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import com.console.ratcord.domain.entity.user.UserMinimal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Debt(
    @PrimaryKey val id: Int,
    val userInCredit: UserExtraMinimal,
    val userInDebt: UserExtraMinimal,
    val amount: Float,
    val isPaid: Boolean,
    val isCanceled: Boolean
    )