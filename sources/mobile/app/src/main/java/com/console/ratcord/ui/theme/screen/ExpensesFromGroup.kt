import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import kotlinx.coroutines.launch

@Composable
fun ExpensesFromGroup(expenseFromGroup: ExpenseService, applicationContext: Context, navController: NavController, groupId: Int?) {
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
                            is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
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

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
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
            IconButton(onClick = { navController.navigate("${ExpenseTab.AddExpenseToGroup}/${groupId}") }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add expense",
                )
            }
        }
    }
}