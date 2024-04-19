import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.console.ratcord.api.GroupService
import com.console.ratcord.domain.entity.group.GroupMinimal

@Composable
fun GroupForm(groupService: GroupService, applicationContext: Context) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    val group = GroupMinimal(
                        groupName = name,
                        groupDesc = description.takeIf { it.isNotBlank() }
                    )
                    val result = groupService.createGroup(group)
                    println("Registration result: $result")
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Create")
        }
    }
}
