package com.go26.chatapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.go26.chatapp.InfiniteFirebaseRecyclerAdapter
import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.userMap
import com.go26.chatapp.model.MessageModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyTextUtil
import com.go26.chatapp.util.SharedPrefManager
import com.google.firebase.database.Query

/**
 * Created by daigo on 2018/02/12.
 */
class ChatAdapter(var context: Context, ref: Query, itemsPerPage: Int, deleteTill: String, chat_messages_recycler: RecyclerView)  :
        InfiniteFirebaseRecyclerAdapter<MessageModel, ChatAdapter.ViewHolder>(
                MessageModel::class.java, R.layout.item_chat_row,
                ChatAdapter.ViewHolder::class.java, ref, itemsPerPage, deleteTill, chat_messages_recycler) {

    var currentUser: UserModel = SharedPrefManager.getInstance(context).savedUserModel!!

    override fun populateViewHolder(viewHolder: ViewHolder, model: MessageModel?, position: Int) {
        val chatMessage = model!!
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (chatMessage.sender_id.toString() == currentUser.uid) {
            viewHolder.llParent.gravity = Gravity.END
            viewHolder.name.visibility = View.GONE
            lp.gravity = Gravity.END
            viewHolder.message.layoutParams = lp
        } else {
            viewHolder.llParent.gravity = Gravity.START

            val name = userMap?.get(chatMessage.sender_id!!)?.name
            if (name != null) {
                viewHolder.name.visibility = View.VISIBLE
                viewHolder.name.text = name
            } else {
                viewHolder.name.visibility = View.GONE
            }
            lp.gravity = Gravity.START
            viewHolder.message.layoutParams = lp
        }
        viewHolder.message.text = chatMessage.message

        try {
            viewHolder.timestamp.text = MyTextUtil().getTimestamp(chatMessage.timestamp?.toLong()!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        viewHolder.rlName.layoutParams.width = viewHolder.message.layoutParams.width
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llParent = itemView.findViewById(R.id.ll_parent) as LinearLayout
        var name = itemView.findViewById(R.id.name) as TextView
        var timestamp = itemView.findViewById(R.id.timestamp) as TextView
//        var rlName = itemView.findViewById(R.id.rl_name) as RelativeLayout
        var message = itemView.findViewById(R.id.message) as TextView
    }
}