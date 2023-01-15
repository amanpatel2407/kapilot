package com.kushagency.presentation.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kushagency.LoginScreen
import com.kushagency.databinding.ActivityMainBinding
import com.kushagency.model.slotModel.SlotDTO
import com.kushagency.model.slotModel.SlotResponse
import com.kushagency.presentation.SlotDetail
import com.kushagency.utils.*
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity(), onSlotClick {
    companion object{
        var USER_ID : String = ""
        var SLOTID = ""
        var LATITUDE = "0"
        var LONGITUDE = "0"
        var ADDRESS = ""
    }
    private var isLoading = false
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var mBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.logout.setOnClickListener {
            SharedPrefrenceHelper.userId = ""
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
        getSlots()


    }
    private fun getLocation() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else{
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f) { location ->
                Log.d(
                    "currentlocationlatlong",
                    "onLocationChanged: " + "Latitude: ${location.extras} " + location.latitude + " , Longitude: " + location.longitude
                )
                LONGITUDE = location.longitude.toString()
                LATITUDE = location.latitude.toString()

                if (!isLoading) {
                    getMapLocation(location.latitude, location.longitude)
                }

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode ->{
                getLocation()
            }
        }
    }
    override fun onSlotClick(slotDTO: SlotDTO) {
        SLOTID = slotDTO.id
        val srgs = Gson().toJson(slotDTO)
        val internt = Intent(this , LoginScreen::class.java)

        internt.putExtra("data", srgs)
        startActivity(internt)

    }


    fun getSlots(){
        Loader.showLoader(this)
        lifecycleScope.launch {
            val queue: RequestQueue = Volley.newRequestQueue(this@MainActivity)
            val request = JsonObjectRequest(Request.Method.GET, "$BASE_URL/getSlots", null, { response ->
                try {
                    Loader.hideLoader()
                    val data = Gson().fromJson(response.toString(), SlotResponse::class.java)
                    Log.d("categories", "sendOtp: $data")
                    val adapter = SlotAdapter(data.data.asReversed(), this@MainActivity)
                    mBinding.rvSlot.adapter = adapter
                    getLocation()
                }catch (e :Exception){
                    Loader.hideLoader()
                    e.printStackTrace()
                }
            }, { error ->
                Loader.hideLoader()
                Log.e("TAG", "RESPONSE IS ${error.localizedMessage}")
                getLocation()
                showToast("Fail to get response")
            })
            queue.add(request)
        }
    }

    private fun getMapLocation(latitude : Double, longitude : Double){
        val geocoder: Geocoder
        val addresses: List<Address>?
        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        Log.d("currentaddress", "getMapLocation: ${addresses?.map { it.getAddressLine(0) }}")

        try {
            ADDRESS =
                addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        }catch (e : Exception){

        }
    }
}