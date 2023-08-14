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
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scanbot.ExampleUtils
import com.example.scanbot.R
import com.example.scanbot.usecases.adapter.BarcodeItemAdapter
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
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
    private val expectedBarcodes = linkedSetOf(
        "12345678", // Add barcode you want to find here, OR just click on it to add it to the list
    )

    private val resultAdapter by lazy { BarcodeItemAdapter() }
    private lateinit var resultView: RecyclerView

    private fun isAcceptedBarcode(barcodeItem: BarcodeItem) =
        expectedBarcodes.contains(barcodeItem.text)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_recycler_view)

        resultView = findViewById(R.id.barcode_recycler_view)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter

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
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_FindAndPickActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }

                override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                    expectedBarcodes.add(barcodeItem.text)
                }
            })
        }

        // Required for the AR overlay to work faster
        barcodeScannerView.viewController.barcodeDetectionInterval = 0

        // Disable the finder view to hide the barcode scanner viewfinder
        // It allows to locate the barcodes on the full screen
        barcodeScannerView.finderViewController.setFinderEnabled(false)

        // Enable the selection overlay (AR Overlay) to show the contours of detected barcodes
        barcodeScannerView.selectionOverlayController.setEnabled(true)

        // Set the color of the AR overlay when the barcode is highlighted
        val highlightedColor = Color.parseColor("#4F7E1C")
        barcodeScannerView.selectionOverlayController.setTextContainerHighlightedColor(highlightedColor)
        barcodeScannerView.selectionOverlayController.setPolygonHighlightedColor(highlightedColor)

        // Set the color of the AR overlay
        val notHighlightedColor = Color.parseColor("#BB4F4A")
        barcodeScannerView.selectionOverlayController.setTextContainerColor(notHighlightedColor)
        barcodeScannerView.selectionOverlayController.setPolygonColor(notHighlightedColor)

        // Set the delegate to highlight only the barcodes you need
        barcodeScannerView.selectionOverlayController.setBarcodeHighlightedDelegate(
            object : BarcodePolygonsView.BarcodeHighlightDelegate {
                override fun shouldHighlight(barcodeItem: BarcodeItem): Boolean {
                    // We highlight only QR codes, you can change this logic
                    return isAcceptedBarcode(barcodeItem)
                }

            }
        )
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewFactory(object : BarcodePolygonsView.BarcodeItemViewFactory {
            override fun createView(): View {
                val inflater = LayoutInflater.from(this@AR_FindAndPickActivity)
                return inflater.inflate(R.layout.custom_find_and_pick_ar, barcodeScannerView, false)
            }
        })

        // In this callback we define a binder for the Overlay view for each barcode
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewBinder(object : BarcodePolygonsView.BarcodeItemViewBinder {
            override fun bindView(view: View, barcodeItem: BarcodeItem, shouldHighlight: Boolean) {
                // For example we may show an image for the product with a given barcode
                // You may implement any other logic here
                view.findViewById<ImageView>(R.id.custom_ar_view_image).setImageResource(
                    if (isAcceptedBarcode(barcodeItem)) {
                        R.drawable.good
                    } else {
                        R.drawable.bad
                    }
                )
                view.findViewById<CardView>(R.id.inner_layout).setBackgroundColor(
                    if (isAcceptedBarcode(barcodeItem)) {
                        Color.parseColor("#4F7E1C")
                    } else {
                        Color.parseColor("#FEF4F1")
                    }
                )
            }
        })

        // In this callback we configure the position of the Overlay view
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewPositionHandler(object :
            BarcodePolygonsView.BarcodeItemViewPositionHandler {
            override fun adjustPosition(view: View, path: List<PointF>, bounds: RectF) {
                view.apply {
                    x = bounds.centerX() - width / 2
                    y = bounds.top - height - height / 3
                }
            }
        })
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            // We need to add the barcode items to the adapter on the main thread
            barcodeScannerView.post {
                resultAdapter.setBarcodeItems(it.barcodeItems.filter { isAcceptedBarcode(it) })
                resultView.scrollToPosition(0)
            }
        } ?: run {
            // We need to clear the adapter on the main thread
            barcodeScannerView.post {
                resultAdapter.setBarcodeItems(emptyList())
            }
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
