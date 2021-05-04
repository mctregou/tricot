package com.tregouet.tricot.module.step

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.ReductionItem
import kotlinx.android.synthetic.main.item_reduction_item_for_popup.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class ReductionItemsForPopupAdapter(private val rulesActivity: StepActivity, private var reductionItemsPopup: ArrayList<ReductionItem>) : RecyclerView.Adapter<ReductionItemsForPopupAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_reduction_item_for_popup, parent,false), this)

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(reductionItemsPopup[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = reductionItemsPopup.size

    /**
     *
     */
    fun setReductionItems(reductionItems: ArrayList<ReductionItem>){
        this.reductionItemsPopup = reductionItems
        notifyDataSetChanged()
    }

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View, private val reductionItemsAdapter: ReductionItemsForPopupAdapter) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(reductionItem: ReductionItem) = with(itemView) {
            itemView.position.text = reductionItem.order.toString()
            itemView.times.text = reductionItem.times.toString()
            itemView.nb_mesh.text = reductionItem.numberOfMesh.toString()
            itemView.side.text = reductionItem.side

            itemView.remove.setOnClickListener {
                reductionItemsAdapter.reductionItemsPopup.remove(reductionItem)
                reductionItemsAdapter.notifyDataSetChanged()
            }
        }
    }
}