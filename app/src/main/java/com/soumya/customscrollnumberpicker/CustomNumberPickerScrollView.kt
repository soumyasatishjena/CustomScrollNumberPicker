package com.soumya.customscrollnumberpicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
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

class CustomNumberPickerScrollView(
    context: Context?,
    attrs: AttributeSet?
) : ScrollView(context, attrs) {

    private var context: Context? = null
    private var views: LinearLayout? = null

    var offset = Utils.OFF_SET_DEFAULT
    private var displayItemCount = OFF_SET_INITIAL
    private var selectedIndex = Utils.OFF_SET_DEFAULT

    private var onScrollViewListener: OnScrollViewListener? = null
    private var scrollerTask: Runnable? = null
    private var scrollDirection = -1

    private var items: MutableList<String>? = null

    private val delay : Long = 50L
    private var initialY = OFF_SET_INITIAL
    private var itemHeight = OFF_SET_INITIAL

    private var viewWidth = OFF_SET_INITIAL
    private var selectedAreaBorder: IntArray? = null
    private var paint: Paint? = null

    init {
        this.context = context
        init()
    }

    private fun init() {
        views =  LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        this.apply {
            isVerticalScrollBarEnabled = false
            addView(views)
        }

        scrollerTask = Runnable {
            val newY = scrollY
            if (initialY - newY == OFF_SET_INITIAL) {
                val remainder = initialY % itemHeight
                val divided = initialY / itemHeight
                if (remainder == OFF_SET_INITIAL) {
                    selectedIndex = divided + offset
                    onSelectedCallBack()
                } else {
                    if (remainder > itemHeight / 2) {
                        post {
                            smoothScrollTo(
                                OFF_SET_INITIAL,
                                initialY - remainder + itemHeight
                            )
                            selectedIndex = divided + offset + 1
                            onSelectedCallBack()
                        }
                    } else {
                        post {
                            smoothScrollTo(OFF_SET_INITIAL, initialY - remainder)
                            selectedIndex = divided + offset
                            onSelectedCallBack()
                        }
                    }
                }
            } else {
                initialY = scrollY
                postDelayed(scrollerTask, delay)
            }
        }
    }

    private fun initData() {
        displayItemCount = offset * 2 + 1
        for (item in items!!) {
            views?.addView(createView(item))
        }
        refreshItemView(OFF_SET_INITIAL)
    }

    private fun createView(item: String): TextView {
        val textView = TextView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setSingleLine(true)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            text = item
            gravity = Gravity.CENTER
            setPadding(
                Utils.dip2px(this.context!!, 19f), Utils.dip2px(this.context!!, 19f),
                Utils.dip2px(this.context!!, 19f), Utils.dip2px(this.context!!, 19f)
            )
        }

        if (OFF_SET_INITIAL == itemHeight) {
            itemHeight = Utils.getViewMeasuredHeight(textView)
            views?.setLayoutParams(
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    itemHeight * displayItemCount
                )
            )
            val linearLayoutParam = this.layoutParams as LinearLayout.LayoutParams
            setLayoutParams(
                LinearLayout.LayoutParams(
                    linearLayoutParam.width,
                    itemHeight * displayItemCount
                )
            )
        }
        return textView
    }

    private fun refreshItemView(y: Int) {
        var position = y / itemHeight + offset
        val remainder = y % itemHeight
        val divided = y / itemHeight
        if (remainder == OFF_SET_INITIAL) {
            position = divided + offset
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1
            }
        }
        val childSize = views?.childCount
        for (i in OFF_SET_INITIAL until childSize!!) {
            val itemTextView = views?.getChildAt(i) as TextView
            if (position == i) {
                itemTextView.apply {
                    setTextColor( ContextCompat.getColor(
                        context!!,
                        R.color.scroll_picker_selected_color
                    ))
                    setTypeface(null, Typeface.BOLD)
                }
            } else {
                itemTextView.apply {
                    setTextColor( ContextCompat.getColor(
                        context!!,
                        R.color.scroll_picker_unselected_color
                    ))
                    setTypeface(null, Typeface.NORMAL)
                }
            }
        }
    }

    override fun setBackground(background: Drawable?) {
        if (viewWidth == OFF_SET_INITIAL) {
            viewWidth = Utils.getScreenWidth((context as Activity?))
        }
        if (paint == null) {
            paint = Paint().apply {
                color = ContextCompat.getColor(
                    context!!,
                    R.color.scroll_picker_unselected_color
                )
                strokeWidth = Utils.dip2px(context!!, 2f).toFloat()
            }
        }
        val background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                paint?.let {
                    canvas.drawLine(
                        viewWidth * (1 / 6f), obtainSelectedAreaBorder()[OFF_SET_INITIAL].toFloat(),
                        viewWidth * 5 / 6f, obtainSelectedAreaBorder()[OFF_SET_INITIAL].toFloat(), it
                    )

                    canvas.drawLine(
                        viewWidth * (1 / 6f), obtainSelectedAreaBorder()[OFF_SET_DEFAULT].toFloat(),
                        viewWidth * 5 / 6f, obtainSelectedAreaBorder()[OFF_SET_DEFAULT].toFloat(), it
                    )
                }

            }

            override fun setAlpha(alpha: Int) {
                //do nothing
            }

            override fun setColorFilter(cf: ColorFilter?) {
                //do nothing
            }

            @SuppressLint("WrongConstant")
            override fun getOpacity(): Int {
                return OFF_SET_INITIAL
            }
        }
        super.setBackground(background)
    }

    private fun obtainSelectedAreaBorder(): IntArray {
        if (selectedAreaBorder == null) {
            selectedAreaBorder = IntArray(2)
            selectedAreaBorder!![OFF_SET_INITIAL] = itemHeight * offset
            selectedAreaBorder!![OFF_SET_DEFAULT] = itemHeight * (offset + OFF_SET_DEFAULT)
        }
        return selectedAreaBorder!!
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        viewWidth = w
        setBackground(null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP ||
            ev.action == MotionEvent.ACTION_DOWN
        ) {
            initialY = scrollY
            postDelayed(scrollerTask, delay)
        }
        return super.onTouchEvent(ev)
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onScrollChanged(l: Int, t: Int, oldL: Int, oldT: Int) {
        super.onScrollChanged(l, t, oldL, oldT)
        refreshItemView(t)
        scrollDirection =
            if (t > oldT) Utils.SCROLL_DIRECTION_DOWN else Utils.SCROLL_DIRECTION_UP
    }

    private fun onSelectedCallBack() {
        if (onScrollViewListener != null) {
            onScrollViewListener!!.onSelected(selectedIndex, items!![selectedIndex])
        }
    }

    fun setOnScrollViewListener(onScrollViewListener: OnScrollViewListener?) {
        this.onScrollViewListener = onScrollViewListener
    }

    fun setSelection(position: Int) {
        selectedIndex = position + offset
        post { smoothScrollTo(OFF_SET_INITIAL, position * itemHeight) }
    }

    fun setItems(list: List<String>?) {
        if (items == null) {
            items = ArrayList()
        }

        items?.let { it ->
            it.apply {
                clear()
                list?.let { it }?.let { list -> addAll(list) }

                for (i in OFF_SET_INITIAL until offset) {
                    add(OFF_SET_INITIAL, "")
                    add("")
                }
            }
        }
        initData()
    }


    open class OnScrollViewListener {
        open fun onSelected(selectedIndex: Int, item: String) {}
    }

}
