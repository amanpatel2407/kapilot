package com.kushagency.presentation.addEntry

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.kushagency.R
import com.kushagency.databinding.ActivityCaptureImageBinding
import java.io.File

class CaptureImage : AppCompatActivity() {
    private var CAMERA_PERMISSION = Manifest.permission.CAMERA
    private var RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO

    private var RC_PERMISSION = 101
    lateinit var binding:ActivityCaptureImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_capture_image)
        val recordFiles = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_MOVIES)
        val storageDirectory = recordFiles[0]
        val imageCaptureFilePath = "${storageDirectory.absoluteFile}/${System.currentTimeMillis()}_image.jpg"

        if (checkPermissions()) startCameraSession() else requestPermissions()


        binding.captureImage.setOnClickListener {
            captureImage(imageCaptureFilePath)
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA_PERMISSION, RECORD_AUDIO_PERMISSION), RC_PERMISSION)
    }

    private fun checkPermissions(): Boolean {
        return ((ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            RC_PERMISSION -> {
                var allPermissionsGranted = false
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    } else {
                        allPermissionsGranted = true
                    }
                }
                if (allPermissionsGranted) startCameraSession() else permissionsNotGranted()
            }
        }
    }

    private fun startCameraSession() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        binding.cameraView.bindToLifecycle(this)
    }

    private fun permissionsNotGranted() {
        AlertDialog.Builder(this).setTitle("Permissions required")
            .setMessage("These permissions are required to use this app. Please allow Camera and Audio permissions first")
            .setCancelable(false)
            .setPositiveButton("Grant") { _, _ -> requestPermissions() }
            .show()
    }


    private fun captureImage(imageCaptureFilePath: String) {
       binding.apply {
           cameraView.takePicture(File(imageCaptureFilePath), ContextCompat.getMainExecutor(this@CaptureImage), object: ImageCapture.OnImageSavedCallback {
               override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                   Toast.makeText(this@CaptureImage, "Image Captured", Toast.LENGTH_SHORT).show()
                   Toast.makeText(this@CaptureImage, imageCaptureFilePath, Toast.LENGTH_SHORT).show()
                   Log.d("Mydata", "onImageSaved $imageCaptureFilePath")
                   imagePath=imageCaptureFilePath
                   finish()
                  // startActivity(Intent(applicationContext,CapturePreview::class.java).putExtra("path",imageCaptureFilePath))

               }

               override fun onError(exception: ImageCaptureException) {
                   Toast.makeText(this@CaptureImage, "Image Capture Failed", Toast.LENGTH_SHORT).show()
                   Log.e("cp", "onError $exception")
               }
           })
       }
    }
    companion object{
        var imagePath:String?=null
    }
}
