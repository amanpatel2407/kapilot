package com.kushagency.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kushagency.databinding.ActivitySlotDetailBinding
import com.kushagency.model.slotModel.SlotDTO
import com.kushagency.model.slotModel.SlotResponse
import com.kushagency.model.vehicleinfo.VehicleDTO
import com.kushagency.model.vehicleinfo.VehicleListDTO
import com.kushagency.presentation.addEntry.AddVehiclePasting
import com.kushagency.presentation.home.MainActivity.Companion.SLOTID
import com.kushagency.presentation.home.SlotAdapter
import com.kushagency.presentation.home.onSlotClick
import com.kushagency.utils.BASE_URL
import com.kushagency.utils.Loader
import com.kushagency.utils.autoList
import com.kushagency.utils.showToast
import kotlinx.coroutines.launch
import org.json.JSONObject

class SlotDetail : AppCompatActivity(), onSlotClick {
    companion object{

        val SLOTS_LIST = ArrayList<VehicleListDTO>()
    }
    private lateinit var mBinding : ActivitySlotDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySlotDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

       val args = intent.extras
        if (args !=null){
            val item = Gson().fromJson(args.getString("data"), SlotDTO::class.java)
            mBinding.textView.text = "${item.name}"
            SLOTID = item.id

        }

        mBinding.ivAdd.setOnClickListener {
            startActivity(Intent(this, AddVehiclePasting::class.java))

        }
        mBinding.ivBack.setOnClickListener {
            finish()
        }


    }

    override fun onSlotClick(slotDTO: SlotDTO) {

    }

    fun getSlots(slotID : String){
        SLOTS_LIST.clear()
        Loader.showLoader(this)
        lifecycleScope.launch {
            val jsonObject = JSONObject()
            jsonObject.put("slot_id", slotID)
            val queue: RequestQueue = Volley.newRequestQueue(this@SlotDetail)
            val request = JsonObjectRequest(Request.Method.POST, "$BASE_URL/getSlotAds", jsonObject, { response ->
                try {
                    Loader.hideLoader()
                    val data = Gson().fromJson(response.toString(), VehicleDTO::class.java)
                    SLOTS_LIST.addAll(data.data)
                    Log.d("categories", "sendOtp: $data")
                    val adapter = AutoListAdapter(data.data)
                    mBinding.rvSlot.adapter = adapter
                }catch (e :Exception){
                    Loader.hideLoader()
                    e.printStackTrace()
                }
            }, { error ->
                Loader.hideLoader()
                Log.e("TAG", "RESPONSE IS ${error.localizedMessage}")
                showToast("Fail to get response")
            })
            queue.add(request)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        getSlots(SLOTID)
    }
}