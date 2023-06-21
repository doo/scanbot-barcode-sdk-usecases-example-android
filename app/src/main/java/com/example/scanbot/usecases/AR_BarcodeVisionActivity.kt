package com.example.scanbot.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.scanbot.ExampleUtils
import com.example.scanbot.R
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class AR_BarcodeVisionActivity : AppCompatActivity() {
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
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_BarcodeVisionActivity)
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

        // We don't need the contour of the barcode to be displayed
        barcodeScannerView.selectionOverlayController.setPolygonColor(Color.TRANSPARENT)

        // In this callback we define a factory for the Overlay view
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewFactory(object : BarcodePolygonsView.BarcodeItemViewFactory {
            override fun createView(): View {
                val inflater = LayoutInflater.from(this@AR_BarcodeVisionActivity)
                return inflater.inflate(R.layout.custom_view_for_ar, barcodeScannerView, false)
            }
        })

        // In this callback we define a binder for the Overlay view for each barcode
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewBinder(object : BarcodePolygonsView.BarcodeItemViewBinder {
            override fun bindView(view: View, barcodeItem: BarcodeItem, shouldHighlight: Boolean) {
                val valueTextView = view.findViewById<TextView>(R.id.custom_ar_view_value_text)
                valueTextView.text = barcodeItem.text

                // For example we may show an image for the product with a given barcode
                // You may implement any other logic here
                view.findViewById<ImageView>(R.id.custom_ar_view_image).setImageResource(
                    if (barcodeItem.text.endsWith("2")) {
                        R.drawable.chocolate
                    } else if (barcodeItem.text.endsWith("3")) {
                        R.drawable.coffee
                    } else if (barcodeItem.text.endsWith("5")) {
                        R.drawable.apple_sauce
                    } else if (barcodeItem.text.endsWith("6")) {
                        R.drawable.tea
                    } else {
                        R.drawable.tea
                    }
                )

                // We may also define a click listener for the button in the Overlay view
                view.findViewById<Button>(R.id.custom_ar_view_open_button).setOnClickListener {
                    // ... open the product details
                }
            }
        })

        // In this callback we configure the position of the Overlay view
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewPositionHandler(object :
            BarcodePolygonsView.BarcodeItemViewPositionHandler {
            override fun adjustPosition(view: View, path: List<PointF>, bounds: RectF) {
                view.apply {
                    x = bounds.centerX() - width / 2
                    y = bounds.centerY() - height / 2
                }
            }
        })
// END OF IMPORTANT FOR THIS EXAMPLE
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
