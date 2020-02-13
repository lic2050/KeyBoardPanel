package com.lllic.keyboard

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.lllic.keyboard.panel.IPanelLayout
import com.lllic.keyboard.panel.KeyBoardPanelSwitchHelper

class KeyBoardStateHelper(activity: FragmentActivity) {

    private val popupWindow: PopupWindow = PopupWindow()
    private val layoutListener: ViewTreeObserver.OnGlobalLayoutListener
    private var windowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null
    private val screenRealHeight: Int
    private var keybordShow = false
    private var displayHeight: Int = 0
    private var navBarHeight: Int = 0
    private var isNavBarShow = false
    private var keyboardHeight = KeyBoardUtil.getKeyboardHeight(activity)
    private var onKeyBoardShowListener: ((show: Boolean, keyBoardHeight: Int) -> Unit)? = null
    private var onNavBarChangeListener: ((show: Boolean, navBarHeight: Int) -> Unit)? = null
    var keyBoardPanelSwitchHelper: KeyBoardPanelSwitchHelper? = null

    init {
        popupWindow.contentView = View(activity)
        popupWindow.width = 0
        popupWindow.height = FrameLayout.LayoutParams.MATCH_PARENT
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.isFocusable = false
        popupWindow.isOutsideTouchable = false
        popupWindow.isClippingEnabled = true
        popupWindow.setBackgroundDrawable(null)
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        try {
            val mLayoutInScreen = PopupWindow::class.java.getDeclaredField("mLayoutInScreen")
            mLayoutInScreen.isAccessible = true
            mLayoutInScreen.set(popupWindow, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        screenRealHeight = activity.screenRealHeight
        layoutListener = ViewTreeObserver.OnGlobalLayoutListener { onStateChange() }
        popupWindow.contentView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            windowFocusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener {focus ->
                if (!focus){
                    keyBoardPanelSwitchHelper?.hideKeyBoardPanel()
                }
            }
        }
        val decorView = activity.window?.decorView
        activity.lifecycle.addObserver(object : DestoryObserver {
            override fun onDestory() {
                popupWindow.contentView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    decorView?.viewTreeObserver?.removeOnWindowFocusChangeListener(windowFocusChangeListener)
                }
                popupWindow.dismiss()
            }
        })
        decorView?.post {
            if (activity.isFinishing || activity.isDestroyed) {
                return@post
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                decorView.viewTreeObserver.addOnWindowFocusChangeListener(windowFocusChangeListener)
            }
            popupWindow.showAtLocation(decorView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    private fun onStateChange() {
        //部分机型的虚拟导航栏可以动态显示隐藏，无法准确获取导航栏是否显示
        //popupWindow不会延伸到导航栏，并且会响应系统UI变化自动调整防止被导航栏遮挡，
        //popupWindow的contentView.height也不会受键盘显示隐藏的影响所以
        //导航栏高度 = 屏幕高度-popupWindow.contentView.height
        val contentHeight = popupWindow.contentView.height
        val rect = Rect()
        popupWindow.contentView.getWindowVisibleDisplayFrame(rect)
        //可见的content内容区域，不包含键盘

        val intArray = IntArray(2)
        popupWindow.contentView.getLocationOnScreen(intArray)
        val newDisplayHeight = rect.bottom - intArray[1]
        if (displayHeight == newDisplayHeight) {
            //高度没有发生变化
            return
        }
        displayHeight = newDisplayHeight
        //计算虚拟导航栏高度
        val newNavHeight = screenRealHeight - contentHeight
        if (navBarHeight != newNavHeight) {
            navBarHeight = newNavHeight
            isNavBarShow = navBarHeight > 0
            onNavBarChangeListener?.invoke(isNavBarShow, navBarHeight)
        }
        //计算键盘高度
        val newKeyBoardHeight = contentHeight - displayHeight
        val newKeyboardShow = newKeyBoardHeight > 0
        if (newKeyboardShow) {
            if (keyboardHeight != newKeyBoardHeight) {
                //键盘高度发生变化
                keyboardHeight = newKeyBoardHeight
                KeyBoardUtil.saveKeyboardHeight(
                    popupWindow.contentView.context,
                    keyboardHeight
                )
                keyBoardPanelSwitchHelper?.onKeyBoardHeightChange(keyboardHeight)
            }
            if (!keybordShow) {
                //键盘显示
                keybordShow = true
                keyBoardPanelSwitchHelper?.onKeyBoardShow(keyboardHeight)
                onKeyBoardShowListener?.invoke(true, keyboardHeight)
            }
        } else if (keybordShow) {
            //键盘隐藏
            keybordShow = false
            keyBoardPanelSwitchHelper?.onKeyBoardClose(keyboardHeight)
            onKeyBoardShowListener?.invoke(false, keyboardHeight)
        }
    }

    /**
     * 键盘显示隐藏监听
     */
    fun onKeyBoardShow(listener: (show: Boolean, keyBoardHeight: Int) -> Unit): KeyBoardStateHelper {
        onKeyBoardShowListener = listener
        return this
    }

    /**
     * 导航栏显示隐藏监听
     */
    fun onNavBarChange(listener: (show: Boolean, navBarHeight: Int) -> Unit): KeyBoardStateHelper {
        onNavBarChangeListener = listener
        return this
    }

    fun bindPanel(panelView: IPanelLayout, focusEditText: EditText): KeyBoardPanelSwitchHelper {
        keyBoardPanelSwitchHelper =
            KeyBoardPanelSwitchHelper(panelView, focusEditText)
        return keyBoardPanelSwitchHelper!!
    }
}

/**
 * 屏幕高度
 * 不包含虚拟导航栏
 * 某些手机的虚拟导航栏可以动态显示隐藏，会导致获取出错，取决于进入页面时是否显示虚拟导航栏
 */
internal val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels

/**
 * 屏幕高度
 * 包含虚拟导航栏
 */
internal val Context.screenRealHeight: Int
    get() {
        var dpi = 0
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val dm = DisplayMetrics()

        try {
            val c = Class.forName("android.view.Display")
            val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
            method.invoke(display, dm)
            dpi = dm.heightPixels
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dpi == 0) {
                dpi = screenHeight
            }
        }
        return dpi
    }

internal fun dp2px(dpValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}