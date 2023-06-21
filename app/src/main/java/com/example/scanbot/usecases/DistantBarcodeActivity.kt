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
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class DistantBarcodeActivity : AppCompatActivity() {
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
                    ExampleUtils.showLicenseExpiredToastAndExit(this@DistantBarcodeActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
// IMPORTANT FOR THIS EXAMPLE:
                    // Set the optical zoom level to 1.0f to allow scanning barcodes from a distance
                    barcodeScannerView.cameraConfiguration.setOpticalZoomLevel(1.0f)
// END OF IMPORTANT FOR THIS EXAMPLE:
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }
            })
        }
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            barcodeScannerView.viewController.isFrameProcessingEnabled = false
            runOnUiThread {
                ExampleUtils.showBarcodeResult(
                    this@DistantBarcodeActivity, it
                ) { barcodeScannerView.viewController.isFrameProcessingEnabled = true }
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
