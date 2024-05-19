package com.console.ratcord.domain.entity.payment

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PaymentDTO(
    val id: Int?,
    val userId: Int,
    val userInCreditId: Int,
    val groupId: Int,
    val amount: Float,
    val debtAdjustmentId: Int?,
    val paymentDate: String?, // issue take good date format
    val type: Int?,
    @Transient
    val imagePath: String? = null
)