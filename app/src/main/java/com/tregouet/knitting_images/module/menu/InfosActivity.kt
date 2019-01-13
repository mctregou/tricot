package com.tregouet.knitting_images.module.menu

import android.os.Bundle
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.module.base.BaseActivity

import com.instabug.library.Instabug
import com.tregouet.knitting_images.BuildConfig
import kotlinx.android.synthetic.main.activity_infos.*

class InfosActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos)

        showInstabugTuto()

        setVersion()
    }

    /**
     * Show instabug message
     */
    private fun showInstabugTuto() {
        contact_message.setOnClickListener {
            Instabug.showIntroMessage()
        }
    }

    /**
     * Set application version
     */
    private fun setVersion() {
        version.text = BuildConfig.VERSION_NAME
    }
}
