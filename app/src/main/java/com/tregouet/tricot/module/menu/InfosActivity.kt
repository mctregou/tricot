package com.tregouet.tricot.module.menu

import android.os.Bundle
import com.tregouet.tricot.R
import com.tregouet.tricot.module.base.BaseActivity

import com.tregouet.tricot.BuildConfig
import kotlinx.android.synthetic.main.activity_infos.*

class InfosActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos)

        setVersion()
    }

    /**
     * Set application version
     */
    private fun setVersion() {
        version.text = BuildConfig.VERSION_NAME
    }
}
