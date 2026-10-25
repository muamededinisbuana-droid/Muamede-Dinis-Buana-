package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BuanaAmberSecondary
import com.example.ui.theme.BuanaTealContainer
import com.example.ui.theme.BuanaTealPrimary
import kotlinx.coroutines.delay

enum class BreathePhase {
    INHALE, HOLD, EXHALE
}

@Composable
fun BreatheExerciseDialog(
    isXimakonde: Boolean,
    onDismiss: () -> Unit
) {
    var isRunning by remember { mutableStateOf(true) }
    var phase by remember { mutableStateOf(BreathePhase.INHALE) }
    var secondsRemaining by remember { mutableIntStateOf(4) }
    var cycleCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            // Phase 1: Inhale 4s
            phase = BreathePhase.INHALE
            for (sec in 4 downTo 1) {
                secondsRemaining = sec
                delay(1000L)
            }
            // Phase 2: Hold 4s
            phase = BreathePhase.HOLD
            for (sec in 4 downTo 1) {
                secondsRemaining = sec
                delay(1000L)
            }
            // Phase 3: Exhale 4s
            phase = BreathePhase.EXHALE
            for (sec in 4 downTo 1) {
                secondsRemaining = sec
                delay(1000L)
            }
            cycleCount++
        }
    }

    val targetScale = when (phase) {
        BreathePhase.INHALE -> 1.35f
        BreathePhase.HOLD -> 1.35f
        BreathePhase.EXHALE -> 0.85f
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
        label = "breathe_scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("breathe_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = BuanaTealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isXimakonde) "Kupumula Mpumu (4-4-4)" else "Respiração Calma (4-4-4)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_breathe_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Text(
                    text = if (isXimakonde)
                        "Vuta mpumu, chimika kidoko, fucha kankano. Kupumula mutima."
                    else
                        "Inspire, segure o ar e solte devagar para acalmar o coração e a mente.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Breathe Visualizer
                Box(
                    modifier = Modifier
                        .size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ripple
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                when (phase) {
                                    BreathePhase.INHALE -> BuanaTealContainer.copy(alpha = 0.5f)
                                    BreathePhase.HOLD -> BuanaAmberSecondary.copy(alpha = 0.3f)
                                    BreathePhase.EXHALE -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                    )

                    // Inner circle
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                when (phase) {
                                    BreathePhase.INHALE -> BuanaTealPrimary
                                    BreathePhase.HOLD -> BuanaAmberSecondary
                                    BreathePhase.EXHALE -> MaterialTheme.colorScheme.outline
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$secondsRemaining",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phase Instruction Text
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val phaseTitle = when (phase) {
                            BreathePhase.INHALE -> if (isXimakonde) "VUTA MPUMU (INSPIRE)" else "INSPIRE DEVAGAR"
                            BreathePhase.HOLD -> if (isXimakonde) "CHIMIKA MPUMU (SEGURE)" else "SEGURE O AR"
                            BreathePhase.EXHALE -> if (isXimakonde) "FUCHA MPUMU (EXPIRE)" else "SOLTE SUAVEMENTE"
                        }
                        Text(
                            text = phaseTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = if (isXimakonde) "Mzunguko wa $cycleCount" else "Ciclo de respiração $cycleCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play / Pause / Finish Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("toggle_breathe_running")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRunning) "Pausar" else "Continuar")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("done_breathe_button")
                    ) {
                        Text(if (isXimakonde) "Nindapumula" else "Sinto-me Melhor")
                    }
                }
            }
        }
    }
}
