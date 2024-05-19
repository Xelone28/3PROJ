import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import com.console.ratcord.api.UserService
import com.console.ratcord.domain.entity.debtAdjustment.DebtAdjustment
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BalancedDebtByGroupDetail(
    debtAdjustmentService: DebtAdjustmentService,
    userService: UserService,
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
    var userNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var loggedInUserSerialized: UserMinimalWithImage? = null
    val loggedInUser: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.USER
    )
    if (loggedInUser != null) {
        loggedInUserSerialized = Json.decodeFromString<UserMinimalWithImage>(loggedInUser)
    }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            isLoading = true
            coroutineScope.launch {
                try {
                    when (val debtsResult = debtAdjustmentService.getDebtAdjustmentByGroupIdAndUserId(context = applicationContext, userId = userId, groupId = groupId)) {
                        is Utils.Companion.Result.Success -> {
                            val debts = debtsResult.data
                            val fetchedUserNames = mutableMapOf<Int, String>()

                            debts.forEach { debt ->
                                val userInCredit = userService.getUserById(applicationContext, debt.userInCreditId)
                                val userInDebt = userService.getUserById(applicationContext, debt.userInDebtId)
                                if (userInCredit != null) {
                                    fetchedUserNames[debt.userInCreditId] = userInCredit.username
                                }
                                if (userInDebt != null) {
                                    fetchedUserNames[debt.userInDebtId] = userInDebt.username
                                }
                            }

                            userDebts = debts
                            userNames = fetchedUserNames
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(PaddingValues(16.dp))
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color(0xFF282C34))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color.White
                )
            }

            if (userDebts.isEmpty()) {
                Text(
                    text = "No debts found for this user.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(userDebts) { debt ->
                        UserDebtDetail(debt, userNames, loggedInUserSerialized?.id)
                        if (loggedInUserSerialized?.id == debt.userInDebtId) {
                            Button(
                                onClick = { navController.navigate("${Screen.SendPaymentPage}/${debt.id}") },
                            ) {
                                Text(text = "Pay Debt", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDebtDetail(debt: DebtAdjustment, userNames: Map<Int, String>, currentUserId: Int?) {
    val isPositive = debt.userInCreditId == currentUserId
    val amountColor = if (isPositive) Color.Green else Color.Red
    val userInCreditName = userNames[debt.userInCreditId] ?: "User ${debt.userInCreditId}"
    val userInDebtName = userNames[debt.userInDebtId] ?: "User ${debt.userInDebtId}"

    val amount = if (isPositive) debt.adjustmentAmount else -debt.adjustmentAmount

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
                text = if (isPositive) "Owes to: $userInDebtName" else "Owed by: $userInCreditName",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isPositive) "+$amount" else "$amount",
                style = MaterialTheme.typography.bodyLarge,
                color = amountColor
            )
        }
    }
}
