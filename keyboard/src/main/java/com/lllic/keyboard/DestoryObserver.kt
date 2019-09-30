package com.lllic.keyboard

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

internal interface DestoryObserver :LifecycleObserver{
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory()
}