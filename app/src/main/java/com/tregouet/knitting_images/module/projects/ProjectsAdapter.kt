package com.tregouet.knitting_images.module.projects

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Project
import com.tregouet.knitting_images.module.project.ProjectActivity
import com.tregouet.knitting_images.utils.Constants
import kotlinx.android.synthetic.main.item_project.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ProjectsAdapter(private val context: Context, private var projects: ArrayList<Project>) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false))

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = projects.size

    /**
     * ViewHolder
     */
    class ViewHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind project to the ViewHolder
         */
        fun bind(project: Project) = with(itemView) {
            itemView.title.text = project.name
            if (project.getRanksNumber() > -1){
                itemView.ranks.text = context.getString(R.string.ranks_number, project.getRanksNumber())
            } else {
                itemView.ranks.text = context.getString(R.string.rank_number, 0)
            }

            setOnClickListener { openProject(project.id) }
        }

        /**
         * Open project screen
         */
        private fun openProject(projectId:Int?){
            val intent = Intent(Intent(context, ProjectActivity::class.java))
            intent.putExtra(Constants.PROJECT_ID, projectId)
            context.startActivity(intent)
        }
    }
}