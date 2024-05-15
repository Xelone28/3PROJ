import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.console.ratcord.Screen
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.Utils

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Profile(applicationContext: Context, navController: NavController) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    if (token != null) {
        val userId: Int? = Utils.getUserIdFromJwt(jwtToken = token)
        if (userId != null) {
            navController.navigate("${Screen.UserDetails}/$userId")
        } else {
            println("issue here, please contact admin")
        }
    } else {
        navController.navigate(Screen.Login.route)
    }
}


