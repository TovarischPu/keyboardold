package com.example.keyboard.education

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.google.firebase.auth.FirebaseUser
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun MainMenuScreen(
    user: FirebaseUser?,
    onCourse1Click: () -> Unit,
    onCourse2Click: () -> Unit,
    onCourse3Click: () -> Unit,
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)
    val contentBackdrop = rememberLayerBackdrop()
    var courseProgress by remember { mutableStateOf(mapOf<String, Boolean>()) }

    LaunchedEffect(user) {
        if (user != null) {
            ProgressSync.loadProgress { cloudMap ->

                if (cloudMap.isEmpty()) {
                    courseProgress = mapOf(
                        "course1" to prefs.getBoolean("course1", false),
                        "course2" to prefs.getBoolean("course2", false)
                    )
                } else {
                    courseProgress = cloudMap
                    cloudMap.forEach { (key, value) ->
                        prefs.edit().putBoolean(key, value).apply()
                    }
                }
            }
        } else {
            courseProgress = mapOf(
                "course1" to prefs.getBoolean("course1", false),
                "course2" to prefs.getBoolean("course2", false)
            )
        }
    }

    BackdropDemoScaffold { backdrop ->
        var isCorrect by remember { mutableStateOf<Boolean?>(null) }
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        val screenHeightDp = LocalConfiguration.current.screenHeightDp

        Column(
            modifier = Modifier.fillMaxSize().layerBackdrop(contentBackdrop),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))

            Text(
                text = "Обученiе",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poiret))
            )
            Spacer(modifier = Modifier.height((screenHeightDp * 0.02f).dp))

            // Курс 1
            Row {
                Spacer(modifier = Modifier.width((screenWidthDp * 0.54f).dp))
                LiquidButton(
                    onClick = onCourse1Click,
                    backdrop = backdrop,
                    modifier = Modifier.height((screenWidthDp * 0.33f).dp)
                        .width((screenWidthDp * 0.33f).dp)
                ) {
                    Text("1", style = TextStyle(Color.Black, 80.sp, fontFamily = FontFamily(Font(R.font.poiret))))
                }
            }

            Row(modifier = Modifier.fillMaxWidth().height((screenHeightDp * 0.13f).dp)) {
                Spacer(modifier = Modifier.width((screenWidthDp * 0.16f).dp).fillMaxHeight())
                Icon(painter = painterResource(R.drawable.steps), contentDescription = null, modifier = Modifier.size(250.dp).rotate(135f))
            }

            // Курс 2
            Row {
                Spacer(modifier = Modifier.width(30.dp))
                LiquidButton(
                    onClick = { if (courseProgress["course1"] == true) onCourse2Click?.invoke() else isCorrect = false },
                    backdrop = backdrop,
                    modifier = Modifier.height((screenWidthDp * 0.33f).dp)
                        .width((screenWidthDp * 0.33f).dp),
                    tint = if (courseProgress["course1"] != true) Color(0xD3D3D3) else Color.Unspecified,
                    isInteractive = courseProgress["course1"] == true
                ) {
                    Text("2", style = TextStyle(Color.Black, 80.sp, fontFamily = FontFamily(Font(R.font.poiret))))
                }
            }

            Row(modifier = Modifier.fillMaxWidth().height((screenHeightDp * 0.13f).dp)) {
                Spacer(modifier = Modifier.width((screenWidthDp * 0.17f).dp).fillMaxHeight())
                Icon(painter = painterResource(R.drawable.steps), contentDescription = null, modifier = Modifier.size(250.dp).rotate(45f))
            }

            // Курс 3
            Row {
                Spacer(modifier = Modifier.width((screenWidthDp * 0.54f).dp))
                LiquidButton(
                    onClick = { if (courseProgress["course2"] == true) onCourse3Click?.invoke() else isCorrect = false },
                    backdrop = backdrop,
                    modifier = Modifier.height((screenWidthDp * 0.33f).dp)
                        .width((screenWidthDp * 0.33f).dp),
                    tint = if (courseProgress["course2"] != true) Color(0xD3D3D3) else Color.Unspecified,
                    isInteractive = courseProgress["course2"] == true
                ) {
                    Text("3", style = TextStyle(Color.Black, 80.sp, fontFamily = FontFamily(Font(R.font.poiret))))
                }
            }
        }

        TopFeedbackBanner(
            isCorrect = isCorrect,
            onDismiss = { isCorrect = null },
            backdrop = contentBackdrop,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                .windowInsetsPadding(WindowInsets.statusBars),
            "Предыдущiй курсъ не пройденъ!"
        )
    }
}