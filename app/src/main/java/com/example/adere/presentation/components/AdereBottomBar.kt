package com.example.adere.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.navigation.NavDestination

@Composable
fun AdereTotalSecurityBottomBar(
    currentTab: NavDestination,
    onSelectTab: (NavDestination) -> Unit,
    onCenterAddClick: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating curved dock matching theme surface & border
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    spotColor = primaryColor
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                BottomBarItem(
                    selected = currentTab == NavDestination.HOME,
                    onClick = { onSelectTab(NavDestination.HOME) },
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    contentDescription = "Home",
                    testTag = "nav_tab_home"
                )

                // Tab 2: Vault
                BottomBarItem(
                    selected = currentTab == NavDestination.VAULT,
                    onClick = { onSelectTab(NavDestination.VAULT) },
                    selectedIcon = Icons.Filled.Lock,
                    unselectedIcon = Icons.Outlined.Lock,
                    contentDescription = "Vault",
                    testTag = "nav_tab_vault"
                )

                // Spacer for the center raised button
                Spacer(modifier = Modifier.size(56.dp))

                // Tab 4: Security / Health Audit
                BottomBarItem(
                    selected = currentTab == NavDestination.SECURITY,
                    onClick = { onSelectTab(NavDestination.SECURITY) },
                    selectedIcon = Icons.Filled.Shield,
                    unselectedIcon = Icons.Outlined.Shield,
                    contentDescription = "Security",
                    testTag = "nav_tab_security"
                )

                // Tab 5: Settings / Profile
                BottomBarItem(
                    selected = currentTab == NavDestination.SETTINGS,
                    onClick = { onSelectTab(NavDestination.SETTINGS) },
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    contentDescription = "Settings",
                    testTag = "nav_tab_settings"
                )
            }
        }

        // Center Raised Floating '+' Action Button
        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(56.dp)
                .shadow(14.dp, CircleShape, spotColor = primaryColor)
                .clip(CircleShape)
                .background(primaryColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCenterAddClick
                )
                .testTag("nav_tab_add"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Item",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String,
    testTag: String
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(54.dp, 42.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (selected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = contentDescription,
            tint = if (selected) primaryColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
