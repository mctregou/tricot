package com.tregouet.knitting_images.module

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tregouet.knitting_images.module.main.MainActivity

class SplashActivity : AppCompatActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}