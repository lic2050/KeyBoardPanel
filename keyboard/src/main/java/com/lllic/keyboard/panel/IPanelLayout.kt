package com.lllic.keyboard.panel

interface IPanelLayout {
    /**
     * 当键盘打开时显示占位
     */
    fun placeHolderOnKeyBoardShow(height :Int)

    /**
     * 显示表情面板
     */
    fun showPanel(height :Int)

    /**
     * 关闭表情面板
     */
    fun closePanel(height :Int)
}