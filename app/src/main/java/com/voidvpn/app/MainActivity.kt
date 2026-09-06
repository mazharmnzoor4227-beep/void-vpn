package com.voidvpn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class VpnServer(
    val country: String,
    val city: String,
    val code: String,
    val flag: String,
    val ping: Int,
    val tag: String
)

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

private val Neon = Color(0xFF72FF72)
private val NeonSoft = Color(0xFFB6FFB6)
private val Background = Color(0xFF050706)
private val Panel = Color(0xFF0A0F0C)
private val Panel2 = Color(0xFF0E1511)
private val Border = Color(0xFF203027)
private val Muted = Color(0xFF829287)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Neon,
                    background = Background,
                    surface = Panel
                )
            ) {

                VoidVpnApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoidVpnApp() {

    val servers = remember {
        listOf(

            VpnServer(
                country = "United Kingdom",
                city = "London",
                code = "UK",
                flag = "🇬🇧",
                ping = 43,
                tag = "FAST"
            ),

            VpnServer(
                country = "United States",
                city = "New York",
                code = "US",
                flag = "🇺🇸",
                ping = 66,
                tag = "STREAM"
            ),

            VpnServer(
                country = "Germany",
                city = "Frankfurt",
                code = "DE",
                flag = "🇩🇪",
                ping = 52,
                tag = "LOW PING"
            ),

            VpnServer(
                country = "Netherlands",
                city = "Amsterdam",
                code = "NL",
                flag = "🇳🇱",
                ping = 48,
                tag = "PRIVACY"
            ),

            VpnServer(
                country = "Singapore",
                city = "Singapore",
                code = "SG",
                flag = "🇸🇬",
                ping = 89,
                tag = "ASIA"
            )
        )
    }

    var selectedServer by remember {
        mutableStateOf(servers.first())
    }

    var connectionState by remember {
        mutableStateOf(ConnectionState.DISCONNECTED)
    }

    var showServers by remember {
        mutableStateOf(false)
    }

    var elapsedSeconds by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(connectionState) {

        if (connectionState == ConnectionState.CONNECTING) {

            delay(2200)

            connectionState = ConnectionState.CONNECTED

            elapsedSeconds = 0
        }
    }

    LaunchedEffect(connectionState) {

        while (connectionState == ConnectionState.CONNECTED) {

            delay(1000)

            elapsedSeconds++
        }
    }

    if (showServers) {

        ModalBottomSheet(
            onDismissRequest = {
                showServers = false
            },
            containerColor = Panel,
            contentColor = Color.White
        ) {

            ServerSelector(
                servers = servers,
                selected = selectedServer,
                onSelect = { server ->

                    selectedServer = server

                    connectionState =
                        ConnectionState.DISCONNECTED

                    showServers = false
                }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 18.dp)
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TopHeader()

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            StatusBar(
                state = connectionState
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ServerCard(
                server = selectedServer,
                onClick = {
                    showServers = true
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                ConnectButton(
                    state = connectionState,
                    onClick = {

                        connectionState =
                            when (connectionState) {

                                ConnectionState.DISCONNECTED ->
                                    ConnectionState.CONNECTING

                                ConnectionState.CONNECTING ->
                                    ConnectionState.DISCONNECTED

                                ConnectionState.CONNECTED ->
                                    ConnectionState.DISCONNECTED
                            }
                    }
                )
            }

            ConnectionStats(
                state = connectionState,
                server = selectedServer,
                elapsedSeconds = elapsedSeconds
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            SecurityStatus(
                state = connectionState
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
fun TopHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Panel,
                    shape = RoundedCornerShape(14.dp)
                )
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Menu",
                tint = Neon
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "VOID VPN",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 3.sp
            )

            Text(
                text = "PRIVATE NETWORK TERMINAL",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Panel,
                    shape = RoundedCornerShape(14.dp)
                )
                .border(
                    width = 1.dp,
                    color = Border,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Shield",
                tint = Neon
            )
        }
    }
}

@Composable
fun StatusBar(
    state: ConnectionState
) {

    val text =
        when (state) {

            ConnectionState.DISCONNECTED ->
                "SYSTEM READY • TUNNEL OFFLINE"

            ConnectionState.CONNECTING ->
                "NEGOTIATING SECURE TUNNEL..."

            ConnectionState.CONNECTED ->
                "SECURE TUNNEL ACTIVE"
        }

    val color =
        if (state == ConnectionState.CONNECTED) {
            Neon
        } else {
            Muted
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Panel,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Spacer(
            modifier = Modifier.size(9.dp)
        )

        Text(
            text = text,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ServerCard(
    server: VpnServer,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Panel2,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    color = Color(0xFF111B14),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = server.flag,
                fontSize = 26.sp
            )
        }

        Spacer(
            modifier = Modifier.size(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "SELECTED SERVER",
                color = Neon,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 1.sp
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "${server.country} • ${server.city}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = "${server.ping} ms • ${server.tag}",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "Select server",
            tint = Neon
        )
    }
}

@Composable
fun ConnectButton(
    state: ConnectionState,
    onClick: () -> Unit
) {

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "connectRotation"
        )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis =
                    if (state == ConnectionState.CONNECTING) {
                        900
                    } else {
                        4500
                    },
                easing = LinearEasing
            )
        ),
        label = "rotation"
    )

    val activeColor =
        when (state) {

            ConnectionState.DISCONNECTED ->
                Color(0xFF36513F)

            ConnectionState.CONNECTING ->
                NeonSoft

            ConnectionState.CONNECTED ->
                Neon
        }

    Box(
        modifier = Modifier
            .size(280.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(245.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
                .border(
                    width = 2.dp,
                    color = activeColor.copy(alpha = 0.35f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(210.dp)
                .border(
                    width = 1.dp,
                    color = activeColor.copy(alpha = 0.20f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(168.dp)
                .background(
                    color = Color(0xFF07100A),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = activeColor,
                    shape = CircleShape
                )
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector =
                        if (state == ConnectionState.CONNECTED) {
                            Icons.Outlined.Lock
                        } else {
                            Icons.Outlined.PowerSettingsNew
                        },
                    contentDescription = "Connect",
                    tint = activeColor,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        when (state) {

                            ConnectionState.DISCONNECTED ->
                                "CONNECT"

                            ConnectionState.CONNECTING ->
                                "CONNECTING"

                            ConnectionState.CONNECTED ->
                                "PROTECTED"
                        },
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text =
                        when (state) {

                            ConnectionState.DISCONNECTED ->
                                "TAP TO START"

                            ConnectionState.CONNECTING ->
                                "AUTH / HANDSHAKE"

                            ConnectionState.CONNECTED ->
                                "TAP TO DISCONNECT"
                        },
                    color = Muted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ConnectionStats(
    state: ConnectionState,
    server: VpnServer,
    elapsedSeconds: Int
) {

    val hours =
        elapsedSeconds / 3600

    val minutes =
        (elapsedSeconds % 3600) / 60

    val seconds =
        elapsedSeconds % 60

    val sessionTime =
        String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Panel,
                shape = RoundedCornerShape(18.dp)
            )
            .border(
                width = 1.dp,
                color = Border,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        StatItem(
            title = "PING",
            value = "${server.ping} ms"
        )

        StatItem(
            title = "SESSION",
            value =
                if (state == ConnectionState.CONNECTED) {
                    sessionTime
                } else {
                    "--:--:--"
                }
        )

        StatItem(
            title = "NODE",
            value = "${server.code}-01"
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = Muted,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp
        )

        Spacer(
            modifier =
