package com.example.scanbot.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.scanbot.ExampleUtils
import com.example.scanbot.R
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class AR_MultiScanActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_view)

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
                    handleSuccess(result)
                } else {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_MultiScanActivity)
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
        // Disable the finder view to hide the barcode scanner viewfinder
        // It allows to locate the barcodes on the full screen
        barcodeScannerView.finderViewController.setFinderEnabled(false)
        // Enable the selection overlay (AR Overlay) to show the contours of detected barcodes
        barcodeScannerView.selectionOverlayController.setEnabled(true)
        // Hide the text box under the barcode
        barcodeScannerView.selectionOverlayController.setTextFormat(BarcodeOverlayTextFormat.NONE)
        // Required for the AR overlay to work faster
        barcodeScannerView.viewController.barcodeDetectionInterval = 0
// END OF IMPORTANT FOR THIS EXAMPLE:
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            findViewById<TextView>(R.id.barcode_text_view).text = it.barcodeItems.joinToString(separator = "\n") { barcodeItem ->
                "${barcodeItem.barcodeFormat}: ${barcodeItem.text}"
            }
            // You may also finish the scanning and proceed to the separate result screen
            // val barcodeItems = it.barcodeItems
            // val intent = Intent()
            // intent.putExtra("BARCODES_ARG", barcodeItems.toTypedArray())
            // finish()
        }
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
