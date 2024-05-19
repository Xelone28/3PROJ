package com.console.ratcord.domain.entity.payment

import android.annotation.SuppressLint
import androidx.annotation.StringRes

enum class Status(val value: Int, @SuppressLint("SupportAnnotationUsage") @StringRes val description: String) {
    WIRED(1, "wired"),
    STRIPE(2, "paid with Stripe"),
    PAYPAL(3, "paid with Paypal")
}
