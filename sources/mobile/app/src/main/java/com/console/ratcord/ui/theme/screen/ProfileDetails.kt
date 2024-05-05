import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.User
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileDetail(userService: UserService, applicationContext: Context, navController: NavController, userId: Int?) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    val coroutineScope = rememberCoroutineScope()
    var userDetails by remember { mutableStateOf<UserMinimalWithId?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = token) {
        if (token != null) {
            if (userId != null) {
                isLoading = true
                coroutineScope.launch {
                    try {
                        userDetails = userService.getUserById(applicationContext, userId)
                    } catch (e: Exception) {
                        println("Failed to retrieve user: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                }
            }
        } else {
            navController.navigate(Screen.Profile.route)
        }
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (userDetails != null) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector =  Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                )
            }

            Text("Username: ${userDetails!!.username}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${userDetails!!.email}", style = MaterialTheme.typography.bodyLarge)
            userDetails!!.paypalUsername?.let {
                Text("PayPal Username: $it", style = MaterialTheme.typography.bodyLarge)
            }
            userDetails!!.rib?.let {
                Text("RIB: $it", style = MaterialTheme.typography.bodyLarge)
            }
            if (token?.let { Utils.getUserIdFromJwt(it) } == userId) {
                IconButton(onClick = {
                    navController.navigate("${Screen.EnsureConnexion}/${userDetails!!.id}")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit User",
                    )
                }
                Button(onClick = {
                    Utils.storeItem(context = applicationContext, value = null, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
                    Utils.storeItem(context = applicationContext, value = null, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.USER)
                    navController.navigate(Screen.Groups.route)
                })
                {
                    Text(text = "Logout")
                }
            }
        }
    }
}


