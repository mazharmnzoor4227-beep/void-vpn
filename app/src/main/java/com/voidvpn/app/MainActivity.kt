package com.voidvpn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

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
private val Bg = Color(0xFF050706)
private val Panel = Color(0xFF0A0F0C)
private val Panel2 = Color(0xFF0E1511)
private val Line = Color(0xFF203027)
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
                "United Kingdom",
                "London",
                "UK",
                "🇬🇧",
                43,
                "FAST"
            ),

            VpnServer(
                "United States",
                "New York",
                "US",
                "🇺🇸",
                66,
                "STREAM"
            ),

            VpnServer(
                "Germany",
                "Frankfurt",
                "DE",
                "🇩🇪",
                52,
                "LOW PING"
            ),

            VpnServer(
                "Netherlands",
                "Amsterdam",
                "NL",
                "🇳🇱",
                48,
                "PRIVACY"
            ),

            VpnServer(
                "Singapore",
                "Singapore",
                "SG",
                "🇸🇬",
                89,
                "ASIA"
            )
        )
    }

    var selected by remember {
        mutableStateOf(servers.first())
    }

    var state by remember {
        mutableStateOf(ConnectionState.DISCONNECTED)
    }

    var showServers by remember {
        mutableStateOf(false)
    }

    var elapsed by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(state) {

        if (state == ConnectionState.CONNECTING) {

            delay(2200)

            state =
                ConnectionState.CONNECTED

            elapsed = 0
        }
    }

    LaunchedEffect(state) {

        while (
            state ==
            ConnectionState.CONNECTED
        ) {

            delay(1000)

            elapsed++
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

            ServerSheet(

                servers = servers,

                selected = selected,

                onSelect = {

                    selected = it

                    showServers = false

                    state =
                        ConnectionState.DISCONNECTED
                }
            )
        }
    }

    Surface(
        modifier =
            Modifier.fillMaxSize(),

        color = Bg
    ) {

        Box {

            CyberBackground()

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(
                        horizontal = 18.dp
                    )
            ) {

                TopBar()

                Spacer(
                    Modifier.height(12.dp)
                )

                StatusStrip(state)

                Spacer(
                    Modifier.height(14.dp)
                )

                ServerCard(

                    server = selected,

                    onClick = {
                        showServers = true
                    }
                )

                Spacer(
                    Modifier.height(18.dp)
                )

                Box(

                    modifier =
                        Modifier.weight(1f),

                    contentAlignment =
                        Alignment.Center
                ) {

                    ConnectCore(

                        state = state,

                        onClick = {

                            state =
                                when (state) {

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
                    state,
                    selected,
                    elapsed
                )

                Spacer(
                    Modifier.height(16.dp)
                )

                SecurityFooter(state)

                Spacer(
                    Modifier.height(14.dp)
                )
            }
        }
    }
}

@Composable
fun CyberBackground() {

    Canvas(
        Modifier.fillMaxSize()
    ) {

        val step =
            38.dp.toPx()

        var x = 0f

        while (x < size.width) {

            drawLine(

                Color(
                    0x0A72FF72
                ),

                Offset(x, 0f),

                Offset(
                    x,
                    size.height
                ),

                1f
            )

            x += step
        }

        var y = 0f

        while (y < size.height) {

            drawLine(

                Color(
                    0x0872FF72
                ),

                Offset(
                    
