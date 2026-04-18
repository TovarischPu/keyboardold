package com.example.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@Composable
fun Education(){
    BackdropDemoScaffold { backdrop ->
        Column(
            modifier = Modifier.fillMaxSize(),
            //horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10f.dp)
        ) {
            val screenWidthDp = LocalConfiguration.current.screenWidthDp
            val screenHeightDp = LocalConfiguration.current.screenHeightDp
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
            Text(text="Обученiе", fontSize = 30.sp,fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(screenHeightDp*0.02f.dp))
            Row() {
                Spacer(modifier = Modifier.width(screenWidthDp*0.54.dp))
                LiquidButton(
                    {},
                    backdrop,
                    modifier = Modifier.height(screenWidthDp*0.33.dp).width(screenWidthDp*0.33.dp),
                ) {
                    BasicText(
                        "1",
                        style = TextStyle(Color.Black, 80f.sp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth().height(screenHeightDp*0.13f.dp)) {
                Spacer(modifier = Modifier.width(screenWidthDp*0.16f.dp).fillMaxHeight())
                Icon(painter = painterResource(com.example.keyboard.R.drawable.steps),
                    contentDescription = "Иконка",
                    modifier = Modifier.size(250.dp).rotate(135f),
                )
            }
            //Spacer(modifier = Modifier.height(5.dp).fillMaxWidth())
            Row() {
                Spacer(modifier = Modifier.width(30.dp))
                LiquidButton(
                    {},
                    backdrop,
                    modifier = Modifier.height(screenWidthDp*0.33.dp).width(screenWidthDp*0.33.dp),
                    tint = Color(0xD3D3D3)
                ) {
                    BasicText(
                        "2",
                        style = TextStyle(Color.Black, 80f.sp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth().height(screenHeightDp*0.13f.dp)) {
                Spacer(modifier = Modifier.width(screenWidthDp*0.17f.dp).fillMaxHeight())
                Icon(painter = painterResource(com.example.keyboard.R.drawable.steps),
                    contentDescription = "Иконка",
                    modifier = Modifier.size(250.dp).rotate(45f),
                )
            }
            Row() {
                Spacer(modifier = Modifier.width(screenWidthDp*0.54.dp))
                LiquidButton(
                    {},
                    backdrop,
                    modifier = Modifier.height(screenWidthDp*0.33.dp).width(screenWidthDp*0.33.dp),
                    tint = Color(0xD3D3D3)
                ) {
                    BasicText(
                        "3",
                        style = TextStyle(Color.Black, 80f.sp)
                    )
                }
            }


//            LiquidButton(
//                {},
//                backdrop,
//                surfaceColor = Color.White.copy(0.3f)
//            ) {
//                BasicText(
//                    "Surface Liquid Button",
//                    style = TextStyle(Color.Black, 15f.sp)
//                )
//            }
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

        }
    }
}