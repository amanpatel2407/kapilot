package com.kushagency.presentation.addEntry

import android.Manifest
import android.R.attr.thumbnail
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.kushagency.databinding.ActivityAddVehiclePastingBinding
import com.kushagency.network.VolleyMultipartRequest
import com.kushagency.presentation.SlotDetail
import com.kushagency.presentation.home.MainActivity
import com.kushagency.presentation.home.MainActivity.Companion.ADDRESS
import com.kushagency.presentation.home.MainActivity.Companion.LATITUDE
import com.kushagency.presentation.home.MainActivity.Companion.LONGITUDE
import com.kushagency.presentation.home.MainActivity.Companion.SLOTID
import com.kushagency.utils.BASE_URL
import com.kushagency.utils.Loader
import com.kushagency.utils.isValidVehicleNumber
import com.kushagency.utils.showToast
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class AddVehiclePasting : AppCompatActivity() {
    private lateinit var mBinding: ActivityAddVehiclePastingBinding
    private val IMAGE_CHOOSE = 1000;
    private val PICK_IMAGE_CAMERA = 2000;
    private val PERMISSION_CODE = 1001;
    private var IMAGE_BITMAP: Bitmap? = null
    private var imageUri: Uri? = null
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var rQueue: RequestQueue
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddVehiclePastingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        Thread {
            //run code on background thread
            this.runOnUiThread {
                getLocation()
            }
        }.start()
        mBinding.textView.text = "Add Pasting Info"

        mBinding.ivBack.setOnClickListener {
            finish()
        }

        mBinding.vehicleNumber.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty() && text.length == 10) {
                if (!isValidVehicleNumber(text.toString())) {
                    mBinding.vehicleNumber.error = "Enter valid vehicle number"
                } else {
                    val search = SlotDetail.SLOTS_LIST.filter {
                        it.autoNumber.toUpperCase() == text.toString().toUpperCase()
                    }
                    if (search.size > 0) {
                        mBinding.vehicleNumber.error =
                            "Vehicle number already register in this Slot."
                    } else {
                        mBinding.vehicleNumber.error = null
                    }
                }
            }
        }
        mBinding.vehicleImg.setOnClickListener {
            selectImage()
        }
        mBinding.Button01.setOnClickListener {

            if (mBinding.vehicleNumber.text.toString().trim().isNullOrEmpty()) {
                mBinding.vehicleNumber.apply {
                    error = "Enter vehicle number"
                    requestFocus()
                }
            } else if (mBinding.driveName.text.toString().trim().isNullOrEmpty()) {
                mBinding.driveName.apply {
                    error = "Enter drive name"
                    requestFocus()
                }
            } else if (mBinding.driverNumber.text.toString().trim().isNullOrEmpty()) {
                mBinding.driverNumber.apply {
                    error = "Enter drive number"
                    requestFocus()
                }
            } else if (IMAGE_BITMAP == null) {
                showToast("Select Image")
            } else {

                uploadImage(IMAGE_BITMAP!!)
            }
        }
    }

    private fun getLocation() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }else{
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0.5f) { location ->
                Log.d(
                    "currentlocationlatlong",
                    "onLocationChanged: " + "Latitude: ${location.extras} " + location.latitude + " , Longitude: " + location.longitude
                )

                showToast("true")
                LONGITUDE = location.longitude.toString()
                LATITUDE = location.latitude.toString()
                getMapLocation(location.latitude, location.longitude)


            }
        }

    }
    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                selectImagePopUp()
            }
        } else {
           /// selectImagePopUp()
        }
    }

    private fun selectImagePopUp(){
        IMAGE_BITMAP = null
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                dialog.dismiss()
               val  values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, PICK_IMAGE_CAMERA)

            } else if (options[item] == "Choose From Gallery") {
                dialog.dismiss()
                chooseImageGallery()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImagePopUp()
                } else {
                    selectImage()
                }
            }

            locationPermissionCode ->{
                getLocation()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {

            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                applicationContext.getContentResolver(),
                data?.data
            )
            IMAGE_BITMAP = bitmap
            mBinding.vehicleImg.setImageURI(data?.data)

        }else if (requestCode == PICK_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {

            try {

                try {
                   IMAGE_BITMAP = MediaStore.Images.Media.getBitmap(
                        contentResolver, imageUri
                    )
                    mBinding.vehicleImg.setImageBitmap(IMAGE_BITMAP)
                  ///  imageurl = getRealPathFromURI(imageUri)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        Loader.showLoader(this)
        val volleyMultipartRequest: VolleyMultipartRequest =
            object : VolleyMultipartRequest(
                Request.Method.POST, "$BASE_URL/addUserAd", { response ->

                    val jsonObject = JSONObject(String(response.data))
                    Log.d("data", "uploadImage: ${jsonObject}")

                    val id = "ads_id"
                    Loader.hideLoader()
                    showToast(jsonObject.getString("message"))

                    finish()
                    jsonObject.toString().replace("\\\\", "")

                },
                { error ->
                    Loader.hideLoader()
                    Log.d("data", "uploadImage: $error")
                }) {
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String, String>()
                    params.put("user_id", MainActivity.USER_ID); // add string parameters
                    params.put("slot_id", SLOTID); // add string parameters
                    params.put("driver_name", mBinding.driveName.text.toString() ); // add string parameters
                    params.put("mobile_number",mBinding.driverNumber.text.toString()); // add string parameters
                    params.put("auto_number", mBinding.vehicleNumber.text.toString()); // add string parameters
                    params.put("location", ADDRESS); // add string parameters
                    params.put("latitude", LATITUDE.toString()); // add string parameters
                    params.put("longitude", LONGITUDE.toString()); // add string parameters
                    // params.put("tags", "ccccc");  add string parameters
                    return params
                }

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params = HashMap<String, DataPart>()
                    val imagename = System.currentTimeMillis()
                    params["file"] = DataPart(
                        "${SlotDetail.SLOTS_LIST.size + 2}" + ".png",
                        getFileDataFromDrawable(bitmap)
                    )
                    return params
                }
            }

        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this)
        rQueue.add(volleyMultipartRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
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
            MainActivity.ADDRESS =
                addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        }catch (e : Exception){

        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

}