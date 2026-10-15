package com.example.app_tutorias.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.app_tutorias.data.Tutor
import com.example.app_tutorias.data.TutoringSession
import com.example.app_tutorias.data.TutoringStatus
import com.example.app_tutorias.ui.theme.TutorAvatarBackground
import com.example.app_tutorias.ui.theme.TutorFavoriteBackground
import com.example.app_tutorias.ui.theme.TutorLinkGreenActive
import com.example.app_tutorias.ui.theme.TutorLinkGreenText
import com.example.app_tutorias.ui.theme.TutorLinkInputBackground
import com.example.app_tutorias.ui.theme.TutorLinkTextSecondary
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TutorAvatar(
    initials: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(TutorAvatarBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = TutorLinkGreenText
        )
    }
}

@Composable
fun TutorCard(
    tutor: Tutor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            TutorAvatar(initials = tutor.initials, modifier = Modifier.size(54.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = tutor.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tutor.specialty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TutorLinkTextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = TutorLinkGreenActive,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = tutor.rating.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = TutorLinkGreenActive
                        )
                    }
                    Text(
                        text = "Q${tutor.pricePerHour}/hora",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = tutor.availability,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TutorLinkGreenText
                )
            }
        }
    }
}

@Composable
fun SessionCard(
    session: TutoringSession,
    modifier: Modifier = Modifier
) {
    val locale = Locale.forLanguageTag("es-GT")
    val dateFormatter = DateTimeFormatter.ofPattern("EEE d 'de' MMM", locale)
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", locale)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(TutorFavoriteBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = TutorLinkGreenActive
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.subject,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    SessionStatusBadge(status = session.status)
                }
                Text(
                    text = session.tutorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TutorLinkTextSecondary
                )
                Text(
                    text = "${session.date.format(dateFormatter)} · ${session.time.format(timeFormatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TutorLinkGreenText
                )
            }
        }
    }
}

@Composable
private fun SessionStatusBadge(status: TutoringStatus) {
    val label = when (status) {
        TutoringStatus.UPCOMING -> "Próxima"
        TutoringStatus.COMPLETED -> "Completada"
        TutoringStatus.CANCELLED -> "Cancelada"
    }
    val background = when (status) {
        TutoringStatus.UPCOMING -> TutorFavoriteBackground
        TutoringStatus.COMPLETED -> TutorLinkInputBackground
        TutoringStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (status) {
        TutoringStatus.UPCOMING -> TutorLinkGreenActive
        TutoringStatus.COMPLETED -> TutorLinkTextSecondary
        TutoringStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    Surface(color = background, shape = CircleShape) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.School,
            contentDescription = null,
            tint = TutorLinkGreenActive,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = TutorLinkTextSecondary
        )
        Spacer(modifier = Modifier.size(4.dp))
    }
}
