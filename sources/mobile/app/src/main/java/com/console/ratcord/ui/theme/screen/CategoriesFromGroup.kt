import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.ExpenseTab
import com.console.ratcord.R
import com.console.ratcord.Screen
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import kotlinx.coroutines.launch

@Composable
fun CategoriesFromGroup(
    categoryService: CategoryService,
    applicationContext: Context,
    navController: NavController,
    groupId: Int
) {
    val token: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.TOKEN_KEY
    )
    val coroutineScope = rememberCoroutineScope()
    var categories by remember { mutableStateOf<List<Category>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            val userId: Int? = Utils.getUserIdFromJwt(token)
            isLoading = true
            coroutineScope.launch {
                try {
                    if (userId is Int) {
                        when (val result =
                            categoryService.getCategoryByGroupId(applicationContext, groupId)) {
                            is Utils.Companion.Result.Success -> {
                                categories = result.data
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
                    }
                } catch (e: Exception) {
                    errorMessage = "Failed to retrieve categories: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        } else {
            println("You must be logged in")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }
            categories?.let { categoryList ->
                categoryList.forEach { category ->
                    CategoryCard(
                        name = category.name,
                        deleteCategory = {
                            coroutineScope.launch {
                                when (val result = categoryService.deleteCategory(
                                    applicationContext,
                                    categoryId = category.id
                                )) {
                                    is Utils.Companion.Result.Success -> {
                                        navController.navigate("${Screen.GroupDetails}/$groupId")
                                    }

                                    is Utils.Companion.Result.Error -> {
                                        val exception = result.exception
                                        val error = when (exception) {
                                            is Utils.Companion.ConflictException -> "Cannot delete the category because it is referenced by an expense."
                                            else -> exception.localizedMessage
                                                ?: "An unknown error occurred."
                                        }
                                        errorMessage = error
                                    }
                                }
                            }
                        }
                    )
                }
            }
            IconButton(
                onClick = { navController.navigate("${Screen.AddCategoryToGroup}/$groupId") },
                modifier = Modifier
                    .padding(16.dp)
                    .background(color = colorResource(id = R.color.green))
                    .clip(RoundedCornerShape(50))
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add category",
                    tint = Color.White
                )
            }
        }
    }
}

