package com.example.keyboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keyboard.design.AnimatedMultiLineText
import com.example.keyboard.design.BackdropDemoScaffold
import com.example.keyboard.design.LiquidButton
import com.example.keyboard.design.LiquidToggle1
import com.example.keyboard.design.TopFeedbackBanner
import com.example.keyboard.translator.ChatViewModel
import com.example.keyboard.translator.TranslatorViewModel
import com.google.firebase.auth.FirebaseUser
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.RoundedRectangle


@Composable
fun MainScreen(user: FirebaseUser?,viewModel: TranslatorViewModel = viewModel(), viewModel1: ChatViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current
    BackdropDemoScaffold { backdrop ->
        val contentBackdrop = rememberLayerBackdrop()
        var isCorrect by remember { mutableStateOf<Boolean?>(null) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val isPressed = event.changes.any { it.pressed }
                            val notConsumedByChild = event.changes.none { it.isConsumed }

                            if (isPressed && notConsumedByChild) {
                                focusManager.clearFocus()
                            }
                        }
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            LazyColumn(
                Modifier.fillMaxSize().layerBackdrop(contentBackdrop),
                contentPadding = PaddingValues(16f.dp),
                verticalArrangement = Arrangement.spacedBy(16f.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Переводчикъ",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.poiret))
                        )
                    }
                }
                item {
                    val uiState by viewModel1.uiState.collectAsState()
                    var text by remember { mutableStateOf("") }
                    var text1 by remember { mutableStateOf("") }
                    var selected by rememberSaveable { mutableStateOf(false) }
                    var i by remember { mutableStateOf(2) }
                    val screenHeightDp = LocalConfiguration.current.screenHeightDp
                    val text2 by viewModel.translatedText.collectAsState()
                    var selected1 by rememberSaveable { mutableStateOf(true) }
                    var textFieldValue by remember {
                        mutableStateOf(TextFieldValue(""))
                    }

                    LaunchedEffect(selected) {
                        if (selected && user == null) {
                            isCorrect=false
                            selected = false

                        }
                    }
                    LaunchedEffect(text2) {
                        text1 = text2
                    }

                    Spacer(modifier = Modifier.height(screenHeightDp * 0.13f.dp).fillMaxWidth())
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(start = 25.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Нейросѣтевой переводъ",
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.poiret))
                            )
                            LiquidToggle1(
                                selected = { selected },
                                onSelect = { selected = it },
                                backdrop = backdrop,
                                modifier = Modifier.padding(horizontal = 32f.dp)
                            )
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
                            .height((screenHeightDp * 0.18f).dp)
                            .padding(start = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth().padding(start = 0.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("")
                                if (selected1) Text(
                                    "Е",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.poiret))
                                )
                                else Text(
                                    "Ѣ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.poiret))
                                )
                            }
                            BasicTextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                modifier = Modifier.fillMaxWidth()
                                    .zIndex(0f).height(22.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.Transparent,
                                    fontSize = 22.sp
                                ),
                                cursorBrush = SolidColor(Color.Transparent),
                            )
                            AnimatedMultiLineText(
                                text = textFieldValue.text,
                                cursorIndex = textFieldValue.selection.start,
                                onCursorIndexChange = { newIndex ->
                                    textFieldValue = TextFieldValue(
                                        text = textFieldValue.text,
                                        selection = androidx.compose.ui.text.TextRange(newIndex)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp),
                                fontSize = 22.sp,
                                cursorColor = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("")
                        LiquidButton(
                            { if (selected1) selected1 = false else selected1 = true },
                            backdrop,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.strelki),
                                contentDescription = "Иконка",
                                modifier = Modifier.size(24.dp)
                            )
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
                            .height(screenHeightDp * 0.18f.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(start = 20.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth().padding(end = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("")
                                if (selected1) Text(
                                    "Ѣ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.poiret))
                                )
                                else Text(
                                    "E",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily(Font(R.font.poiret))
                                )
                            }
                            AnimatedMultiLineText(
                                text = text1,
                                cursorIndex = textFieldValue.selection.start,
                                onCursorIndexChange = { newIndex ->
                                    textFieldValue = TextFieldValue(
                                        text = textFieldValue.text,
                                        selection = androidx.compose.ui.text.TextRange(newIndex)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 100.dp),
                                fontSize = 22.sp,
                                cursorColor = Color.Black,
                                y=0,
                                m=0f
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())
                    LiquidButton(
                        {
                            if (!selected) {
                                text1 = ""
                                viewModel.translate(textFieldValue.text)
                            } else {
                                viewModel1.sendMessage(textFieldValue.text)
                                uiState.response?.let { resp ->
                                    text1 = resp
                                }
                            }
                        },
                        backdrop
                    ) {
                        BasicText(
                            "Перевести",
                            style = TextStyle(
                                Color.Black,
                                15f.sp,
                                fontFamily = FontFamily(Font(R.font.poiret))
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(screenHeightDp * 0.07f.dp).fillMaxWidth())
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
                            .height((160f) * i.dp)
                            .fillMaxWidth()
                    ) {

                    }
                    Spacer(modifier = Modifier.height(screenHeightDp * 0.04f.dp).fillMaxWidth())
                }
                item {
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                }
            }
        }
        TopFeedbackBanner(
            isCorrect = isCorrect,
            onDismiss = { isCorrect = null },
            backdrop = contentBackdrop,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                .windowInsetsPadding(WindowInsets.statusBars),
            "Войдите в аккаунт гугл!"
        )
    }
}
