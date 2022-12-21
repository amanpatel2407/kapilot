package com.kushagency.model.slotModel


import com.google.gson.annotations.SerializedName

data class SlotResponse(
    @SerializedName("code")
    var code: Int,
    @SerializedName("data")
    var data: List<SlotDTO>,
    @SerializedName("status")
    var status: String
)