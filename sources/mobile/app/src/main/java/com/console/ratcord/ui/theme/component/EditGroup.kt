import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.group.GroupMinimal
@Composable
fun EditGroup(groupService: GroupService, applicationContext: Context, navController: NavController, groupId: Int?) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (groupId != null) {
        var groupName by remember { mutableStateOf("") }
        var groupDescription by remember { mutableStateOf("") }

        val coroutineScope = rememberCoroutineScope()

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

            IconButton(onClick = {
                coroutineScope.launch {
                    when (val result = groupService.deleteGroup(context = applicationContext, groupId = groupId)) {
                        is Utils.Companion.Result.Success -> {
                            navController.navigate(Screen.Groups.route)
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
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Group",
                )
            }

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Name") },
                modifier = Modifier.padding(top = 8.dp)
            )
            OutlinedTextField(
                value = groupDescription,
                onValueChange = { groupDescription = it },
                label = { Text("Description") },
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        val groupMinimal = GroupMinimal(
                            groupName = groupName,
                            groupDesc = groupDescription,
                        )
                        when (val result = groupService.updateGroup(
                            context = applicationContext,
                            groupId = groupId,
                            groupMinimal = groupMinimal
                        )) {
                            is Utils.Companion.Result.Success -> {
                                navController.navigate(Screen.Groups.route)
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
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Update")
            }
        }
    }
}
