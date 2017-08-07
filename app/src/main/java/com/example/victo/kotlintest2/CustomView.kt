package com.example.victo.kotlintest2

import android.content.Context
import android.graphics.*
import android.support.annotation.FloatRange
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

/**
 * Created by victo on 2017/8/3.
 */
class CustomView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        isClickable = true
    }

    val strokeWidthBig = 20f
    val strokeWidthSmall = 4f
    val mRadiusPoint = 40f
    val colorStrokeGray = Color.GRAY
    val colorStrokeGreen = Color.GREEN
    val colorStrokeWhite = Color.WHITE
    val padding
        get() = paddingLeft


    val bkg by lazy { BitmapFactory.decodeResource(resources, R.drawable.bkg) }

    val paintStrokeGreen by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
            strokeWidth = strokeWidthSmall
            style = Paint.Style.STROKE
            color = colorStrokeGreen
        }
    }
    val paintCircleBkg by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = colorStrokeWhite
        }
    }

    val paintGray by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
            strokeWidth = strokeWidthBig + 4
            style = Paint.Style.STROKE
            color = colorStrokeGray
        }
    }

    val mCenterX
        get() = width / 2f
    val mCenterY
        get() = height / 2f

    val offsetAngle = 180
    var mStartAngle = 0f
    var mSweepAngle = 50f

    var mEndAngle
        get() = (mStartOffsetAngle + mSweepAngle) % 360
        set(value) {
            mSweepAngle = if (value >= 360f) 360f else ((value - mStartOffsetAngle + 360) % 360)
        }

    var mStartOffsetAngle
        get() = (mStartAngle + offsetAngle) % 360
        set(value) {
            val end = mEndAngle
            val diff = value - mStartOffsetAngle
            mStartAngle += diff
            mSweepAngle = (end - mStartOffsetAngle + 360) % 360
            Log.d(TAG, "diff:$diff mStartOffsetAngle:$mStartOffsetAngle mSweepAngle:$mSweepAngle endAngle:$end")
        }
    val TAG = "CustomView"

    val radiusArc
        get() = (width - strokeWidthBig) / 2f - padding

    val startPt
        get() = getPoint(mStartOffsetAngle)
    val endPt
        get() = getPoint(mStartOffsetAngle + mSweepAngle)

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        val rectBkg = Rect(padding, padding, width - padding, height - padding)
        canvas.drawBitmap(bkg, null, rectBkg, null)
        val radius = width / 2f - paddingLeft - strokeWidthBig
        canvas.drawCircle(mCenterX, mCenterY, radius, paintCircleBkg)
        canvas.drawArc(rectBkg.toFloat.apply { inset(strokeWidthBig / 2f, strokeWidthBig / 2f) },
                mStartOffsetAngle % 360, -(360f - mSweepAngle), false, paintGray)
        val pt0 = startPt
        canvas.drawCircle(pt0.x, pt0.y, mRadiusPoint, paintCircleBkg)
        canvas.drawCircle(pt0.x, pt0.y, mRadiusPoint, paintStrokeGreen)
        val pt1 = endPt
        canvas.drawCircle(pt1.x, pt1.y, mRadiusPoint, paintCircleBkg)
        canvas.drawCircle(pt1.x, pt1.y, mRadiusPoint, paintStrokeGreen)
        //TODO 绘制文字。。。
    }

    fun getPoint(angle: Float) = PointF(mCenterX, mCenterY).apply {
        val r = radiusArc
        val rad = Math.toRadians(angle.toDouble())
        offset(r * Math.cos(rad).toFloat(), r * Math.sin(rad).toFloat())
    }

    enum class Stat {
        start, end, none
    }

    var stat = Stat.none

    fun isInCircle(pt0: PointF, pt1: PointF, r: Float = mRadiusPoint) = (pt0.x - pt1.x).let { it * it } + (pt0.y - pt1.y).let { it * it } <= r * r

    var toast: Toast? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return super.onTouchEvent(event)
        val x = event.x
        val y = event.y
        val p = PointF(x, y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                stat = if (isInCircle(p, endPt)) Stat.end else if (isInCircle(p, startPt)) Stat.start else Stat.none
                if (stat != Stat.none) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (stat != Stat.none) {
                    stat = Stat.none
                    return true
                } else {
                    return super.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (stat != Stat.none) {
                    val absAngle = getAbsAngle(p) % 360
                    if (stat == Stat.start) {
                        if (startPointMovable) mStartOffsetAngle = absAngle
                    } else {
                        if (enableLoop) {
                            mEndAngle = absAngle
                        } else {
                            val diff = absAngle - mStartOffsetAngle
                            //val sweep = absAngle - mStartOffsetAngle
                            val limit = 40
                            if (mSweepAngle <= limit && diff < 0) {
                                mSweepAngle = 0.000001f
                            } else if (mSweepAngle >= 360 - limit && diff > 0) {
                                mSweepAngle = 360f
                            } else {
                                mEndAngle = absAngle
                            }
                        }
                    }

                    toast?.cancel()
                    toast = Toast.makeText(context, "value:${value.toInt()} percentage:${(value / maxValue * 100).toInt()}%", Toast.LENGTH_LONG).apply { show() }
                    postInvalidate()
                    return true
                } else {
                    return super.onTouchEvent(event)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getAbsAngle(p: PointF): Float {
        val dx = (p.x - mCenterX).toDouble()
        val dy = (p.y - mCenterY).toDouble()

        val toFloat = Math.toDegrees(Math.atan(Math.abs(dy / dx))).toFloat()
        if (dx >= 0 && dy >= 0) {
            return toFloat
        } else if (dx >= 0 && dy <= 0) {
            return 360 - toFloat
        } else if (dx <= 0 && dy >= 0) {
            return 180 - toFloat
        } else {
            return 180 + toFloat
        }
    }

    val Rect.toFloat: RectF
        get() = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())


    var maxValue = 360
    var value
        get() = mSweepAngle / 360f * maxValue
        set(value) {
            mSweepAngle = value / maxValue * 360f % maxValue
        }

    var startPointMovable = false

    var proportion: Float
        get() = value / maxValue
        @FloatRange(from = 0.0, to = 1.0, toInclusive = true)
        set(v) {
            value = v * maxValue % maxValue
        }

    var enableLoop = false
}
