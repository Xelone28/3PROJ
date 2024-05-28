import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.console.ratcord.ExpenseTab
import com.console.ratcord.Screen
import com.console.ratcord.api.CategoryService
import com.console.ratcord.api.DebtAdjustmentService
import com.console.ratcord.api.DebtService
import com.console.ratcord.api.ExpenseService
import com.console.ratcord.api.GroupService
import com.console.ratcord.api.LocalStorage
import com.console.ratcord.api.PaymentService
import com.console.ratcord.api.UserInGroupService
import com.console.ratcord.api.UserService
import com.console.ratcord.api.Utils
import com.console.ratcord.domain.entity.user.UserMinimalWithImage
import kotlinx.serialization.json.Json

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun bottomNavigationItems() : List<BottomNavigationItem> {
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
            ),
            BottomNavigationItem(
                label = "Messages",
                icon = Icons.Filled.Face,
                route = Screen.PrivateChat.route
            )
        )
    }
}

class Navigation() {

    val userService = UserService()
    val groupService = GroupService()
    val userInGroupService = UserInGroupService()
    val expenseService = ExpenseService()
    val categoryService = CategoryService()
    val debtService = DebtService()
    val debtAdjustmentService = DebtAdjustmentService()
    val paymentService = PaymentService()

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BottomNavigationBar(applicationContext: Context) {
        var navigationSelectedItem by remember {
            mutableStateOf(0)
        }
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar {
                    BottomNavigationItem().bottomNavigationItems()
                        .forEachIndexed { index, navigationItem ->
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
                                            saveState = false
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            )
                        }
                }
            }
        ) {
            paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Profile.route,
                modifier = Modifier.padding(paddingValues = paddingValues),
            ) {
                composable(Screen.Login.route) {
                    LoginForm(
                        userService = userService,
                        applicationContext = applicationContext,
                        navController = navController
                    )
                }
                composable(Screen.Register.route) {
                    RegisterForm(applicationContext = applicationContext, userService = userService, navController = navController)
                }
                composable(Screen.Profile.route) {
                    Profile(
                        applicationContext = applicationContext,
                        navController = navController
                    )
                }
                composable(
                    "${Screen.UserDetails}/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val userId = navBackStackEntry.arguments?.getInt("userId")
                    ProfileDetail(
                        userService = userService,
                        applicationContext = applicationContext,
                        navController = navController,
                        userId = userId
                    )
                }

                composable(Screen.RegisterGroup.route) {
                    GroupForm(
                        groupService = groupService,
                        applicationContext = applicationContext,
                        navController = navController
                    )
                }
                composable(Screen.Groups.route) {
                    Groups(
                        userInGroupService = userInGroupService,
                        applicationContext = applicationContext,
                        navController = navController
                    )
                }
                composable(
                    "${Screen.EditGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        EditGroup(
                            groupId = groupId,
                            applicationContext = applicationContext,
                            navController = navController,
                            groupService = groupService
                        )
                    }
                }
                composable(
                    "${Screen.BalancedDebtByGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        BalancedDebtByGroup(
                            groupId = groupId,
                            applicationContext = applicationContext,
                            navController = navController,
                            debtAdjustmentService = debtAdjustmentService,
                            userService = userService
                        )
                    }
                }
                composable(
                    "${Screen.SendPaymentPage}/{debtAdjustmentId}",
                    arguments = listOf(navArgument("debtAdjustmentId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val debtAdjustmentId = navBackStackEntry.arguments?.getInt("debtAdjustmentId")
                    if (debtAdjustmentId != null) {
                        EditPayment(
                            debtAdjustmentId = debtAdjustmentId,
                            applicationContext = applicationContext,
                            navController = navController,
                            debtAdjustmentService = debtAdjustmentService,
                            paymentService = paymentService
                        )
                    }
                }
                composable(
                    "${Screen.BalancedDebtByGroupDetail}/{groupId}/{userId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType }, navArgument("userId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    val userId = navBackStackEntry.arguments?.getInt("userId")

                    if (groupId != null && userId != null) {
                        BalancedDebtByGroupDetail(
                            groupId = groupId,
                            userId = userId,
                            applicationContext = applicationContext,
                            navController = navController,
                            debtAdjustmentService = debtAdjustmentService,
                            userService = userService
                        )
                    }
                }
                composable(Screen.PrivateChat.route) {
                    val loggedInUser: String? = Utils.getItem(
                        context = applicationContext,
                        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
                        key = LocalStorage.USER
                    )
                    val loggedInUserSerialized: UserMinimalWithImage? =
                        loggedInUser?.let { Json.decodeFromString<UserMinimalWithImage>(it) }
                    if (loggedInUserSerialized != null) {
                        PrivateChat(
                            userLoggedIn = loggedInUserSerialized,
                            applicationContext = applicationContext,
                            userInGroupService = userInGroupService
                        )
                    }
                }
                composable(
                    "${Screen.Chat}/{roomName}/{groupId}",
                    arguments = listOf(navArgument("roomName") { type = NavType.StringType }, navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val roomName = navBackStackEntry.arguments?.getString("roomName")
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    val loggedInUser: String? = Utils.getItem(
                        context = applicationContext,
                        fileKey = LocalStorage.PREFERENCES_FILE_KEY,
                        key = LocalStorage.USER
                    )
                    val loggedInUserSerialized: UserMinimalWithImage? =
                        loggedInUser?.let { Json.decodeFromString<UserMinimalWithImage>(it) }

                    if (roomName != null) {
                        if (loggedInUserSerialized != null) {
                            if (groupId != null) {
                                ChatScreen(
                                    username = loggedInUserSerialized.username,
                                    roomName = "GroupMesages$groupId"
                                )
                            }
                        }
                    }
                }
                composable(Screen.GroupsInvitation.route) {
                    GroupsInvitation(
                        userInGroupService = userInGroupService,
                        applicationContext = applicationContext,
                        navController = navController
                    )
                }
                composable(
                    "${Screen.GroupDetails}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    GroupDetails(
                        groupService = groupService,
                        userInGroupService = userInGroupService,
                        applicationContext = applicationContext,
                        navController = navController,
                        groupId = groupId
                    )
                }
                composable(
                    "${Screen.AddUserInGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        AddUserToGroup(
                            context = applicationContext,
                            userInGroupService = userInGroupService,
                            userService = userService,
                            navController = navController,
                            groupId = groupId
                        )
                    }
                }
                composable(
                    "${Screen.EditUser}/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val userId = navBackStackEntry.arguments?.getInt("userId")
                    EditUserForm(
                        userService = userService,
                        applicationContext = applicationContext,
                        navController = navController,
                        userId = userId
                    )
                }
                composable(
                    "${Screen.EnsureConnexion}/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val userId = navBackStackEntry.arguments?.getInt("userId")
                    EnsureConnexion(
                        userService = userService,
                        applicationContext = applicationContext,
                        navController = navController,
                        screenRedirection = "${Screen.EditUser}/$userId"
                    )
                }
                composable(
                    "${Screen.AddCategoryToGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        AddCategoryToGroup(
                            navController = navController,
                            groupId = groupId,
                            context = applicationContext,
                            categoryService = categoryService
                        )
                    }
                }
                composable(
                    "${Screen.CategoriesFromGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        CategoriesFromGroup(
                            navController = navController,
                            groupId = groupId,
                            applicationContext = applicationContext,
                            categoryService = categoryService
                        )
                    }
                }
                composable(
                    "${ExpenseTab.UsersFromGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    UsersFromGroup(
                        groupService = groupService,
                        userInGroupService = userInGroupService,
                        applicationContext = applicationContext,
                        navController = navController,
                        groupId = groupId
                    )
                }
                composable(
                    "${ExpenseTab.Expenses}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ){ navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    ExpensesFromGroup(
                        applicationContext = applicationContext,
                        groupId = groupId,
                        navController = navController,
                        expenseFromGroup = expenseService
                    )
                }
                composable(
                    "${ExpenseTab.ExpenseDetails}/{expenseId}",
                    arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val expenseId = navBackStackEntry.arguments?.getInt("expenseId")
                    if (expenseId != null) {
                        ExpenseDetails(
                            expenseService = expenseService,
                            expenseId = expenseId,
                            navController = navController,
                            applicationContext = applicationContext,
                            debtService = debtService
                        )
                    }
                }
                composable(
                    "${ExpenseTab.AddExpenseToGroup}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val groupId = navBackStackEntry.arguments?.getInt("groupId")
                    if (groupId != null) {
                        AddExpenseToGroup(
                            navController = navController,
                            groupId = groupId,
                            applicationContext = applicationContext,
                            userInGroupService = userInGroupService,
                            categoryService = categoryService,
                            expenseService = expenseService
                        )
                    }
                }
                composable(
                    "${ExpenseTab.EditExpense}/{expenseId}",
                    arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
                ) { navBackStackEntry ->
                    val expenseId = navBackStackEntry.arguments?.getInt("expenseId")
                    if (expenseId != null) {
                        EditExpenseFromGroup(
                            expenseService = expenseService,
                            navController = navController,
                            categoryService = categoryService,
                            userInGroupService = userInGroupService,
                            applicationContext = applicationContext,
                            expenseId = expenseId
                        )
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun TopNavigationBar(navController: NavController, groupId: Int, groupName: String, applicationContext: Context) {
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabTitles = listOf("Users", "Expenses", "Categories", "Group chat", "Balance", "Edit")

        Column {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> UsersContent(groupId = groupId, applicationContext=applicationContext, navController = navController)
                1 -> ExpensesContent(groupId = groupId, applicationContext = applicationContext, navController = navController)
                2 -> CategoriesContent(groupId = groupId, navController = navController, applicationContext = applicationContext)
                3 -> GroupChatContent(groupId = groupId, applicationContext = applicationContext)
                4 -> DebtAdjustment(applicationContext = applicationContext, navController = navController, groupId = groupId)
                5 -> EditGroup(groupId = groupId, applicationContext = applicationContext, navController = navController)
            }
        }
    }

    @Composable
    fun UsersContent(groupId: Int, applicationContext: Context, navController: NavController) {
        // Replace with your actual Users content
        UsersFromGroup(
            groupService = groupService,
            userInGroupService = userInGroupService,
            applicationContext = applicationContext,
            navController = navController,
            groupId = groupId
        )
    }

    @Composable
    fun ExpensesContent(groupId: Int, applicationContext: Context, navController: NavController) {
        // Replace with your actual Expenses content
        ExpensesFromGroup(
            applicationContext = applicationContext,
            groupId = groupId,
            navController = navController,
            expenseFromGroup = expenseService
        )
    }

    @Composable
    fun CategoriesContent(groupId: Int, applicationContext: Context, navController: NavController) {
        CategoriesFromGroup(
            navController = navController,
            groupId = groupId,
            applicationContext = applicationContext,
            categoryService = categoryService
        )    }

    @Composable
    fun GroupChatContent(groupId: Int, applicationContext: Context) {
        val loggedInUser: String? = Utils.getItem(
            context = applicationContext,
            fileKey = LocalStorage.PREFERENCES_FILE_KEY,
            key = LocalStorage.USER
        )
        val loggedInUserSerialized: UserMinimalWithImage? =
            loggedInUser?.let { Json.decodeFromString<UserMinimalWithImage>(it) }

        if (loggedInUserSerialized != null) {
            println("Am i passing here ?")
            ChatScreen(
                username = loggedInUserSerialized.username,
                roomName = "GroupMesages$groupId"
            )
        } else {
            println(loggedInUserSerialized)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun DebtAdjustment(groupId: Int, applicationContext: Context, navController: NavController) {
        BalancedDebtByGroup(
            groupId = groupId,
            applicationContext = applicationContext,
            navController = navController,
            debtAdjustmentService = debtAdjustmentService,
            userService = userService
        )
    }

    @Composable
    fun EditGroup(groupId: Int, applicationContext: Context, navController: NavController) {
        EditGroup(
            groupId = groupId,
            applicationContext = applicationContext,
            navController = navController,
            groupService = groupService
        )
    }
}