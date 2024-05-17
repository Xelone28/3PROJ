import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.domain.entity.userInGroup.UserInGroupInvitation

@Composable
fun GroupInvitationCard(invitation: UserInGroupInvitation, acceptInvitation: () -> Unit, denyInvitation: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(invitation.group.groupName, style = MaterialTheme.typography.bodyLarge)

        invitation.group.groupDesc?.let {
            Text(
                if (it.length > 10) {
                    it.substring(0, 10) + "..."
                } else {
                    it
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row {
            IconButton(onClick = acceptInvitation) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Accept",
                )
            }
            IconButton(onClick = denyInvitation) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Deny",
                )
            }
        }
    }
}
