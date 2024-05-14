import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.console.ratcord.api.UserService
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.domain.entity.user.UserMinimal
import kotlin.reflect.KSuspendFunction1

@Composable
fun RegisterForm(applicationContext: Context, userService: UserService, navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rib by remember { mutableStateOf("") }
    var paypalUsername by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            applicationContext.contentResolver.openInputStream(it)?.use { inputStream ->
                bitmap = BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    Column(modifier = Modifier.padding(PaddingValues(16.dp))) {
        errorMessage?.let { message ->
            AlertBaner(message = message, onAnimationEnd = { errorMessage = null })
        }
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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = rib,
            onValueChange = { rib = it },
            label = { Text("RIB") },
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = paypalUsername,
            onValueChange = { paypalUsername = it },
            label = { Text("PayPal Username") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Pick Image")
        }
        Button(
            onClick = {
                coroutineScope.launch {
                    val user = UserMinimal(
                        username = username,
                        email = email,
                        password = password,
                        rib = rib.takeIf { it.isNotBlank() },
                        paypalUsername = paypalUsername.takeIf { it.isNotBlank() },
                        imagePath = imageUri?.path

                    )
                    if (userService.createUser(context = applicationContext, user, imageUri)) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        errorMessage = "Register failed. Please your inputs."
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Register")
        }
    }
}
