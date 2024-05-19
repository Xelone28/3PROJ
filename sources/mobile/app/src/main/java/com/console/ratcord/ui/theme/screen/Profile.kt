import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Profile(applicationContext: Context, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF4CAF50))

        LaunchedEffect(Unit) {
            val token: String? = Utils.getItem(
                context = applicationContext,
                fileKey = LocalStorage.PREFERENCES_FILE_KEY,
                key = LocalStorage.TOKEN_KEY
            )
            if (token != null) {
                val userId: Int? = Utils.getUserIdFromJwt(jwtToken = token)
                if (userId != null) {
                    navController.navigate("${Screen.UserDetails}/$userId")
                } else {
                    println("Issue here, please contact admin")
                }
            } else {
                navController.navigate(Screen.Login.route)
            }
        }
    }
}
