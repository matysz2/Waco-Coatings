package com.example.waco.camera

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TouchOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val points = mutableListOf<PointF>()
    private var r = 255
    private var g = 0
    private var b = 0

    fun setPaintColor(red: Int, green: Int, blue: Int) {
        r = red
        g = green
        b = blue
    }

    fun getPoints(): List<PointF> = points

    fun clearPoints() {
        points.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    points.add(PointF(event.getX(i), event.getY(i)))
                }
                invalidate()
            }
        }
        return true
    }
}
