package com.android.events.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)