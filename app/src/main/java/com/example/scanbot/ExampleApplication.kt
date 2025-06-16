package com.example.scanbot

import android.app.Application
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer


class ExampleApplication : Application() {
    companion object {
        private const val LICENSE_KEY = "" // "YOUR_SCANBOT_SDK_LICENSE_KEY"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize the Scanbot Barcode Scanner SDK:
        ScanbotBarcodeScannerSDKInitializer()
            .withLogging(true)
            .license(this, LICENSE_KEY)
            .initialize(this)
    }
}