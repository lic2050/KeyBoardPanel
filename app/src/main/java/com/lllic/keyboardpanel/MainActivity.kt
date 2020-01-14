package com.lllic.keyboardpanel

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.lllic.keyboard.KeyBoardUtil
import com.lllic.keyboard.panel.KeyBoardPanelSwitchHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var panelSwitchHelper: KeyBoardPanelSwitchHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTranslucent(window!!)
        setTranslucentNav(window!!)
        setContentView(R.layout.activity_main)
        initView()
        setView()

    }

    private fun setView() {
        panelSwitchHelper = KeyBoardUtil.attach(this).apply {
            onKeyBoardShow { show, keyBoardHeight ->
                if (show) {
                    //键盘显示
                } else {
                    //键盘隐藏
                }
            }
            onNavBarChange { show, navBarHeight ->
                if (show) {
                    //导航栏显示
                    (llBottom.layoutParams as? ViewGroup.MarginLayoutParams?)?.let { layoutParams ->
                        layoutParams.bottomMargin = navBarHeight
                        llBottom.layoutParams = layoutParams
                    }

                } else {
                    //导航栏隐藏
                    (llBottom.layoutParams as? ViewGroup.MarginLayoutParams?)?.let { layoutParams ->
                        layoutParams.bottomMargin = 0
                        llBottom.layoutParams = layoutParams
                    }
                }
            }
        }.bindPanel(panelView, etContent)
        panelSwitchHelper?.attachSwitch(ivSwitch)?.apply {
            onKeyBoardPanelShow { show, height ->
                if (show) {
                    //键盘或者表情面板显示
                } else {
                    //键盘或者表情面板隐藏
                }
            }
            onKeyBoardPanelShowDelay { show, height ->
                //在动画结束之后
                if (show) {
                    //键盘或者表情面板显示
                    textview3.visibility = View.VISIBLE
                } else {
                    //键盘或者表情面板隐藏
                    textview3.visibility = View.GONE
                }
            }
            onKeyBoardPanelSwitch { isKeyBoard, height, switchView ->
                if (isKeyBoard) {
                    //键盘显示
                    ivSwitch.setImageResource(R.mipmap.ic_emoji)
                } else {
                    //表情面板显示
                    ivSwitch.setImageResource(R.mipmap.ic_keyboard)
                }
            }
        }
    }

    private fun initView() {
        tvContent.setOnTouchListener { _, _ ->
            panelSwitchHelper?.let { KeyBoardUtil.hideKeyboardPanel(it) }
            return@setOnTouchListener false
        }
    }

    override fun onBackPressed() {
        if (panelSwitchHelper?.onBackPressed() == true) {
            //收起表情
            return
        }
        super.onBackPressed()
    }
}


/**
 * 设置状态栏半透明度
 * 根布局上移
 *
 * @param activity
 */
private fun setTranslucent(window: Window, alpha: Int = 0) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = Color.argb(alpha, 0, 0, 0)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    } else {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

/**
 * 设置状态栏半透明度
 * 根布局上移
 *
 * @param activity
 */
private fun setTranslucentNav(window: Window, alpha: Int = 0) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.navigationBarColor = Color.argb(alpha, 0, 0, 0)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    } else {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}
