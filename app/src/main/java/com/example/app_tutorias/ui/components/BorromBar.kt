package com.example.app_tutorias.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.navigation.Destination
import com.example.app_tutorias.ui.theme.InterFont
import com.example.app_tutorias.ui.theme.TutorLinkGreen
import com.example.app_tutorias.ui.theme.TutorLinkGreenActive
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary

private data class BottomBarItem(
    val destination: Destination,
    val icon: ImageVector
)

private val bottomBarItems = listOf(
    BottomBarItem(
        destination = Destination.Home,
        icon = Icons.Outlined.Home
    ),
    BottomBarItem(
        destination = Destination.Search,
        icon = Icons.Outlined.Search
    ),
    BottomBarItem(
        destination = Destination.Calendar,
        icon = Icons.Outlined.CalendarMonth
    ),
    BottomBarItem(
        destination = Destination.Settings,
        icon = Icons.Outlined.Settings
    )
)

@Composable
fun TutorLinkBottomBar(
    currentRoute: String?,
    onDestinationSelected: (Destination) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp
        ),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomBarItems.forEach { item ->
                val selected =
                    currentRoute == item.destination.route

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    TutorLinkGreen
                                } else {
                                    Color.Transparent
                                }
                            )
                            .clickable {
                                onDestinationSelected(
                                    item.destination
                                )
                            }
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription =
                                item.destination.label,
                            modifier = Modifier.size(20.dp),
                            tint = if (selected) {
                                TutorLinkGreenActive
                            } else {
                                TutorLinkTextSecondary
                            }
                        )

                        Text(
                            text = item.destination.label,
                            color = if (selected) {
                                TutorLinkGreenActive
                            } else {
                                TutorLinkTextSecondary
                            },
                            fontFamily = InterFont,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                    }
                }
            }
        }
    }
}