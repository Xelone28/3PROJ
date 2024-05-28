import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileDetail(userService: UserService, applicationContext: Context, navController: NavController, userId: Int?) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    val coroutineScope = rememberCoroutineScope()
    var userDetails by remember { mutableStateOf<UserMinimalWithImage?>(null) }
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
                        Utils.storeItem(context = applicationContext, value = null, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
                        Utils.storeItem(context = applicationContext, value = null, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.USER)
                    } finally {
                        isLoading = false
                    }
                }
            }
        } else {
            navController.navigate(Screen.Profile.route)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Header(navController)
        if (isLoading) {
            CircularProgressIndicator()
        } else if (userDetails != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageUrl = userDetails!!.image
                    if (imageUrl is String) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Profile picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(128.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Black, CircleShape),
                                alignment = Alignment.Center
                            )
                        }
                    }
                    Row {
                        Text(text = "Bonjour ",
                            style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 24.sp
                        ))
                        Text(text = "${userDetails!!.username},",
                            style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp)
                        )
                    }

                    Text("${userDetails!!.email}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(128.dp))
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
    }
}
