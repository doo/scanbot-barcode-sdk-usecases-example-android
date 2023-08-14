package com.example.scanbot

import android.app.Application
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer


class ExampleApplication : Application() {
    companion object {
        private const val LICENSE_KEY =
            "Xi7T4WkRh0RA5aKl/LFz/RuAfxsQdN" +
                    "lypx6/LfpDgJkJ93ObZ3FXHd47m9+O" +
                    "t6e11wWnzMBfYpeQJ2UNOBWd9htOwQ" +
                    "LjBFGuS8BVy6EF7OWVG7BIaQfUHIwy" +
                    "RDoEbDfOL1Ni8czL6yf4EK3Kj0I5RP" +
                    "IqibclgPT2r4V9qVdtuIiilQ+ekU6E" +
                    "szo9xy/ZDJjkIOS/vddjJ/t7vbOKXi" +
                    "JciuojPEgkVTxh1Dc+uao8tEGDslA6" +
                    "uZ8UvJ9UGsUrvD/C1/ccjR2JxJ6apc" +
                    "4RZIukI43xVkLTVwFwvLFTZaysdaMU" +
                    "Tgv506hxrYnFHezUwXK5o1+Co8uFAj" +
                    "JMAIQeydnoVw==\nU2NhbmJvdFNESw" +
                    "ppby5zY2FuYm90LmJhcmNvZGVzZGt1" +
                    "c2VjYXNlcwoxNjk0NzM1OTk5CjU5MA" +
                    "oz\n";
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