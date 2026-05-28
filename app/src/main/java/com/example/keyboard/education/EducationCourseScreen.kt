package com.example.keyboard.education

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Education1(onLearningStateChanged: (Boolean) -> Unit,user: FirebaseUser?) {

    var selectedCourse by rememberSaveable { mutableStateOf<Int?>(null) }

    var currentStep by rememberSaveable { mutableStateOf(0) }

        AnimatedContent(
            targetState = selectedCourse,
            transitionSpec = {
                val isEntering = targetState != null
                val fadeSpec = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy)

                if (isEntering) {

                    slideInVertically(
                        spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                    ) { it } +
                            fadeIn(fadeSpec) with
                            slideOutVertically(spring()) { -it / 2 } + fadeOut(fadeSpec)
                } else {

                    slideInVertically(
                        spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                    ) { -it } +
                            fadeIn(fadeSpec) with
                            slideOutVertically(spring()) { it / 2 } + fadeOut(fadeSpec)
                }.using(SizeTransform(clip = false))
            },
            label = "ModeTransition"
        ) { courseId ->
            if (courseId == null) {
                MainMenuScreen(
                    onCourse1Click = {
                        selectedCourse = 1; currentStep = 0; onLearningStateChanged(
                        true
                    )
                    },
                    onCourse2Click = {
                        selectedCourse = 2; currentStep = 0; onLearningStateChanged(
                        true
                    )
                    },
                    onCourse3Click = {
                        selectedCourse = 3; currentStep = 0; onLearningStateChanged(
                        true
                    )
                    },
                    user=user
                    )
            } else {
                val t: String
                if (selectedCourse == 1) t = "course1" else if (selectedCourse == 2) t =
                    "course2" else t = ""
                EducationFlow(
                    courseId = courseId,
                    currentStep = currentStep,
                    onBack = {
                        if (currentStep > 0) currentStep--
                        else {
                            selectedCourse = null; currentStep = 0; onLearningStateChanged(false)
                        }
                    },
                    onNext = {
                        val totalSteps = getTotalSteps(courseId)
                        if (currentStep < totalSteps - 1) currentStep++
                        else {
                            selectedCourse = null; currentStep = 0; onLearningStateChanged(false)
                        }
                    },
                    t
                )
            }
        }
}