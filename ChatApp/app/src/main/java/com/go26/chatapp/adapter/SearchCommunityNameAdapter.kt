package com.go26.chatapp.adapter

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.go26.chatapp.R
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.util.MyViewUtils

/**
 * Created by daigo on 2018/01/23.
 */
class SearchCommunityNameAdapter(private val foundCommunityList: ArrayList<CommunityModel>, private val itemClick: (Int) -> Unit)
    : RecyclerView.Adapter<SearchCommunityNameAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchViewHolder =
            SearchViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search, parent, false), itemClick)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.communityName.text = foundCommunityList[position].name
        MyViewUtils.loadRoundImage(holder.profileImage, foundCommunityList[position].imageUrl!!)

        holder.setUp(position)
    }

    override fun getItemCount(): Int = foundCommunityList.size

    class SearchViewHolder(itemView: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image) as AppCompatImageView
        val communityName: TextView = itemView.findViewById(R.id.name_text)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)

        fun setUp(position: Int) {
            layout.setOnClickListener { itemClick(position) }
        }
    }

}