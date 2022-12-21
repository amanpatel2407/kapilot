package com.kushagency.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.kushagency.databinding.LayoutLoaderBinding

object Loader {
    private lateinit var dialog : Dialog

    fun showLoader(context : Activity){
       val  dialogMainBinding= LayoutLoaderBinding.inflate(context.layoutInflater)
        dialog= Dialog(context)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogMainBinding.root);
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    fun  hideLoader(){
        try {
            dialog.hide()
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

}