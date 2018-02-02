package com.go26.chatapp.adapter

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
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.FriendModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils
import java.util.HashMap

/**
 * Created by daigo on 2018/01/24.
 */
class FriendRequestsAdapter(private val friendRequests: MutableList<UserModel>, private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<FriendRequestsAdapter.RequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestViewHolder =
            RequestViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_contact, parent, false), itemClick)

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.userName.text = friendRequests[position].name
        MyViewUtils.loadRoundImage(holder.profileImage, friendRequests[position].imageUrl!!)
        holder.confirmButton.visibility = View.VISIBLE

        holder.confirmButton.setOnClickListener {
            holder.confirmButton.visibility = View.INVISIBLE
            holder.disconfirmButton.visibility = View.INVISIBLE

            val friendModel = FriendModel(friendDeleted = false)
            val members: HashMap<String, UserModel> = hashMapOf()
            members.put(currentUser?.uid!!, currentUser!!)
            members.put(friendRequests[position].uid!!, friendRequests[position])

            friendModel.members = members

            MyChatManager.confirmFriendRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    holder.isConfirmed.visibility = View.VISIBLE
                    holder.isConfirmed.text = "承認済み"
                }
            }, DataConstants.currentUser?.uid!!, friendRequests[position].uid!!, friendModel, NetworkConstants().CONFIRM_REQUEST)

        }
        holder.disconfirmButton.visibility = View.VISIBLE
        holder.disconfirmButton.setOnClickListener {
            holder.confirmButton.visibility = View.INVISIBLE
            holder.disconfirmButton.visibility = View.INVISIBLE

            MyChatManager.disconfirmFriendRequest(object : NotifyMeInterface {
                override fun handleData(obj: Any, requestCode: Int?) {
                    holder.isConfirmed.visibility = View.VISIBLE
                    holder.isConfirmed.text = "拒否済み"
                }
            }, DataConstants.currentUser?.uid!!, friendRequests[position].uid!!, NetworkConstants().DISCONFIRM_REQUEST)

        }
        holder.setUp(position)

    }

    override fun getItemCount(): Int = friendRequests.size

    class RequestViewHolder(itemView: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image)
        val userName: TextView = itemView.findViewById(R.id.name_text)
        val confirmButton: Button = itemView.findViewById(R.id.confirm_button)
        val disconfirmButton: Button = itemView.findViewById(R.id.disconfirm_button)
        val isConfirmed: TextView = itemView.findViewById(R.id.isConfirmed_text)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)

        fun setUp(position: Int) {
            layout.setOnClickListener { itemClick(position) }
        }
    }
}