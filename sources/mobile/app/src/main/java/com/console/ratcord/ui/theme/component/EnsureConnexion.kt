import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.console.ratcord.api.UserService
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.serialization.json.Json

@Composable
fun EnsureConnexion(userService: UserService, applicationContext: Context, navController: NavController, screenRedirection: String) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Header(navController = navController)
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
        OutlinedTextField(
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it },
            label = { Text("Password") }
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    val loggedInUser: String? = Utils.getItem(
                        context = applicationContext,
                        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
                        key = LocalStorage.USER
                    )
                    if (loggedInUser != null) {
                        val loggedInUserSerialized: UserMinimalWithImage =
                            Json.decodeFromString<UserMinimalWithImage>(loggedInUser)
                        if (userService.login(
                            context = applicationContext,
                            email = loggedInUserSerialized.email,
                            password = password
                        )) {
                            navController.navigate(screenRedirection)
                        } else {
                            errorMessage = "Wrong password"
                        }
                    } else {
                        println("not logged in")
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Ensure connexion")
        }
    }
}

