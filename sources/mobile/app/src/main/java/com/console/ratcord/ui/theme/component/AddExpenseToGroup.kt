import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.ExpenseMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseToGroup(userInGroupService: UserInGroupService, categoryService: CategoryService, expenseService: ExpenseService, applicationContext: Context, navController: NavController, groupId: Int?) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var usersInGroup by remember { mutableStateOf<List<UserMinimalWithUserId>?>(emptyList()) }
    var categoriesFromGroup by remember { mutableStateOf<List<Category>?>(emptyList()) }

    if (groupId != null) {
        LaunchedEffect(key1 = groupId) {
            coroutineScope.launch {
                try {
                    usersInGroup = userInGroupService.getUsersInGroup(applicationContext, groupId)
                    categoriesFromGroup = categoryService.getCategoryByGroupId(applicationContext, groupId)
                } catch (e: Exception) {
                    errorMessage = "Failed to retrieve users from group"
                }
            }
        }

        var userId by remember { mutableStateOf<Int?>(null) }
        var categoryId by remember { mutableStateOf<Int?>(null) }
        var amount by remember { mutableStateOf<String>("") }
        var date by remember { mutableStateOf<String?>(null) }
        var dateLabel by remember { mutableStateOf("Date") }
        var place by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var usersInvolved by remember { mutableStateOf<List<Int>?>(emptyList()) }

        Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            //Original expense user
            SearchableDropDown(
                context = applicationContext,
                label = "Paid by",
                entities = usersInGroup!!,
                displayTextExtractor = { user ->
                    user.username
                },
                onEntitySelected = { user ->
                    userId = user.userId
                }
            )
            //Category
            SearchableDropDown(
                context = applicationContext,
                label = "Category",
                entities = categoriesFromGroup!!,
                displayTextExtractor = { category ->
                    category.name
                },
                onEntitySelected = { category ->
                    categoryId = category.id
                }
            )
            //User Involved
            SearchableDropDownMultipleOptions(
                context = applicationContext,
                label = "User Involved",
                entities = usersInGroup!!,
                displayTextExtractor = { user ->
                    user.username
                },
                onEntitiesSelected = { selectedUsers ->
                    usersInvolved = emptyList()
                    selectedUsers.forEach { user ->
                        usersInvolved = (usersInvolved ?: emptyList()) + user.userId
                    }
                }
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    amount = newValue
                },
                label = { Text("Amount") },
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                label = { Text("Place") },
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.padding(top = 8.dp)
            )
            DatePicker(
                label = dateLabel,
                value = "",
                onValueChange = {value ->
                    dateLabel = value
                    date = value
                })
            Button(
                onClick = {
                    if (userId is Int && categoryId is Int && date is String && usersInvolved is List<Int> && description != "" && place != "") {
                        coroutineScope.launch {
                            val newAmount: Float? = try {
                                amount.toFloat()
                            } catch (e: Exception) {
                                errorMessage = "Please modify amount"
                                null
                            }
                            if (newAmount is Float) {
                                var expenseTimestamp = SimpleDateFormat("yyyy-MM-dd").parse(date)
                                val newExpense = ExpenseMinimal(
                                    groupId = groupId,
                                    userId = userId!!,
                                    amount = newAmount,
                                    categoryId = categoryId!!,
                                    date = expenseTimestamp.time / 1000,
                                    description = description,
                                    place = place,
                                    userIdInvolved = usersInvolved!!
                                )
                                if (expenseService.createExpense(
                                        context = applicationContext,
                                        expense = newExpense
                                    )
                                ) {
                                    navController.navigate("${ExpenseTab.Expenses}/${groupId}")
                                } else {
                                    errorMessage = "Something went wrong, please try again"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Register")
            }        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    pattern: String = "yyyy-MM-dd",
) {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = if (value.isNotBlank()) LocalDate.parse(value, formatter) else LocalDate.now()
    val dialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            onValueChange(LocalDate.of(year, month + 1, dayOfMonth).toString())
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth,
    )

    TextField(
        label = { Text(label) },
        value = value,
        onValueChange = onValueChange,
        enabled = false,
        modifier = Modifier.clickable { dialog.show() },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}
