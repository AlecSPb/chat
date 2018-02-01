package com.go26.chatapp.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.UserRowViewHolder
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.friendList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.ui.ChatRoomsFragment
import com.go26.chatapp.util.MyViewUtils

/**
 * Created by daigo on 2018/01/29.
 */
class FriendContactsAdapter(val context: Context) : RecyclerView.Adapter<UserRowViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserRowViewHolder =
            UserRowViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: UserRowViewHolder, position: Int) {
        holder.tvName.text = friendList[position].name
        holder.tvName.layout
        holder.tvEmail.visibility = View.GONE
        MyViewUtils.loadRoundImage(holder.ivProfile, friendList[position].imageUrl!!)


        holder.layout.setOnClickListener({
//            val friend = friendList[position]
//            val list: MutableList<String> = mutableListOf("トーク")
//            MaterialDialog.Builder(context).items(list).itemsCallback { _, _, _, _ ->
//                val chatRoomModel = ChatRoomModel(friend.uid!!, friend.name!!, friend.imageUrl!!, AppConstants().FRIEND_CHAT)
//                MyChatManager.hasChatRoom(object : NotifyMeInterface {
//                    override fun handleData(obj: Any, requestCode: Int?) {
//                        if (obj as Boolean) {
//                            val chatRoomsFragment = ChatRoomsFragment.newInstance(false, chatRoomModel)
//                            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
//                            val fragmentTransaction = fragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.fragment, chatRoomsFragment)
//                            fragmentTransaction.commit()
//                        } else {
//                            MyChatManager.createChatRoom(object : NotifyMeInterface {
//                                override fun handleData(obj: Any, requestCode: Int?) {
//                                    if (obj as Boolean) {
//                                        val chatRoomsFragment = ChatRoomsFragment.newInstance(false, chatRoomModel)
//                                        val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
//                                        val fragmentTransaction = fragmentManager.beginTransaction()
//                                        fragmentTransaction.replace(R.id.fragment, chatRoomsFragment)
//                                        fragmentTransaction.commit()
//                                    }
//                                }
//                            }, DataConstants.currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
//                        }
//                    }
//                }, DataConstants.currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
//            }.show()
        })
    }

    override fun getItemCount(): Int = friendList.size

}