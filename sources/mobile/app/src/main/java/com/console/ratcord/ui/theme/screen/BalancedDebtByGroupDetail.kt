import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils
import com.console.ratcord.Screen
import com.console.ratcord.api.DebtAdjustmentService
import com.console.ratcord.domain.entity.debtAdjustment.DebtAdjustment
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BalancedDebtByGroupDetail(
    debtAdjustmentService: DebtAdjustmentService,
    applicationContext: Context,
    navController: NavController,
    userId: Int,
    groupId: Int
) {
    val token: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.TOKEN_KEY
    )
    val coroutineScope = rememberCoroutineScope()
    var userDebts by remember { mutableStateOf<List<DebtAdjustment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            isLoading = true
            coroutineScope.launch {
                try {
                    when (val debtsResult = debtAdjustmentService.getDebtAdjustmentByGroupIdAndUserId(context = applicationContext, userId = userId, groupId = groupId)) {
                        is Utils.Companion.Result.Success -> {
                            userDebts = debtsResult.data
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
                    errorMessage = "Failed to retrieve debts: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            navController.navigate(Screen.Profile.route)
        }
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            LazyColumn {
                items(userDebts) { debt ->
                    UserDebtDetail(debt)
                }
            }
        }
    }
}

@Composable
fun UserDebtDetail(debt: DebtAdjustment) {
    val isPositive = debt.adjustmentAmount > 0
    val amountColor = if (isPositive) Color.Green else Color.Red

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "User in Credit: ${debt.userInCreditId}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "User in Debt: ${debt.userInDebtId}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isPositive) "+${debt.adjustmentAmount}" else "${debt.adjustmentAmount}",
                style = MaterialTheme.typography.bodyLarge,
                color = amountColor
            )
        }
    }
}
