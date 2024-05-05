package com.soumya.customscrollnumberpicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.soumya.customscrollnumberpicker.Utils.OFF_SET_DEFAULT
import com.soumya.customscrollnumberpicker.Utils.OFF_SET_INITIAL
import com.soumya.customscrollnumberpicker.Utils.dip2px

class CustomNumberPickerScrollView(
    context: Context?,
    attrs: AttributeSet?
) : ScrollView(context, attrs) {

    private var onScrollViewListener: OnScrollViewListener? = null
    private var scrollerTask: Runnable? = null
    val properties = CustomNumberPickerProperties()

    init {
        properties.context = context
        init()
    }

    private fun init() {
        properties.views = LinearLayout(properties.context).apply {
            orientation = LinearLayout.VERTICAL
        }
        this.apply {
            isVerticalScrollBarEnabled = false
            addView(properties.views)
        }

        scrollerTask = Runnable {
            val newY = scrollY
            properties.let {
                val height = it.itemHeight
                val initialY = it.initialY
                if (initialY - newY == OFF_SET_INITIAL) {
                    val remainder = initialY % height
                    val divided = initialY / height
                    if (remainder == OFF_SET_INITIAL) {
                        it.selectedIndex = divided + it.offset
                        onSelectedCallBack()
                    } else {
                        if (remainder > height / 2) {
                            post {
                                smoothScrollTo(
                                    OFF_SET_INITIAL,
                                    initialY - remainder + height
                                )
                                it.selectedIndex = divided + it.offset + 1
                                onSelectedCallBack()
                            }
                        } else {
                            post {
                                smoothScrollTo(OFF_SET_INITIAL, initialY - remainder)
                                it.selectedIndex = divided + it.offset
                                onSelectedCallBack()
                            }
                        }
                    }
                } else {
                    it.initialY = scrollY
                    postDelayed(scrollerTask, properties.delay)
                }
            }
        }
    }

    private fun initData() {
        properties.displayItemCount = properties.offset * 2 + 1
        for (item in properties.items!!) {
            properties.views?.addView(createView(item))
        }
        refreshItemView(OFF_SET_INITIAL)
    }

    private fun createView(item: String): TextView {
        val textView = TextView(properties.context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setSingleLine(true)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            text = item
            gravity = Gravity.CENTER
            properties.context?.let {
                setPadding(
                    dip2px(it, 19f), dip2px(it, 19f),
                    dip2px(it, 19f), dip2px(it, 19f)
                )
            }
        }

        if (OFF_SET_INITIAL == properties.itemHeight) {
            properties.itemHeight = Utils.getViewMeasuredHeight(textView)
            properties.views?.setLayoutParams(
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    properties.itemHeight * properties.displayItemCount
                )
            )
            val linearLayoutParam = this.layoutParams as LinearLayout.LayoutParams
            setLayoutParams(
                LinearLayout.LayoutParams(
                    linearLayoutParam.width,
                    properties.itemHeight * properties.displayItemCount
                )
            )
        }
        return textView
    }

    private fun refreshItemView(y: Int) {
        var position = y / properties.itemHeight + properties.offset
        val remainder = y % properties.itemHeight
        val divided = y / properties.itemHeight
        if (remainder == OFF_SET_INITIAL) {
            position = divided + properties.offset
        } else {
            if (remainder > properties.itemHeight / 2) {
                position = divided + properties.offset + 1
            }
        }
        val childSize = properties.views?.childCount
        for (i in OFF_SET_INITIAL until childSize!!) {
            val itemTextView = properties.views?.getChildAt(i) as TextView
            if (position == i) {
                itemTextView.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            properties.context!!,
                            R.color.scroll_picker_selected_color
                        )
                    )
                    setTypeface(null, Typeface.BOLD)
                }
            } else {
                itemTextView.apply {
                    setTextColor(
                        ContextCompat.getColor(
                            properties.context!!,
                            R.color.scroll_picker_unselected_color
                        )
                    )
                    setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }

    override fun setBackground(background: Drawable?) {
        if (properties.viewWidth == OFF_SET_INITIAL) {
            properties.viewWidth = Utils.getScreenWidth((properties.context as Activity?))
        }
        if (properties.paint == null) {
            properties.paint = Paint().apply {
                color = ContextCompat.getColor(
                    properties.context!!,
                    R.color.scroll_picker_unselected_color
                )
                strokeWidth = dip2px(properties.context!!, 2f).toFloat()
            }
        }
        val pickerBackground = object : Drawable() {
            override fun draw(canvas: Canvas) {
                properties.paint?.let {
                    canvas.drawLine(
                        properties.viewWidth * (1 / 6f),
                        obtainSelectedAreaBorder()[OFF_SET_INITIAL].toFloat(),
                        properties.viewWidth * 5 / 6f,
                        obtainSelectedAreaBorder()[OFF_SET_INITIAL].toFloat(),
                        it
                    )

                    canvas.drawLine(
                        properties.viewWidth * (1 / 6f),
                        obtainSelectedAreaBorder()[OFF_SET_DEFAULT].toFloat(),
                        properties.viewWidth * 5 / 6f,
                        obtainSelectedAreaBorder()[OFF_SET_DEFAULT].toFloat(),
                        it
                    )
                }

            }

            override fun setAlpha(alpha: Int) {
                //do nothing
            }

            override fun setColorFilter(cf: ColorFilter?) {
                //do nothing
            }


           @Deprecated("Deprecated in Java",
               ReplaceWith("PixelFormat.UNKNOWN", "android.graphics.PixelFormat")
           )
           override fun getOpacity(): Int {
                return PixelFormat.UNKNOWN
            }
        }
        super.setBackground(pickerBackground)
    }

    private fun obtainSelectedAreaBorder(): IntArray {
        properties.let {
            if (it.selectedAreaBorder == null) {
                it.selectedAreaBorder = IntArray(2)
                it.selectedAreaBorder!![OFF_SET_INITIAL] = it.itemHeight * it.offset
                it.selectedAreaBorder!![OFF_SET_DEFAULT] =
                    it.itemHeight * (it.offset + OFF_SET_DEFAULT)
            }
        }
        return properties.selectedAreaBorder!!
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        properties.viewWidth = w
        setBackground(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP ||
            ev.action == MotionEvent.ACTION_DOWN
        ) {
            properties.initialY = scrollY
            postDelayed(scrollerTask, properties.delay)
        }
        return super.onTouchEvent(ev)
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onScrollChanged(l: Int, t: Int, oldL: Int, oldT: Int) {
        super.onScrollChanged(l, t, oldL, oldT)
        refreshItemView(t)
        properties.scrollDirection =
            if (t > oldT) Utils.SCROLL_DIRECTION_DOWN else Utils.SCROLL_DIRECTION_UP
    }

    private fun onSelectedCallBack() =
        onScrollViewListener?.let {
            properties.let {
                onScrollViewListener!!.onSelected(it.selectedIndex, it.items!![it.selectedIndex])
            }
        }

    fun setOnScrollViewListener(onScrollViewListener: OnScrollViewListener?) {
        this.onScrollViewListener = onScrollViewListener
    }

    fun setSelection(position: Int) {
        properties.selectedIndex = position + properties.offset
        post { smoothScrollTo(OFF_SET_INITIAL, position * properties.itemHeight) }
    }

    fun setItems(list: List<String>?) {
        properties.let {
            it.items = ArrayList()
        }

        properties.items?.let {
            it.apply {
                clear()

                list?.let { list -> addAll(list) }

                for (i in OFF_SET_INITIAL until properties.offset) {
                    add(OFF_SET_INITIAL, "")
                    add("")
                }
            }
        }
        initData()
    }


    fun interface OnScrollViewListener {
        fun onSelected(selectedIndex: Int, item: String)
    }

}
