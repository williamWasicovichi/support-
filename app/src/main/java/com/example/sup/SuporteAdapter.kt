package com.example.sup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView // Assuming you have TextViews in your list item layout

class SuporteAdapter(
    private val context: Context,
    private val dataSource: List<SupportItem> // Pass the actual data
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong() // Or use item.id if it's a Long and unique
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View
        val holder: ViewHolder

        if (convertView == null) {
            // Inflate your custom row layout here (e.g., R.layout.list_item_suporte)
            rowView = inflater.inflate(R.layout.list_item_suporte, parent, false) // Replace with your layout
            holder = ViewHolder()
            // Example: Assuming your list_item_suporte.xml has these TextViews
            holder.titleTextView = rowView.findViewById(R.id.textViewItemTitle)
            holder.statusTextView = rowView.findViewById(R.id.textViewItemStatus)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position) as SupportItem

        holder.titleTextView.text = item.title
        holder.statusTextView.text = "Status: ${item.status}"
        // Bind other data to other views in your holder

        return rowView
    }

    // ViewHolder pattern improves ListView performance (though RecyclerView enforces it better)
    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var statusTextView: TextView
        // Add other views from your list item layout
    }
}