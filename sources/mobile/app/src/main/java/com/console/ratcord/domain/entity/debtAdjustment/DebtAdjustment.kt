package com.console.ratcord.domain.entity.debtAdjustment

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class DebtAdjustment(
    @PrimaryKey val id: Int,
    val groupId : Int,
    val userInCreditId: Int,
    val userInDebtId: Int,
    val adjustmentAmount: Float,
    val adjustmentDate: String //issue here
    )