package com.kushagency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kushagency.databinding.ActivityLoginScreenBinding
import com.kushagency.model.slotModel.SlotDTO
import com.kushagency.presentation.SlotDetail
import com.kushagency.presentation.home.MainActivity
import com.kushagency.utils.BASE_URL
import com.kushagency.utils.Loader
import com.kushagency.utils.SharedPrefrenceHelper
import com.kushagency.utils.showToast
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginScreen : AppCompatActivity() {
    private lateinit var mBinding : ActivityLoginScreenBinding
    var slotID = ""
    var slotDTO = SlotDTO("","")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_screen)

        val args = intent.extras
        if (args !=null){
            val item = Gson().fromJson(args.getString("data"), SlotDTO::class.java)
            slotDTO = item
            MainActivity.SLOTID = item.id
            slotID = item.id

        }
        mBinding.ivBack.setOnClickListener {
            finish()
        }
        mBinding.button.setOnClickListener {

            val username = mBinding.editText.text.toString().trim()
            val password = mBinding.editText2.text.toString().trim()
            if (username.isNullOrEmpty()){
                mBinding.editText.apply {
                    requestFocus()
                    error ="Please enter username."
                }
            }else if (password.isNullOrEmpty()){
                mBinding.editText2.apply {
                    requestFocus()
                    error ="Please enter password."
                }
            }else{
               loginUser(userName = username, passWord = password)
            }
        }

    }

    fun loginUser(userName: String, passWord : String) {
        Loader.showLoader(this)
        val jsonObject = JSONObject()
        jsonObject.put("username", userName)
        jsonObject.put("password", passWord)
        jsonObject.put("slot_id", slotID)
        lifecycleScope.launch {
            Log.d("LoginFrag", "sendOtp: $jsonObject")
            val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
            val request = JsonObjectRequest(Request.Method.POST, "$BASE_URL/login", jsonObject, { response ->
                Loader.hideLoader()
                Log.d("LoginFrag", "sendOtp: $response")
                val status = response.getString("status")
                val message = response.getString("message")

                if (status.equals("success")){
                    mBinding.editText.text.clear()
                    mBinding.editText2.text.clear()
                    val user_id = response.getString("user_id")
                    SharedPrefrenceHelper.userId = user_id
                    MainActivity.USER_ID = user_id
                    val srgs = Gson().toJson(slotDTO)
                    val internt = Intent(this@LoginScreen , SlotDetail::class.java)
                    internt.putExtra("data", srgs)
                    startActivity(internt)
                }else{
                    showToast(message)
                }

            }, { error ->
                Loader.hideLoader()
                Log.e("LoginFrag", "RESPONSE IS ${error.localizedMessage}")
                Toast.makeText(this@LoginScreen, "Fail to get response", Toast.LENGTH_SHORT)
                    .show()
            })
            // at last we are adding
            // our request to our queue.
            queue.add(request)
        }
    }
}