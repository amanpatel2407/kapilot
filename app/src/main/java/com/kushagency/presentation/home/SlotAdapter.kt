package com.kushagency.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kushagency.databinding.ItemSlotsBinding
import com.kushagency.model.slotModel.SlotDTO

class SlotAdapter(val listSlot: List<SlotDTO>, val onSlotClick: onSlotClick) : RecyclerView.Adapter<SlotAdapter.MySlotVM>(){
    class MySlotVM(val binding : ItemSlotsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(slotDTO: SlotDTO, onSlotClick: onSlotClick) {
            binding.textView.text = "${adapterPosition + 1}) " +slotDTO.name
            binding.root.setOnClickListener {
                onSlotClick.onSlotClick(slotDTO)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySlotVM {
        val binding = ItemSlotsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MySlotVM(binding)
    }

    override fun onBindViewHolder(holder: MySlotVM, position: Int) {
        holder.bindView(listSlot[position], onSlotClick
        )
    }

    override fun getItemCount(): Int {
       return listSlot.size
    }
}


interface onSlotClick{
    fun onSlotClick(slotDTO: SlotDTO)
}