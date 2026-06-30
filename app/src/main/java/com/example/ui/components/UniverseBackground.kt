package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.random.Random

// Data structure representing a star in the universe background
private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val animOffset: Float,
    val twinkleSpeed: Int
)

@Composable
fun UniverseBackground(modifier: Modifier = Modifier) {
    // Generate star coordinates deterministically using a fixed seed so they don't jump around on recompositions
    val stars = remember {
        val random = Random(42) // Fixed seed
        List(150) {
            val size = random.nextDouble(1.0, 3.5).toFloat()
            val colorType = random.nextInt(4)
            val color = when (colorType) {
                0 -> Color(0xFFE3F2FD) // Soft icy blue
                1 -> Color(0xFFFCE4EC) // Soft celestial pink
                2 -> Color(0xFFFFFDE7) // Soft starlight yellow
                else -> Color.White
            }
            Star(
                x = random.nextFloat(),
                y = random.nextFloat(),
                size = size,
                color = color,
                animOffset = random.nextFloat() * 2 * Math.PI.toFloat(),
                twinkleSpeed = random.nextInt(2000, 5000)
            )
        }
    }

    // Nebula animation: slowly drift the nebulae for a dynamic feeling
    val infiniteTransition = rememberInfiniteTransition(label = "UniverseDrift")
    val nebulaOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Nebula1"
    )

    val nebulaOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Nebula2"
    )

    // Twinkle progress
    val twinkleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (width == 0f || height == 0f) return@Canvas

        // 1. Draw Space Background (Deep black to celestial blue gradient)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF030308), // Void black
                    Color(0xFF070719), // Deep cosmic navy
                    Color(0xFF13092C), // Cosmic indigo
                    Color(0xFF050510)  // Dark horizon
                )
            )
        )

        // 2. Draw Soft Nebula Clouds (Dynamic radial gradients to simulate interstellar dust clouds)
        // Nebula 1: Vibrant Purple
        val n1X = width * 0.2f + (Math.sin(nebulaOffset1.toDouble()).toFloat() * 100f)
        val n1Y = height * 0.3f + (Math.cos(nebulaOffset1.toDouble()).toFloat() * 120f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF8A2BE2).copy(alpha = 0.22f), // Electric Purple
                    Color(0xFF4B0082).copy(alpha = 0.08f), // Deep Violet
                    Color.Transparent
                ),
                center = Offset(n1X, n1Y),
                radius = width * 0.8f
            ),
            center = Offset(n1X, n1Y),
            radius = width * 0.8f
        )

        // Nebula 2: Glowing Cyan / Indigo
        val n2X = width * 0.8f + (Math.cos(nebulaOffset2.toDouble()).toFloat() * 150f)
        val n2Y = height * 0.7f + (Math.sin(nebulaOffset2.toDouble()).toFloat() * 100f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00CED1).copy(alpha = 0.15f), // Cyan
                    Color(0xFF00008B).copy(alpha = 0.05f), // Dark Blue
                    Color.Transparent
                ),
                center = Offset(n2X, n2Y),
                radius = width * 0.7f
            ),
            center = Offset(n2X, n2Y),
            radius = width * 0.7f
        )

        // Nebula 3: Supernova Pink Accents
        val n3X = width * 0.5f + (Math.sin((nebulaOffset2 + nebulaOffset1).toDouble() / 2).toFloat() * 80f)
        val n3Y = height * 0.5f + (Math.cos((nebulaOffset2 + nebulaOffset1).toDouble() / 2).toFloat() * 80f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF1493).copy(alpha = 0.10f), // Neon pink
                    Color.Transparent
                ),
                center = Offset(n3X, n3Y),
                radius = width * 0.5f
            ),
            center = Offset(n3X, n3Y),
            radius = width * 0.5f
        )

        // 3. Draw Stars with animated glowing alphas (twinkling)
        stars.forEach { star ->
            val starX = star.x * width
            val starY = star.y * height

            // Calculate individualized twinkle effect based on time, speed, and phase offset
            val wave = Math.sin((twinkleTime * (4000f / star.twinkleSpeed) + star.animOffset).toDouble()).toFloat()
            // Map sine wave from [-1, 1] to [0.3, 1.0] for alpha
            val alpha = 0.35f + (0.65f * (wave + 1f) / 2f)

            // Draw glowing core for larger stars
            if (star.size > 2.5f) {
                drawCircle(
                    color = star.color.copy(alpha = alpha * 0.3f),
                    radius = star.size * 2f,
                    center = Offset(starX, starY)
                )
            }

            drawCircle(
                color = star.color.copy(alpha = alpha),
                radius = star.size,
                center = Offset(starX, starY)
            )
        }
    }
}
