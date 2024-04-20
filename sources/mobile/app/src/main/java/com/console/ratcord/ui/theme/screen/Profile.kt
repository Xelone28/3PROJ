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
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Profile(applicationContext: Context, navController: NavController) {
    val token: String? = Utils.getItem(context = applicationContext, fileKey = LocalStorage.PREFERENCES_FILE_KEY, key = LocalStorage.TOKEN_KEY)
    if (token != null) {
        val userId: Int? = Utils.getUserIdFromJwt(jwtToken = token)
        if (userId != null) {
            navController.navigate("${Screen.UserDetails.route}/$userId")
        } else {
            println("issue here, please contact admin")
        }
    } else {
        navController.navigate(Screen.Login.route)
    }
}


