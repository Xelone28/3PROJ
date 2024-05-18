import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.console.ratcord.Screen
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.group.Group
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import kotlinx.coroutines.launch
@Composable
fun Groups(userInGroupService: UserInGroupService, applicationContext: Context, navController: NavController) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
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
                                is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
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

    Column(modifier = Modifier.padding(16.dp)) {
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        if (isLoading) {
            Text(text = "Loading...")
        } else {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
            Button(onClick = { navController.navigate(Screen.RegisterGroup.route) }) {
                Text(text = "Create group")
            }
            Button(onClick = { navController.navigate(Screen.GroupsInvitation.route) }) {
                Text(text = "Invitations")
            }
            groups?.let { groupList ->
                groupList.forEach { group ->
                    GroupCard(
                        name = group.groupName,
                        description = group.groupDesc ?: "",
                        onClick = {
                            navController.navigate("${Screen.GroupDetails}/${group.id}")
                        }
                    )
                }
            }
        }
    }
}