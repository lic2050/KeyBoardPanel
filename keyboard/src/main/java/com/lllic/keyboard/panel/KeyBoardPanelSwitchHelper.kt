package com.lllic.keyboard.panel

import android.view.View
import android.widget.EditText
import com.lllic.keyboard.KeyBoardUtil

class KeyBoardPanelSwitchHelper(val panelView: IPanelLayout, val editText: EditText) {

    /**
     * 键盘和表情面板切换监听
     */
    private var onKeyBoardPanelSwitchListener: ((isKeyBoard: Boolean, height: Int, switchView: View?) -> Unit)? =
        null
    /**
     * 键盘或表情面板显示隐藏监听
     */
    private var onKeyBoardPanelShowListener: ((show: Boolean, height: Int) -> Unit)? = null

    var reactKeyboardChange = true

    fun onKeyBoardPanelShow(listener: (show: Boolean, height: Int) -> Unit): KeyBoardPanelSwitchHelper {
        onKeyBoardPanelShowListener = listener
        return this
    }

    fun onKeyBoardPanelShowDelay(listener: (show: Boolean, height: Int) -> Unit): KeyBoardPanelSwitchHelper {
        panelView.setOnKeyBoardPanelShowDelayListener(listener)
        return this
    }

    fun onKeyBoardPanelSwitch(listener: (isKeyBoard: Boolean, height: Int, switchView: View?) -> Unit): KeyBoardPanelSwitchHelper {
        onKeyBoardPanelSwitchListener = listener
        return this
    }

    var keyBoardPanelShowing = false
    private var panelShowing = false
    private var needShowPanel = false

    internal fun onKeyBoardShow(height: Int) {
        if (!reactKeyboardChange) {
            return
        }
        if (!keyBoardPanelShowing) {
            panelView.placeHolderOnKeyBoardShow(height, true)
            onKeyBoardPanelShowListener?.invoke(true, height)
        } else {
            panelView.placeHolderOnKeyBoardShow(height, false)
        }
        onKeyBoardPanelSwitchListener?.invoke(true, height, clickSwitchBotton)
        clickSwitchBotton = null
        panelShowing = false
        keyBoardPanelShowing = true
    }

    internal fun onKeyBoardHeightChange(height: Int) {
        if (!reactKeyboardChange){
            return
        }
        panelView.placeHolderOnKeyBoardShow(height, false)
    }

    fun hideKeyBoardPanel() {
        if (panelShowing) {
            panelView.closePanel(0, true)
            keyBoardPanelShowing = false
            panelShowing = false
            onKeyBoardPanelShowListener?.invoke(
                false,
                KeyBoardUtil.getKeyboardHeight(editText.context)
            )
        } else {
            KeyBoardUtil.hideKeyboard(editText)
        }
    }

    internal fun onKeyBoardClose(height: Int) {
        if (!reactKeyboardChange) {
            return
        }
        if (needShowPanel) {
            showPanelLayout(height)
        } else {
            hidePanelLayout()
        }
        clickSwitchBotton = null
    }

    private fun switchKeyBoardPanel() {
        if (panelShowing) {
            needShowPanel = false
            KeyBoardUtil.showKeyboard(editText)
        } else {
            needShowPanel = true
            KeyBoardUtil.hideKeyboard(editText)
        }
    }

    private var clickSwitchBotton: View? = null

    private fun showPanelLayout(height: Int) {
        needShowPanel = false
        if (!keyBoardPanelShowing) {
            keyBoardPanelShowing = true
            onKeyBoardPanelShowListener?.invoke(true, height)
            panelView.showPanel(height, true)
        } else {
            panelView.showPanel(height, false)
        }
        panelShowing = true
        onKeyBoardPanelSwitchListener?.invoke(false, height, clickSwitchBotton)
        clickSwitchBotton = null
    }

    private fun hidePanelLayout() {
        val keyboardHeight = KeyBoardUtil.getKeyboardHeight(editText.context)
        panelView.closePanel(keyboardHeight, true)
        keyBoardPanelShowing = false
        panelShowing = false
        onKeyBoardPanelShowListener?.invoke(false, keyboardHeight)
    }

    fun attachSwitch(btSwitch: View): KeyBoardPanelSwitchHelper {
        btSwitch.setOnClickListener {
            clickSwitchBotton = it
            if (keyBoardPanelShowing) {
                switchKeyBoardPanel()
            } else {
                showPanelLayout(KeyBoardUtil.getKeyboardHeight(it.context))
            }
        }
        return this
    }

    fun onBackPressed(): Boolean {
        if (panelShowing) {
            hidePanelLayout()
            return true
        }
        return false
    }
}