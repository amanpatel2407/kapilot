package com.kushagency.model.vehicleinfo


import com.google.gson.annotations.SerializedName

data class VehicleDTO(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var data: List<VehicleListDTO>,
    @SerializedName("status")
    var status: String
)