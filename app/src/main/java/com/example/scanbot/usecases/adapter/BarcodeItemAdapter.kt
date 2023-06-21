package com.example.scanbot.usecases.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scanbot.R
import io.scanbot.sdk.barcode.entity.BarcodeItem

class BarcodeItemAdapter : RecyclerView.Adapter<BarcodeItemAdapter.BarcodeViewHolder>() {
    private val items: MutableList<BarcodeItem> = mutableListOf()

    inner class BarcodeViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val image: ImageView by lazy { item.findViewById(R.id.image) }
        val barcodeType: TextView by lazy { item.findViewById(R.id.barcodeFormat) }
        val text: TextView by lazy { item.findViewById(R.id.docText) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.barcode_item, parent, false))
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = items.get(position)
        holder.text.text = item.textWithExtension
        holder.barcodeType.text = item.barcodeFormat.name
        holder.image.setImageBitmap(item.image)
    }

    fun addBarcodeItems(items: List<BarcodeItem>) {
        // lets check duplicates
        items.forEach { item ->
            var insertedCount = 0
            if (!this.items.any { it.textWithExtension == item.textWithExtension }) {
                this.items.add(0, item)
                insertedCount += 1
            }
            notifyItemRangeInserted(0, insertedCount)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItems(): List<BarcodeItem> = items

}
