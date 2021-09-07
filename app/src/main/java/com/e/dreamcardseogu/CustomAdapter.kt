package com.e.dreamcardseogu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e.dreamcardseogu.databinding.ItemRecyclerBinding
import kotlin.math.roundToInt

class CustomAdapter:RecyclerView.Adapter<Holder>() {
    var listData= mutableListOf<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data= listData.get(position)
        holder.setData(data)

        holder.binding.recyclerItemItem.setOnClickListener {
            itemClickListener.onClick(it,position)
        }
    }

    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }
    private lateinit var itemClickListener: OnItemClickListener

    override fun getItemCount(): Int {
       return listData.size
    }
}
class Holder(val binding: ItemRecyclerBinding ):RecyclerView.ViewHolder(binding.root){
    fun setData(data:Data){
        binding.recyclerItemName.text="${data.name}"
        binding.recyclerItemPhone.text="${data.number}"
        binding.recyclerItemAddress.text="${data.address}"
        binding.recyclerItemRating.text="★ ${(data.rating * 100).roundToInt() / 100f}"

    }
}
