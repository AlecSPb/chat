package com.go26.chatapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.UserRowViewHolder
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class ParticipantsAdapter(var callback: NotifyMeInterface, var type: String, var groupId: String) : RecyclerView.Adapter<UserRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserRowViewHolder =
            UserRowViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: UserRowViewHolder, position: Int) {
        val user = DataConstants.selectedUserList[position]

        try {
            holder.tvName.text = user.name
            holder.tvEmail.text = user.email

            loadRoundImage(holder.ivProfile, user.imageUrl!!)

            //Only admin get to see overflow menu of communities members
            if (DataConstants.communityMap?.get(groupId)?.members?.get(DataConstants.currentUser?.uid)?.admin!!) {
                holder.ivOverflow.visibility = View.VISIBLE
            } else {
                holder.ivOverflow.visibility = View.INVISIBLE
            }



            if (user.admin != null && user.admin!!) {
                holder.labelAdmin.visibility = View.VISIBLE
                holder.tvMakeAdmin.text = "Remove Admin"

            } else {
                holder.labelAdmin.visibility = View.GONE
                holder.tvMakeAdmin.text = "Make Admin"
                user.admin = false
            }

            holder.ivOverflow.setOnClickListener({
                holder.llOverflowItems.visibility = View.VISIBLE
            })

            holder.tvMakeAdmin.setOnClickListener({
                when (type) {
                    AppConstants().CREATION -> {
                        holder.llOverflowItems.visibility = View.GONE
                        if (holder.tvMakeAdmin.text.equals("Make Admin")) {
                            user.admin = true
                            holder.tvMakeAdmin.text = "Remove Admin"
                            holder.labelAdmin.visibility = View.VISIBLE
                        } else {
                            user.admin = false
                            holder.tvMakeAdmin.text = "Make Admin"
                            holder.labelAdmin.visibility = View.GONE
                        }
                    }

                    AppConstants().DETAILS -> {
                        holder.llOverflowItems.visibility = View.GONE
                        if (holder.tvMakeAdmin.text.equals("Make Admin")) {
                            user.admin = true
                            holder.tvMakeAdmin.text = "Remove Admin"
                            holder.labelAdmin.visibility = View.VISIBLE
                        } else {
                            user.admin = false
                            holder.tvMakeAdmin.text = "Make Admin"
                            holder.labelAdmin.visibility = View.GONE

                        }
                        MyChatManager.changeAdminStatusOfUser(null, groupId, user.uid, user.admin!!)
                    }
                }

            })

            holder.tvRemoveMember.setOnClickListener({
                when (type) {
                    AppConstants().CREATION -> {
                        holder.llOverflowItems.visibility = View.GONE
                        DataConstants.selectedUserList.remove(user)
                        notifyDataSetChanged()
                        callback.handleData(true, 1)
                    }

                    AppConstants().DETAILS -> {
                        holder.llOverflowItems.visibility = View.GONE
                        DataConstants.selectedUserList.remove(user)
                        MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                if (obj as Boolean) {
                                    notifyDataSetChanged()
                                }
                            }

                        }, groupId, user.uid)
                    }
                }


            })


            holder.layout.setOnClickListener({
                holder.llOverflowItems.visibility = View.GONE
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int = DataConstants.selectedUserList.size


}