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
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import kotlinx.coroutines.launch

@Composable
fun Groups(groupService: GroupService, applicationContext: Context, navController: NavController) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf<List<GroupMinimalWithId>?>(null) }
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = token) {
        if (token != null) {
            val userId: Int? = Utils.getUserIdFromJwt(token)

            isLoading = true
            coroutineScope.launch {
                try {
                    groups = groupService.getGroups(applicationContext) // change this for get group by user id --> userId
                } catch (e: Exception) {
                    println("Failed to retrieve group: ${e.message}")
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
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }
            Button(onClick = {navController.navigate(Screen.RegisterGroup.route) }) {
                Text(text = "Register")
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
