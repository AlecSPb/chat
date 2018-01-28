package com.go26.chatapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.UserRowViewHolder
import com.go26.chatapp.constants.DataConstants.Companion.mapList
import com.go26.chatapp.constants.DataConstants.Companion.myFriends
import com.go26.chatapp.constants.DataConstants.Companion.userList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class UserListAdapter(context: Context, var callback: NotifyMeInterface) : RecyclerView.Adapter<UserRowViewHolder>() {

    var holderMap: MutableMap<String, UserRowViewHolder> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserRowViewHolder =
            UserRowViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: UserRowViewHolder, position: Int) {
        val user = myFriends[position]

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email

        loadRoundImage(holder.ivProfile, user.imageUrl!!)

        holder.layout.setOnClickListener {

            if (mapList.containsKey(user.uid)) {
                //Already Selected remove from the list
                mapList.remove(user.uid!!)
                holder.ivSelected.visibility = View.INVISIBLE
                holderMap.remove(user.uid!!)
                callback.handleData(user, NetworkConstants().USER_REMOVED)
            } else {
                //User haven't selected the member so add him to list
                mapList.put(user.uid!!, user)
                holder.ivSelected.visibility = View.VISIBLE
                holderMap.put(user.uid!!, holder)
                callback.handleData(user, NetworkConstants().USER_ADDED)
            }
        }
    }

    fun resetView(uid: String) {
        holderMap[uid]?.ivSelected?.visibility = View.GONE
    }

    override fun getItemCount(): Int = myFriends.size


}