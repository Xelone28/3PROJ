import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserExtraMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.coroutines.launch

@Composable
fun PrivateChat(userLoggedIn: UserMinimalWithImage, applicationContext: Context, userInGroupService: UserInGroupService) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var usersInGroup by remember { mutableStateOf<List<UserExtraMinimal>?>(emptyList()) }
    var userToCommunicateWith by remember { mutableStateOf<UserExtraMinimal?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = 1) {
        coroutineScope.launch {
            when (val result = userInGroupService.getUsersInUserGroups(applicationContext, userLoggedIn.id)) {
                is Utils.Companion.Result.Success -> {
                    usersInGroup = result.data
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
        }
    }

    errorMessage?.let { message ->
        AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                if (orderedList is List<Int>) {
                    val privateRecipient = "PrivateChat${orderedList[0]}${orderedList[1]}"
                    ChatScreen(username = userLoggedIn.username, privateRecipient = privateRecipient)
                } else {
                    errorMessage = "An error occurred, please contact an administrator"
                }
            }
        }
    }
}
