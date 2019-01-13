package com.tregouet.knitting_images.module.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
import com.balysv.materialmenu.MaterialMenuDrawable
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.module.base.BaseActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.top_bar.*
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.tregouet.knitting_images.model.Project
import com.tregouet.knitting_images.model.Step
import com.tregouet.knitting_images.module.menu.InfosActivity
import com.tregouet.knitting_images.module.menu.StatsActivity
import com.tregouet.knitting_images.module.menu.TutosActivity
import com.tregouet.knitting_images.module.projects.ProjectsActivity
import com.tregouet.knitting_images.module.step.StepActivity
import com.tregouet.knitting_images.module.stitches.StitchesActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.menu.*


class MainActivity : BaseActivity() {

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

        how_it_works.setOnClickListener { showHowItWorks() }
        my_stats.setOnClickListener { showMyStats() }
        infos.setOnClickListener { showInfos() }

        current_step_layout.setOnClickListener { showCurrentStep() }
        projects_layout.setOnClickListener { showProjects() }
        rules_layout.setOnClickListener { showRules() }
    }

    override fun onResume() {
        super.onResume()

        val step = getCurrentStep()
        if (step != null){
            welcome.visibility = View.GONE
            current_step_layout.visibility = View.VISIBLE
            val project = getCurrentProject(step.projectId!!)
            project_name.text = step.name
            step_name.text = project.name
            current_rank.text = step.currentRank.toString()
        } else {
            welcome.visibility = View.VISIBLE
            current_step_layout.visibility = View.GONE
        }

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

    private fun getCurrentStep(): Step? {
        RealmManager().open()
        val step = RealmManager().createStepDao().loadLastStep()
        RealmManager().close()
        return step
    }

    private fun getCurrentProject(projectId: Int): Project {
        RealmManager().open()
        val project = RealmManager().createProjectDao().loadBy(projectId)
        RealmManager().close()
        return project
    }

    private fun showCurrentStep() {

        RealmManager().open()
        var step = RealmManager().createStepDao().loadLastStep()
        RealmManager().close()

        if (step != null) {
            val intent = Intent(Intent(this, StepActivity::class.java))
            intent.putExtra(Constants().STEP_ID, step.id)
            intent.putExtra(Constants().PROJECT_ID, step.projectId)
            startActivity(intent)
        }
    }


    private fun showProjects() {
        startActivity(Intent(this, ProjectsActivity::class.java))
    }

    private fun showRules() {
        startActivity(Intent(this, StitchesActivity::class.java))
    }
}