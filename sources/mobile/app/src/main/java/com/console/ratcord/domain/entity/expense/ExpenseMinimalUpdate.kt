package com.console.ratcord.domain.entity.expense

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ExpenseMinimalUpdate(
    val userIdsInvolved: List<Int>?,
    val categoryId: Int?,
    val date: Long?,
    val place: String?,
    val description: String?,
    @Transient
    val imagePath: String? = null
)