package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.category.CategoryMinimal
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class CategoryService {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun getCategoryById(context: Context, categoryId: Int): Utils.Companion.Result<Category?> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/category/$categoryId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }

            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to user data."))
                }
                HttpStatusCode.OK -> {
                    val body: String = response.bodyAsText()
                    Utils.Companion.Result.Success(Json.decodeFromString(body))
                }
                else -> {
                    Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getCategoryByGroupId(context: Context, groupId: Int): Utils.Companion.Result<List<Category>?> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/category/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }

            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to user data."))
                }
                HttpStatusCode.OK -> {
                    val body: String = response.bodyAsText()
                    Utils.Companion.Result.Success(Json.decodeFromString(body))
                }
                else -> {
                    Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun createCategory(context: Context, category: CategoryMinimal): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:5000/category") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                contentType(ContentType.Application.Json)
                setBody(category)
            }

            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to user data."))
                }
                HttpStatusCode.Created -> {
                    Utils.Companion.Result.Success(true)
                }
                else -> {
                    Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }
    suspend fun deleteCategory(context: Context, categoryId: Int): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.delete("http://10.0.2.2:5000/category/$categoryId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }

            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
                }
                HttpStatusCode.NoContent -> {
                    Utils.Companion.Result.Success(true)
                }
                HttpStatusCode.Conflict -> {
                    Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("You cannot delete a category that is in use"))
                }
                else -> {
                    Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}"))
                }
            }
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }
}