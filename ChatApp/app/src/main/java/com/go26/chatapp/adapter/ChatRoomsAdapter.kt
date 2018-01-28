package com.go26.chatapp.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.UserRowViewHolder
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.userMap
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.ui.ChatFragment
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class ChatRoomsAdapter(val context: Context) : RecyclerView.Adapter<UserRowViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserRowViewHolder =
            UserRowViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: UserRowViewHolder, position: Int) {
        var user2 = UserModel()
        val community = DataConstants.myCommunities.get(position)

        if (community.community!!) {
            holder.tvName.text = community.name

            if (!community.lastMessage?.message?.isEmpty()!!) {
                holder.tvEmail.text = userMap?.get(community?.lastMessage?.sender_id!!)?.name?.substring(0, 6) + ": " + community?.lastMessage?.message
            } else {
                holder.tvEmail.text = "No messages in the communities"
            }

            if (community.members.get(currentUser?.uid!!)?.unreadCommunityCount!! > 0) {
                holder.tvUnreadCount.visibility = View.VISIBLE
                holder.tvUnreadCount.text = community.members.get(currentUser?.uid!!)?.unreadCommunityCount!!.toString()
            } else {
                holder.tvUnreadCount.visibility = View.GONE
            }

            loadRoundImage(holder.ivProfile, community.imageUrl!!)


        } else {

            for (member in community.members) {
                if (member.key != currentUser?.uid) {
                    user2 = member.value
                }
            }
            user2 = userMap?.get(user2.uid)!!

            holder.tvName.text = user2.name!!

            if (!community.lastMessage?.message?.isEmpty()!!) {
                holder.tvEmail.text = userMap?.get(community?.lastMessage?.sender_id!!)?.name?.substring(0, 6) + ": " + community?.lastMessage?.message
            } else {
                holder.tvEmail.text = "No messages"
            }

            if (community.members.get(currentUser?.uid!!)?.unreadCommunityCount!! > 0) {
                holder.tvUnreadCount.visibility = View.VISIBLE
                holder.tvUnreadCount.text = community.members.get(currentUser?.uid!!)?.unreadCommunityCount!!.toString()
            } else {
                holder.tvUnreadCount.visibility = View.GONE
            }

            loadRoundImage(holder.ivProfile, user2?.imageUrl!!)

        }

        holder.layout.setOnClickListener({

            val chatFragment =
                    if (community.community!!) {
                        ChatFragment.newInstance(community.communityId!!, AppConstants().COMMUNITY_CHAT, "", position)
                    } else {
                        ChatFragment.newInstance(community.communityId!!, AppConstants().ONE_ON_ONE_CHAT, user2.uid!!, position)
                    }

            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, chatFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })


    }

    override fun getItemCount(): Int = DataConstants.myCommunities.size

}