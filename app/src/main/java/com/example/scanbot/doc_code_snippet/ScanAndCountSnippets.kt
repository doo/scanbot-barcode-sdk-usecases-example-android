package com.example.scanbot.doc_code_snippet

import android.content.Context
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodePolygonsStaticView
import io.scanbot.sdk.barcode.ui.BarcodeScanAndCountView
import io.scanbot.sdk.barcode.ui.IBarcodeScanCountViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

fun scanAndCountClassicUiSnippet(barcodeCounterView: BarcodeScanAndCountView, context: Context) {
    barcodeCounterView.initCamera()
    val scanbotSDK = ScanbotBarcodeScannerSDK(context)
    val barcodeDetector = scanbotSDK.createBarcodeScanner()

    barcodeCounterView.initScanningBehavior(
        barcodeDetector,
        callback = object : IBarcodeScanCountViewCallback {
            override fun onScanAndCountStarted() {
                //here you can start to show some progress animation
            }

            override fun onLicenseError() {
                //stop progress animation
            }

            override fun onScanAndCountFinished(barcodes: List<BarcodeItem>) {
                //stop progress animation
                // `barcodes` - list of barcodes of last scan
            }

            override fun onCameraOpen() {
                //camera is initialized and preview is shown
            }
        }
    )
}

fun getCountedBarcodesSnippet(barcodeCounterView: BarcodeScanAndCountView) {
    val scannedBarcodes = barcodeCounterView.countedBarcodes
}

fun appearanceConfigurationSnippet(
    barcodeCounterView: BarcodeScanAndCountView,
    drawPolygon: Boolean,
    strokeWidth: Float,
    strokeColor: Int,
    fillColor: Int,
    cornerRadius: Float
) {
    barcodeCounterView.counterOverlayController.setBarcodeAppearanceDelegate(object :
        BarcodePolygonsStaticView.BarcodeAppearanceDelegate {
        override fun getPolygonStyle(
            defaultStyle: BarcodePolygonsStaticView.BarcodePolygonStyle,
            barcodeItem: BarcodeItem
        ): BarcodePolygonsStaticView.BarcodePolygonStyle {
            return defaultStyle.copy(
                drawPolygon = drawPolygon,
                strokeWidth = strokeWidth,
                strokeColor = strokeColor,
                fillColor = fillColor,
                cornerRadius = cornerRadius,
            )
        }
    })
}