package com.binay.shaw.justap.presentation.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.account.AccountScreen
import com.binay.shaw.justap.presentation.connect.ConnectScannerScreen
import com.binay.shaw.justap.presentation.connect.ConnectScreen
import com.binay.shaw.justap.presentation.history.HistoryScreen
import com.binay.shaw.justap.presentation.home.HomeScreen
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import com.binay.shaw.justap.presentation.themes.FadeIn
import com.binay.shaw.justap.presentation.themes.FadeOut
import com.binay.shaw.justap.utilities.composeUtils.BackPressCompose
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

val LocalNavHost = staticCompositionLocalOf<NavHostController> {
    error("No Parameter is available")
}

@SuppressLint("ComposableDestinationInComposeScope", "ComposableNavGraphInComposeScope")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()

    val screensWithoutNavBar = persistentListOf(
        Screens.ConnectScannerScreen.name
    )

    BackPressCompose()

    CompositionLocalProvider(LocalNavHost provides navController) {
        Scaffold(
            bottomBar = {
                ShowBottomNavigationBar(
                    backStackEntry = backStackEntry,
                    screensWithoutNavBar = screensWithoutNavBar
                )
            },
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.then(modifier)
        ) {
            NavHost(
                navController,
                startDestination = Screens.HomeScreen.name,
                modifier = Modifier.padding(it),
                enterTransition = { FadeIn },
                exitTransition = { FadeOut },
                popEnterTransition = { FadeIn },
                popExitTransition = { FadeOut },
            ) {

                composable(route = Screens.HomeScreen.name) {
                    HomeScreen()
                }

                composable(route = Screens.ConnectScreen.name) {
                    ConnectScreen()
                }

                composable(route = Screens.HistoryScreen.name) {
                    HistoryScreen()
                }

                composable(route = Screens.AccountScreen.name) {
                    AccountScreen()
                }

                composable(route = Screens.ConnectScannerScreen.name) {
                    ConnectScannerScreen()
                }
            }
        }
    }
}


@Composable
fun ShowBottomNavigationBar(
    backStackEntry: State<NavBackStackEntry?>,
    screensWithoutNavBar: PersistentList<String>,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavHost.current

    if (backStackEntry.value?.destination?.route !in screensWithoutNavBar) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background
        ) {

            val bottomNavItems = listOf(
                BottomNavItem(
                    name = stringResource(R.string.home),
                    route = Screens.HomeScreen.name,
                    unselectedIcon = Icons.Outlined.Home,
                    selectedIcon = Icons.Default.Home
                ),
                BottomNavItem(
                    name = stringResource(R.string.connect),
                    route = Screens.ConnectScreen.name,
                    unselectedIcon = Icons.Outlined.QrCode,
                    selectedIcon = Icons.Default.QrCode
                ),
                BottomNavItem(
                    name = stringResource(R.string.history),
                    route = Screens.HistoryScreen.name,
                    unselectedIcon = Icons.Outlined.Search,
                    selectedIcon = Icons.Default.Search
                ),
                BottomNavItem(
                    name = stringResource(R.string.account),
                    route = Screens.AccountScreen.name,
                    unselectedIcon = Icons.Outlined.Person,
                    selectedIcon = Icons.Default.Person
                )
            )

            bottomNavItems.forEach { item ->

                val isSelected = backStackEntry.value?.destination?.route == item.route
                val animateIconSize by animateFloatAsState(
                    if (isSelected) 1f else 0.9f,
                    label = "iconScale"
                )
                val animateTextColor by animateColorAsState(
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "textColor"
                )

                NavigationBarItem(
                    modifier = modifier,
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.name,
                            modifier = Modifier.scale(animateIconSize),
                            tint = animateTextColor
                        )
                    },
                    label = {
                        Text(
                            text = item.name,
                            fontFamily = DMSansFontFamily,
                            color = animateTextColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold
                            else FontWeight.Normal,
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        val currentDestination =
                            navController.currentBackStackEntry?.destination?.route
                        if (item.route != currentDestination) {
                            navController.navigate(item.route) {
                                navController.graph.findStartDestination().let { route ->
                                    popUpTo(route.id) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}
