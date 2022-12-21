package com.kushagency.model.vehicleinfo


import com.google.gson.annotations.SerializedName

data class VehicleListDTO(
    @SerializedName("auto_number")
    var autoNumber: String,   //created_at
    @SerializedName("created_at")
    var created_at: String,
    @SerializedName("document_url")
    var documentUrl: String,
    @SerializedName("driver_name")
    var driverName: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("location")
    var location: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("mobile_number")
    var mobileNumber: String,
    @SerializedName("slot_id")
    var slotId: String,
    @SerializedName("user_id")
    var userId: String
)