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
                try {
                    groupDetails = groupService.getGroupById(applicationContext, groupId)
                    usersInGroup = userInGroupService.getUsersInGroup(applicationContext, groupId)
                } catch (e: Exception) {
                    println(e)
                    errorMessage = "Failed to retrieve group"
                } finally {
                    isLoading = false
                }
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
                        imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
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
                if (groupId is Int) {
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
}