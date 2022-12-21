package com.kushagency

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import com.kushagency.presentation.home.MainActivity
import com.kushagency.utils.Loader
import com.kushagency.utils.SharedPrefrenceHelper

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

       /* val image = findViewById<ImageView>(R.id.imageView)
        val scaleX =  ObjectAnimator.ofFloat(image, "scaleX", 1f,1.5f)
        val scaleY =  ObjectAnimator.ofFloat(image, "scaleY", 1f,1.5f)
        val alpha =  ObjectAnimator.ofFloat(image, "alpha", 0f,1f)*/

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.


        Handler().postDelayed({
            Log.d("useriddsd", "onCreate: ${SharedPrefrenceHelper.userId}")

           if (SharedPrefrenceHelper.userId.isNullOrEmpty()){
               val intent = Intent(this, LoginScreen::class.java)
               startActivity(intent)
               finish()
           }else{
               MainActivity.USER_ID = SharedPrefrenceHelper.userId.toString()
               val intent = Intent(this, MainActivity::class.java)
               startActivity(intent)
               finish()
           }

        }, 2000) // 3000 is the delayed time in milliseconds.
    }
}