package com.console.ratcord.domain.entity.expense

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ExpenseMinimalWithImage(
    val userId: Int,
    val groupId: Int,
    val userIdInvolved: List<Int>,
    val categoryId: Int,
    val amount: Float,
    val date: Long,
    val place: String,
    val description: String,
    @Transient
    val imagePath: String? = null
)
