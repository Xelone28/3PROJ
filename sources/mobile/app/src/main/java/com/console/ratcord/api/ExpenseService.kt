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

class ExpenseService {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun getExpenseById(context: Context, expenseId: Int): Utils.Companion.Result<Expense> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getExpenseByGroupId(context: Context, groupId: Int): Utils.Companion.Result<List<ExpenseMinimal>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/expense/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun createExpense(context: Context, expense: ExpenseMinimalWithImage, imageUri: Uri?): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:5000/expense") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
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
                        expense.userIdsInvolved.forEach { userId ->
                            append("userIdsInvolved[]", userId.toString())
                        }
                        expense.weights.forEach { weight ->
                            append("weights[]", weight)
                        }
                        if (imageUri != null) {
                            appendFile("image", imageUri, context)
                        }
                    }
                )
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("HTTP request failed: ${e.localizedMessage}"))
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
    suspend fun updateExpense(context: Context, expense: ExpenseMinimalUpdate, expenseId: Int, imageUri: Uri?): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.patch("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                body = MultiPartFormDataContent(
                    formData {
                        expense.categoryId?.let {
                            append("categoryId", it.toString())
                        }
                        expense.date?.let {
                            append("date", it.toString())
                        }
                        expense.place?.let {
                            append("place", it)
                        }
                        expense.description?.let {
                            append("description", it)
                        }
                        expense.userIdsInvolved?.forEach { userId ->
                            append("userIdInvolved[]", userId.toString())
                        }
                        if (imageUri != null) {
                            appendFile("image", imageUri, context)
                        }
                    }
                )
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("HTTP request failed: ${e.localizedMessage}"))
        }
    }

    suspend fun deleteExpense(context: Context, expenseId: Int): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.delete("http://10.0.2.2:5000/expense/$expenseId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponse(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    private suspend fun handleResponse(response: HttpResponse): Utils.Companion.Result<Boolean> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.NoContent, HttpStatusCode.OK, HttpStatusCode.Created -> {
                Utils.Companion.Result.Success(true)
            }
            else -> {
                val errorMessage = response.bodyAsText() // Read the error message from the response body
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }

    private suspend inline fun <reified T> handleResponseWithBody(response: HttpResponse): Utils.Companion.Result<T> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.OK -> {
                val body: String = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true }
                val data = json.decodeFromString<T>(body)
                Utils.Companion.Result.Success(data)
            }
            else -> {
                val errorMessage = response.bodyAsText() // Read the error message from the response body
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }
}