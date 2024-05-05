import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId

@Composable
fun UserCard(userMinimalWithId: UserMinimalWithUserId, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(userMinimalWithId.username, style = MaterialTheme.typography.bodyLarge)
        Text(userMinimalWithId.email, style = MaterialTheme.typography.bodyLarge)
    }
}
