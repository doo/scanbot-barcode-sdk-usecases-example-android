package com.example.scanbot.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.scanbot.ExampleUtils
import com.example.scanbot.R
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class AR_FindAndPickActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_view_full_screen)

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)

        val barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()
        barcodeDetector.modifyConfig {
            // Specify the barcode format you want to scan
            // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
        }

        barcodeScannerView.apply {
            initCamera(CameraUiSettings(true))
            initDetectionBehavior(barcodeDetector, { result ->
                if (result is FrameHandlerResult.Success) {
// IMPORTANT FOR THIS EXAMPLE:
                    // We keep this part empty as we process barcodes only when the barcode was tapped on AR overlay layer
// END OF IMPORTANT FOR THIS EXAMPLE:
                } else {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_FindAndPickActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }
            })
        }

// IMPORTANT FOR THIS EXAMPLE:
        // Required for the AR overlay to work faster
        barcodeScannerView.viewController.barcodeDetectionInterval = 0

        // Disable the finder view to hide the barcode scanner viewfinder
        // It allows to locate the barcodes on the full screen
        barcodeScannerView.finderViewController.setFinderEnabled(false)

        // Enable the selection overlay (AR Overlay) to show the contours of detected barcodes
        barcodeScannerView.selectionOverlayController.setEnabled(true)

        barcodeScannerView.selectionOverlayController.setTextFormat(BarcodeOverlayTextFormat.NONE)

        // Set the color of the AR overlay
        val highlightedColor = ContextCompat.getColor(this, R.color.ar_overlay_highlighted)
        barcodeScannerView.selectionOverlayController.setTextContainerHighlightedColor(highlightedColor)
        barcodeScannerView.selectionOverlayController.setPolygonHighlightedColor(highlightedColor)

        // Set the delegate to highlight only the barcodes you need
        barcodeScannerView.selectionOverlayController.setBarcodeHighlightedDelegate(
            object : BarcodePolygonsView.BarcodeHighlightDelegate {
                override fun shouldHighlight(barcodeItem: BarcodeItem): Boolean {
                    // We highlight only QR codes, you can change this logic
                    return barcodeItem.barcodeFormat == BarcodeFormat.QR_CODE
                    // for example:
                    // return barcodeItem.text.endsWith("5")
                    // or
                    // return barcodeItem.text == expectedText (which you can get from the user)
                }
            }
        )
// END OF IMPORTANT FOR THIS EXAMPLE:
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerView.viewController.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.viewController.onPause()
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
    }
}
