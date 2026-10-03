package com.example.signal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.signal.data.local.MemoryEntity
import com.example.signal.data.local.SignalDatabase
import com.example.signal.ui.theme.EchoBlack
import com.example.signal.ui.theme.EchoDarkGreen
import com.example.signal.ui.theme.EchoDarkGreenLight
import com.example.signal.ui.theme.EchoMint
import com.example.signal.ui.theme.EchoMintBright
import com.example.signal.ui.theme.EchoMintLight
import com.example.signal.ui.theme.EchoTextMuted
import com.example.signal.ui.theme.EchoTextPrimary
import com.example.signal.ui.theme.EchoTextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onLibraryClick: () -> Unit,
    onMemoryClick: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { SignalDatabase.getDatabase(context) }
    val memories by database.memoryDao().getAllMemories()
        .collectAsState(initial = emptyList())

    val placeCount = memories.count { it.targetLocation.isNotBlank() }
    val recentMemory = memories.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEF8F1))
    ) {
        // ── Organic ambient background circles (from screenshot) ──
        Box(
            modifier = Modifier
                .size(360.dp)
                .offset(x = 120.dp, y = (-80).dp)
                .clip(CircleShape)
                .background(Color(0xFFD6F5E3).copy(alpha = 0.55f))
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 160.dp, y = 360.dp)
                .clip(CircleShape)
                .background(Color(0xFFD8F6E5).copy(alpha = 0.5f))
        )

        // ── Scrollable content ──────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 96.dp)  // clearance for floating bottom bar
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Top Bar: Logo badge + Echo title + circular 3-dot menu ──
            EchoTopBar()

            Spacer(modifier = Modifier.height(26.dp))

            // ── Greeting text ──
            GreetingSection()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Big Dark Green Capture Hero Card ──
            CaptureCard(onCaptureClick = onCaptureClick)

            Spacer(modifier = Modifier.height(28.dp))

            // ── YOUR ECHO Stats (Memories & Places) ──
            YourEchoSection(
                memoryCount = memories.size,
                placeCount = placeCount
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Recently Remembered Card ──
            if (recentMemory != null) {
                RecentlyRememberedSection(
                    memory = recentMemory,
                    onClick = { onMemoryClick(recentMemory.id) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Floating Rounded Bottom Navigation Pill Bar ──
        EchoFloatingBottomNav(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            onHomeClick = {},
            onCaptureClick = onCaptureClick,
            onLibraryClick = onLibraryClick
        )
    }
}

// ─────────────────────────────────────────────────────────────
// TOP BAR
// ─────────────────────────────────────────────────────────────

@Composable
private fun EchoTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // E logo badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(EchoBlack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "E",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "ECHO",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EchoTextPrimary,
                letterSpacing = 2.sp,
                fontSize = 17.sp
            )
        }

        // Circular white 3-dot menu button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Menu",
                tint = EchoTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// GREETING
// ─────────────────────────────────────────────────────────────

@Composable
private fun GreetingSection() {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "GOOD MORNING"
            hour < 17 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }
    }

    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF75857B),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.8.sp,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "What did you\nsave for later?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111814),
            lineHeight = 42.sp,
            fontSize = 36.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────
// CAPTURE CARD
// ─────────────────────────────────────────────────────────────

@Composable
private fun CaptureCard(onCaptureClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF0F3B28))
            .clickable { onCaptureClick() }
            .padding(24.dp)
    ) {
        // Decorative translucent circles on top right
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(x = 160.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color(0xFF1D5239).copy(alpha = 0.55f))
        )
        Box(
            modifier = Modifier
                .size(110.dp)
                .offset(x = 200.dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color(0xFF266346).copy(alpha = 0.35f))
        )

        Column {
            // Capture badge row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA6F7CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF0F3B28),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "CAPTURE",
                    color = Color(0xFFA6F7CD),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 2.sp,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Save it now.\nEcho remembers later.",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Links • places • ideas • everything",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp
                )

                // Plus button inside capture card
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFA6F7CD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Capture",
                        tint = Color(0xFF0F3B28),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// YOUR ECHO (STAT CARDS)
// ─────────────────────────────────────────────────────────────

@Composable
private fun YourEchoSection(
    memoryCount: Int,
    placeCount: Int
) {
    Column {
        Text(
            text = "YOUR ECHO",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF75857B),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.8.sp,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                count = memoryCount.toString(),
                label = "memories"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                count = placeCount.toString(),
                label = "places"
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    count: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB8F5CE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 22.dp,
                vertical = 20.dp
            )
        ) {
            Text(
                text = count,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111814)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF4A5A50),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RECENTLY REMEMBERED
// ─────────────────────────────────────────────────────────────

@Composable
private fun RecentlyRememberedSection(
    memory: MemoryEntity,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = "Recently remembered",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111814),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    // Soft green badge with sparkle icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFB8F5CE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF0F3B28),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = memory.title.ifBlank { "Untitled Memory" },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111814),
                            lineHeight = 22.sp
                        )

                        val subtitleText = when {
                            memory.summary.isNotBlank() -> memory.summary
                            memory.targetLocation.isNotBlank() -> memory.targetLocation
                            else -> memory.content
                        }

                        if (subtitleText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitleText,
                                fontSize = 13.sp,
                                color = Color(0xFF75857B),
                                maxLines = 2,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Green pill chip at bottom left
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFB8F5CE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = memory.sourceType.ifBlank { "PLACE" }.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F3B28),
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FLOATING BOTTOM NAVIGATION (Capsule design matching screenshot)
// ─────────────────────────────────────────────────────────────

@Composable
private fun EchoFloatingBottomNav(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onCaptureClick: () -> Unit,
    onLibraryClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(Color(0xFF0F1713))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onHomeClick() }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Home",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Prominent Circular Mint + Button in center
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA6F7CD))
                    .clickable { onCaptureClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Capture",
                    tint = Color(0xFF0F3B28),
                    modifier = Modifier.size(30.dp)
                )
            }

            // Library tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onLibraryClick() }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Slanted rectangular cards icon symbol
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.55f))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Library",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}