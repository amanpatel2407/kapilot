package com.kushagency.model.slotModel

import com.google.gson.annotations.SerializedName

data class SlotDTO(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String
)

