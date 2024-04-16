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
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.console.ratcord.Screen
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.UserService

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {

    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Register",
                icon = Icons.Filled.Home,
                route = Screen.Register.route
            ),
            BottomNavigationItem(
                label = "Login",
                icon = Icons.Filled.Info,
                route = Screen.Login.route
            ),
            BottomNavigationItem(
                label = "User",
                icon = Icons.Filled.AccountCircle,
                route = Screen.User.route
            ),
            BottomNavigationItem(
                label = "RegisterGroup",
                icon = Icons.Filled.Add,
                route = Screen.RegisterGroup.route
            ),
            BottomNavigationItem(
                label = "Groups",
                icon = Icons.Filled.Info,
                route = Screen.Groups.route
            ),
        )
    }
}

@Composable
fun BottomNavigationBar(applicationContext: Context) {
    var navigationSelectedItem by remember {
        mutableStateOf(0)
    }
    val navController = rememberNavController()
    val userService: UserService = UserService()
    val groupService: GroupService = GroupService()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed {index,navigationItem ->
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
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screen.Login.route) {
                LoginForm(userService = userService, applicationContext = applicationContext )
            }
            composable(Screen.Register.route) {
                RegisterForm(userService = userService, applicationContext = applicationContext)
            }
            composable(Screen.User.route) {
                ProfileDetail(userService = userService, applicationContext = applicationContext)
            }
            composable(Screen.RegisterGroup.route) {
                GroupForm(groupService = groupService, applicationContext = applicationContext)
            }
            composable(Screen.Groups.route) {
                Groups(groupService = groupService, applicationContext = applicationContext, navController = navController)
            }
            composable(
                "${Screen.GroupDetails}/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.IntType })
            ) { navBackStackEntry ->
                GroupDetails(groupService, applicationContext, navBackStackEntry, navController)
            }
        }
    }
}