package com.example.keyboard.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun AnimatedMultiLineText(
    text: String,
    cursorIndex: Int,
    onCursorIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    cursorColor: Color = Color.Black,
    lineHeightMultiplier: Float = 1.2f,
    y: Int = -25,
    m: Float = 1f
) {
    BoxWithConstraints(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.toPx().toInt() }

        val textStyle = TextStyle(
            fontSize = fontSize,
            lineHeight = fontSize * lineHeightMultiplier
        )

        val layoutResult = remember(text, maxWidthPx, textStyle) {
            textMeasurer.measure(
                text = text,
                style = textStyle,
                constraints = Constraints(maxWidth = maxWidthPx),
                softWrap = true
            )
        }

        val cursorPosition = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(layoutResult, cursorIndex) {
            val safeIndex = cursorIndex.coerceIn(0, text.length)
            val cursorRect = layoutResult.getCursorRect(safeIndex)
            scope.launch {
                cursorPosition.animateTo(
                    targetValue = Offset(cursorRect.left, cursorRect.top),
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioNoBouncy
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = y.dp)
                .pointerInput(layoutResult) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            event.changes.forEach { pointerChange ->
                                if (pointerChange.pressed) {
                                    val offset = pointerChange.position
                                    val clickedIndex = layoutResult.getOffsetForPosition(offset)
                                    onCursorIndexChange(clickedIndex)
                                    pointerChange.consume()
                                }
                            }
                        }
                    }
                }
        ) {
            BlinkingCursor(
                modifier = Modifier.offset(
                    x = with(density) { cursorPosition.value.x.toDp() },
                    y = with(density) { cursorPosition.value.y.toDp() }
                ),
                color = cursorColor,
                height = with(density) { fontSize.toDp() },
                m = m
            )

            text.forEachIndexed { index, char ->
                val bounds: Rect = layoutResult.getBoundingBox(index)
                key(index) {
                    Box(
                        modifier = Modifier.offset(
                            x = with(density) { bounds.left.toDp() },
                            y = with(density) { bounds.top.toDp() }
                        )
                    ) {
                        AnimatedLetter(char, fontSize)
                    }
                }
            }
        }
    }
}
@Composable
fun AnimatedLetter(
    char: Char,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000, easing = LinearOutSlowInEasing)) +
                slideInVertically(initialOffsetY = { -it / 3 }, animationSpec = tween(500, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(100, easing = FastOutSlowInEasing)) +
                slideOutVertically(targetOffsetY = { it / 3 }, animationSpec = tween(150, easing = FastOutSlowInEasing))
    ) {
        Text(
            text = char.toString(),
            fontSize = fontSize,
            lineHeight = fontSize * 1.2f,
            modifier = modifier
        )
    }
}

@Composable
fun BlinkingCursor(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    width: Dp = 2.dp,
    height: Dp = 22.dp,
    m: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = m,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(color.copy(alpha = alpha))
    )
}