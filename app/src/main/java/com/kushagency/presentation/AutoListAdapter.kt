package com.kushagency.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kushagency.databinding.ItemAutolistBinding
import com.kushagency.model.AutoItemDTO
import com.kushagency.model.vehicleinfo.VehicleListDTO
import com.squareup.picasso.Picasso

class AutoListAdapter(val list : List<VehicleListDTO>): RecyclerView.Adapter<AutoListAdapter.AutoListVH>() {
    class AutoListVH(val binding : ItemAutolistBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(autoItemDTO: VehicleListDTO) {
            binding.pasterName.text = autoItemDTO.driverName
            binding.vehicleName.text = autoItemDTO.mobileNumber
            binding.vehicleNumber.text = autoItemDTO.autoNumber
            binding.vehiclePastingDate.text = autoItemDTO.created_at
            Picasso.get().load(autoItemDTO.documentUrl).into(binding.vehicleImg)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoListVH {
        val binding = ItemAutolistBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return  AutoListVH(binding)
    }

    override fun onBindViewHolder(holder: AutoListVH, position: Int) {
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}