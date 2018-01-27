package com.tregouet.tricot.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.tregouet.tricot.R
import kotlinx.android.synthetic.main.global_popup.*

/**
 * Created by mariececile.tregouet on 27/01/2018.
 */
class Utils {

    fun showDialog(context : Context, title : Int, message : Int, listener : View.OnClickListener) : Dialog{
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.global_popup)
        dialog.title.text = context.getString(title)
        dialog.message.text = context.getString(message)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.ok.setOnClickListener(listener)
        dialog.cancel.setOnClickListener { dialog.dismiss() }
        return dialog
    }
}