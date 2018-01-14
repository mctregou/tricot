package com.tregouet.tricot.module.main

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.module.project.ProjectActivity
import com.tregouet.tricot.utils.Constants
import kotlinx.android.synthetic.main.item_project.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ProjectsAdapter(private val context: Context, private var projects: ArrayList<Project>) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])

    override fun getItemCount() = projects.size

    class ViewHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(project: Project) = with(itemView) {
            itemView.title.text = project.name
            System.out.println("Project id=" + project.id)
            setOnClickListener { openProject(project.id) }
        }

        private fun openProject(projectId:Int?){
            val intent = Intent(Intent(context, ProjectActivity::class.java))
            intent.putExtra(Constants().PROJECT_ID, projectId)
            context.startActivity(intent)
        }
    }

    fun setProjects(projects: ArrayList<Project>) {
        this.projects = projects
        notifyDataSetChanged()
    }
}