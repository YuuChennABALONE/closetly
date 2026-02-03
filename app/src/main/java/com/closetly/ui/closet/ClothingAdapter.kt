package com.closetly.ui.closet

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.closetly.data.ClothingItem
import com.closetly.databinding.ItemClothingBinding
import java.io.File

class ClothingAdapter : ListAdapter<ClothingItem, ClothingAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemClothingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ItemClothingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClothingItem) {
            binding.title.text = "${item.category} · ${item.color}"
            val f = File(item.imagePath)
            if (f.exists()) {
                binding.image.setImageURI(Uri.fromFile(f))
            } else {
                binding.image.setImageDrawable(null)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ClothingItem>() {
            override fun areItemsTheSame(oldItem: ClothingItem, newItem: ClothingItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ClothingItem, newItem: ClothingItem) = oldItem == newItem
        }
    }
}
