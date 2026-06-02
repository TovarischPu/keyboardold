package com.example.keyboard.design

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class CharItem(
    val id: Int,
    val char: Char,
    var index: Int,
    var visible: Boolean,
    var cachedBounds: Rect = Rect.Zero
)

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

        var charItems by remember { mutableStateOf<List<CharItem>>(emptyList()) }
        var nextId by remember { mutableIntStateOf(0) }

        LaunchedEffect(text) {
            val currentItems = charItems.toMutableList()
            val newItems = mutableListOf<CharItem>()

            text.forEachIndexed { index, char ->
                val existing = currentItems.find { it.char == char && it.visible && it.index == index }
                if (existing != null) {
                    existing.index = index
                    newItems.add(existing)
                } else {
                    newItems.add(CharItem(id = nextId++, char = char, index = index, visible = true))
                }
            }

            val removedItems = currentItems.filterNot { newItems.contains(it) }
                .map { it.copy(visible = false) }

            charItems = newItems + removedItems
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
            // Курсор
            BlinkingCursor(
                modifier = Modifier.offset(
                    x = with(density) { cursorPosition.value.x.toDp() },
                    y = with(density) { cursorPosition.value.y.toDp() }
                ),
                color = cursorColor,
                height = with(density) { fontSize.toDp() },
                m = m
            )


            charItems.forEach { item ->
                if (item.visible && item.index < layoutResult.layoutInput.text.length) {
                    item.cachedBounds = layoutResult.getBoundingBox(item.index)
                }

                key(item.id) {
                    AnimatedLetter(
                        char = item.char,
                        fontSize = fontSize,
                        visible = item.visible,
                        onExitFinished = {
                            charItems = charItems.filter { it.id != item.id }
                        },
                        modifier = Modifier.offset(
                            x = with(density) { item.cachedBounds.left.toDp() },
                            y = with(density) { item.cachedBounds.top.toDp() }
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLetter(
    char: Char,
    fontSize: TextUnit,
    visible: Boolean,
    onExitFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val distancePx = with(density) { 10.dp.toPx() }

    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(distancePx) }
    val offsetY = remember { Animatable(-distancePx) }
    val blurRadius = remember { Animatable(20f) }

    LaunchedEffect(visible) {
        if (visible) {
            coroutineScope {
                launch { alpha.animateTo(1f, tween(350, easing = FastOutSlowInEasing)) }
                launch { offsetX.animateTo(0f, tween(350, easing = FastOutSlowInEasing)) }
                launch { offsetY.animateTo(0f, tween(350, easing = FastOutSlowInEasing)) }
                launch { blurRadius.animateTo(0f, tween(350, easing = FastOutSlowInEasing)) }
            }
        } else {
            coroutineScope {
                launch { alpha.animateTo(0f, tween(350, easing = FastOutSlowInEasing)) }
                launch { offsetX.animateTo(-distancePx, tween(350, easing = FastOutSlowInEasing)) }
                launch { offsetY.animateTo(-distancePx, tween(350, easing = FastOutSlowInEasing)) }
                launch { blurRadius.animateTo(25f, tween(350, easing = FastOutSlowInEasing)) }
            }
            delay(350)
            onExitFinished()
        }
    }
    val blurDp = with(density) { blurRadius.value.toDp() }

    Text(
        text = char.toString(),
        fontSize = fontSize,
        lineHeight = fontSize * 1.2f,
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha.value
                this.translationX = offsetX.value
                this.translationY = offsetY.value
            }
            .blur(blurDp)
    )
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