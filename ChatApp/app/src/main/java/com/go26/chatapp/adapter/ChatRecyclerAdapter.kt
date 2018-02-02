package com.go26.chatapp.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.userMap
import com.go26.chatapp.model.MessageModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyTextUtil
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.database.Query

/**
 * Created by daigo on 2018/01/18.
 */
class ChatRecyclerAdapter(var context: Context, var ref: Query)  :
        FirebaseRecyclerAdapter<MessageModel, ChatRecyclerAdapter.ViewHolder>(
                MessageModel::class.java, R.layout.item_chat_row,
                ChatRecyclerAdapter.ViewHolder::class.java, ref) {

    var firstMessage: MessageModel = MessageModel()
    var totalCount: Int = 0
    var currentUser: UserModel = SharedPrefManager.getInstance(context).savedUserModel!!

    override fun populateViewHolder(holder: ViewHolder?, model: MessageModel?, position: Int) {
        val viewHolder = holder as ViewHolder
        val chatMessage = model!!
        totalCount = position
        if (position == 0) {
            firstMessage = chatMessage
        }

        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (chatMessage.sender_id.toString() == currentUser.uid) {
            viewHolder.llParent.gravity = Gravity.END
            viewHolder.name.text = "You"

            lp.gravity = Gravity.RIGHT
            viewHolder.message.layoutParams = lp
        } else {
            viewHolder.llParent.gravity = Gravity.START
            viewHolder.name.text = userMap?.get(chatMessage.sender_id!!)?.name

            lp.gravity = Gravity.LEFT
            viewHolder.message.layoutParams = lp
        }
        viewHolder.message.text = chatMessage.message
        try {
            viewHolder.timestamp.text = MyTextUtil().getTimestamp(chatMessage.timestamp?.toLong()!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        viewHolder.rlName.layoutParams.width = viewHolder.message.layoutParams.width

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_chat_row, parent, false))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llParent = itemView.findViewById(R.id.ll_parent) as LinearLayout
        var llChild = itemView.findViewById(R.id.ll_child) as LinearLayout
        var name = itemView.findViewById(R.id.name) as TextView
        var timestamp = itemView.findViewById(R.id.timestamp) as TextView
        var rlName = itemView.findViewById(R.id.rl_name) as RelativeLayout
        var message = itemView.findViewById(R.id.message) as TextView
    }
}