import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId
import kotlinx.coroutines.launch

@Composable
fun PrivateChat(userLoggedIn: UserMinimalWithImage, applicationContext: Context, userInGroupService: UserInGroupService) { //NOT GOOD HERE
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var usersInGroup by remember { mutableStateOf<List<UserExtraMinimal>?>(emptyList()) }
    var userToCommunicateWith by remember { mutableStateOf<UserExtraMinimal?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = 1) {
        coroutineScope.launch {
            try {
                usersInGroup = userInGroupService.getUsersInUserGroups(applicationContext, userLoggedIn.id)
            } catch (e: Exception) {
                errorMessage = "Failed to retrieve users from group"
            }
        }
    }
    errorMessage?.let { message ->
        AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
    }
    if (userToCommunicateWith == null && usersInGroup != null) {
        SearchableDropDown(
            context = applicationContext,
            label = "Friends",
            entities = usersInGroup!!,
            displayTextExtractor = { user -> user.username },
            onEntitySelected = { user -> userToCommunicateWith = user }
        )
    } else {
        val orderedList = userToCommunicateWith?.let { listOf(userLoggedIn.id, it.id).sorted() }
        if (orderedList is List){
            val privateRecipient = "PrivateChat"+orderedList[0].toString()+orderedList[1].toString()
            ChatScreen(username = userLoggedIn.username, privateRecipient = privateRecipient)
        } else {
            errorMessage = "An error occurred, please contact an administrator"
        }
    }
}
