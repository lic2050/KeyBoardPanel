package com.lllic.keyboardpanel

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lllic.keyboard.KeyBoardUtil
import com.lllic.keyboard.panel.KeyBoardPanelSwitchHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var panelSwitchHelper: KeyBoardPanelSwitchHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                } else {
                    //导航栏隐藏
                }
            }
        }.bindPanel(panelView, etContent)
        panelSwitchHelper?.attachSwitch(btSwitch)?.apply {
            onKeyBoardPanelShow { show, height ->
                if (show) {
                    //键盘或者表情面板显示
                } else {
                    //键盘或者表情面板隐藏
                    btSwitch.text = "表情"
                }

            }
            onKeyBoardPanelSwitch { isKeyBoard, height, switchView ->
                if (isKeyBoard) {
                    //键盘显示
                    btSwitch.text = "表情"
                } else {
                    //表情面板显示
                    btSwitch.text = "键盘"
                }
            }
            onKeyBoardPanelShowDelay { show, height ->
                if (show) {
                    //键盘或者表情面板显示
                    textview1.visibility = View.VISIBLE
                    textview2.visibility = View.GONE
                    textview4.visibility = View.GONE
                    textview3.visibility = View.VISIBLE
                    textview5.visibility = View.VISIBLE
                } else {
                    //键盘或者表情面板隐藏
                    btSwitch.text = "表情"

                    textview3.visibility = View.GONE
                    textview5.visibility = View.GONE
                    textview1.visibility = View.VISIBLE
                    textview2.visibility = View.VISIBLE
                    textview4.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initView() {
        tvContent.setOnTouchListener { _, _ ->
            panelSwitchHelper?.let { KeyBoardUtil.hideKeyboardPanel(it) }
            return@setOnTouchListener false
        }
        textview1.setOnClickListener {
            panelSwitchHelper?.reactKeyboardChange = false
            Dialog(this).apply {
                setContentView(R.layout.dialog_edit)
                setOnDismissListener {
                    panelSwitchHelper?.reactKeyboardChange = true
                }
            }.show()
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
