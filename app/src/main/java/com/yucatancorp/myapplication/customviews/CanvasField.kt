package com.yucatancorp.myapplication.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream

class CanvasField(context: Context, attr: AttributeSet) : View(context, attr) {

    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private val path: Path = Path()
    private val bitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
    private val paint: Paint = Paint()
    private var previousX: Float = 0.0f
    private var previousY: Float = 0.0f
    private val touchTolerance = 4f
    private val lineThickness = 4f
    var drowned: Boolean = false

    init {
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = Color.argb(255,155,55,45)
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = lineThickness
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, if(h>0) h else (this.parent as View).height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE)
        canvas?.drawBitmap(bitmap, 0f, 0f, bitmapPaint)
        canvas?.drawPath(path, paint)
        this.parent.requestDisallowInterceptTouchEvent(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val x = event.x
        val y = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> touchMove(x, y)
            MotionEvent.ACTION_UP -> touchUp()
        }

        invalidate()
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        previousX = x
        previousY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = kotlin.math.abs(x - previousX)
        val dy = kotlin.math.abs(y - previousY)

        if (dx >= touchTolerance || dy >= touchTolerance) {/*
            path.quadTo(previousX, previousY, (x + previousX / 2), (y + previousY) / 2)*/
            path.quadTo(previousX, previousY, (x + previousX) / 2, (y + previousY) / 2)
            previousX = x
            previousY = y
        }
    }

    private fun touchUp() {
        if (!path.isEmpty) {
            path.lineTo(previousX, previousY)
            canvas.drawPath(path, paint)
        } else {
            canvas.drawPoint(previousX, previousY, paint)
        }
        path.reset()
        drowned = true
    }

    private fun clear() {
        canvas.drawColor(Color.WHITE)
        drowned = false
        invalidate()
    }

    private fun getBitmap(): Bitmap {
        val v = this as View
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    private fun getByteArray(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val scaleBitmap: Bitmap = Bitmap.createScaledBitmap(getBitmap(), 512, 256 , false)
        scaleBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun getBase64String(): String {
        return Base64.encodeToString(getByteArray(), Base64.NO_WRAP)
    }

    fun drawSign(base64String: String){
        val decodedString = Base64.decode(base64String, Base64.NO_WRAP)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        val scaleBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, this.width, this.height, false)
        canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
        drowned = true
    }

}