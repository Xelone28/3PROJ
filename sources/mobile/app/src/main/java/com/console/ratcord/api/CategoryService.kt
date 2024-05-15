package com.console.ratcord.api

import android.content.Context
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.category.CategoryMinimal
import com.console.ratcord.domain.entity.exception.AuthorizationException
import io.ktor.client.HttpClient
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

class CategoryService() {
    private val client: HttpClient = Utils.getHttpClient()
    suspend fun getCategoryById(context: Context, categoryId: Int): Category? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/category/$categoryId") {
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
                return Json.decodeFromString<Category>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun getCategoryByGroupId(context: Context, groupId: Int): List<Category>? {
        val response: HttpResponse = try {
            client.get("http://10.0.2.2:5000/category/group/$groupId") {
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
                return Json.decodeFromString<List<Category>>(body)
            }

            else -> {
                println("Received unexpected status: ${response.status}")
                return null
            }
        }
    }

    suspend fun createCategory(context: Context, category: CategoryMinimal): Boolean {
        val response: HttpResponse = try {
            client.post("http://10.0.2.2:5000/category") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context = context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                contentType(ContentType.Application.Json)
                setBody(category)
            }
        } catch (e: Exception) {
            println(e)
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
}