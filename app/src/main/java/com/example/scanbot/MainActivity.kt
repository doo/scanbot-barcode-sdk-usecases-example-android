package com.example.scanbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = listOf(
            ViewType.Header("Barcode Scanning Use Cases"),
            ViewType.Option(UseCase.SINGLE_BARCODE, "Scanning Single Barcodes"),
            ViewType.Option(UseCase.MULTIPLE_BARCODE, "Scanning Multiple Barcodes"),
            ViewType.Option(UseCase.BATCH_SCANNING, "Batch Scanning"),
            ViewType.Option(UseCase.TINY_BARCODE, "Scanning Tiny Barcodes"),
            ViewType.Option(UseCase.DISTANT_BARCODE, "Scanning Distant Barcodes"),
            ViewType.Header("Barcode AR Overlay Use Cases"),
            ViewType.Option(UseCase.AR_MULTI_SCAN, "AR-MultiScan"),
            ViewType.Option(UseCase.AR_SELECT_SCAN, "AR-SelectScan"),
            ViewType.Option(UseCase.AR_FIND_AND_PICK, "AR-FindAndPick"),
            ViewType.Option(UseCase.AR_SCAN_AND_COUNT, "AR-ScanAndCount"),
            ViewType.Option(UseCase.AR_BARCODE_VISION, "AR-BarcodeVision"),
            ViewType.Support(),
        )

        val adapter = OptionAdapter(items)
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
