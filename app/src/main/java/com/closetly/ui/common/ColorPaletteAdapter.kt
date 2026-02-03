package com.closetly.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.closetly.R

class ColorPaletteAdapter(
    private val items: List<Colors.ColorOption>,
    private val onPick: (Colors.ColorOption) -> Unit
) : RecyclerView.Adapter<ColorPaletteAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val swatch: FrameLayout = itemView.findViewById(R.id.swatch)
        val label: TextView = itemView.findViewById(R.id.label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_color_swatch, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.label.text = it.name
        holder.swatch.background.mutate().setTint(it.rgb)
        holder.itemView.setOnClickListener { onPick(it) }
    }

    override fun getItemCount(): Int = items.size
}
