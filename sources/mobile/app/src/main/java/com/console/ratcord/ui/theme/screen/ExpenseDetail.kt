import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
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
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ExpenseDetails(expenseService: ExpenseService, categoryService: CategoryService, applicationContext: Context, navController: NavController, expenseId: Int?) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    val coroutineScope = rememberCoroutineScope()
    var expenseDetails by remember { mutableStateOf<Expense?>(null) }
    var category by remember { mutableStateOf<Category?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            if (expenseId != null) {
                isLoading = true
                coroutineScope.launch {
                    try {
                        expenseDetails = expenseService.getExpenseById(context = applicationContext, expenseId = expenseId)
                        category = categoryService.getCategoryById(context = applicationContext, categoryId = expenseDetails!!.categoryId)
                    } catch (e: Exception) {
                        println("Failed to retrieve expense: ${e.message}")
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
            Text("Amount: ${expenseDetails!!.amount}", style = MaterialTheme.typography.bodyLarge)
            Text("Description: ${expenseDetails!!.description}", style = MaterialTheme.typography.bodyLarge)
            Text("Place: ${expenseDetails!!.place}", style = MaterialTheme.typography.bodyLarge)
            Text("Date: ${expenseDetails!!.date}", style = MaterialTheme.typography.bodyLarge)
            Text("Cateogry: ${category!!.name}", style = MaterialTheme.typography.bodyLarge)

            expenseDetails!!.userIdInvolved.let { userList ->
                userList.forEach { user ->
                    Text("User: ${user}", style = MaterialTheme.typography.bodyLarge)
                }
            }
            IconButton(onClick = { coroutineScope.launch {
                expenseService.deleteExpense(context = applicationContext, expenseId = expenseDetails!!.id)
                navController.navigate("${ExpenseTab.Expenses}/${expenseDetails!!.groupId}")
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
