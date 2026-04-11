package com.example.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.BackdropDemoScaffold
import com.example.keyboard.LiquidButton
import com.example.keyboard.LiquidToggle1


@Composable
fun MainScreen() {
    BackdropDemoScaffold { backdrop ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16f.dp)
        ) {
            var selected by rememberSaveable { mutableStateOf(false) }
            LiquidButton(
                {},
                backdrop
            ) {
                BasicText(
                    "Transparent Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
            LiquidButton(
                {},
                backdrop,
                surfaceColor = Color.White.copy(0.3f)
            ) {
                BasicText(
                    "Surface Liquid Button",
                    style = TextStyle(Color.Black, 15f.sp)
                )
            }
//            LiquidButton(
//                {},
//                backdrop,
//                tint = Color(0xFF0088FF)
//            ) {
//                BasicText(
//                    "Tinted Liquid Button",
//                    style = TextStyle(Color.White, 15f.sp)
//                )
//            }
//            LiquidButton(
//                {},
//                backdrop,
//                tint = Color(0xFFFF8D28)
//            ) {
//                BasicText(
//                    "Tinted Liquid Button",
//                    style = TextStyle(Color.White, 15f.sp)
//                )
//            }
            LiquidToggle1(selected = { selected },
                onSelect = { selected = it },
                backdrop = backdrop,
                modifier = Modifier.padding(horizontal = 32f.dp))
        }
    }
}
