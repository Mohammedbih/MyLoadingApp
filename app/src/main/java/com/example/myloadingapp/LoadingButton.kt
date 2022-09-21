package com.example.myloadingapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

@SuppressLint("ResourceAsColor")
class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var animateWidth = 0f
    private val valueAnimator = ValueAnimator()
    private var text = resources.getString(R.string.download)

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                text = resources.getString(R.string.button_loading)
                loadingAnimation(true)
            }

            ButtonState.Loading -> {
                text = resources.getString(R.string.button_loading)
                loadingAnimation(false)
            }
            ButtonState.Completed -> {
                if (valueAnimator.isRunning) {
                    valueAnimator.removeAllUpdateListeners()
                    valueAnimator.end()
                }
                text = resources.getString(R.string.download)
                animateWidth = 0f
                invalidate()
            }
        }
    }

    private var _backgroundColor: Int = R.color.colorPrimary
    private var _backgroundDarkColor: Int = R.color.colorPrimaryDark
    private var _circlePaintColor: Int = R.color.colorAccent

    //For Colors
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            _backgroundColor =
                getColor(R.styleable.LoadingButton_backgroundColor, R.color.colorPrimary)
            _backgroundDarkColor =
                getColor(R.styleable.LoadingButton_backgroundDarkColor, R.color.colorPrimaryDark)
            _circlePaintColor =
                getColor(R.styleable.LoadingButton_colorAccent, R.color.colorAccent)
        }

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = _backgroundColor
    }

    private val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = _backgroundDarkColor
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = _circlePaintColor
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.NORMAL)
        color = Color.WHITE
    }


    private fun loadingAnimation(noUrl: Boolean) {
        valueAnimator.apply {
            setFloatValues(0f, widthSize.toFloat())
            duration = 10000
            repeatCount = 0
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                animateWidth = animatedValue as Float
                this@LoadingButton.invalidate()
                if (noUrl && animateWidth == widthSize.toFloat())
                    buttonState = ButtonState.Completed
            }
            start()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF(0f, 0f, widthSize.toFloat(), heightSize.toFloat())
        val rectDark = RectF(0f, 0f, animateWidth, heightSize.toFloat())
        val rectCircle = RectF(
            widthSize.toFloat() / 1.4f,
            heightSize.toFloat() / 2 - 30,
            widthSize.toFloat() / 1.4f + 60,
            heightSize.toFloat() / 2 + 30
        )
        canvas.drawRect(rect, paint)
        canvas.drawRect(rectDark, darkPaint)
        canvas.drawArc(rectCircle, 0f, (animateWidth / widthSize) * 360, true, circlePaint)
        canvas.drawText(text, widthSize.toFloat() / 2, heightSize.toFloat() / 2 + 15, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}
