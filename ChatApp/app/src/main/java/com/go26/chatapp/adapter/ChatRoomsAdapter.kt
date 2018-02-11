package com.go26.chatapp.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.go26.chatapp.R
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.ui.ChatFragment
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import com.google.firebase.database.Query

/**
 * Created by daigo on 2018/01/14.
 */
class ChatRoomsAdapter(val context: Context, var ref: Query) : FirebaseRecyclerAdapter<ChatRoomModel, ChatRoomsAdapter.ViewHolder>(
        ChatRoomModel::class.java, R.layout.item_user,
        ChatRoomsAdapter.ViewHolder::class.java, ref)  {

    override fun populateViewHolder(viewHolder: ViewHolder?, model: ChatRoomModel?, position: Int) {
        viewHolder?.tvName?.text = model?.name

        if (model?.lastMessage != null && model.lastMessage != "") {
            viewHolder?.tvEmail?.text = model.lastMessage
        } else {
            viewHolder?.tvEmail?.text = "No messages in the communities"
        }

        if (model?.unreadCount!! > 0) {
            viewHolder?.tvUnreadCount?.visibility = View.VISIBLE
            viewHolder?.tvUnreadCount?.text = model.unreadCount.toString()
        } else {
            viewHolder?.tvUnreadCount?.visibility = View.GONE
        }

        loadRoundImage(viewHolder?.ivProfile!!, model.imageUrl!!)

        viewHolder.layout.setOnClickListener({

            val chatFragment = ChatFragment.newInstance(model)
            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, chatFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfile = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        var tvName = itemView.findViewById(R.id.tv_name) as TextView
        var tvEmail = itemView.findViewById(R.id.tv_email) as TextView
        var layout = itemView.findViewById(R.id.rl_parent) as RelativeLayout
        var tvUnreadCount = itemView.findViewById(R.id.tv_unreadcount) as TextView
    }
}