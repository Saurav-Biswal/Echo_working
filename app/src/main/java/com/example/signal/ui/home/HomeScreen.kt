package com.example.signal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.signal.ui.theme.InstagramPink
import com.example.signal.ui.theme.SignalBlue
import com.example.signal.ui.theme.SignalPurple
import com.example.signal.ui.theme.SignalSurface
import com.example.signal.ui.theme.SignalTextMuted
import com.example.signal.ui.theme.SignalTextPrimary
import com.example.signal.ui.theme.SignalTextSecondary

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onLibraryClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        floatingActionButton = {
            FloatingActionButton(
                onClick = onCaptureClick,
                modifier = Modifier.navigationBarsPadding(),
                shape = RoundedCornerShape(20.dp),
                containerColor = SignalTextPrimary,
                contentColor = SignalSurface
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Capture"
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            HomeHeader()

            Spacer(modifier = Modifier.height(22.dp))

            AiHeroCard()

            Spacer(modifier = Modifier.height(18.dp))

            SearchCard(
                onClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuickActions(
                onLibraryClick = onLibraryClick
            )

            Spacer(modifier = Modifier.height(26.dp))

            RecentSection()
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = "GOOD TO SEE YOU",
                style = MaterialTheme.typography.labelMedium,
                color = SignalTextMuted
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Signal",
                style = MaterialTheme.typography.headlineMedium,
                color = SignalTextPrimary
            )
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(SignalSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifications",
                tint = SignalTextPrimary
            )
        }
    }
}

@Composable
private fun AiHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SignalBlue,
                        SignalPurple
                    )
                )
            )
            .padding(24.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "SIGNAL INTELLIGENCE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Remember\nwhat matters.",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Save anything. Signal understands it, connects it and helps you find it later.",
                color = Color.White.copy(alpha = 0.84f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        )
                ) {
                    Text(
                        text = "AI MEMORY",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD7F64A))
                )
            }
        }
    }
}

@Composable
private fun SearchCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SignalSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        SignalBlue.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = SignalBlue
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Search your memory",
                    style = MaterialTheme.typography.titleMedium,
                    color = SignalTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Try “that cafe Reel I saved”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SignalTextMuted
                )
            }
        }
    }
}

@Composable
private fun QuickActions(
    onLibraryClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        QuickAction(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.BookmarkBorder,
            title = "Library",
            subtitle = "All memories",
            onClick = onLibraryClick
        )

        QuickAction(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocationOn,
            title = "Nearby",
            subtitle = "Memories around you",
            onClick = {}
        )
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SignalSurface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        SignalPurple.copy(alpha = 0.10f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SignalPurple
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = SignalTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SignalTextSecondary
            )
        }
    }
}

@Composable
private fun RecentSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Recently captured",
            style = MaterialTheme.typography.titleLarge,
            color = SignalTextPrimary
        )

        Text(
            text = "See all",
            style = MaterialTheme.typography.labelMedium,
            color = SignalBlue
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            MemoryCard(
                source = "Instagram",
                title = "Cold coffee recipe",
                subtitle = "Saved today",
                accent = InstagramPink
            )
        }

        item {
            MemoryCard(
                source = "YouTube",
                title = "3 focus techniques",
                subtitle = "Saved yesterday",
                accent = Color(0xFFFF0033)
            )
        }
    }
}

@Composable
private fun MemoryCard(
    source: String,
    title: String,
    subtitle: String,
    accent: Color
) {
    Card(
        modifier = Modifier.width(250.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SignalSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = source.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = SignalTextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = SignalTextPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SignalTextSecondary
            )
        }
    }
}