package com.example.keyboard.design


import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.example.keyboard.R

@Composable
fun BackdropDemoScaffold(
    modifier: Modifier = Modifier,
    @DrawableRes initialPainterResId: Int = R.drawable.beije,
    content: @Composable BoxScope.(backdrop: LayerBackdrop) -> Unit
) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var painter: Painter? by remember { mutableStateOf(null) }
        val context = LocalContext.current
        val pickMedia = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val imageBitmap = BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                        if (imageBitmap != null) {
                            painter = BitmapPainter(imageBitmap)
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        val backdrop = rememberLayerBackdrop()

        Image(
            painter ?: painterResource(initialPainterResId),
            null,
            Modifier
                .layerBackdrop(backdrop)
                .then(modifier)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        content(backdrop)
    }
}
