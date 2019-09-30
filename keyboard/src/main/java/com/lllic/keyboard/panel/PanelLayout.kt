package com.lllic.keyboard.panel

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.core.view.isGone


class PanelLayout : FrameLayout, IPanelLayout {

    var enableChangeAnim = true
    private var duration = 300L
    private var interpolator: Interpolator? = AccelerateDecelerateInterpolator()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    /**
     * 当键盘打开时显示占位
     */
    override fun placeHolderOnKeyBoardShow(height: Int) {
        if (isGone) {
            visibility = View.INVISIBLE
        }
        if (isInvisibleChild) {
            for (i in 0 until childCount) {
                getChildAt(i)?.visibility = View.INVISIBLE
            }
        } else {
            visibility = View.INVISIBLE
        }
        interpolator = DecelerateInterpolator()
        duration = 150L
        changePanelHeight(height)
    }

    /**
     * 显示表情面板
     */
    override fun showPanel(height: Int) {
        visibility = View.VISIBLE
        if (isInvisibleChild) {
            for (i in 0 until childCount) {
                getChildAt(i)?.visibility = View.VISIBLE
            }
        } else {
            visibility = View.VISIBLE
        }
        duration = 200L
        interpolator = AccelerateDecelerateInterpolator()
        changePanelHeight(height)
    }

    /**
     * 关闭表情面板
     */
    override fun closePanel(height: Int) {
        changePanelHeight(0)
    }

    private fun changePanelHeight(height: Int) {
        if (enableChangeAnim) {
            val animator = ValueAnimator()
            animator.setIntValues(layoutParams.height, height)
            animator.duration = duration
            animator.interpolator = interpolator
            animator.addUpdateListener {
                layoutParams.height = it.animatedValue as Int
                requestLayout()

            }
            animator.start()
        } else {
            layoutParams.height = height
            requestLayout()
        }
    }

    var isInvisibleChild = true
}