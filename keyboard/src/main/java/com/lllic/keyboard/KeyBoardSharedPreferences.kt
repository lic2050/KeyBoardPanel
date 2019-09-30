package com.lllic.keyboard

import android.content.Context
import android.content.SharedPreferences


/**
 * 保存键盘高度
 */
internal object KeyBoardSharedPreferences {

    private val FILE_NAME = "keyboard.common"

    private val KEY_KEYBOARD_HEIGHT = "sp.key.keyboard.height"

    @Volatile
    private var sp: SharedPreferences? = null

    fun save(context: Context, keyboardHeight: Int) {
        with(context)!!.edit()
                .putInt(KEY_KEYBOARD_HEIGHT, keyboardHeight)
                .apply()
    }

    private fun with(context: Context): SharedPreferences? {
        if (sp == null) {
            synchronized(KeyBoardSharedPreferences::class.java) {
                if (sp == null) {
                    sp = context.getSharedPreferences(
                        FILE_NAME, Context.MODE_PRIVATE)
                }
            }
        }

        return sp
    }

    operator fun get(context: Context, defaultHeight: Int): Int {
        return with(context)!!.getInt(KEY_KEYBOARD_HEIGHT, defaultHeight)
    }
}
