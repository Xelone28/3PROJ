import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileDetail(userService: UserService, applicationContext: Context) {
    val token: String? = Utils.getToken(applicationContext)
    val coroutineScope = rememberCoroutineScope()
    var userDetails by remember { mutableStateOf<UserMinimalWithId?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            val userId: String? = Utils.getUserIdFromJwt(token)
            if (userId != null) {
                isLoading = true
                coroutineScope.launch {
                    try {
                        userDetails = userService.getUser(applicationContext, userId.toInt())
                    } catch (e: Exception) {
                        println("Failed to retrieve user: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }
        } else {
        }
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (userDetails != null) {
            Text("Username: ${userDetails!!.username}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${userDetails!!.email}", style = MaterialTheme.typography.bodyLarge)
            userDetails!!.paypalUsername?.let {
                Text("PayPal Username: $it", style = MaterialTheme.typography.bodyLarge)
            }
            userDetails!!.rib?.let {
                Text("RIB: $it", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Text("No details available or not logged in.", style = MaterialTheme.typography.bodyLarge)
        }
        logoutButton(applicationContext)
    }

}

@Composable
fun logoutButton(applicationContext: Context) {
    Button(onClick = { Utils.storeToken(applicationContext, "") }) {
        Text(text = "Logout")
    }
}
