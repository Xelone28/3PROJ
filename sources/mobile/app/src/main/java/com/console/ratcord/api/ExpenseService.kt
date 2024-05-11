package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.exception.AuthorizationException
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class ExpenseService() {
    private val client: HttpClient = Utils.getHttpClient()
    suspend fun getExpenseById(context: Context, expenseId: Int): Expense? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/expense/$expenseId") {
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
                return Json.decodeFromString<Expense>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun getExpenseByGroupId(context: Context, groupId: Int): List<Expense>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/expense/group/$groupId") {
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
                throw AuthorizationException("Unauthorized access to user data.")
            }

            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                return Json.decodeFromString<List<Expense>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun createExpense(context: Context, expense: ExpenseMinimal): Boolean {
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/expense") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                contentType(ContentType.Application.Json)
                setBody(expense)
            }
        } catch (e: Exception) {
            println("Something went wrong: "+e)
            return false
        }
        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to user data.")
            }

            HttpStatusCode.Created -> {
                return true
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return false
            }
        }
    }
    suspend fun updateExpense(context: Context, expense: ExpenseMinimal, expenseId: Int): Boolean {
        val response: HttpResponse = try {
            client.patch("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                    contentType(ContentType.Application.Json)
                    setBody(expense)
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return false
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to user data.")
            }
            HttpStatusCode.NoContent -> {
                return true
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return false
            }
        }
    }
    suspend fun deleteExpense(context: Context, expenseId: Int): Boolean {
        val response: HttpResponse = try {
            client.delete("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
        } catch (e: Exception) {
            println("Network error occurred: ${e.localizedMessage}")
            return false
        }

        when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to data.")
            }
            HttpStatusCode.NoContent -> {
                return true
            }
            else -> {
                println("Received unexpected status: ${response.status}")
                return false
            }
        }
    }
}