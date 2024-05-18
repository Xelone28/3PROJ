import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
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
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.Category
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import kotlinx.coroutines.launch
@Composable
fun CategoriesFromGroup(categoryService: CategoryService, applicationContext: Context, navController: NavController, groupId: Int) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
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
                        when (val result = categoryService.getCategoryByGroupId(applicationContext, groupId)) {
                            is Utils.Companion.Result.Success -> {
                                categories = result.data
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
        modifier = Modifier.padding(16.dp)
    ) {
        if (isLoading) {
            Text(text = "Loading...")
        } else {
            errorMessage?.let { message ->
                AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
            }
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            IconButton(onClick = { navController.navigate("${Screen.AddCategoryToGroup}/$groupId") } ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add category"
                )
            }
            categories?.let { categoryList ->
                categoryList.forEach { category ->
                    CategoryCard(
                        name = category.name,
                        deleteCategory = {
                            coroutineScope.launch {
                                when (val result = categoryService.deleteCategory(applicationContext, categoryId = category.id)) {
                                    is Utils.Companion.Result.Success -> {
                                        navController.navigate("${Screen.CategoriesFromGroup}/$groupId")
                                    }
                                    is Utils.Companion.Result.Error -> {
                                        val exception = result.exception
                                        val error = when (exception) {
                                            is Utils.Companion.ConflictException -> "Cannot delete the category because it is referenced by an expense."
                                            else -> exception.localizedMessage ?: "An unknown error occurred."
                                        }
                                        // Display the error message to the user
                                        errorMessage = error
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}