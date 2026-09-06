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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
    val flag: String,
    val country: String,
    val city: String,
    val code: String,
    val ping: Int
)

enum class VpnState {
    OFF,
    CONNECTING,
    ON
}

private val Bg = Color(0xFF050706)
private val Panel = Color(0xFF0B100D)
private val Line = Color(0xFF203027)
private val Neon = Color(0xFF72FF72)
private val Muted = Color(0xFF829287)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Neon,
                    background = Bg,
                    surface = Panel
                )
            ) {
                VoidVpnScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoidVpnScreen() {

    val servers = remember {
        listOf(
            VpnServer("🇬🇧", "United Kingdom", "London", "UK", 43),
            VpnServer("🇺🇸", "United States", "New York", "US", 66),
            VpnServer("🇩🇪", "Germany", "Frankfurt", "DE", 52),
            VpnServer("🇳🇱", "Netherlands", "Amsterdam", "NL", 48),
            VpnServer("🇸🇬", "Singapore", "Singapore", "SG", 89)
        )
    }

    var selected by remember {
        mutableStateOf(servers[0])
    }

    var state by remember {
        mutableStateOf(VpnState.OFF)
    }

    var serverMenu by remember {
        mutableStateOf(false)
    }

    var seconds by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(state) {
        if (state == VpnState.CONNECTING) {
            delay(1800)
            state = VpnState.ON
            seconds = 0
        }
    }

    LaunchedEffect(state) {
        while (state == VpnState.ON) {
            delay(1000)
            seconds++
        }
    }

    if (serverMenu) {
        ModalBottomSheet(
            onDismissRequest = {
                serverMenu = false
            },
            containerColor = Panel
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {

                Text(
                    text = "SELECT EXIT NODE",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                )

                Spacer(
                    modifier = Modifier.height(15.dp)
                )

                servers.forEach { server ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .background(
                                color =
                                    if (server == selected)
                                        Color(0xFF102117)
                                    else
                                        Color(0xFF09100B),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .border(
                                width = 1.dp,
                                color =
                                    if (server == selected)
                                        Neon
                                    else
                                        Line,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .clickable {
                                selected = server
                                state = VpnState.OFF
                                serverMenu = false
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = server.flag,
                            fontSize = 25.sp
                        )

                        Spacer(
                            modifier = Modifier.size(12.dp)
                        )

                        Column {

                            Text(
                                text = server.country,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${server.city} • ${server.ping} ms",
                                color = Muted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(25.dp)
                )
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Bg
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "VOID VPN",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 23.sp,
                letterSpacing = 4.sp
            )

            Text(
                text = "PRIVATE NETWORK TERMINAL",
                color = Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            ServerCard(
                server = selected,
                onClick = {
                    serverMenu = true
                }
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            ConnectCore(
                state = state,
                onClick = {
                    state =
                        when (state) {
                            VpnState.OFF -> VpnState.CONNECTING
                            VpnState.CONNECTING -> VpnState.OFF
                            VpnState.ON -> VpnState.OFF
                        }
                }
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text =
                    when (state) {
                        VpnState.OFF ->
                            "SYSTEM READY • TUNNEL OFFLINE"

                        VpnState.CONNECTING ->
                            "NEGOTIATING SECURE TUNNEL..."

                        VpnState.ON ->
                            "SECURE TUNNEL ACTIVE"
                    },
                color =
                    if (state == VpnState.ON)
                        Neon
                    else
                        Muted,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Stats(
                server = selected,
                seconds = seconds,
                connected = state == VpnState.ON
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text =
                    if (state == VpnState.ON)
                        "ENCRYPTED ROUTE ACTIVE"
                    else
                        "READY FOR SECURE ROUTE",
                color =
                    if (state == VpnState.ON)
                        Neon
                    else
                        Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )

            Text(
                text = "REAL VPN ENGINE WILL BE ADDED NEXT",
                color = Muted,
                fontSize = 8.sp
            )
        }
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
                color = Panel,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = Line,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = server.flag,
            fontSize = 30.sp
        )

        Spacer(
            modifier = Modifier.size(14.dp)
        )

        Column {

            Text(
                text = "SELECTED SERVER",
                color = Neon,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp
            )

            Text(
                text = "${server.country} • ${server.city}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = "${server.ping} ms • ${server.code}-01",
                color = Muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ConnectCore(
    state: VpnState,
    onClick: () -> Unit
) {

    val transition =
        rememberInfiniteTransition(
            label = "vpn"
        )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis =
                    if (state == VpnState.CONNECTING)
                        700
                    else
                        4000,
                easing = LinearEasing
            )
        ),
        label = "rotation"
    )

    val ring =
        when (state) {
            VpnState.OFF -> Color(0xFF35513F)
            VpnState.CONNECTING -> Color(0xFFB6FFB6)
            VpnState.ON -> Neon
        }

    Box(
        modifier = Modifier.size(270.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(235.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
                .border(
                    width = 2.dp,
                    color = ring.copy(alpha = 0.35f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(165.dp)
                .background(
                    color = Color(0xFF07100A),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = ring,
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

                Text(
                    text = "⏻",
                    color = ring,
                    fontSize = 42.sp
                )

                Text(
                    text =
                        when (state) {
                            VpnState.OFF -> "CONNECT"
                            VpnState.CONNECTING -> "CONNECTING"
                            VpnState.ON -> "PROTECTED"
                        },
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp
                )

                Text(
                    text =
                        if (state == VpnState.ON)
                            "TAP TO DISCONNECT"
                        else
                            "TAP TO START",
                    color = Muted,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun Stats(
    server: VpnServer,
    seconds: Int,
    connected: Boolean
) {

    val time =
        String.format(
            "%02d:%02d:%02d",
            seconds / 3600,
            (seconds % 3600) / 60,
            seconds % 60
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
                color = Line,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(vertical = 15.dp),
        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        StatItem(
            "PING",
            "${server.ping} ms"
        )

        StatItem(
            "SESSION",
            if (connected)
                time
            else
                "--:--:--"
        )

        StatItem(
            "NODE",
            "${server.code}-01"
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = Muted,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
