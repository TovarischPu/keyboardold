package com.example.keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import com.example.keyboard.design.LiquidBottomTab
import com.example.keyboard.design.LiquidBottomTabs
import com.example.keyboard.education.Education1
import com.google.firebase.auth.FirebaseUser
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop

@Composable
fun Navigation(backdrop: LayerBackdrop, user: FirebaseUser?,
                onSignIn: () -> Unit,
                onSignOut: () -> Unit) {
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
    var isLearningActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
        ) {
            NavHost(
                navController = navController,
                startDestination = "screen1",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("screen1") { isLearningActive=false;MainScreen(user = user) }
                composable("screen2") {
                    Education1(onLearningStateChanged = {
                        isLearningActive = it
                    },user=user)
                }
                composable("screen3") { isLearningActive=false;Settings(user = user, onSignIn = onSignIn, onSignOut = onSignOut) }
            }
        }
        AnimatedVisibility(
            visible = !isLearningActive,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ){
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
                            .paint(
                                painterResource(R.drawable.translator),
                                colorFilter = iconColorFilter
                            )
                    )
                }


                LiquidBottomTab(
                    onClick = { navigateTo(navController, "screen2", 1, 0) }
                ) {
                    Box(
                        Modifier
                            .size(28f.dp)
                            .paint(
                                painterResource(R.drawable.education),
                                colorFilter = iconColorFilter
                            )
                    )
                }


                LiquidBottomTab(
                    onClick = { navigateTo(navController, "screen3", 2, 0) }
                ) {
                    Box(
                        Modifier
                            .size(28f.dp)
                            .paint(
                                painterResource(R.drawable.settings),
                                colorFilter = iconColorFilter
                            )
                    )
                }
            }
    }}
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