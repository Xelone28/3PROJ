import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.expense.Expense
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UsersFromGroup(groupService: GroupService, userInGroupService: UserInGroupService, applicationContext: Context, navController: NavController, groupId: Int?) {
    val coroutineScope = rememberCoroutineScope()
    var groupDetails by remember { mutableStateOf<GroupMinimalWithId?>(null) }
    var usersInGroup by remember { mutableStateOf<List<UserMinimalWithUserId>?>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (groupId != null) {
        LaunchedEffect(key1 = groupId) {
            isLoading = true
            coroutineScope.launch {
                when (val groupResult = groupService.getGroupById(applicationContext, groupId)) {
                    is Utils.Companion.Result.Success -> {
                        groupDetails = groupResult.data
                    }
                    is Utils.Companion.Result.Error -> {
                        val exception = groupResult.exception
                        errorMessage = when (exception) {
                            is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                            is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                            is Utils.Companion.UnexpectedResponseException -> exception.message ?: "An unexpected error occurred."
                            else -> "An unknown error occurred."
                        }
                    }
                }

                when (val usersResult = userInGroupService.getUsersInGroup(applicationContext, groupId)) {
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
                isLoading = false
            }
        }
    } else {
        errorMessage = "Failed to retrieve group"
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            if (groupDetails != null && usersInGroup != null) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                    )
                }
                Text("Name: ${groupDetails!!.groupName}", style = MaterialTheme.typography.bodyLarge)
                Text("Description: ${groupDetails!!.groupDesc}", style = MaterialTheme.typography.bodyLarge)
                usersInGroup?.let { userList ->
                    userList.forEach { user ->
                        UserCard(
                            userMinimalWithId = user,
                            onClick = {
                                navController.navigate("${Screen.UserDetails}/${user.userId}")
                            }
                        )
                    }
                }
                IconButton(onClick = { navController.navigate("${Screen.AddUserInGroup}/${groupId}") }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add user",
                    )
                }
            }
        }
    }
}