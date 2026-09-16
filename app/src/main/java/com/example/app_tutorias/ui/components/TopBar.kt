package com.example.app_tutorias.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.ui.theme.TutorAvatarBackground
import com.example.app_tutorias.ui.theme.TutorLinkBackground
import com.example.app_tutorias.ui.theme.TutorLinkGreenActive
import com.example.app_tutorias.ui.theme.TutorLinkGreenText
import com.example.app_tutorias.ui.theme.TutorLinkInputBackground

@Composable
fun TutorLinkTopBar(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TutorLinkBackground)
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TutorAvatarBackground)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = TutorLinkGreenText,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = "TutorLink",
                style = MaterialTheme.typography.titleLarge,
                color = TutorLinkGreenText
            )
        }

        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TutorLinkInputBackground)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = TutorLinkGreenActive,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}