package com.droidcon.credentialmanagersample.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droidcon.credentialmanagersample.R
import com.droidcon.credentialmanagersample.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: User,
    onSignOut: () -> Unit,
    isLoading: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (isLoading) 0.6f else 1f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Welcome, ${user.name}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            // Main Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Profile Picture
                            Card(
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(bottom = 16.dp),
                                shape = CircleShape,
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color(0xFF667eea)
                                    )
                                }
                            }
                            
                            Text(
                                text = user.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = user.email,
                                fontSize = 16.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                
                // Stats Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Credits",
                            value = "1,250",
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Level",
                            value = "Gold",
                            icon = Icons.Default.EmojiEvents,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Features List
                item {
                    Text(
                        text = "Features",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                val features = listOf(
                    FeatureItem(
                        title = "Secure Storage",
                        description = "Your credentials are safely stored using Android's Credential Manager",
                        icon = Icons.Default.Security
                    ),
                    FeatureItem(
                        title = "Auto-Fill",
                        description = "Seamlessly fill forms with saved credentials",
                        icon = Icons.Default.AutoFixHigh
                    ),
                    FeatureItem(
                        title = "Biometric Auth",
                        description = "Use fingerprint or face recognition for quick access",
                        icon = Icons.Default.Fingerprint
                    ),
                    FeatureItem(
                        title = "Sync Across Devices",
                        description = "Access your credentials on all your devices",
                        icon = Icons.Default.Sync
                    )
                )
                
                items(features) { feature ->
                    FeatureCard(feature = feature)
                }
            }
        }
        
        // Loading Overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF667eea)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Signing out...",
                            fontSize = 16.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 8.dp),
                tint = Color(0xFF667eea)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun FeatureCard(feature: FeatureItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp),
                tint = Color(0xFF667eea)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = feature.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = feature.description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

data class FeatureItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)
