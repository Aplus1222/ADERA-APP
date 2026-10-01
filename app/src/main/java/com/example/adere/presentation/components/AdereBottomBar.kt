package com.example.adere.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.navigation.NavDestination
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TotalSecurityPrimary

@Composable
fun AdereTotalSecurityBottomBar(
    currentTab: NavDestination,
    onSelectTab: (NavDestination) -> Unit,
    onCenterAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating curved white dock
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), spotColor = Color(0x1F1A1C29)),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, CleanBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
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
                Spacer(modifier = Modifier.size(54.dp))

                // Tab 4: Security / Health Audit
                BottomBarItem(
                    selected = currentTab == NavDestination.SECURITY,
                    onClick = { onSelectTab(NavDestination.SECURITY) },
                    selectedIcon = Icons.Filled.Shield,
                    unselectedIcon = Icons.Outlined.Shield,
                    contentDescription = "Security",
                    testTag = "nav_tab_security"
                )

                // Tab 5: Profile / Settings
                BottomBarItem(
                    selected = currentTab == NavDestination.SETTINGS,
                    onClick = { onSelectTab(NavDestination.SETTINGS) },
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    testTag = "nav_tab_settings"
                )
            }
        }

        // Center Raised Floating '+' Action Button
        Box(
            modifier = Modifier
                .offset(y = (-18).dp)
                .size(54.dp)
                .shadow(12.dp, CircleShape, spotColor = TotalSecurityPrimary)
                .clip(CircleShape)
                .background(TotalSecurityPrimary)
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
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(48.dp)
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
            tint = if (selected) TextDarkPrimary else TextDarkMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}
