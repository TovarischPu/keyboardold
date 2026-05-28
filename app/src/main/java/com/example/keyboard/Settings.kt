package com.example.keyboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.keyboard.design.AnimatedMultiLineText
import com.example.keyboard.design.BackdropDemoScaffold
import com.example.keyboard.design.LiquidButton
import com.example.keyboard.design.LiquidToggle1
import com.example.keyboard.design.TopFeedbackBanner
import com.google.firebase.auth.FirebaseUser
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop

@Composable
fun Settings(
    user: FirebaseUser?,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    BackdropDemoScaffold { backdrop ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))

            Text(
                text = "Настройки",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.poiret))
            )

            Spacer(Modifier.height(16.dp))

            if (user == null) {
                LiquidButton(
                    onClick = onSignIn,
                    backdrop,
                    modifier = Modifier.fillMaxWidth(),
                    tint = Color(0xFF0088FF)
                ) {
                    Text("Войти через Google")
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Вы вошли как:", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = user.displayName ?: user.email ?: "Пользователь",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    LiquidButton(onClick = onSignOut,backdrop,tint = Color(0x80FF0000)) {
                        Text("Выйти")
                    }
                }
            }
        }
    }
}