import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.coroutines.launch
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BalancedDebtByGroup(
    debtAdjustmentService: DebtAdjustmentService,
    userService: UserService,
    applicationContext: Context,
    navController: NavController,
    groupId: Int?
) {
    val token: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.TOKEN_KEY
    )
    val coroutineScope = rememberCoroutineScope()
    var userDebts by remember { mutableStateOf<Map<Int, Float>>(emptyMap()) }
    var userNames by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            if (groupId != null) {
                isLoading = true
                coroutineScope.launch {
                    try {
                        when (val debtsInGroup = debtAdjustmentService.getDebtAdjustmentByGroupId(applicationContext, groupId)) {
                            is Utils.Companion.Result.Success -> {
                                val debts = debtsInGroup.data
                                val debtMap = mutableMapOf<Int, Float>()
                                val fetchedUserNames = mutableMapOf<Int, String>()

                                debts.forEach { debt ->
                                    debtMap[debt.userInCreditId] = (debtMap[debt.userInCreditId] ?: 0f) + debt.adjustmentAmount
                                    debtMap[debt.userInDebtId] = (debtMap[debt.userInDebtId] ?: 0f) - debt.adjustmentAmount
                                }

                                for (userId in debtMap.keys) {
                                    val user = userService.getUserById(applicationContext, userId)
                                    if (user != null) {
                                        fetchedUserNames[userId] = user.username
                                    }
                                }

                                userDebts = debtMap
                                userNames = fetchedUserNames
                            }
                            is Utils.Companion.Result.Error -> {
                                val exception = debtsInGroup.exception
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

            userDebts.let { debts ->
                val maxAmount = debts.values.maxOfOrNull { abs(it) } ?: 1f
                LazyColumn {
                    items(debts.entries.toList()) { debt ->
                        if (groupId != null) {
                            val userName = userNames[debt.key] ?: "User ${debt.key}"
                            UserDebtItem(
                                userName = userName,
                                userId = debt.key,
                                groupId = groupId,
                                amount = debt.value,
                                maxAmount = maxAmount,
                                onClick = { userId, groupId ->
                                    navController.navigate("${Screen.BalancedDebtByGroupDetail}/$groupId/$userId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserDebtItem(userName: String, userId: Int, groupId: Int, amount: Float, maxAmount: Float, onClick: (Int, Int) -> Unit) {
    val isPositive = amount > 0
    val amountColor = if (isPositive) Color.Green else Color.Red

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        DebtBar(
            amount = amount,
            userName = userName,
            amountColor = amountColor,
            maxAmount = maxAmount,
            onClick = { onClick(userId, groupId) }
        )
    }
}

@Composable
fun DebtBar(amount: Float, userName: String, amountColor: Color, maxAmount: Float, onClick: () -> Unit) {
    val displayAmount = if (amount > 0) "+$amount" else "$amount"
    val barWidthPercentage = (abs(amount) / maxAmount) / 2

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = userName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(barWidthPercentage)
                .height(24.dp)
                .background(amountColor)
        ) {
            if (abs(amount) > maxAmount / 5) {
                Text(
                    text = displayAmount,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        if (abs(amount) <= maxAmount / 5) {
            Text(
                text = displayAmount,
                style = MaterialTheme.typography.bodyLarge,
                color = amountColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
