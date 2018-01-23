package com.go26.chatapp.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.selectedUserList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class UserSelectionAdapter(context: Context, var callback: NotifyMeInterface) : RecyclerView.Adapter<UserSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_selected_user, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = selectedUserList?.get(position)

        holder.tvName.text = user?.name

        loadRoundImage(holder.ivProfile, user?.image_url!!)
        holder.ivSelected.visibility = View.VISIBLE

        holder.ivSelected.setOnClickListener {
            callback.handleData(user.uid!!, NetworkConstants().USER_REMOVED)
            selectedUserList?.remove(user)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = selectedUserList?.size!!

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile = itemView.findViewById(R.id.iv_profile) as AppCompatImageView
        var tvName = itemView.findViewById(R.id.tv_name) as TextView
        var layout = itemView.findViewById(R.id.rl_parent) as RelativeLayout
        var ivSelected = itemView.findViewById(R.id.iv_selected) as AppCompatImageView
    }
}