import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.category.CategoryMinimal

@Composable
fun AddCategoryToGroup(context: Context, categoryService: CategoryService, navController: NavController, groupId: Int) {
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

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
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                if (name.isNotEmpty()) {
                    coroutineScope.launch {
                        val categoryMinimal = CategoryMinimal(groupId = groupId, name = name)
                        when (val result = categoryService.createCategory(context = context, category = categoryMinimal)) {
                            is Utils.Companion.Result.Success -> {
                                navController.navigate("${Screen.CategoriesFromGroup}/${groupId}")
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
                } else {
                    errorMessage = "Name is empty, please put a name to the category"
                    // `errorMessage` will be handled and displayed to the user elsewhere in your code
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add")
        }
    }
}
