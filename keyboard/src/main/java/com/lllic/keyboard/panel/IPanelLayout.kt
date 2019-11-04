package com.lllic.keyboard.panel

interface IPanelLayout {

    fun setOnKeyBoardPanelShowDelayListener(listener: (show: Boolean, height: Int) -> Unit)
    /**
     * 当键盘打开时显示占位
     */
    fun placeHolderOnKeyBoardShow(height :Int,notifyListener: Boolean)

    /**
     * 显示表情面板
     */
    fun showPanel(height :Int,notifyListener: Boolean)

    /**
     * 关闭表情面板
     */
    fun closePanel(height :Int,notifyListener: Boolean)
}