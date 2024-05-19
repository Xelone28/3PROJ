import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.group.Group
import kotlinx.coroutines.launch

@Composable
fun Groups(
    userInGroupService: UserInGroupService,
    applicationContext: Context,
    navController: NavController
) {
    val token: String? = Utils.getItem(
        context = applicationContext,
        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
        key = LocalStorage.TOKEN_KEY
    )
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf<List<Group>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            val userId: Int? = Utils.getUserIdFromJwt(token)
            isLoading = true
            coroutineScope.launch {
                if (userId != null) {
                    when (val result = userInGroupService.getGroupsFromUserId(applicationContext, userId)) {
                        is Utils.Companion.Result.Success -> {
                            groups = result.data
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
                } else {
                    errorMessage = "User ID is null. Please login again."
                }
                isLoading = false
            }
        } else {
            println("You must be logged in")
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F2F5))
    ) {
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .background(Color(0xFF282C34))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = Color.White
                )
            }
            Button(
                onClick = { navController.navigate(Screen.RegisterGroup.route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Create group", color = Color.White)
            }
            Button(
                onClick = { navController.navigate(Screen.GroupsInvitation.route) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Invitations", color = Color.White)
            }
            groups?.let { groupList ->
                groupList.forEach { group ->
                    GroupCard(
                        name = group.groupName,
                        description = group.groupDesc ?: "",
                        onClick = {
                            navController.navigate("${Screen.GroupDetails}/${group.id}")
                        },
                    )
                }
            }
        }
    }
}
