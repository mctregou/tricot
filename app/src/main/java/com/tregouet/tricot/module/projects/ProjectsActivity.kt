package com.tregouet.tricot.module.projects

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.module.base.BaseActivity
import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.content_projects.*
import kotlinx.android.synthetic.main.popup_add_project.*
import android.view.View
import android.view.animation.TranslateAnimation
import kotlinx.android.synthetic.main.activity_projects.*

class ProjectsActivity : BaseActivity() {

    private var adapter : ProjectsAdapter?=null
    private var projects : ArrayList<Project> = ArrayList()
    private var dialog: Dialog?=null

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)

        fab.setOnClickListener { _ ->
            dialog = Dialog(this)
            dialog?.setContentView(R.layout.popup_add_project)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.validate_project?.setOnClickListener{
                if (dialog?.project_title?.text.toString() != "") {
                    val index = RealmManager().createProjectDao().nextId()
                    RealmManager().createProjectDao().save(Project(index, dialog?.project_title?.text.toString()))
                    getProjects()
                }
                dialog?.dismiss()
            }
            dialog?.show()
        }

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
}
