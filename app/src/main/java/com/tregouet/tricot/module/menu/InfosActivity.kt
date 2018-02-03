package com.tregouet.tricot.module.menu

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.LinearLayoutManager
import com.balysv.materialmenu.MaterialMenuDrawable
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.module.base.BaseActivity
import com.tregouet.tricot.utils.RealmManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.popup_add_project.*
import kotlinx.android.synthetic.main.top_bar.*
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.instabug.library.Instabug
import com.tregouet.tricot.BuildConfig
import kotlinx.android.synthetic.main.activity_infos.*
import kotlinx.android.synthetic.main.menu.*


class InfosActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infos)

        contact_message.setOnClickListener {
            Instabug.showIntroMessage()
        }
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()

        setVersion()
    }

    private fun setVersion() {
        version.text = BuildConfig.VERSION_NAME
    }
}
