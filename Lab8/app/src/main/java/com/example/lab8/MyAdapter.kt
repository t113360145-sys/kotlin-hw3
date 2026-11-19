package com.example.lab8

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab8.databinding.AdapterRowBinding

class MyAdapter(
    private val data: MutableList<Contact>
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // 用 ViewBinding 取代 findViewById 儲存畫面元件
    class ViewHolder(val binding: AdapterRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        // 監聽器只在建立 ViewHolder 時設定一次，不必每次 bind 都產生新的
        binding.imgDelete.setOnClickListener {
            // 依照「目前的位置」刪除，同名同電話的聯絡人也不會刪錯
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) removeAt(position)
        }
        return holder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.binding.tvName.text = item.name
        holder.binding.tvPhone.text = item.phone
    }

    // 新增聯絡人：只通知新增的那一列，RecyclerView 會有插入動畫
    fun add(contact: Contact) {
        data.add(contact)
        notifyItemInserted(data.lastIndex)
    }

    // 刪除聯絡人：只通知被刪除的那一列，不必重畫整個清單
    private fun removeAt(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}
