import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.DebtAdjustmentService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.PaymentService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.debtAdjustment.DebtAdjustment
import com.console.ratcord.domain.entity.payment.PaymentDTO
import com.console.ratcord.domain.entity.payment.Status
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EditPayment(
    debtAdjustmentService: DebtAdjustmentService,
    paymentService: PaymentService,
    applicationContext: Context,
    navController: NavController,
    debtAdjustmentId: Int?
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var debtAdjustment by remember { mutableStateOf<DebtAdjustment?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var paymentType by remember { mutableStateOf<Status?>(null) }

    LaunchedEffect(key1 = debtAdjustmentId) {
        isLoading = true
        coroutineScope.launch {
            try {
                if (debtAdjustmentId != null) {
                    when (val debtsResult = debtAdjustmentService.getDebtAdjustmentById(
                        context = applicationContext,
                        debtAdjustmentId = debtAdjustmentId
                    )) {
                        is Utils.Companion.Result.Success -> {
                            debtAdjustment = debtsResult.data
                        }
                        is Utils.Companion.Result.Error -> {
                            val exception = debtsResult.exception
                            errorMessage = when (exception) {
                                is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                is Utils.Companion.UnexpectedResponseException -> exception.message
                                    ?: "An unexpected error occurred."
                                else -> "An unknown error occurred."
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Failed to retrieve debt adjustment: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (debtAdjustment != null) {
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
            uri?.let {
                applicationContext.contentResolver.openInputStream(it)?.use { inputStream ->
                    bitmap = BitmapFactory.decodeStream(inputStream)
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Header(navController = navController)
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            OutlinedTextField(
                value = debtAdjustment!!.adjustmentAmount.toString(),
                onValueChange = {},
                label = { Text("Amount") },
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 32.dp)
                    .fillMaxWidth(),
                enabled = false
            )

            // Dropdown for Payment Type
            SearchableDropDown(
                context = applicationContext,
                label = "Payment Type",
                entities = Status.values().toList(),
                displayTextExtractor = { it.description },
                onEntitySelected = { paymentType = it }
            )

            Row (){
                Button(modifier = Modifier.padding(horizontal = 16.dp), onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Pick Image")
                }

                imageUri?.let {
                    bitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newPayment = PaymentDTO(
                                id = null,
                                userId = debtAdjustment!!.userInDebtId,
                                userInCreditId = debtAdjustment!!.userInCreditId,
                                groupId = debtAdjustment!!.groupId,
                                amount = debtAdjustment!!.adjustmentAmount,
                                debtAdjustmentId = debtAdjustmentId,
                                paymentDate = null,
                                type = paymentType?.value,
                                imagePath = imageUri.toString()
                            )

                            when (val result = paymentService.createPayment(applicationContext, newPayment, imageUri)) {
                                is Utils.Companion.Result.Success -> {
                                    navController.navigate("${Screen.BalancedDebtByGroup}/${debtAdjustment!!.groupId}")
                                }
                                is Utils.Companion.Result.Error -> {
                                    val exception = result.exception
                                    errorMessage = when (exception) {
                                        is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                        is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                        is Utils.Companion.UnexpectedResponseException -> exception.message
                                            ?: "An unexpected error occurred."
                                        else -> "An unknown error occurred."
                                    }
                                }
                            }
                        }
                    },
                ) {
                    Text("Send Payment")
                }
            }

        }
    }
}
