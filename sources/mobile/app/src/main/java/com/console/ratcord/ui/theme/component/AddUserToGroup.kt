import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import com.console.ratcord.api.UserService
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.domain.entity.user.UserMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import com.console.ratcord.domain.entity.userInGroup.UserInGroupMinimal
import kotlin.reflect.KSuspendFunction1

@Composable
fun AddUserToGroup(context: Context, userInGroupService: UserInGroupService, userService : UserService, navController: NavController, groupId: Int) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isGroupAdmin by remember { mutableStateOf<Boolean>(false) }

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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Checkbox(
                checked = isGroupAdmin,
                onCheckedChange = { isGroupAdmin = it },
                enabled = true
            )
            Text("Group Admin")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val user = userService.getUserByEmail(context, email = email)
                    if (user is UserMinimalWithId) {
                        val userInGroup = UserInGroupMinimal(groupId = groupId, isGroupAdmin = isGroupAdmin, isActive = false, userId = user.id)
                        userInGroupService.addUserInGroup(context, userInGroup)
                        navController.navigate(Screen.Profile.route)
                    } else {
                        errorMessage = "Register failed. Please your inputs."
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add")
        }
    }
}
