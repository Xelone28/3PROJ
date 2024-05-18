package com.console.ratcord

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.console.ratcord.api.Utils

sealed class Screen(val route: String) {
    object Register : Screen("Register")
    object Login : Screen("Login")
    object Profile : Screen("Profile")
    object UserDetails : Screen("UserDetails")
    object RegisterGroup : Screen("RegisterGroup")
    object GroupDetails : Screen("group_details/{groupId}")
    object AddUserInGroup : Screen("addUserInGroup/{groupId}")
    object Groups : Screen("Groups")
    object EditGroup : Screen("EditGroup/{groupId}")
    object EditUser : Screen("EditUser/{userId}")
    object EnsureConnexion : Screen("EnsureConnexion")
    object GroupsInvitation : Screen("GroupsInvitation")
    object Chat : Screen("Chat/{roomName}/{groupId}")
    object PrivateChat : Screen("PrivateChat")
}

sealed class ExpenseTab(val route: String) {
    object Expenses : ExpenseTab("ExpensesFromGroup/{groupId}")
    object UsersFromGroup : ExpenseTab("UsersFromGroup/{groupId}")
    object ExpenseDetails : ExpenseTab("ExpenseDetails/{expenseId}")
    object AddExpenseToGroup : ExpenseTab("addExpenseToGroup/{groupId}")
    object EditExpense : ExpenseTab("EditExpense/{expenseId}")
}
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Utils.getNavigation().BottomNavigationBar(applicationContext)
        }
    }
}