package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.expense.ExpenseMinimalWithImage
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import io.ktor.http.*
import io.ktor.client.request.forms.*
import android.net.Uri
import com.console.ratcord.domain.entity.debt.Debt
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import com.console.ratcord.domain.entity.expense.ExpenseMinimalUpdate
import io.ktor.util.InternalAPI

class DebtService() {
    private val client: HttpClient = Utils.getHttpClient()
    suspend fun getByExpenseId(context: Context, expenseId: Int): List<Debt>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/debt/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return null
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to expense data.")
            }

            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                return Json.decodeFromString<List<Debt>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }
}