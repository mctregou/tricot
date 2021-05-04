package com.tregouet.tricot.module.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import com.tregouet.tricot.R

import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.pager.view.*


class TutosFragment() : Fragment() {

    var message: Int? = null
    var resource: Int? = null

    @SuppressLint("ValidFragment")
    constructor(text: Int, image: Int?) : this() {
        this.message = text
        this.resource = image
    }

    /**
     * onCreate
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pager, container, false) as ViewGroup

        view.text.text = getString(message!!)
        view.image.setImageResource(resource!!)


        return view
    }
}
