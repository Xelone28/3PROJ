import android.content.Context
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.expense.ExpenseMinimalUpdate
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditExpenseFromGroup(userInGroupService: UserInGroupService, categoryService: CategoryService, expenseService: ExpenseService, applicationContext: Context, navController: NavController, expenseId: Int) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var usersInGroup by remember { mutableStateOf<List<UserMinimalWithUserId>?>(emptyList()) }
    var categoriesFromGroup by remember { mutableStateOf<List<Category>?>(emptyList()) }
    var expense by remember { mutableStateOf<Expense?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(key1 = expense) {
        isLoading = true
        coroutineScope.launch {
            try {
                when (val expenseResult = expenseService.getExpenseById(applicationContext, expenseId)) {
                    is Utils.Companion.Result.Success -> {
                        expense = expenseResult.data
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

                expense?.let { exp ->
                    when (val usersResult = userInGroupService.getUsersInGroup(applicationContext, exp.groupId)) {
                        is Utils.Companion.Result.Success -> {
                            usersInGroup = usersResult.data
                        }
                        is Utils.Companion.Result.Error -> {
                            val exception = usersResult.exception
                            errorMessage = when (exception) {
                                is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                                else -> "An unknown error occurred."
                            }
                        }
                    }

                    when (val categoriesResult = categoryService.getCategoryByGroupId(applicationContext, exp.groupId)) {
                        is Utils.Companion.Result.Success -> {
                            categoriesFromGroup = categoriesResult.data
                        }
                        is Utils.Companion.Result.Error -> {
                            val exception = categoriesResult.exception
                            errorMessage = when (exception) {
                                is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                                else -> "An unknown error occurred."
                            }
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

    if (isLoading) {
        CircularProgressIndicator()
    } else if (expense != null) {
        var categoryId by remember { mutableStateOf<Int?>(null) }
        var date by remember { mutableStateOf<String?>(null) }
        var dateLabel by remember { mutableStateOf("Date") }
        var place by remember { mutableStateOf(expense!!.place) }
        var description by remember { mutableStateOf(expense!!.description) }
        var usersInvolved by remember { mutableStateOf<List<Int>?>(null) }

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

        Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            // Category
            SearchableDropDown(
                context = applicationContext,
                label = "Category",
                entities = categoriesFromGroup!!,
                displayTextExtractor = { category -> category.name },
                onEntitySelected = { category -> categoryId = category.id }
            )

            // User Involved
            SearchableDropDownMultipleOptions(
                context = applicationContext,
                label = "User Involved",
                entities = usersInGroup!!,
                displayTextExtractor = { user -> user.username },
                onEntitiesSelected = { selectedUsers ->
                    usersInvolved = emptyList()
                    selectedUsers.forEach { user ->
                        usersInvolved = (usersInvolved ?: emptyList()) + user.userId
                    }
                }
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
                onValueChange = { value: String ->
                    dateLabel = value
                    date = value
                }
            )

            // Image picker button
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Pick Image")
            }

            // Display the selected image
            //imageUri?.let {
            //    bitmap?.let { bmp ->
            //        Image(bitmap = bmp.asImageBitmap(), contentDescription = "Selected Image",
            //            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), contentScale = ContentScale.Fit)
            //    }
            //}

            Button(
                onClick = {
                    coroutineScope.launch {
                        var expenseTimestamp: Date? = date?.let {
                            SimpleDateFormat("yyyy-MM-dd").parse(it)
                        }

                        val newExpense = ExpenseMinimalUpdate(
                            categoryId = categoryId,
                            date = expenseTimestamp?.time?.div(1000),
                            description = description,
                            place = place,
                            userIdsInvolved = usersInvolved
                        )

                        when (val updateResult = expenseService.updateExpense(
                            context = applicationContext,
                            expense = newExpense,
                            expenseId = expense!!.id,
                            imageUri = imageUri
                        )) {
                            is Utils.Companion.Result.Success -> {
                                navController.navigate("${ExpenseTab.Expenses}/${expense!!.groupId}")
                            }
                            is Utils.Companion.Result.Error -> {
                                val exception = updateResult.exception
                                errorMessage = when (exception) {
                                    is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                    is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                    is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                                    else -> "An unknown error occurred."
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Edit Expense")
            }
        }
    }
}