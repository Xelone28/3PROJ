import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GroupCard(name: String, description: String? = null, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text("Name: ${name}", style = MaterialTheme.typography.bodyLarge)

        description?.let {
            Text(
                if (it.length > 10) {
                    it.substring(0, 10) + "..."
                } else {
                    it
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
