package jp.gr.java_conf.cody.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.popularCommunityList
import jp.gr.java_conf.cody.util.MyViewUtils

/**
 * Created by daigo on 2018/02/09.
 */
class SearchAdapter(val context: Context, private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchViewHolder =
            SearchViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_search_community, parent, false), itemClick)

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.communityName.text = popularCommunityList[position].name

        if (popularCommunityList[position].location != null) {
            val location = "活動場所: " + popularCommunityList[position].location
            holder.location.text = location
        }

        val memberCount = "メンバー: " + popularCommunityList[position].memberCount?.toString() + "人"
        holder.memberCount.text = memberCount

        MyViewUtils.loadImageFromUrl(holder.profileImage, popularCommunityList[position].imageUrl)

        holder.setUp(position)
    }

    override fun getItemCount(): Int = popularCommunityList.size

    class SearchViewHolder(itemView: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        val communityName: TextView = itemView.findViewById(R.id.name_text_view)
        val location: TextView = itemView.findViewById(R.id.location_text_view)
        val memberCount: TextView = itemView.findViewById(R.id.member_count_text_view)
        val layout: LinearLayout = itemView.findViewById(R.id.parent_layout)

        fun setUp(position: Int) {
            layout.setOnClickListener { itemClick(position) }
        }
    }
}