package com.closetly.ui.add

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class VectorCategoryAdapter(
    context: Context,
    private val index: CategoryIndex
) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1), Filterable {

    private var items: List<String> = index.topK("")

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): String? = items.getOrNull(position)

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val q = constraint?.toString().orEmpty()
            items = index.topK(q, 30)
            return FilterResults().apply {
                values = items
                count = items.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }
}
