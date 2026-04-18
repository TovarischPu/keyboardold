package com.example.keyboard

import android.transition.Transition
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedTypingTextField() {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().offset(y = (28).dp)
                .zIndex(-1f),
            textStyle = LocalTextStyle.current.copy(color = Color.Transparent, fontSize = 22.sp),
            cursorBrush = SolidColor(Color.Black),
        )
        

        AnimatedText(text = text,-28)
    }
}

@Composable
fun AnimatedText(text: String,x: Int) {
    val textMeasurer = rememberTextMeasurer()

    val cursorOffsetPx = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        val textStyle = TextStyle(fontSize = 23.27.sp)

        val layoutResult = textMeasurer.measure(
            text = text,
            style = textStyle,

        )

        val targetPx = if (text.isNotEmpty()) layoutResult.getLineRight(0) else 0f

        scope.launch {
            cursorOffsetPx.animateTo(
                targetPx,
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow,
                    dampingRatio = Spring.DampingRatioNoBouncy
                )
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth().offset(y=x.dp)) {
        BlinkingCursor(
            modifier = Modifier.graphicsLayer {
                translationX = cursorOffsetPx.value },
        )
        text.forEachIndexed { index, char ->
            key(index) {
                AnimatedLetter(char)
            }
        }

    }


}

@Composable
fun AnimatedLetter(char: Char) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000, easing = LinearOutSlowInEasing)) +
                slideInVertically(
                    initialOffsetY = { -it / 3 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
//                scaleIn(
//                    initialScale = 1.8f,
//                    animationSpec = tween(500, easing = FastOutSlowInEasing)
//                )
    ) {
        Text(
            text = char.toString(),
            fontSize = 22.sp
        )
    }
}

@Composable
fun BlinkingCursor(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    width: Dp = 2.dp,
    height: Dp = 22.dp

) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(  // 🔹 infiniteRepeatable передаётся КАК параметр
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
//    val scale by animateFloatAsState(
//        targetValue = if (onType) 1.3f else 1f,
//        animationSpec = spring(stiffness = Spring.StiffnessHigh)
//    )
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(color.copy(alpha = alpha))
    )
}