package com.example.keyboard.education

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.keyboard.design.AnimatedMultiLineText
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.RoundedRectangle


@Composable
fun Course1Step(step: Int, onNext: () -> Unit, onBack: () -> Unit,backdrop: Backdrop,test: (Boolean)-> Unit,text1: (String)-> Unit) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    when (step) {
        0 -> StepTemplate(title = "Курс 1: Теория 1", onNext = onNext, onBack = onBack) {
            test(false)
            Text("Въ дореволюцiонной грамматикѣ было нѣсколько отличiй отъ современной орѳографiи. 4 нынѣ не существующихъ буквы и нѣкоторыя отличiя въ " +
                    "грамматикѣ. Въ этомъ блокѣ для разминки разберёмъ двѣ простыхъ буквы: i,ѵ", fontSize = 18.sp)
        }
        1 -> StepTemplate(title = "Курс 1: Теория 2 / Тест 1", onNext = onNext, onBack = onBack) {
            test(false)
            Text("i использовалась на мѣстѣ современной И передъ гласными буквами и Й (тогда считалась гласной)\n" +
                    "Обрати вниманiе на исключенiя: мiръ (въ значенiи world)\n Примѣръ: миръ во всёмъ мiрѣ, Владимiръ", fontSize = 18.sp)
        }
        2 -> StepTemplate(title = "Курс 2: Тест", onNext = onNext, onBack = onBack, isFinish = true) {
            Text("Переведи: Мефодий", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            test(true)
            Box(
                Modifier
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedRectangle(32f.dp) },
                        effects = {
                            vibrancy()
                            lens(16f.dp.toPx(), 32f.dp.toPx())
                        },
                        shadow = {Shadow(
                            radius = 1f.dp,
                            color = Color.Black.copy(alpha = 0.05f)
                        )}
                    )
                    .height(35.dp)
                    .padding(start = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.fillMaxWidth()
                        .zIndex(0f).height(30.dp),
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
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    y = 5
                )
            }
            text1(textFieldValue.text)
        }
        3 -> StepTemplate(title = "Курс 1: Ижица", onNext = onNext, onBack = onBack, isFinish = true) {
            test(false)
            Text("Ижица использовалась въ церковныхъ словахъ. Ея примѣненiе сложно представить внѣ контекста церкви. Произносится какъ И\n " +
                    "Примѣры: мѵро, сѵмволъ (какъ божiй знакъ)", fontSize = 20.sp)
        }
        4 -> StepTemplate(title = "Курс 1: Финал", onNext = onNext, onBack = onBack, isFinish = true) {
            test(false)
            Text("🎉 Поздравляем! Курс 1 пройден.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Course2Step(step: Int, onNext: () -> Unit, onBack: () -> Unit,backdrop: Backdrop,test: (Boolean)-> Unit,text1: (String)-> Unit) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    when (step) {
        0 -> StepTemplate(title = "Курс 2: Теория 1", onNext = onNext, onBack = onBack) {
            Text("Перейдёмъ к болѣе сложнымъ буквамъ: Ѳ и Ѣ. ", fontSize = 18.sp)
        }
        1 -> StepTemplate(title = "Курс 2: Тест 1", onNext = onNext, onBack = onBack) {
            Text("Ѳ встрѣчается не такъ часто, однако словъ с ней достаточно.\n" +
                    "Фита родомъ изъ греческаго алфавита и ставится на мѣстѣ буквы Ф. Чаще всего её можно увидѣть" +
                    " въ географическихъ названияхъ и математикѣ\n Но какъ же запомнить? Есть лайфхакъ: " +
                    "если на мѣстѣ Ф в англiйскомъ варiантѣ слова стоитъ th, то в дореформленном русскомъ ставится фита\n" +
                    "Напримѣръ: Aѳины (Athens), логариѳмъ (logarithm)\n Но: телефонъ (telephone)", fontSize = 18.sp)
        }
        2 -> StepTemplate(title = "Курс 1: Тест", onNext = onNext, onBack = onBack, isFinish = true) {
            Text("Переведи: амбиция", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            test(true)
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
                    .height(35.dp)
                    .padding(start = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.fillMaxWidth()
                        .zIndex(0f).height(30.dp),
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
                    modifier = Modifier.fillMaxWidth().zIndex(1f),
                    y = 5
                )
            }
            text1(textFieldValue.text)
        }
        3 -> StepTemplate(title = "Курс 2: Финал", onNext = onNext, onBack = onBack, isFinish = true) {
            Text("🎉 Курс 2 завершён!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Course3Step(step: Int, onNext: () -> Unit, onBack: () -> Unit) {
    when (step) {
        0 -> StepTemplate(title = "Курс 3: Теория 1", onNext = onNext, onBack = onBack) {
            Text("Контент теории для Курса 3, экран 1.", fontSize = 18.sp)
        }
        1 -> StepTemplate(title = "Курс 3: Тест 1", onNext = onNext, onBack = onBack) {
            Text("Тесты для Курса 3, экран 2.", fontSize = 18.sp)
        }
        2 -> StepTemplate(title = "Курс 3: Финал", onNext = onNext, onBack = onBack, isFinish = true) {
            Text("🎉 Курс 3 завершён!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}