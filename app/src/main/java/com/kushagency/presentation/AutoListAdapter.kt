package com.kushagency.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kushagency.databinding.ItemAutolistBinding
import com.kushagency.model.vehicleinfo.VehicleListDTO
import com.squareup.picasso.Picasso

class AutoListAdapter(val list : List<VehicleListDTO>, val onSlotItemClick: onSlotItemClick): RecyclerView.Adapter<AutoListAdapter.AutoListVH>() {
    class AutoListVH(val binding : ItemAutolistBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(autoItemDTO: VehicleListDTO, onSlotItemClick: onSlotItemClick) {
            binding.pasterName.text = autoItemDTO.driverName
            binding.vehicleName.text = autoItemDTO.mobileNumber
            binding.vehicleNumber.text = autoItemDTO.autoNumber
            binding.vehiclePastingDate.text = autoItemDTO.created_at
            Picasso.get().load(autoItemDTO.documentUrl).into(binding.vehicleImg)

            binding.vehicleImg.setOnClickListener {
                onSlotItemClick.onItemClick(autoItemDTO)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoListVH {
        val binding = ItemAutolistBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return  AutoListVH(binding)
    }

    override fun onBindViewHolder(holder: AutoListVH, position: Int) {
        holder.bindView(list[position], onSlotItemClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


interface onSlotItemClick{
    fun onItemClick(vehicleListDTO: VehicleListDTO)
}