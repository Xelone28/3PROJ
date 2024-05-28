import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.group.GroupMinimal

@Composable
fun GroupForm(groupService: GroupService, applicationContext: Context, navController: NavController) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(navController)
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val group = GroupMinimal(
                                groupName = name,
                                groupDesc = description.takeIf { it.isNotBlank() }
                            )
                            when (val result =
                                groupService.createGroup(applicationContext, group)) {
                                is Utils.Companion.Result.Success -> {
                                    navController.navigate(Screen.Groups.route)
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
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Create")
                }
            }
        }
    }
}
