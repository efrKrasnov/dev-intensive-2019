package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.content.res.TypedArray
import android.graphics.*
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.*
import ru.skillbranch.devintensive.R


open class AvatarImageView @JvmOverloads constructor(context: Context, @Nullable attrs: AttributeSet? = null) :
    ImageView(context, attrs) {

    private var mBitmapShader: Shader? = null
    private var mShaderMatrix: Matrix

    private var mBitmapDrawBounds: RectF
    private var mStrokeBounds: RectF
    private var mBitmap: Bitmap? = null

    private var mBitmapPaint: Paint
    private var mStrokePaint: Paint
    private var mPressedPaint: Paint

    private var mInitialized: Boolean = false
    private var mPressed: Boolean = false
    private var mHighlightEnable: Boolean = false

    private var initialsStr: String = ""

    companion object    {
        private const val DEF_PRESS_HIGHLIGHT_COLOR = 0x32000000
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2f
    }

    init {
        var strokeColor = Color.TRANSPARENT
        var strokeWidth = 0f
        var highlightEnable = true
        var highlightColor = DEF_PRESS_HIGHLIGHT_COLOR

        if(attrs != null)   {
            val a:TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView, 0, 0)

            strokeColor = a.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            strokeWidth = a.getDimensionPixelSize(R.styleable.AvatarImageView_aiv_borderWidth, DEFAULT_BORDER_WIDTH.toInt()).toFloat()
            highlightEnable = false
            highlightColor = DEF_PRESS_HIGHLIGHT_COLOR

            a.recycle()
        }

        mShaderMatrix = Matrix()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()

        mStrokePaint.color = strokeColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = strokeWidth

        mPressedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPressedPaint.color = highlightColor
        mPressedPaint.style = Paint.Style.FILL

        mHighlightEnable = highlightEnable
        mInitialized = true

        setupBitmap()
    }

    fun setInitials(str:String)    {
        initialsStr = str

        val width = this.layoutParams.width
        val height = this.layoutParams.height

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paintText = Paint()

        canvas.drawColor(Color.BLACK)

        paintText.color = Color.WHITE
        paintText.textSize = height / 2f
        paintText.textAlign = Paint.Align.CENTER

        val xPos = canvas.width / 2
        val yPos = (canvas.height / 2 - (paintText.descent() + paintText.ascent()) / 2)
        canvas.drawText(initialsStr, xPos.toFloat(), yPos, paintText)

        setImageDrawable(BitmapDrawable(resources, output))
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = mStrokePaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()

    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        drawBitmap(canvas)
        drawStroke(canvas)
        drawHighlight(canvas)
    }

    fun isHighlightEnable(): Boolean = mHighlightEnable

    fun setHighlightEnable(enable: Boolean )  {
        mHighlightEnable = enable
        invalidate()
    }

    @ColorInt
    fun getHighlightColor():Int = mPressedPaint.color

    fun setHighlightColor(@ColorInt color: Int)  {
        mPressedPaint.color = color
        invalidate()
    }

    @ColorInt
    fun getStrokeColor(): Int {
        return mStrokePaint.color
    }

    fun setStrokeColor(@ColorInt color: Int) {
        mStrokePaint.color = color
        invalidate()
    }

    @Dimension
    fun getStrokeWidth(): Float {
        return mStrokePaint.strokeWidth
    }

    fun setStrokeWidth(@Dimension width: Float) {
        mStrokePaint.strokeWidth = width
        invalidate()
    }

    private fun drawHighlight(canvas: Canvas) {
        if(mHighlightEnable && mPressed)    {
            canvas.drawOval(mBitmapDrawBounds, mPressedPaint)
        }
    }

    private fun drawStroke(canvas: Canvas) {
        if(mStrokePaint.strokeWidth > 0f)   {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    @Dimension
    fun getBorderWidth(): Int {
        return getStrokeWidth().toInt()
    }

    fun setBorderWidth(@Dimension width: Int) {
        setStrokeWidth(width.toFloat())
    }

    fun getBorderColor():Int    {
        return getStrokeColor()
    }

    fun setBorderColor(hex:String)  {
        setStrokeColor(Color.parseColor(hex))
    }

    fun setBorderColor(@ColorRes colorId: Int)  {
        setStrokeColor(resources.getColor(colorId))
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth: Float = width - paddingLeft.toFloat() - paddingRight.toFloat()
        val contentHeight: Float = height - paddingTop.toFloat() - paddingBottom.toFloat()

        var left: Float = paddingLeft.toFloat()
        var top: Float = paddingTop.toFloat()

        if (contentWidth > contentHeight)   {
            left += (contentWidth - contentHeight) / 2f
        }
        else    {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter:Float = Math.min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    fun setupBitmap() {
        if(!mInitialized)   {
            return
        }

        mBitmap = getBitmapFromDrawable(drawable)
        if(mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if(mBitmap == null)
            return

        val dx: Float
        val dy: Float

        val scale: Float

        if(mBitmap!!.width < mBitmap!!.height )  {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width.toFloat()
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - (mBitmap!!.height * scale / 2f) + (mBitmapDrawBounds.width() / 2f)
        }
        else    {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height.toFloat()
            dx = mBitmapDrawBounds.left - (mBitmap!!.width * scale / 2f) + (mBitmapDrawBounds.width() / 2f)
            dy = mBitmapDrawBounds.top
        }

        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    open fun getBitmapFromDrawable(drawable: Drawable?): Bitmap?  {
        if(drawable==null)  {
            return null
        }

        if(drawable is BitmapDrawable)  {
            drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }



}
