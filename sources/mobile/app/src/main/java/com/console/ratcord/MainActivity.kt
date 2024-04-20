package com.console.ratcord

import BottomNavigationBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

sealed class Screen(val route: String) {
    object Register : Screen("Register")
    object Login : Screen("Login")
    object Profile : Screen("Profile")
    object UserDetails : Screen("UserDetails")
    object RegisterGroup : Screen("RegisterGroup")
    object GroupDetails : Screen("group_details/{groupId}")
    object Groups : Screen("Groups")
    object EditUser : Screen("EditUser")
    object EnsureConnexion : Screen("EnsureConnexion")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomNavigationBar(applicationContext)
        }
    }
}