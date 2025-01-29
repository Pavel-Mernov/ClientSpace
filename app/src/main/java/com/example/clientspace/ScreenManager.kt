package com.example.clientspace

import android.app.Activity
import android.os.Build
import android.view.WindowMetrics

// object responsible for getting screen width and height
object ScreenManager {
    fun getScreenWidth(activity : Activity) : Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics : WindowMetrics = activity.windowManager.currentWindowMetrics
            windowMetrics.bounds.width()
        }
        else {
            val displayMetrics = activity.resources.displayMetrics
            displayMetrics.widthPixels
        }
    }
}