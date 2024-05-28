import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.console.ratcord.domain.entity.user.UserMinimalWithUserId

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropDownMultipleOptions(
    context: Context,
    label: String,
    entities: List<T>,
    displayTextExtractor: (T) -> String,
    onEntitiesSelected: (List<T>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedEntities by remember { mutableStateOf(mutableStateListOf<T>()) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedEntities.joinToString { displayTextExtractor(it) },
                onValueChange = {},
                label = { Text(text = label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )

            if (expanded) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {}
                ) {
                    entities.forEach { entity ->
                        DropdownMenuItem(
                            text = { Text(text = displayTextExtractor(entity)) },
                            onClick = {
                                if (selectedEntities.contains(entity)) {
                                    selectedEntities.remove(entity)
                                } else {
                                    println("adding a new entity to list  :")
                                    println(entity)
                                    selectedEntities.add(entity)
                                }
                                onEntitiesSelected(selectedEntities)
                                Toast.makeText(context, displayTextExtractor(entity), Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}