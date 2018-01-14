package com.tregouet.tricot.module.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.utils.RealmManager

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.popup_add_project.*

class MainActivity : AppCompatActivity() {

    private var adapter : ProjectsAdapter?=null
    private var projects : ArrayList<Project> = ArrayList<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_project)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.validate_project.setOnClickListener{
                if (dialog.project_title.text.toString() != "") {
                    RealmManager.open()
                    var index = RealmManager.createProjectDao().nextId()
                    RealmManager.createProjectDao().save(Project(index, dialog.project_title.text.toString()))
                    RealmManager.close()
                    getProjects()
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()

        getProjects()
    }

    private fun getProjects(){
        RealmManager.open()
        projects = ArrayList(RealmManager.createProjectDao().loadAll().toList())
        RealmManager.close()

        projects_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = ProjectsAdapter(this, projects)
        projects_recyclerview.adapter = adapter
    }
}
