import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.console.ratcord.domain.entity.userInGroup.UserInGroupInvitation

@Composable
fun GroupInvitationCard(
    invitation: UserInGroupInvitation,
    acceptInvitation: () -> Unit,
    denyInvitation: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
        ) {
            Text(
                text = invitation.group.groupName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            invitation.group.groupDesc?.let {
                Text(
                    text = if (it.length > 10) {
                        it.substring(0, 10) + "..."
                    } else {
                        it
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = acceptInvitation,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Accept",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = denyInvitation,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Deny",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
