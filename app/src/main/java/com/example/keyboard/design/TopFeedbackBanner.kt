package com.example.keyboard.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.delay

@Composable
fun TopFeedbackBanner(isCorrect: Boolean?, onDismiss: () -> Unit,backdrop: LayerBackdrop,modifier: Modifier = Modifier,text: String="") {
    AnimatedVisibility(
        visible = isCorrect != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        if (isCorrect != null) {
            LaunchedEffect(isCorrect) {
                delay(1500)
                onDismiss()
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedRectangle(32f.dp) },
                        effects = {
                            vibrancy()
                            lens(20f.dp.toPx(),
                                44f.dp.toPx(),
                                depthEffect = true,
                                chromaticAberration = true)

                        },
                        shadow = {Shadow(
                            radius = 1f.dp,
                            color = Color.Black.copy(alpha = 0.05f)
                        )}
                    )
                    .background(
                        color = if (isCorrect) Color(0x802ECC71) else Color(0x80E74C3C),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCorrect) "✅ Верно" else "❌ "+text,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}