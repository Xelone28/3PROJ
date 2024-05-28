import android.content.Context
import androidx.compose.foundation.background
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
import com.console.ratcord.ExpenseTab
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.userInGroup.UserInGroupInvitation
import kotlinx.coroutines.launch

@Composable
fun GroupsInvitation(
    userInGroupService: UserInGroupService,
    applicationContext: Context,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    var userInvitationsToGroup by remember { mutableStateOf<List<UserInGroupInvitation>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = userInvitationsToGroup) {
        val token: String? = Utils.getItem(
            context = applicationContext,
            fileKey = LocalStorage.PREFERENCES_FILE_KEY,
            key = LocalStorage.TOKEN_KEY
        )
        if (token != null) {
            userId = Utils.getUserIdFromJwt(token)
            isLoading = true
            coroutineScope.launch {
                userId?.let {
                    when (val result = userInGroupService.getInvitationsToGroup(applicationContext, it)) {
                        is Utils.Companion.Result.Success -> {
                            userInvitationsToGroup = result.data
                        }
                        is Utils.Companion.Result.Error -> {
                            val exception = result.exception
                            errorMessage = when (exception) {
                                is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                is Utils.Companion.NotFound -> "You have no invitation"
                                is Utils.Companion.UnexpectedResponseException -> exception.message
                                    ?: "An unexpected error occurred."
                                else -> "An unknown error occurred."
                            }
                        }
                    }
                }
                isLoading = false
            }
        } else {
            println("You must be logged in")
        }
    }

    Column(
        modifier = Modifier
    ) {
        Header(navController = navController)
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4CAF50))
        } else {
            userInvitationsToGroup?.let { invitations ->
                invitations.forEach { invitation ->
                    GroupInvitationCard(
                        invitation = invitation,
                        acceptInvitation = {
                            coroutineScope.launch {
                                userId?.let {
                                    when (val result = userInGroupService.updateUserInGroup(
                                        context = applicationContext,
                                        groupId = invitation.group.id,
                                        userId = it,
                                        isActive = true,
                                        isGroupAdmin = invitation.isGroupAdmin
                                    )) {
                                        is Utils.Companion.Result.Success -> {
                                            when (val invitationsResult = userInGroupService.getInvitationsToGroup(applicationContext, it)) {
                                                is Utils.Companion.Result.Success -> {
                                                    userInvitationsToGroup = invitationsResult.data
                                                    navController.navigate(Screen.Groups.route)
                                                }
                                                is Utils.Companion.Result.Error -> {
                                                    val exception = invitationsResult.exception
                                                    errorMessage = when (exception) {
                                                        is Utils.Companion.AuthorizationException -> "Unauthorized access. Please login again."
                                                        is Utils.Companion.NetworkException -> "Network error. Please check your connection."
                                                        is Utils.Companion.UnexpectedResponseException -> exception.message
                                                            ?: "An unexpected error occurred."
                                                        else -> "An unknown error occurred."
                                                    }
                                                }
                                            }
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
                                }
                            }
                        },
                        denyInvitation = {
                            coroutineScope.launch {
                                userId?.let {
                                    when (val result = userInGroupService.deleteUserFromGroup(
                                        context = applicationContext,
                                        groupId = invitation.group.id,
                                        userId = it
                                    )) {
                                        is Utils.Companion.Result.Success -> {
                                            navController.navigate(Screen.Groups.route)
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
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
