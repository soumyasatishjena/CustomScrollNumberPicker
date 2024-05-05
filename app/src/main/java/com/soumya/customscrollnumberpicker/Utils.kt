package com.soumya.customscrollnumberpicker

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets

object Utils {

    private const val MAX_INCH_VALUE = 11
    private const val MAX_FEET_DEFAULT = 8
    internal const val FEET_TEXT = " feet, "
    internal const val INCH = " inch"
    internal const val OFF_SET_INITIAL = 0
    internal const val OFF_SET_DEFAULT = 1
    internal const val SCROLL_DIRECTION_UP = 0
    internal const val SCROLL_DIRECTION_DOWN = 1


    internal fun dip2px(context: Context?, dpValue: Float): Int =
        (dpValue * (context?.resources?.displayMetrics?.density!!) + 0.5f).toInt()


    internal fun getViewMeasuredHeight(view: View): Int = view.apply {
        measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        )
    }.measuredHeight


    internal fun getScreenWidth(activity: Activity?): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity?.windowManager?.currentWindowMetrics
            val insets = windowMetrics?.getWindowInsets()
                ?.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics?.bounds?.width()!! - insets?.left!! - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }

    internal fun setNumberPickerList(): Pair<List<String>, List<String>> {
        val feetList = arrayListOf<String>()
        for (i in OFF_SET_DEFAULT..MAX_FEET_DEFAULT) {
            feetList.add("$i")
        }

        val inchList = arrayListOf<String>()

        for (i in 0..MAX_INCH_VALUE) {
            inchList.add("$i")
        }

        return Pair(feetList, inchList)
    }
}
