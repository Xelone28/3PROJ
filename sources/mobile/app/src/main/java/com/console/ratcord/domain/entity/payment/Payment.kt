package com.console.ratcord.domain.entity.payment

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Payment")
data class Payment(
    @PrimaryKey val id: Int,
    val user: UserExtraMinimal,
    val userInCredit: UserExtraMinimal,
    val groupId: Int,
    val amount: Float,
    val debtAdjustmentId: Int?,
    val paymentDate: String,
    val type: String,
    val image: String
)