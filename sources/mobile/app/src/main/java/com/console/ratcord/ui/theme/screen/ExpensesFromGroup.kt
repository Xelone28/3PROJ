import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.R
import com.console.ratcord.Screen
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import kotlinx.coroutines.launch

@Composable
fun ExpensesFromGroup(
    expenseFromGroup: ExpenseService,
    applicationContext: Context,
    navController: NavController,
    groupId: Int?
) {
    val coroutineScope = rememberCoroutineScope()
    var expenses by remember { mutableStateOf<List<ExpenseMinimal>?>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (groupId != null) {
        LaunchedEffect(key1 = groupId) {
            isLoading = true
            coroutineScope.launch {
                when (val result = expenseFromGroup.getExpenseByGroupId(context = applicationContext, groupId = groupId)) {
                    is Utils.Companion.Result.Success -> {
                        expenses = result.data
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
                isLoading = false
            }
        }
    } else {
        errorMessage = "Failed to retrieve group"
    }

    Column(
        modifier = Modifier
            .padding(PaddingValues(16.dp))
            .background(Color(0xFFF0F2F5))
    ) {
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            expenses?.let { expenseList ->
                expenseList.forEach { expense ->
                    ExpenseCard(
                        expense = expense,
                        onClick = {
                            navController.navigate("${ExpenseTab.ExpenseDetails}/${expense.id}")
                        }
                    )
                }
            }
        }
        Box(contentAlignment = Alignment.BottomEnd) {
            IconButton(
                onClick = { navController.navigate("${ExpenseTab.AddExpenseToGroup}/$groupId") },
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = colorResource(id = R.color.green))
                    .clip(RoundedCornerShape(50))
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add expense",
                    tint = Color.White
                )
            }
        }
    }
}
