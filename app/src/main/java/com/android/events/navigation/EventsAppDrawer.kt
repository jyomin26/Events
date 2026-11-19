package com.android.events.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.events.R
import com.android.events.details.EventDetailScreen
import com.android.events.events.EventListScreen
import com.android.events.events.EventsViewModel
import com.android.events.navigation.NavRoutes.DETAIL_EVENT
import com.android.events.navigation.NavRoutes.EVENT_ID_ARG
import com.android.events.navigation.NavRoutes.EVENT_TITLE_ARG
import com.android.events.navigation.NavRoutes.NEARBY_EVENTS
import com.android.events.navigation.NavRoutes.SAVED_EVENTS
import kotlinx.coroutines.launch
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsAppDrawer() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem(
            title = stringResource(R.string.nearby_events),
            route = NEARBY_EVENTS,
            icon = Icons.Default.LocationOn
        ),
        DrawerItem(
            title = stringResource(R.string.saved_events),
            route = SAVED_EVENTS,
            icon = Icons.Default.Favorite
        )
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isDetailScreen = currentRoute == "$DETAIL_EVENT/{$EVENT_ID_ARG}/{$EVENT_TITLE_ARG}"

    val eventTitle = backStackEntry?.arguments?.getString(EVENT_TITLE_ARG)

    val screenTitle = when {
        isDetailScreen -> eventTitle ?: stringResource(R.string.event_details)
        currentRoute == SAVED_EVENTS -> stringResource(R.string.saved_events)
        else -> "Nearby Events"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = stringResource(R.string.local_events_explorer),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        horizontal = NavigationDrawerItemDefaults.ItemPadding.calculateLeftPadding(
                            LayoutDirection.Ltr
                        ),
                        vertical = Dp(24f)
                    )
                )

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }

                            navController.navigate(item.route) {
                                popUpTo(NEARBY_EVENTS) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(screenTitle) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (isDetailScreen) {
                                    navController.popBackStack()
                                } else {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isDetailScreen) {
                                    Icons.Default.ArrowBack
                                } else {
                                    Icons.Default.Menu
                                },
                                contentDescription = if (isDetailScreen) {
                                    stringResource(R.string.back)
                                } else {
                                    stringResource(R.string.open_navigation_drawer)
                                }
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = NEARBY_EVENTS,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(NEARBY_EVENTS) {
                    val viewModel: EventsViewModel = hiltViewModel()
                    val events by viewModel.events.collectAsState()

                    EventListScreen(
                        uiState = events,
                        onEventClick = { event ->
                            navController.navigate(
                                DETAIL_EVENT + "/${event.id}/${Uri.encode(event.title)}"
                            )
                        },
                        onBookmarkClick = viewModel::toggleBookmark,
                        onRetry = {

                        }
                    )
                }

                composable(SAVED_EVENTS) {
                    val viewModel: EventsViewModel = hiltViewModel()
                    val events by viewModel.bookmarkedEvents.collectAsState()

                    EventListScreen(
                        uiState = events,
                        onEventClick = { event ->
                            navController.navigate(
                                DETAIL_EVENT + "/${event.id}/${Uri.encode(event.title)}"
                            )
                        },
                        onBookmarkClick = viewModel::toggleBookmark,
                        onRetry = {
                            viewModel.retryLoadingEvents()
                        }
                    )
                }

                composable(
                    route = "$DETAIL_EVENT/{$EVENT_ID_ARG}/{$EVENT_TITLE_ARG}",
                    arguments = listOf(
                        navArgument(EVENT_ID_ARG) {
                            type = NavType.StringType
                        },
                        navArgument(EVENT_TITLE_ARG) {
                            type = NavType.StringType
                        }
                    )
                ) { entry ->

                    val eventId = entry.arguments?.getString(EVENT_ID_ARG) ?: ""
                    EventDetailScreen(eventId = eventId)
                }
            }
        }
    }
}