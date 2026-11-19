package com.example.lab7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes

class MyAdapter(
    context: Context,
    data: List<Item>,
    @LayoutRes private val layout: Int,
    // 由呼叫端決定文字要怎麼顯示，Adapter 不必再判斷是哪一種 layout
    private val formatText: (Item) -> String
) : ArrayAdapter<Item>(context, layout, data) {

    private val inflater = LayoutInflater.from(context)

    // ViewHolder：把 findViewById 的結果存起來，畫面重複利用時不必再找一次元件
    private class ViewHolder(view: View) {
        val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        val tvMsg: TextView = view.findViewById(R.id.tvMsg)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            // 傳入 parent 並設定 attachToRoot = false，layout 檔裡的寬高與 padding 才會生效
            view = inflater.inflate(layout, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = getItem(position) ?: return view
        holder.imgPhoto.setImageResource(item.photo)
        holder.tvMsg.text = formatText(item)
        return view
    }
}
