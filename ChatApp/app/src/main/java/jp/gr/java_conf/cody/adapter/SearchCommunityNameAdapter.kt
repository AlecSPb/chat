package jp.gr.java_conf.cody.adapter

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.model.CommunityModel
import jp.gr.java_conf.cody.util.MyViewUtils

/**
 * Created by daigo on 2018/01/23.
 */
class SearchCommunityNameAdapter(private val foundCommunityList: MutableList<CommunityModel>, private val itemClick: (Int) -> Unit)
    : RecyclerView.Adapter<SearchCommunityNameAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchViewHolder =
            SearchViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search_community, parent, false), itemClick)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.communityName.text = foundCommunityList[position].name
        holder.description.text = foundCommunityList[position].description
        
        if (foundCommunityList[position].location != null) {
            val location = "活動場所: " + foundCommunityList[position].location
            holder.location.text = location
        }
        val memberCount = "メンバー: " + foundCommunityList[position].memberCount?.toString() + "人"
        holder.memberCount.text = memberCount
        MyViewUtils.loadRoundImage(holder.profileImage, foundCommunityList[position].imageUrl!!)

        holder.setUp(position)
    }

    override fun getItemCount(): Int = foundCommunityList.size

    class SearchViewHolder(itemView: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        val communityName: TextView = itemView.findViewById(R.id.name_text_view)
        val description: TextView = itemView.findViewById(R.id.description_text_view)
        val location: TextView = itemView.findViewById(R.id.location_text_view)
        val memberCount: TextView = itemView.findViewById(R.id.member_count_text_view)
        val layout: LinearLayout = itemView.findViewById(R.id.parent_layout)

        fun setUp(position: Int) {
            layout.setOnClickListener { itemClick(position) }
        }
    }

}