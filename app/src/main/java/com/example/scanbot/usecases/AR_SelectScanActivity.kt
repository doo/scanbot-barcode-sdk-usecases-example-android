package com.example.scanbot.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scanbot.ExampleUtils
import com.example.scanbot.R
import com.example.scanbot.usecases.adapter.BarcodeItemAdapter
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class AR_SelectScanActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView

    // IMPORTANT FOR THIS EXAMPLE:
    private val resultAdapter by lazy { BarcodeItemAdapter() }
    private lateinit var resultView: RecyclerView
// END OF IMPORTANT FOR THIS EXAMPLE:

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_recycler_view)

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
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_SelectScanActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }

                // IMPORTANT FOR THIS EXAMPLE:
                override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                    // We need to add the barcode items to the adapter on the main thread
                    barcodeScannerView.post {
                        resultAdapter.addBarcodeItems(listOf(barcodeItem))
                        resultView.scrollToPosition(0)
                    }
// END OF IMPORTANT FOR THIS EXAMPLE:
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
        barcodeScannerView.selectionOverlayController.setTextFormat(BarcodeOverlayTextFormat.CODE)
        barcodeScannerView.selectionOverlayController.setTextContainerColor(
            ContextCompat.getColor(this, R.color.scanbot_secondary)
        )

        barcodeScannerView.selectionOverlayController.setPolygonHighlightedColor(Color.GREEN)
        barcodeScannerView.selectionOverlayController.setTextContainerHighlightedColor(Color.GREEN)

        // Required for the AR overlay to work faster
        barcodeScannerView.viewController.barcodeDetectionInterval = 0

        barcodeScannerView.selectionOverlayController.setBarcodeHighlightedDelegate(object : BarcodePolygonsView.BarcodeHighlightDelegate {
            override fun shouldHighlight(barcodeItem: BarcodeItem): Boolean {
                // We highlight only the barcodes that were already added to the list
                return resultAdapter.getItems()
                    .any {
                        it.textWithExtension == barcodeItem.textWithExtension
                                && it.barcodeFormat == barcodeItem.barcodeFormat
                    }
            }
        })

        resultView = findViewById(R.id.barcode_recycler_view)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter
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
