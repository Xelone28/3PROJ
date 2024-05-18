import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCard(name: String, deleteCategory: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Text("Name: $name", style = MaterialTheme.typography.bodyLarge)
            IconButton(onClick = deleteCategory ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete category")
            }
        }

    }
}
