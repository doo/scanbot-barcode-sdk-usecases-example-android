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
            ViewType.Option(UseCase.DETECTION_ON_THE_IMAGE, "Detecting Barcodes on Still Images"),
            ViewType.Header("Barcode AR Overlay Use Cases"),
            ViewType.Option(UseCase.AR_FIND_AND_PICK, "AR-FindAndPick"),
            ViewType.Support(),
        )

        val adapter = OptionAdapter(items)
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
