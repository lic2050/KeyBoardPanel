package com.lllic.keyboard

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.FragmentActivity

import com.lllic.keyboard.panel.KeyBoardPanelSwitchHelper

object KeyBoardUtil {

    private var lastSaveKeyboardHeight = 0

    fun showKeyboard(view: View) {
        val imm = view.context.getInputMethodManager()
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
        val applicationContext = view.context.applicationContext
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN || resultCode == InputMethodManager.RESULT_HIDDEN) {
                    toggleSoftInput(applicationContext)
                }
            }
        })
    }

    /**
     * Toggle the soft input display or not.
     */
    fun toggleSoftInput(context: Context) {
        val imm = context.getInputMethodManager()
        imm.toggleSoftInput(0, 0)
    }

    fun hideKeyboard(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) {
            val decorView = activity.window.decorView
            val focusView = decorView.findViewWithTag<View>("keyboardTagView")
            if (focusView == null) {
                view = EditText(activity)
                view.tag = "keyboardTagView"
                (decorView as ViewGroup).addView(view, 0, 0)
            } else {
                view = focusView
            }
            view.requestFocus()
        }
        hideKeyboard(view)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getInputMethodManager()
        val applicationContext = view.context.applicationContext
        imm.hideSoftInputFromWindow(view.windowToken, 0, object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN || resultCode == InputMethodManager.RESULT_SHOWN) {
                    toggleSoftInput(applicationContext)
                }
            }
        })
    }

    fun saveKeyboardHeight(context: Context, keyboardHeight: Int) {
        if (lastSaveKeyboardHeight == keyboardHeight) {
            return
        }
        if (keyboardHeight < 0) {
            return
        }
        lastSaveKeyboardHeight = keyboardHeight
        Log.d("KeyBordUtil", String.format("save keyboard: %d", keyboardHeight))
        KeyBoardSharedPreferences.save(context, keyboardHeight)
    }

    fun attach(activity: FragmentActivity): KeyBoardStateHelper {
        return KeyBoardStateHelper(activity)
    }

    fun getKeyboardHeight(context: Context): Int {
        if (lastSaveKeyboardHeight == 0) {
            lastSaveKeyboardHeight = KeyBoardSharedPreferences[context, dp2px(300f)]
        }
        return lastSaveKeyboardHeight
    }

    fun hideKeyboardPanel(panelSwitchHelper: KeyBoardPanelSwitchHelper) {
        panelSwitchHelper.hideKeyBoardPanel()
    }
}

internal fun Context.getInputMethodManager():InputMethodManager{
    return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}