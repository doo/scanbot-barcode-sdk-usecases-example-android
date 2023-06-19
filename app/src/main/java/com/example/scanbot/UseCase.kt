package com.example.scanbot

import android.app.Activity
import com.example.scanbot.usecases.*
import kotlin.reflect.KClass

enum class UseCase(val activityClass: KClass<out Activity>) {
    SINGLE_BARCODE(SingleBarcodeActivity::class),
    TINY_BARCODE(TinyBarcodeActivity::class),
    DISTANT_BARCODE(DistantBarcodeActivity::class),
    MULTIPLE_BARCODE(MultipleBarcodeActivity::class),
    BATCH_SCANNING(BatchScanningActivity::class),
    AR_MULTI_SCAN(AR_MultiScanActivity::class),
    AR_SELECT_SCAN(AR_SelectScanActivity::class),
    AR_FIND_AND_PICK(AR_FindAndPickActivity::class),
    AR_SCAN_AND_COUNT(AR_ScanAndCountActivity::class),
    AR_BARCODE_VISION(AR_BarcodeVisionActivity::class)
}


sealed class ViewType(val type: Int) {
    class Header(val title: String) : ViewType(0)
    class Option(val useCase: UseCase, val title: String) : ViewType(1)
}