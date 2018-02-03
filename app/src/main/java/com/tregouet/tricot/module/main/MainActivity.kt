package com.tregouet.tricot.module.main

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
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
import android.view.animation.TranslateAnimation
import com.tregouet.tricot.module.menu.InfosActivity
import com.tregouet.tricot.module.menu.StatsActivity
import com.tregouet.tricot.module.menu.TutosActivity
import kotlinx.android.synthetic.main.menu.*


class MainActivity : BaseActivity() {

    private var adapter : ProjectsAdapter?=null
    private var projects : ArrayList<Project> = ArrayList()
    private var materialMenu: MaterialMenuDrawable? = null

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        materialMenu = MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN)
        burger.setImageDrawable(materialMenu)
        burger.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)){
                materialMenu!!.animateIconState(MaterialMenuDrawable.IconState.BURGER)
                drawer_layout.closeDrawers()
            } else {
                materialMenu!!.animateIconState(MaterialMenuDrawable.IconState.ARROW)
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }

        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                materialMenu?.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        if (drawer_layout.isDrawerOpen(GravityCompat.START)) 2 - slideOffset else slideOffset
                )
            }

            override fun onDrawerStateChanged(newState: Int) {
                if(newState == DrawerLayout.STATE_IDLE) {
                    if (drawer_layout.isDrawerOpen(GravityCompat.START)){
                        materialMenu?.iconState = MaterialMenuDrawable.IconState.ARROW
                    } else {
                        materialMenu?.iconState = MaterialMenuDrawable.IconState.BURGER
                    }
                }
            }
        })

        fab.setOnClickListener { _ ->
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_project)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.validate_project.setOnClickListener{
                if (dialog.project_title.text.toString() != "") {
                    val index = RealmManager().createProjectDao().nextId()
                    RealmManager().createProjectDao().save(Project(index, dialog.project_title.text.toString()))
                    getProjects()
                }
                dialog.dismiss()
            }
            dialog.show()
        }

        how_it_works.setOnClickListener { showHowItWorks() }
        my_stats.setOnClickListener { showMyStats() }
        infos.setOnClickListener { showInfos() }

    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()

        getProjects()
    }

    /**
     * Get all projects from database
     */
    private fun getProjects(){
        RealmManager().open()
        projects = ArrayList(RealmManager().createProjectDao().loadAll().toList())
        RealmManager().close()

        projects_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = ProjectsAdapter(this, projects)
        projects_recyclerview.adapter = adapter

        if (projects.isEmpty()){
            no_step_layout.visibility = View.VISIBLE
            startAnimation()
        } else {
            no_step_layout.visibility = View.GONE
        }
    }

    /**
     * Start the arrow animation
     */
    private fun startAnimation() {
        val animation = TranslateAnimation(-100f, 100f, 0f, 0f)
        animation.duration = 700
        animation.fillAfter = true
        animation.repeatCount = ValueAnimator.INFINITE
        arrow.startAnimation(animation)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawers()
            return
        }

        super.onBackPressed()
    }

    private fun showHowItWorks() {
        drawer_layout.closeDrawers()
        startActivity(Intent(this, TutosActivity::class.java))
    }

    private fun showMyStats() {
        drawer_layout.closeDrawers()
        startActivity(Intent(this, StatsActivity::class.java))
    }

    private fun showInfos() {
        drawer_layout.closeDrawers()
        startActivity(Intent(this, InfosActivity::class.java))
    }
}
