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
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import com.console.ratcord.domain.entity.expense.ExpenseMinimalUpdate
import io.ktor.util.InternalAPI

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

    suspend fun getExpenseByGroupId(context: Context, groupId: Int): List<ExpenseMinimal>? {
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
                return Json.decodeFromString<List<ExpenseMinimal>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun createExpense(context: Context, expense: ExpenseMinimalWithImage, imageUri: Uri): Boolean {
        val client = HttpClient()
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/expense") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                body = MultiPartFormDataContent(
                    formData {
                        append("userId", expense.userId.toString())
                        append("groupId", expense.groupId.toString())
                        append("categoryId", expense.categoryId.toString())
                        append("amount", expense.amount.toString())
                        append("date", expense.date.toString())
                        append("place", expense.place)
                        append("description", expense.description)
                        expense.userIdInvolved.forEach { userId ->
                            append("userIdInvolved[]", userId.toString())
                        }
                        appendFile("image", imageUri, context)
                    }
                )
            }
        } catch (e: Exception) {
            println("HTTP request failed: ${e.message}")
            return false
        }

        return when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created -> true
            else -> {
                println("Failed with status: ${response.status}")
                false
            }
        }
    }

    fun FormBuilder.appendFile(partName: String, fileUri: Uri, context: Context) {
        val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"
        val inputStream = context.contentResolver.openInputStream(fileUri) ?: return
        append(
            partName,
            inputStream.readBytes(),
            Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${fileUri.lastPathSegment}\"")
                append(HttpHeaders.ContentType, mimeType)
            }
        )
    }

    @OptIn(InternalAPI::class)
    suspend fun updateExpense(context: Context, expense: ExpenseMinimalUpdate, expenseId: Int, imageUri: Uri?): Boolean {
        val response: HttpResponse = try {
            client.patch("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                body = MultiPartFormDataContent(
                    formData {
                        expense.categoryId?.let {
                            append("place", expense.categoryId.toString())
                        }
                        expense.date?.let {
                            append("place", expense.date.toString())
                        }
                        expense.place?.let {
                            append("place", expense.place.toString())
                        }
                        expense.description?.let {
                            append("description", expense.description.toString())
                        }
                        expense.userIdInvolved?.forEach { userId ->
                            append("userIdInvolved[]", userId.toString())
                        }
                        if (imageUri is Uri) {
                            appendFile("image", imageUri, context)
                        }

                    }
                )
            }
        } catch (e: Exception) {
            println("HTTP request failed: ${e.message}")
            return false
        }

        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                throw AuthorizationException("Unauthorized access to user data.")
            }
            HttpStatusCode.NoContent, HttpStatusCode.OK -> {
                true
            }
            else -> {
                println("Failed with status: ${response.status}")
                false
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