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
                expense = expenseService.getExpenseById(context = applicationContext, expenseId = expenseId)
                usersInGroup = userInGroupService.getUsersInGroup(applicationContext, expense!!.groupId)
                categoriesFromGroup = categoryService.getCategoryByGroupId(applicationContext, expense!!.groupId)
            } catch (e: Exception) {
                println("Failed to retrieve expense: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    if (isLoading) {
        CircularProgressIndicator()
    } else if (expense is Expense) {
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
                })
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
                        var expenseTimestamp: Date?;
                        if (date != null) {
                            expenseTimestamp = SimpleDateFormat("yyyy-MM-dd").parse(date)
                        } else {
                            expenseTimestamp = null
                        }

                        val newExpense = ExpenseMinimalUpdate(
                            categoryId = categoryId,
                            date = expenseTimestamp?.time?.div(1000),
                            description = description,
                            place = place,
                            userIdsInvolved = usersInvolved
                        )

                        if (expenseService.updateExpense(
                                context = applicationContext,
                                expense = newExpense,
                                expenseId = expense!!.id,
                                imageUri = imageUri

                            )
                        ) {
                            navController.navigate("${ExpenseTab.Expenses}/${expense!!.groupId}")
                        } else {
                            // Consider that it could timeout due to the image size
                            errorMessage = "Something went wrong, please try again"
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