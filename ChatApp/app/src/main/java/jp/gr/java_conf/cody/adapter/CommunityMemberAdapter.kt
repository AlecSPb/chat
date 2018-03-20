package jp.gr.java_conf.cody.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMemberList
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/02/12.
 */
class CommunityMemberAdapter(val context: Context, private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<CommunityMemberAdapter.CommunityMemberViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommunityMemberViewHolder =
            CommunityMemberViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_community_member, parent, false), itemClick)

    override fun onBindViewHolder(holder: CommunityMemberViewHolder, position: Int) {
        val name = communityMemberList[position].name?.split(Regex("\\s+"))
        val first = name!![0]
        val last = name[1]
        val lastToFirst = last + " " + first
        holder.name.text = lastToFirst
        loadRoundImage(holder.profileImage, communityMemberList[position].imageUrl!!)

        holder.setUp(position)
    }

    override fun getItemCount(): Int = communityMemberList.size

    class CommunityMemberViewHolder(itemView: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        val name: TextView = itemView.findViewById(R.id.name_text_view)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)

        fun setUp(position: Int) {
            layout.setOnClickListener { itemClick(position) }
        }
    }
}