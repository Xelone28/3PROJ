import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.console.ratcord.domain.entity.expense.ExpenseMinimalWithImage
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseToGroup(
    userInGroupService: UserInGroupService,
    categoryService: CategoryService,
    expenseService: ExpenseService,
    applicationContext: Context,
    navController: NavController,
    groupId: Int?
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var usersInGroup by remember { mutableStateOf<List<UserMinimalWithUserId>?>(emptyList()) }
    var categoriesFromGroup by remember { mutableStateOf<List<Category>?>(emptyList()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

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

    if (groupId != null) {
        LaunchedEffect(key1 = groupId) {
            coroutineScope.launch {
                val usersResult = userInGroupService.getUsersInGroup(applicationContext, groupId)
                val categoriesResult = categoryService.getCategoryByGroupId(applicationContext, groupId)

                when (usersResult) {
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

                when (categoriesResult) {
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            // Original expense user
            SearchableDropDown(
                context = applicationContext,
                label = "Paid by",
                entities = usersInGroup!!,
                displayTextExtractor = { user -> user.username },
                onEntitySelected = { user -> userId = user.userId }
            )

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
                    selectedUsers.forEach { user -> usersInvolved = (usersInvolved ?: emptyList()) + user.userId }
                }
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue -> amount = newValue },
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
                onValueChange = { value ->
                    dateLabel = value
                    date = value
                }
            )

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Pick Image")
            }

            // Display the selected image
            // imageUri?.let {
            //    bitmap?.let { bmp ->
            //        Image(bitmap = bmp.asImageBitmap(), contentDescription = "Selected Image",
            //            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), contentScale = ContentScale.Fit)
            //    }
            // }

            Button(
                onClick = {
                    if (userId != null && categoryId != null && date != null && usersInvolved != null && description.isNotEmpty() && place.isNotEmpty()) {
                        coroutineScope.launch {
                            val newAmount: Float? = try {
                                amount.toFloat()
                            } catch (e: Exception) {
                                errorMessage = "Please enter a valid amount."
                                null
                            }
                            if (newAmount != null) {
                                val expenseTimestamp = SimpleDateFormat("yyyy-MM-dd").parse(date).time / 1000
                                val newExpense = ExpenseMinimalWithImage(
                                    groupId = groupId,
                                    userId = userId!!,
                                    amount = newAmount,
                                    categoryId = categoryId!!,
                                    date = expenseTimestamp,
                                    description = description,
                                    place = place,
                                    userIdsInvolved = usersInvolved!!,
                                    imagePath = imageUri?.path
                                )

                                val createExpenseResult = expenseService.createExpense(
                                    context = applicationContext,
                                    expense = newExpense,
                                    imageUri = imageUri
                                )

                                when (createExpenseResult) {
                                    is Utils.Companion.Result.Success -> {
                                        navController.navigate("${ExpenseTab.Expenses}/${groupId}")
                                    }
                                    is Utils.Companion.Result.Error -> {
                                        val exception = createExpenseResult.exception
                                        errorMessage = when (exception) {
                                            is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                            is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                            is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                                            else -> "An unknown error occurred."
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        errorMessage = "Please fill in all fields."
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Register")
            }
        }
    }
}
