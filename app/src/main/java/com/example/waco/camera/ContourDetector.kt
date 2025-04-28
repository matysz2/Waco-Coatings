package com.example.waco.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

object ContourDetector {

    fun detectAndFill(bitmap: Bitmap, touchX: Float, touchY: Float, fillColor: Int): Path? {
        val width = bitmap.width
        val height = bitmap.height
        if (touchX < 0 || touchY < 0 || touchX >= width || touchY >= height) return null

        val targetColor = bitmap.getPixel(touchX.toInt(), touchY.toInt())
        if (targetColor == fillColor) return null

        val visited = Array(width) { BooleanArray(height) }
        val path = Path()

        val stack = mutableListOf(PointF(touchX, touchY))

        path.moveTo(touchX, touchY)

        while (stack.isNotEmpty()) {
            val point = stack.removeAt(stack.lastIndex)
            val x = point.x.toInt()
            val y = point.y.toInt()

            if (x < 0 || y < 0 || x >= width || y >= height) continue
            if (visited[x][y]) continue
            if (bitmap.getPixel(x, y) != targetColor) continue

            visited[x][y] = true
            path.lineTo(point.x, point.y)

            stack.add(PointF(point.x + 1, point.y))
            stack.add(PointF(point.x - 1, point.y))
            stack.add(PointF(point.x, point.y + 1))
            stack.add(PointF(point.x, point.y - 1))
        }

        return path
    }
}
