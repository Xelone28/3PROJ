import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.R
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
                                is Utils.Companion.NotFound -> "You have no group"
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
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(navController)
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            Row {
                Button(
                    onClick = { navController.navigate(Screen.RegisterGroup.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(text = "Create group", color = Color.White)
                }
                Button(
                    onClick = { navController.navigate(Screen.GroupsInvitation.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Text(text = "Invitations", color = Color.White)
                }
            }
            groups?.let { groupList ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(groupList) { group ->
                        GroupCard(
                            name = group.groupName,
                            description = group.groupDesc ?: "",
                            onClick = {
                                navController.navigate("${Screen.GroupDetails}/${group.id}")
                            },
                            imageUrl = R.drawable.background
                        )
                    }
                }
            }
        }
    }
}
