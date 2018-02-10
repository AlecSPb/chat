package com.go26.chatapp.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils

/**
 * Created by daigo on 2018/01/24.
 */
class CommunityRequestsAdapter(private val context: Context, private val communityRequestList: MutableList<Pair<String, UserModel>>) : RecyclerView.Adapter<CommunityRequestsAdapter.RequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestViewHolder =
            RequestViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_community_request, parent, false))

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.userName.text = communityRequestList[position].second.name

        for (myCommunity in DataConstants.myCommunities) {
            if (myCommunity.communityId == communityRequestList[position].first) {
                holder.communityName.text = myCommunity.name
            }
        }
        MyViewUtils.loadRoundImage(holder.profileImage, communityRequestList[position].second.imageUrl!!)
        holder.confirmButton.visibility = View.VISIBLE
        holder.confirmButton.setOnClickListener {
            holder.confirmButton.visibility = View.INVISIBLE
            holder.disconfirmButton.visibility = View.INVISIBLE

            MyChatManager.setmContext(context)
            MyChatManager.confirmCommunityJoinRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    holder.isConfirmed.visibility = View.VISIBLE
                    holder.isConfirmed.text = "承認済み"
                }
            }, communityRequestList[position].second.uid!!, communityRequestList[position].first, NetworkConstants().CONFIRM_REQUEST)

        }
        holder.disconfirmButton.visibility = View.VISIBLE
        holder.disconfirmButton.setOnClickListener {
            holder.confirmButton.visibility = View.INVISIBLE
            holder.disconfirmButton.visibility = View.INVISIBLE

            MyChatManager.setmContext(context)
            MyChatManager.disconfirmCommunityJoinRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    holder.isConfirmed.visibility = View.VISIBLE
                    holder.isConfirmed.text = "拒否済み"
                }
            }, communityRequestList[position].second.uid!!, communityRequestList[position].first, NetworkConstants().DISCONFIRM_REQUEST)

        }

    }

    override fun getItemCount(): Int = communityRequestList.size

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view)
        val userName: TextView = itemView.findViewById(R.id.name_text_view)
        val communityName: TextView = itemView.findViewById(R.id.community_name_text)
        val confirmButton: Button = itemView.findViewById(R.id.confirm_button)
        val disconfirmButton: Button = itemView.findViewById(R.id.disconfirm_button)
        val isConfirmed: TextView = itemView.findViewById(R.id.isConfirmed_text)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
    }
}
