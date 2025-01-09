package com.example.scanbot.doc_code_snippet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.scanbot.R
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.entity.textWithExtension
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView

fun enableBarcodeSelectionOverlaySnippet(barcodeScannerView: BarcodeScannerView) {
    barcodeScannerView.selectionOverlayController.setEnabled(true)
    barcodeScannerView.viewController.apply {
        // This is important for Selection Overlay to work properly
        barcodeScanningInterval = 0
    }
}

fun customViewForResultSnippet(barcodeScannerView: BarcodeScannerView, context: Context) {
    // Simple result storage to showcase the custom view
    val resultsMap = hashMapOf<String, Long>()
    // Setting a Factory for the Views
    barcodeScannerView.selectionOverlayController.setBarcodeItemViewFactory(object :
        BarcodePolygonsView.BarcodeItemViewFactory {
        override fun createView(): View {
            val inflater = LayoutInflater.from(context)
            return inflater.inflate(R.layout.custom_view_for_ar, barcodeScannerView, false)
        }
    })
    // Setting a binding callback when the View is bound to the corresponding barcode
    barcodeScannerView.selectionOverlayController.setBarcodeItemViewBinder(object :
        BarcodePolygonsView.BarcodeItemViewBinder {
        override fun bindView(view: View, barcodeItem: BarcodeItem, shouldHighlight: Boolean) {
            val textWithExtension = barcodeItem.textWithExtension
            if (!resultsMap.containsKey(textWithExtension)) {
                // TODO: here we emulate loading info from the database/internet
                resultsMap[textWithExtension] = System.currentTimeMillis() + 2500
            }
            val valueTextView = view.findViewById<TextView>(R.id.custom_ar_view_value_text)
            val resultIsReady = resultsMap[textWithExtension]!! < System.currentTimeMillis()
            valueTextView.isVisible = resultIsReady
            valueTextView.text = textWithExtension
        }
    })
}

fun selectionOverlayAppearanceConfigSnippet(
    barcodeScannerView: BarcodeScannerView,
    overlayStrokeWidth: Float,
    overlayPolygonColor: Int,
    overlayHighlightedPolygonColor: Int,
    overlayTextColor: Int,
    overlayTextHighlightedColor: Int,
    overlayTextContainerColor: Int,
    overlayTextContainerHighlightedColor: Int,
    overlayTextFormat: BarcodeOverlayTextFormat
) {
    barcodeScannerView.selectionOverlayController.setBarcodeAppearanceDelegate(object :
        BarcodePolygonsView.BarcodeAppearanceDelegate {
        override fun getPolygonStyle(
            defaultStyle: BarcodePolygonsView.BarcodePolygonStyle,
            barcodeItem: BarcodeItem
        ): BarcodePolygonsView.BarcodePolygonStyle {
            return defaultStyle.copy(
                strokeWidth = overlayStrokeWidth,
                fillColor = overlayPolygonColor,
                fillHighlightedColor = overlayHighlightedPolygonColor,
                strokeColor = overlayPolygonColor,
                strokeHighlightedColor = overlayHighlightedPolygonColor,
            )
        }

        override fun getTextViewStyle(
            defaultStyle: BarcodePolygonsView.BarcodeTextViewStyle,
            barcodeItem: BarcodeItem
        ): BarcodePolygonsView.BarcodeTextViewStyle {
            return defaultStyle.copy(
                textColor = overlayTextColor,
                textHighlightedColor = overlayTextHighlightedColor,
                textContainerColor = overlayTextContainerColor,
                textContainerHighlightedColor = overlayTextContainerHighlightedColor,
                textFormat = overlayTextFormat
            )
        }
    })
}