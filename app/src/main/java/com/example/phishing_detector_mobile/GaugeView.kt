package com.example.phishing_detector_mobile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class GaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#333333") // Dark Gray for track
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#4CAF50") // Green default
        strokeWidth = 40f
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(10f, 0f, 0f, Color.parseColor("#4CAF50")) // Neon glow effect
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE // White text
        textSize = 110f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY // Light Gray text
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private var score: Float = 0f
    private val rect = RectF()

    fun setScore(newScore: Float) {
        score = newScore.coerceIn(0f, 1f)
        val newColor = calculateColor(score)
        progressPaint.color = newColor
        progressPaint.setShadowLayer(15f, 0f, 0f, newColor)
        invalidate()
    }

    private fun calculateColor(score: Float): Int {
        // 0.0 (Safe) -> Neon Green, 1.0 (Phishing) -> Neon Red
        return when {
            score < 0.4f -> Color.parseColor("#00E676") // Neon Green
            score < 0.7f -> Color.parseColor("#FFD740") // Neon Amber
            else -> Color.parseColor("#FF5252") // Neon Red
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 50f
        val size = min(w, h) - (padding * 2)

        val left = (w - size) / 2
        val top = (h - size) / 2
        rect.set(left, top, left + size, top + size)

        // Draw background arc (135 to 405 degrees)
        canvas.drawArc(rect, 135f, 270f, false, backgroundPaint)

        // Draw progress arc
        val sweepAngle = 270f * score
        canvas.drawArc(rect, 135f, sweepAngle, false, progressPaint)

        // Draw Score Text
        val centerX = w / 2
        val centerY = h / 2
        
        val scoreText = "%.2f".format(score)
        canvas.drawText(scoreText, centerX, centerY, textPaint)
        
        canvas.drawText("Risk Score", centerX, centerY + 60f, labelPaint)
    }
}
