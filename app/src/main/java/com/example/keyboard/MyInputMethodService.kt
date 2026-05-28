package com.example.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.keyboard.translator.GptClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyInputMethodService : InputMethodService() {
    private var isRussian = false
    private lateinit var vibrator: Vibrator
    private lateinit var rootLayout: FrameLayout
    private lateinit var keyboardContainer: LinearLayout
    private lateinit var previewView: TextView
    private lateinit var translateButton: ImageButton
    private lateinit var globeButton: ImageButton
    private val TAG = "MyIME"
    private val keyboardHeightPercent = 0.6f
    private val doubleTapThreshold = 300L
    private var lastTapTime = 0L
    private val doubleTapHandler = Handler(Looper.getMainLooper())

    private var isCapsLock = false
    private var isShiftActive = false
    private var lastShiftTapTime = 0L

    private val letterButtons = mutableListOf<Button>()
    private lateinit var shiftCapsButton: Button

    // 🔹 Таймер для долгого нажатия на кнопку удаления
    private val backspaceHandler = Handler(Looper.getMainLooper())
    private var isBackspaceLongPressed = false
    private val backspaceRunnable = object : Runnable {
        override fun run() {
            currentInputConnection?.deleteSurroundingText(1, 0)
            if (isBackspaceLongPressed) {
                backspaceHandler.postDelayed(this, 50L)
            }
        }
    }

    private val imeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isTranslating = false

    override fun onEvaluateInputViewShown(): Boolean = true

    override fun onCreate() {
        super.onCreate()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Log.e(TAG, "1️⃣ onCreate() ")
    }

    override fun onBindInput() {
        super.onBindInput()
        Log.e(TAG, "2️⃣ onBindInput() ")
    }

    override fun onStartInput(editorInfo: EditorInfo, restarting: Boolean) {
        super.onStartInput(editorInfo, restarting)
        Log.e(TAG, "3️⃣ onStartInput() inputType=0x${editorInfo.inputType.toString(16)} ")
    }

    override fun onCreateInputView(): View {
        Log.e(TAG, "4️⃣ onCreateInputView() START ")
        val screenHeight = resources.displayMetrics.heightPixels
        val keyboardHeight = (screenHeight * keyboardHeightPercent).toInt()
        return try {
            rootLayout = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight
                )
                setBackgroundColor(Color.parseColor("#E0E0E0"))
            }

            keyboardContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                setPadding(8, 8, 8, 80)
            }
            rootLayout.addView(keyboardContainer)

            previewView = TextView(this).apply {
                textSize = 20f
                setTextColor(Color.parseColor("#212121"))
                gravity = Gravity.CENTER
                setPadding(10, 4, 10, 4)
                elevation = 10f
                visibility = View.INVISIBLE
                background = GradientDrawable().apply {
                    cornerRadius = 24f
                    setColor(Color.WHITE)
                    setStroke(2, Color.parseColor("#E0E0E0"))
                }
                val density = resources.displayMetrics.density
                layoutParams = FrameLayout.LayoutParams((48 * density).toInt(), (65 * density).toInt()).apply {
                    gravity = Gravity.TOP or Gravity.START
                }
            }
            rootLayout.addView(previewView)

            renderKeyboard()
            Log.e(TAG, "✅ onCreateInputView() SUCCESS ")
            rootLayout
        } catch (e: Exception) {
            Log.e(TAG, "💥 CRASH in onCreateInputView ", e)
            View(this).apply { layoutParams = ViewGroup.LayoutParams(0, 0) }
        }
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.e(TAG, "5️⃣ onStartInputView() ")
    }


    private fun renderKeyboard() {
        dismissKeyPreview()
        doubleTapHandler.removeCallbacksAndMessages(null)
        backspaceHandler.removeCallbacksAndMessages(null)
        lastTapTime = 0L
        letterButtons.clear()
        keyboardContainer.removeAllViews()

        translateButton = ImageButton(this).apply {
            setImageResource(R.drawable.translate)
            setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            setPadding(0, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 120
            ).apply { gravity = Gravity.CENTER_HORIZONTAL }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background = GradientDrawable().apply {
                cornerRadius = 16f
                setColor(Color.parseColor("#E0E0E0"))
            }
            setOnClickListener { if (!isTranslating) startTranslation() }
        }

        globeButton = ImageButton(this).apply {
            setImageResource(R.drawable.globe)
            setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f).apply {
                setMargins(10, 3, 10, 3)
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            background = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(ContextCompat.getColor(context, android.R.color.white))
            }
            setOnClickListener {
                vibrateKey()
                isRussian = !isRussian
                renderKeyboard()
            }
        }

        keyboardContainer.addView(translateButton)

        val rows = if (isRussian) RU_ROWS else EN_ROWS

        rows.forEachIndexed { index, row ->
            val isLastRow = index == rows.lastIndex
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
                ).apply { topMargin = 3; bottomMargin = 3 }
                setPadding(4, 0, 4, 0)
            }

            if (isLastRow) rowLayout.addView(createShiftCapsButton())

            row.forEach { key ->
                val isCyrillicE = key == "е" && isRussian
                val isCyrillicER = key == "ь" && isRussian
                val isCyrillicI = key == "и" && isRussian
                val isCyrillicF = key == "ф" && isRussian
                val doubleTapAction = when {
                    isCyrillicE -> { { sendText("ѣ") } }
                    isCyrillicER -> { { sendText("ъ") } }
                    isCyrillicI -> { { sendText("i") } }
                    isCyrillicF -> { { sendText("ѳ") } }
                    else -> null
                }
                val btn = createKeyView(key, 1f, action = { sendText(key) }, onDoubleTap = doubleTapAction, last = isLastRow)
                rowLayout.addView(btn)
                letterButtons.add(btn)
            }

            if (isLastRow) {
                val backspaceBtn = Button(this).apply {
                    text = "⌫"
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f).apply {
                        setMargins(10, 3, 10, 3)
                    }
                    textSize = 19f
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, 0)
                    background = GradientDrawable().apply {
                        cornerRadius = 40f
                        setColor(ContextCompat.getColor(context, android.R.color.white))
                    }

                    setOnTouchListener { _, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                (background as GradientDrawable).setColor(Color.parseColor("#BDBDBD"))
                                backspaceHandler.postDelayed({
                                    isBackspaceLongPressed = true
                                    currentInputConnection?.deleteSurroundingText(1, 0)
                                    backspaceRunnable.run()
                                }, 400L)
                            }
                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                backspaceHandler.removeCallbacksAndMessages(null)
                                (background as GradientDrawable).setColor(ContextCompat.getColor(context, android.R.color.white))
                                if (!isBackspaceLongPressed) {

                                    vibrateKey()
                                    currentInputConnection?.deleteSurroundingText(1, 0)
                                } else {
                                    isBackspaceLongPressed = false
                                }
                            }
                        }
                        true
                    }
                }
                rowLayout.addView(backspaceBtn)
            }
            keyboardContainer.addView(rowLayout)
        }

        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.8f
            ).apply { topMargin = 3; bottomMargin = 3 }
            setPadding(4, 0, 4, 0)
        }
        bottomRow.addView(globeButton)
        bottomRow.addView(createKeyView(",", 1f, action = { sendText(",") }))
        bottomRow.addView(createKeyView(if (!isRussian) "EN" else "RU", 5f, action = { sendText(" ") }))
        bottomRow.addView(createKeyView(".", 1f, action = { sendText(".") }))
        bottomRow.addView(createKeyView("->", 2f, action = { sendText("\n") }))
        keyboardContainer.addView(bottomRow)

        updateKeyLabels()
    }

    private fun updateKeyLabels() {
        val toUpperCase = isCapsLock || isShiftActive
        letterButtons.forEach { btn ->
            val original = btn.tag as String
            btn.text = if (toUpperCase) original.uppercase() else original
        }
        updateShiftAppearance()
    }

    private fun createShiftCapsButton(): View {
        shiftCapsButton = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.2f).apply {
                setMargins(3, 0, 0, 6)
            }.apply { gravity = Gravity.CENTER_HORIZONTAL }
            textSize = 32f
            gravity = Gravity.CENTER
            setPadding(2, 8, 2, 8)
            background = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(ContextCompat.getColor(context, android.R.color.white))
            }

            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> (background as GradientDrawable).setColor(Color.parseColor("#9E9E9E"))
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> updateShiftAppearance()
                }
                false
            }

            setOnClickListener {
                vibrateKey()
                val now = SystemClock.elapsedRealtime()
                if (now - lastShiftTapTime < doubleTapThreshold) {
                    isCapsLock = !isCapsLock
                    isShiftActive = false
                    lastShiftTapTime = 0L
                } else {
                    if (isShiftActive || isCapsLock) {
                        isShiftActive = false
                        isCapsLock = false
                    } else {
                        isShiftActive = true
                        isCapsLock = false
                    }
                    lastShiftTapTime = now
                }
                updateKeyLabels()
            }
        }
        return shiftCapsButton
    }

    private fun updateShiftAppearance() {
        shiftCapsButton.apply {
            text = if (isCapsLock) "⇪" else "⇧"
            if (isCapsLock || isShiftActive) setTextColor(Color.WHITE) else setTextColor(Color.BLACK)
            (background as GradientDrawable).setColor(
                if (isCapsLock || isShiftActive) Color.parseColor("#333333")
                else ContextCompat.getColor(context, android.R.color.white)
            )
        }
    }

    private fun createKeyView(
        label: String,
        weight: Float,
        action: () -> Unit,
        onDoubleTap: (() -> Unit)? = null,
        last: Boolean = false
    ): Button {
        return Button(this).apply {
            tag = label
            text = label
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply {
                setMargins(10, 3, 10, 3)
            }.apply { if (!last) gravity = Gravity.CENTER_HORIZONTAL }
            textSize = 19f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
            background = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(ContextCompat.getColor(context, android.R.color.white))
            }

            val showPreview = label.length == 1 &&
                    (label[0].isLetterOrDigit() || label[0] in listOf(',', '.'))

            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        (background as GradientDrawable).setColor(Color.parseColor("#BDBDBD"))
                        if (showPreview) showKeyPreview(v as View)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        (background as GradientDrawable).setColor(ContextCompat.getColor(context, android.R.color.white))
                        dismissKeyPreview()
                    }
                }
                false
            }

            setOnClickListener {
                vibrateKey()
                if (onDoubleTap != null) {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastTapTime < doubleTapThreshold) {
                        doubleTapHandler.removeCallbacksAndMessages(null)
                        currentInputConnection?.deleteSurroundingText(1, 0)
                        onDoubleTap()
                        lastTapTime = 0L
                    } else {
                        action()
                        lastTapTime = now
                        doubleTapHandler.postDelayed({ lastTapTime = 0L }, doubleTapThreshold)
                    }
                } else {
                    action()
                }
            }
        }
    }

    private fun vibrateKey() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }

    private fun startTranslation() {
        val ic = currentInputConnection ?: return
        val before = ic.getTextBeforeCursor(5000, 0)?.toString() ?: " "
        val after = ic.getTextAfterCursor(5000, 0)?.toString() ?: " "
        val fullText = (before + after).trim()

        if (fullText.isBlank()) {
            Toast.makeText(this, "Нет текста для перевода", Toast.LENGTH_SHORT).show()
            return
        }

        isTranslating = true
        imeScope.launch {
            val result = GptClient.askModel(fullText)
            withContext(Dispatchers.Main) {
                isTranslating = false
                result.onSuccess { response ->
                    ic.deleteSurroundingText(10000, 10000)
                    ic.commitText(response, 1)
                }.onFailure { e ->
                    Toast.makeText(this@MyInputMethodService, "Ошибка AI: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendText(text: String) {
        try {
            var processedText = text
            if (isCapsLock || isShiftActive) {
                processedText = text.uppercase()
                if (isShiftActive) {
                    isShiftActive = false
                    updateKeyLabels()
                }
            }
            currentInputConnection?.commitText(processedText, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to commit text", e)
        }
    }

    private fun showKeyPreview(keyView: View) {
        previewView.apply {
            text = (keyView.tag as? String) ?: ""
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
                scaleX = 0.8f
                scaleY = 0.8f
                alpha = 0f
            }
            animate().cancel()
            animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(60)
                .start()

            val keyPos = IntArray(2)
            keyView.getLocationOnScreen(keyPos)
            val rootPos = IntArray(2)
            rootLayout.getLocationOnScreen(rootPos)

            val gap = 16f * resources.displayMetrics.density
            var x = keyPos[0] - rootPos[0] + keyView.width / 2f - width / 2f
            var y = keyPos[1] - rootPos[1] - height - gap

            x = x.coerceAtLeast(0f).coerceAtMost(rootLayout.width.toFloat() - width)
            y = y.coerceAtLeast(0f)

            this.x = x
            this.y = y
        }
    }

    private fun dismissKeyPreview() {
        previewView.apply {
            if (visibility == View.VISIBLE) {
                animate().cancel()
                animate()
                    .scaleX(0.6f).scaleY(0.6f).alpha(0f)
                    .setDuration(80)
                    .withEndAction { visibility = View.INVISIBLE }
                    .start()
            }
        }
    }

    companion object {
        private val EN_ROWS = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            listOf("z", "x", "c", "v", "b", "n", "m")
        )
        private val RU_ROWS = listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х"),
            listOf("ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э"),
            listOf("я", "ч", "с", "м", "и", "т", "ь", "б", "ю")
        )
    }
}