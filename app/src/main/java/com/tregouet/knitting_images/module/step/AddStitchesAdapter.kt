package com.tregouet.knitting_images.module.step

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Rule
import kotlinx.android.synthetic.main.item_add_stitch.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class AddStitchesAdapter(private val stitches: ArrayList<Rule>, private val selectedStitches: ArrayList<Rule>) : RecyclerView.Adapter<AddStitchesAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_add_stitch, parent,false), selectedStitches)

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(stitches[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = stitches.size


    fun getSelectedStitchIds(): ArrayList<Int> {
        val stitchIds  = ArrayList<Int>()
        selectedStitches.mapTo(stitchIds) { it.id!! }
        return stitchIds
    }
    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View, private val selectedStitches : ArrayList<Rule>) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(stitch: Rule) = with(itemView) {
            itemView.title.text = stitch.stitch
            for (selectedStitch in selectedStitches){
                if (stitch.id == selectedStitch.id){
                    itemView.stitch_background.isSelected = true
                    itemView.stitch_checked.visibility = View.VISIBLE
                }
            }

            itemView.setOnClickListener {
                var isAlreadyInList = false

                for (localStitch in selectedStitches){
                    if (stitch.id == localStitch.id){
                        isAlreadyInList = true
                    }
                }
                if (isAlreadyInList){
                    itemView.stitch_background.isSelected = false
                    itemView.stitch_checked.visibility = View.INVISIBLE
                    for (localStitch in selectedStitches){
                        if (stitch.id == localStitch.id){
                            selectedStitches.remove(localStitch)
                            break
                        }
                    }
                } else {
                    itemView.stitch_background.isSelected = true
                    itemView.stitch_checked.visibility = View.VISIBLE
                    selectedStitches.add(stitch)
                }
            }
        }
    }
}