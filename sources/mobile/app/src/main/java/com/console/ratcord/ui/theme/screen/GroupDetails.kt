import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.console.ratcord.api.GroupService
import com.console.ratcord.domain.entity.group.GroupMinimalWithId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GroupDetails(groupService: GroupService, applicationContext: Context, navController: NavController, groupId: Int?) {
    val coroutineScope = rememberCoroutineScope()
    var groupDetails by remember { mutableStateOf<GroupMinimalWithId?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Button(onClick = { navController.popBackStack() }) {
        Text(text = "Go Back")
    }

    if (groupId != null) {
        LaunchedEffect(key1 = groupId) {
            isLoading = true
            coroutineScope.launch {
                try {
                    groupDetails = groupService.getGroupById(applicationContext, groupId)
                } catch (e: Exception) {
                    println("Failed to retrieve group: ${e.message}")
                } finally {
                    isLoading = false
                }
            }
        }
    } else {
        println("groupdId is null")
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (groupDetails != null) {
            Text("Name: ${groupDetails!!.groupName}", style = MaterialTheme.typography.bodyLarge)
            Text("Description: ${groupDetails!!.groupDesc}", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text("No details available or not logged in.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}