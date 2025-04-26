package com.example.waco.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class TransparentOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = android.graphics.Color.WHITE
        strokeWidth = 5f
    }

    var rect = Rect(300, 500, 800, 1000)

    private var lastX = 0f
    private var lastY = 0f
    private var dragging = false
    private var resizing = false
    private val resizeThreshold = 40

    fun setBorderColor(color: Int) {
        paint.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rect, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isNearCorner(event.x, event.y)) {
                    resizing = true
                } else if (rect.contains(event.x.toInt(), event.y.toInt())) {
                    dragging = true
                }
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - lastX).toInt()
                val dy = (event.y - lastY).toInt()

                if (resizing) {
                    rect.right += dx
                    rect.bottom += dy
                    invalidate()
                } else if (dragging) {
                    rect.offset(dx, dy)
                    invalidate()
                }

                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                dragging = false
                resizing = false
            }
        }
        return true
    }

    private fun isNearCorner(x: Float, y: Float): Boolean {
        return abs(x - rect.right) < resizeThreshold && abs(y - rect.bottom) < resizeThreshold
    }
}
