package com.example.scanbot

import android.app.Activity
import android.widget.Toast

object ExampleUtils {
    fun showLicenseExpiredToastAndExit(activity: Activity) {
        activity.runOnUiThread {
            Toast.makeText(
                activity, "License has expired!", Toast.LENGTH_LONG
            ).show()
            activity.finish()
        }
    }
}