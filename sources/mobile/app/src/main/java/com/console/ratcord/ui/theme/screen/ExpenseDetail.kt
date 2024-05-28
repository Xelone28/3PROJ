import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.console.ratcord.ExpenseTab
import com.console.ratcord.R
import com.console.ratcord.Screen
import com.console.ratcord.api.DebtService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.debt.Debt
import com.console.ratcord.domain.entity.expense.Expense
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ExpenseDetails(
    expenseService: ExpenseService,
    debtService: DebtService,
    applicationContext: Context,
    navController: NavController,
    expenseId: Int?
) {
    val token: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.TOKEN_KEY
    )
    val coroutineScope = rememberCoroutineScope()
    var expenseDetails by remember { mutableStateOf<Expense?>(null) }
    var debts by remember { mutableStateOf<List<Debt>?>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            if (expenseId != null) {
                isLoading = true
                coroutineScope.launch {
                    try {
                        when (val expenseResult = expenseService.getExpenseById(applicationContext, expenseId)) {
                            is Utils.Companion.Result.Success -> {
                                expenseDetails = expenseResult.data
                            }
                            is Utils.Companion.Result.Error -> {
                                val exception = expenseResult.exception
                                errorMessage = when (exception) {
                                    is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                    is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                    is Utils.Companion.UnexpectedResponseException -> exception.message
                                        ?: "An unexpected error occurred."
                                    else -> "An unknown error occurred."
                                }
                            }
                        }

                        when (val debtsResult = debtService.getByExpenseId(applicationContext, expenseId)) {
                            is Utils.Companion.Result.Success -> {
                                debts = debtsResult.data
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
                    } catch (e: Exception) {
                        errorMessage = "Failed to retrieve expense: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        } else {
            navController.navigate(Screen.Profile.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Header(navController)
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else if (expenseDetails != null) {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = expenseDetails!!.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    when (val deleteResult = expenseService.deleteExpense(applicationContext, expenseDetails!!.id)) {
                                        is Utils.Companion.Result.Success -> {
                                            navController.navigate("${ExpenseTab.Expenses}/${expenseDetails!!.groupId}")
                                        }
                                        is Utils.Companion.Result.Error -> {
                                            val exception = deleteResult.exception
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
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.green))
                                .clip(RoundedCornerShape(50))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Expense",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate("${ExpenseTab.EditExpense}/${expenseDetails!!.id}") },
                            modifier = Modifier
                                .background(color = colorResource(id = R.color.green))
                                .clip(RoundedCornerShape(50))
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit expense",
                                tint = Color.White
                            )
                        }
                    }

                    val imageUrl = expenseDetails!!.image
                    if (imageUrl is String) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Expense Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    val timestamp = expenseDetails?.date?.toLong()
                    val formattedDate = if (timestamp != null) {
                        val date = Instant.ofEpochSecond(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        date.format(formatter)
                    } else {
                        "No date"
                    }

                    Text("Amount: ${expenseDetails!!.amount}", style = MaterialTheme.typography.bodyLarge)
                    Text("Description: ${expenseDetails!!.description}", style = MaterialTheme.typography.bodyLarge)
                    Text("Place: ${expenseDetails!!.place}", style = MaterialTheme.typography.bodyLarge)
                    Text("Date: $formattedDate", style = MaterialTheme.typography.bodyLarge)
                    Text("Category: ${expenseDetails!!.category.name}", style = MaterialTheme.typography.bodyLarge)
                    Text("Paid by: ${expenseDetails!!.user.username}", style = MaterialTheme.typography.bodyLarge)

                    debts?.forEach { debt ->
                        Text("${debt.userInDebt.username} : ${debt.amount}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
