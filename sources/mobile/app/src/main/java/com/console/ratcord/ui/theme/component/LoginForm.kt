import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import kotlinx.coroutines.launch

@Composable
fun LoginForm(userService: UserService, applicationContext: Context, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
            )
        }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    userService.login(username, password, applicationContext);
                    navController.navigate(Screen.Profile.route)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Log In")
        }
        Button(onClick = {navController.navigate(Screen.Register.route) }) {
            Text(text = "Register")
        }
    }
}