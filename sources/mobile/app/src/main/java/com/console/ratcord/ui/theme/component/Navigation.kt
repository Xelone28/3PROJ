import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.console.ratcord.Screen
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserMinimal
import com.console.ratcord.domain.entity.user.UserMinimalWithId
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {

    fun bottomNavigationItems(applicationContext: Context) : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Groups",
                icon = Icons.Filled.Info,
                route = Screen.Groups.route
            ),
            BottomNavigationItem(
                label = "Profile",
                icon = Icons.Filled.AccountCircle,
                route = Screen.Profile.route
            )
        )
    }
}

@Composable
fun BottomNavigationBar(applicationContext: Context) {
    var navigationSelectedItem by remember {
        mutableStateOf(0)
    }
    val navController = rememberNavController()
    val userService = UserService()
    val groupService = GroupService()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems(applicationContext).forEachIndexed {index,navigationItem ->
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = {
                            Text(navigationItem.label)
                        },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                        },
                        onClick = {
                            navigationSelectedItem = index
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Profile.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screen.Login.route) {
                LoginForm(userService = userService, applicationContext = applicationContext, navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterForm(userService = userService, navController = navController)
            }
            composable(Screen.Profile.route) {
                Profile(applicationContext = applicationContext, navController = navController)
            }
            composable(
                "${Screen.UserDetails.route}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val userId = navBackStackEntry.arguments?.getInt("userId")
                ProfileDetail(userService = userService, applicationContext = applicationContext, navController = navController, userId = userId)
            }

            composable(Screen.RegisterGroup.route) {
                GroupForm(groupService = groupService, navController = navController)
            }
            composable(Screen.Groups.route) {
                Groups(groupService = groupService, applicationContext = applicationContext, navController = navController)
            }
            composable(
                "${Screen.GroupDetails}/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val groupId = navBackStackEntry.arguments?.getInt("groupId")
                GroupDetails(groupService, applicationContext, navController, groupId)
            }
            composable(
                "${Screen.EditUser}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val userId = navBackStackEntry.arguments?.getInt("userId")
                EditUserForm(userService = userService, applicationContext = applicationContext, navController = navController, userId = userId)
            }
            composable(
                "${Screen.EnsureConnexion}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val userId = navBackStackEntry.arguments?.getInt("userId")
                EnsureConnexion(userService = userService, applicationContext = applicationContext, navController = navController, screenRedirection = "${Screen.EditUser}/$userId")
            }
        }
    }
}