package com.example.sup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView // Assuming you have TextViews in your list item layout

class SuporteAdapter(
    private val context: Context,
    private val dataSource: List<SupportItem>
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
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View
        val holder: ViewHolder

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.list_item_suporte, parent, false)
            holder = ViewHolder()
            holder.titleTextView = rowView.findViewById(R.id.textViewItemTitle)
            holder.statusTextView = rowView.findViewById(R.id.textViewItemStatus)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position) as SupportItem

        holder.titleTextView.text = item.motive
        holder.statusTextView.text = "Status: ${item.status}"
        // Bind other data to other views in your holder

        return rowView
    }
    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var statusTextView: TextView

    }
}