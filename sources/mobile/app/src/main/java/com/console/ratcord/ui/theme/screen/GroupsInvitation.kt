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
import com.console.ratcord.domain.entity.userInGroup.UserInGroupInvitation
import kotlinx.coroutines.launch

@Composable
fun GroupsInvitation(userInGroupService: UserInGroupService, applicationContext: Context, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var userInvitationsToGroup by remember { mutableStateOf<List<UserInGroupInvitation>?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(key1 = userInvitationsToGroup) {
        val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
        if (token != null) {
            userId = Utils.getUserIdFromJwt(token)
            isLoading = true
            coroutineScope.launch {
                try {
                    if (userId is Int) {
                        userInvitationsToGroup = userInGroupService.getInvitationsToGroup(applicationContext, userId!!)
                    }
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
            userInvitationsToGroup?.let { invitations ->
                invitations.forEach { invitation ->
                    GroupInvitationCard(
                        invitation = invitation,
                        acceptInvitation = {
                            coroutineScope.launch {
                                userId?.let {
                                    userInGroupService.updateUserInGroup(
                                        context = applicationContext,
                                        groupId = invitation.group.id,
                                        userId = it,
                                        isActive = true,
                                        isGroupAdmin = invitation.isGroupAdmin
                                    )
                                }
                                userInvitationsToGroup = userInGroupService.getInvitationsToGroup(applicationContext, userId!!)
                            }
                        },
                        denyInvitation = {
                            coroutineScope.launch {
                                userId?.let {
                                    userInGroupService.deleteUserFromGroup(
                                        context = applicationContext,
                                        groupId = invitation.group.id,
                                        userId = it,
                                    )
                                }
                                userInvitationsToGroup = userInGroupService.getInvitationsToGroup(applicationContext, userId!!)
                            }
                        },
                    )
                }
            }
        }
    }
}
