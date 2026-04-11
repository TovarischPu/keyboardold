package com.example.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun Navigation1() {
    val iconColorFilter = ColorFilter.tint(contentColor)
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedTabIndex by rememberUpdatedState(
        when (currentRoute) {
            "screen1" -> 0
            "screen2" -> 1
            "screen3" -> 2
            else -> 0
        }
    )

    val backdrop = rememberLayerBackdrop()

    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = "screen1",
            modifier = Modifier
                .fillMaxSize()

        ) {
            composable("screen1") { MainScreen() }
            composable("screen2") { Education() }
            composable("screen3") { Settings() }
        }

        // 2. Плавающий бар поверх
        LiquidBottomTabs(
            selectedTabIndex = { selectedTabIndex },
            onTabSelected = { index ->
                val route = listOf("screen1", "screen2", "screen3")[index]
                navController.navigate(route) {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            },
            backdrop = backdrop,
            tabsCount = 3,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            LiquidBottomTab(
                onClick = { navigateTo(navController, "screen1", 0, 0) }
            ) {
                Box(
                    Modifier
                        .size(28f.dp)
                        .paint(painterResource(com.example.keyboard.R.drawable.translator), colorFilter = iconColorFilter)
                )
            }


            LiquidBottomTab(
                onClick = { navigateTo(navController, "screen2", 1, 0) }
            ) {
                Box(
                    Modifier
                        .size(28f.dp)
                        .paint(painterResource(com.example.keyboard.R.drawable.translator), colorFilter = iconColorFilter)
                )
            }


            LiquidBottomTab(
                onClick = { navigateTo(navController, "screen3", 2, 0) }
            ) {
                Box(
                    Modifier
                        .size(28f.dp)
                        .paint(painterResource(com.example.keyboard.R.drawable.translator), colorFilter = iconColorFilter)
                )
            }
        }
    }
}
private fun navigateTo(
    navController: NavHostController,
    route: String,
    newIndex: Int,
    oldIndex: Int
) {

    navController.navigate(route) {
        popUpTo("home") { inclusive = false }
        launchSingleTop = true
    }
}