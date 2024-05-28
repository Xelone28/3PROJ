import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.expense.ExpenseMinimalWithImage
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
        var amount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf<String?>(null) }
        var dateLabel by remember { mutableStateOf("Date") }
        var place by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var usersInvolved by remember { mutableStateOf<List<Int>?>(emptyList()) }
        var weights by remember { mutableStateOf<List<Float>>(emptyList()) }
        var paidByWeight by remember { mutableStateOf<Float?>(null) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Header(navController = navController)
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier
                    .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SearchableDropDown(
                        context = applicationContext,
                        label = "Paid by",
                        entities = usersInGroup!!,
                        displayTextExtractor = { user -> user.username },
                        onEntitySelected = { user ->
                            userId = user.userId
                            paidByWeight = 1f // Show weight input for the paid by user when selected
                        }
                    )

                    SearchableDropDown(
                        context = applicationContext,
                        label = "Category",
                        entities = categoriesFromGroup!!,
                        displayTextExtractor = { category -> category.name },
                        onEntitySelected = { category -> categoryId = category.id }
                    )

                    SearchableDropDownMultipleOptions(
                        context = applicationContext,
                        label = "User Involved",
                        entities = usersInGroup!!,
                        displayTextExtractor = { user -> user.username },
                        onEntitiesSelected = { selectedUsers ->
                            usersInvolved = selectedUsers.map { it.userId }
                            weights = List(selectedUsers.size) { 1f }
                        }
                    )

                    val totalWeight = (paidByWeight ?: 0f) + weights.sum()

                    usersInvolved?.forEachIndexed { index, userId ->
                        val userWeight = weights.getOrNull(index) ?: 1f
                        val userShare = if (totalWeight > 0) (amount.toFloatOrNull() ?: 0f) * userWeight / totalWeight else 0f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = if (userWeight % 1 == 0f) userWeight.toInt().toString() else userWeight.toString(),
                                onValueChange = { newValue ->
                                    weights = weights.toMutableList().also { it[index] = newValue.toFloatOrNull() ?: 1f }
                                },
                                label = { Text("Weight for user ID $userId") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 8.dp)
                            )
                            Text(
                                text = "(${String.format("%.2f", userShare)})",
                                modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                            )
                        }
                    }

                    if (userId != null) {
                        val paidByShare = if (totalWeight > 0) (amount.toFloatOrNull() ?: 0f) * (paidByWeight ?: 0f) / totalWeight else 0f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = if (paidByWeight?.rem(1) == 0f) paidByWeight?.toInt().toString() else paidByWeight.toString(),
                                onValueChange = { newValue ->
                                    paidByWeight = newValue.toFloatOrNull() ?: 1f
                                },
                                label = { Text("Weight for user who paid") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 8.dp)
                            )
                            Text(
                                text = "(${String.format("%.2f", paidByShare)})",
                                modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                            )
                        }
                    }

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

                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Pick Image")
                    }

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
                                            imagePath = imageUri?.path,
                                            weights = weights
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Add expense")
                    }
                }
            }
        }
    }
}
