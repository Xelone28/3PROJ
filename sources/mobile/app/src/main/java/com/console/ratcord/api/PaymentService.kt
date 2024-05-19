package com.console.ratcord.api

import android.content.Context
import android.net.Uri
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import com.console.ratcord.domain.entity.debtAdjustment.DebtAdjustment
import com.console.ratcord.domain.entity.payment.Payment
import com.console.ratcord.domain.entity.payment.PaymentDTO
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
class PaymentService {
    private val client: HttpClient = Utils.getHttpClient()

    suspend fun getPaymentByGroupId(context: Context, groupId: Int): Utils.Companion.Result<List<Payment>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/api/payment/group/$groupId") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
            }
            handleResponseWithBody(response)
        } catch (e: Exception) {
            Utils.Companion.Result.Error(Utils.Companion.NetworkException("Network error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun getDebtAdjustmentByGroupId(context: Context, groupId: Int): Utils.Companion.Result<List<DebtAdjustment>> {
        return try {
            val response: HttpResponse = client.get("http://10.0.2.2:5000/debtadjustment/group/$groupId") {
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
    suspend fun createPayment(context: Context, payment: PaymentDTO, imageUri: Uri?): Utils.Companion.Result<Boolean> {
        return try {
            val response: HttpResponse = client.post("http://10.0.2.2:5000/api/payment") {
                headers {
                    append("Authorization", "Bearer ${Utils.getItem(context, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)}")
                }
                body = MultiPartFormDataContent(
                    formData {
                        append("userId", payment.userId.toString())
                        append("groupId", payment.groupId.toString())
                        append("amount", payment.amount.toString())
                        append("debtAdjustmentId", payment.debtAdjustmentId.toString())
                        append("type", payment.type.toString())
                        if (imageUri != null) {
                            appendFile("image", imageUri, context)
                        }
                    }
                )
            }
            handleNoContentResponse(response)
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

    private suspend inline fun <reified T> handleResponseWithBody(response: HttpResponse): Utils.Companion.Result<T> {
        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                Utils.Companion.Result.Error(Utils.Companion.AuthorizationException("Unauthorized access to data."))
            }
            HttpStatusCode.NoContent -> {
                Utils.Companion.Result.Success(true as T)
            }
            else -> {
                val errorMessage = response.bodyAsText()
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }

    private suspend inline fun handleNoContentResponse(response: HttpResponse): Utils.Companion.Result<Boolean> {
        return when (response.status) {
            HttpStatusCode.NoContent -> {
                Utils.Companion.Result.Success(true)
            }
            else -> {
                val errorMessage = response.bodyAsText()
                Utils.Companion.Result.Error(Utils.Companion.UnexpectedResponseException("Received unexpected status: ${response.status}. Message: $errorMessage"))
            }
        }
    }
}
