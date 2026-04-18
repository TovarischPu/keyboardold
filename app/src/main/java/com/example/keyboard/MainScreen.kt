package com.example.keyboard

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.example.keyboard.BackdropDemoScaffold
import com.example.keyboard.LiquidButton
import com.example.keyboard.LiquidToggle1
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.RoundedRectangle


@Composable
fun MainScreen() {
    BackdropDemoScaffold { backdrop ->
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16f.dp),
            verticalArrangement = Arrangement.spacedBy(16f.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text="Переводчикъ", fontSize = 30.sp,fontWeight = FontWeight.Bold)
                }
            }

            item {
                var text by remember {mutableStateOf("")}
                var selected by rememberSaveable { mutableStateOf(false) }
                var i by remember { mutableStateOf(2) }
                val screenWidthDp = LocalConfiguration.current.screenWidthDp
                val screenHeightDp = LocalConfiguration.current.screenHeightDp
                var text2 by remember {mutableStateOf("")}
                val context = LocalContext.current
                Spacer(modifier = Modifier.height(screenHeightDp*0.13f.dp).fillMaxWidth())
                Box(modifier = Modifier.fillMaxWidth().padding(start = 25.dp),
                    ){
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Нейросѣтевой переводъ", fontSize = 18.sp)
                        LiquidToggle1(selected = { selected },
                            onSelect = { selected = it },
                            backdrop = backdrop,
                            modifier = Modifier.padding(horizontal = 32f.dp))
                    }
                    }

                Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                Box(
                    Modifier
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedRectangle(32f.dp) },
                            effects = {
                                vibrancy()
                                lens(16f.dp.toPx(), 32f.dp.toPx())
                            }
                        )
                        .height(screenHeightDp*0.18f.dp)
                        .padding(start = 20.dp)
                        .fillMaxWidth()
                ){
                    Column(modifier = Modifier){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().padding(start = 0.dp, end=20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("")
                        Text("Е", fontSize = 18.sp,fontWeight = FontWeight.Bold)
                    }
                        //Spacer(modifier = Modifier.height(20.dp))

                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth()
                            .zIndex(0f),
                        textStyle = LocalTextStyle.current.copy(color = Color.Transparent, fontSize = 22.sp),
                        cursorBrush = SolidColor(Color.Transparent),
                        )
                        AnimatedText(text = text,-28)
                }
                }
                Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("")
                    LiquidButton(
                        {},
                        backdrop,
                        ) {
                        Icon(painter = painterResource(com.example.keyboard.R.drawable.strelki),
                            contentDescription = "Иконка",
                            modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                Box(
                    Modifier
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedRectangle(32f.dp) },
                            effects = {
                                vibrancy()
                                lens(16f.dp.toPx(), 32f.dp.toPx())
                            }
                        )
                        .height(screenHeightDp*0.18f.dp)
                        .fillMaxWidth()
                ){
                    Column(modifier = Modifier.padding(start = 20.dp)) {
                        Row(
                        modifier = Modifier
                            .fillMaxWidth().padding(end=20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("")
                        Text("Ѣ",fontSize = 18.sp,fontWeight = FontWeight.Bold)
                    }
                        AnimatedText(text = text,0) }
                }
                Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                LiquidButton(
                    {
                        //text2=Translatoric(context,text).orEmpty()
                    },
                    backdrop
                ) {
                    BasicText(
                        "Перевести",
                        style = TextStyle(Color.Black, 15f.sp)
                )
            }
                Spacer(modifier = Modifier.height(screenHeightDp*0.07f.dp).fillMaxWidth())
                Box(
                    Modifier
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { RoundedRectangle(32f.dp) },
                            effects = {
                                vibrancy()
                                lens(16f.dp.toPx(), 32f.dp.toPx())
                            }
                        )
                        .height((160f)*i.dp)
                        .fillMaxWidth()
                ){

                }
                Spacer(modifier = Modifier.height(screenHeightDp*0.04f.dp).fillMaxWidth())
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}
