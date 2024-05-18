import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.Screen
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.Expense
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.fillMaxWidth
import coil.compose.rememberAsyncImagePainter
import com.console.ratcord.api.DebtService
import com.console.ratcord.domain.entity.debt.Debt
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ExpenseDetails(expenseService: ExpenseService, debtService: DebtService, applicationContext: Context, navController: NavController, expenseId: Int?) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
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
                                    is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
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
                                    is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
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

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (expenseDetails != null) {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
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

            val imageUrl = expenseDetails!!.image
            if (imageUrl is String) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Expense Image",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(onClick = {
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
                                is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                                else -> "An unknown error occurred."
                            }
                        }
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Expense",
                )
            }

            IconButton(onClick = {
                navController.navigate("${ExpenseTab.EditExpense}/${expenseDetails!!.id}")
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit expense",
                )
            }
        }
    }
}
