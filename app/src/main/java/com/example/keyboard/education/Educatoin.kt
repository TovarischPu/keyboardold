package com.example.keyboard.education

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.keyboard.ProgressSync
import com.example.keyboard.R
import com.example.keyboard.design.BackdropDemoScaffold
import com.example.keyboard.design.LiquidButton
import com.example.keyboard.design.TopFeedbackBanner
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.RoundedRectangle

fun getTotalSteps(courseId: Int): Int = when (courseId) {
    1 -> 4 // 1:Теория -> 2:Теория/Тест -> 3:Финиш
    2 -> 4
    3 -> 4
    4 -> 4
    else -> 4
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EducationFlow(
    courseId: Int,
    currentStep: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    course: String
) {
    val totalSteps = getTotalSteps(courseId)
    val progress = (currentStep + 1).toFloat() / totalSteps
    val isLastStep = currentStep == totalSteps - 1
    var isTest by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    val uiBackdrop = rememberLayerBackdrop()
    val contentBackdrop = rememberLayerBackdrop()
    val prefs = LocalContext.current.getSharedPreferences("progress", Context.MODE_PRIVATE)
    AnimatedContent(targetState = currentStep,
        transitionSpec = {
            if (targetState > initialState) {
                // Вперёд: слайд справа + плавное появление
                slideInHorizontally(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) { it } +
                        fadeIn(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) with
                        slideOutHorizontally(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) { -it } +
                        fadeOut(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy))
            } else {
                slideInHorizontally(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) { -it } +
                        fadeIn(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) with
                        slideOutHorizontally(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy)) { it } +
                        fadeOut(spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy))
            }.using(SizeTransform(clip = false))
        },
        label = "StepTransition") { step ->
        BackdropDemoScaffold { backdrop ->

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                    .layerBackdrop(contentBackdrop)
                    .graphicsLayer {
                        alpha = 0.99f
                    }
                    .drawWithContent {
                        drawContent()
                        drawRect(Color.Black.copy(alpha = 0.005f))
                    }) {
                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .drawBackdrop(
                                backdrop = backdrop,
                                shape = { RoundedRectangle(32f.dp) },
                                effects = {
                                    vibrancy()
                                    lens(16f.dp.toPx(), 32f.dp.toPx())
                                }
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )

                    // Контент шага
                    when (courseId) {
                        1 -> Course1Step(
                            step,
                            onNext,
                            onBack,
                            backdrop,
                            test = { isTest = it },
                            text1 = { text = it })

                        2 -> Course2Step(
                            step,
                            onNext, onBack,
                            backdrop,
                            test = { isTest = it },
                            text1 = { text = it })

                        3 -> Course3Step(step, onNext, onBack)
                    }
                    if (isTest) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LiquidButton(
                                onClick = {
                                    if (text == "амбицiя") isCorrect = true else isCorrect = false
                                },
                                backdrop = backdrop,
                                modifier = Modifier.weight(1f)
                            ) { Text("Провѣрить") }
                        }


                    }

                    // Кнопки навигации
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LiquidButton(onClick = onBack, backdrop, modifier = Modifier.weight(1f)) {
                            Text(if (step > 0) "Назад" else "Меню")
                        }
                        LiquidButton(
                            onClick = {
                                if (isLastStep) {
                                    prefs.edit().putBoolean(course, true).apply()
                                    ProgressSync.saveProgress(course, true)
                                    onNext?.invoke()
                                } else onNext?.invoke()
                            },
                            backdrop,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isLastStep) "Завершить" else "Далее")
                        }
                    }

                }
                TopFeedbackBanner(
                    isCorrect = isCorrect,
                    onDismiss = { isCorrect = null },
                    backdrop = contentBackdrop,
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    "Неверно!"
                )
            }
        }
    }
}


@Composable
 fun StepTemplate(
    title: String,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isFinish: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poiret)),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
}
