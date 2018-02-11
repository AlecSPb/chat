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
import com.afollestad.materialdialogs.MaterialDialog
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface
import com.go26.chatapp.R
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.friendList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.ui.ChatRoomsFragment
import com.go26.chatapp.ui.contacts.ContactsDetailFragment
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/29.
 */
class FriendContactsAdapter(val context: Context) : RecyclerView.Adapter<FriendContactsAdapter.ContactsViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactsViewHolder =
            ContactsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_friend, parent, false))

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        var friend: UserModel? = null
        for (member in friendList[position].members) {
            if (member.key != currentUser?.uid) {
                friend = member.value
            }
        }

        if (friend != null) {
            holder.friendName.text = friend.name
            loadRoundImage(holder.profileImage, friend.imageUrl!!)


            holder.layout.setOnClickListener({
                val list: MutableList<String> = mutableListOf("トーク", "詳細")
                MaterialDialog.Builder(context).title(friend.name!!).items(list).itemsCallback { _, _, _, text ->
                    MyChatManager.setmContext(context)
                    if (text == "トーク") {
                        val chatRoomModel = ChatRoomModel(friendList[position].friendId!!, friend.name!!, friend.imageUrl!!,
                                friendList[position].lastMessage?.message!!, friendList[position].members[DataConstants.currentUser?.uid]?.unreadCount!!, AppConstants().FRIEND_CHAT)
                        MyChatManager.hasChatRoom(object : NotifyMeInterface {
                            override fun handleData(obj: Any, requestCode: Int?) {
                                if (obj as Boolean) {
                                    val chatRoomsFragment = ChatRoomsFragment.newInstance(true, chatRoomModel)
                                    val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                                    val fragmentTransaction = fragmentManager.beginTransaction()
                                    fragmentTransaction.replace(R.id.fragment, chatRoomsFragment)
                                    fragmentTransaction.commit()
                                } else {
                                    MyChatManager.createChatRoom(object : NotifyMeInterface {
                                        override fun handleData(obj: Any, requestCode: Int?) {
                                            if (obj as Boolean) {
                                                val chatRoomsFragment = ChatRoomsFragment.newInstance(true, chatRoomModel)
                                                val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                                                val fragmentTransaction = fragmentManager.beginTransaction()
                                                fragmentTransaction.replace(R.id.fragment, chatRoomsFragment)
                                                fragmentTransaction.commit()
                                            }
                                        }
                                    }, DataConstants.currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
                                }
                            }
                        }, DataConstants.currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
                    }  else if (text.toString() == "詳細") {
                        val contactsDetailFragment = ContactsDetailFragment.newInstance(friend.uid, AppConstants().FRIEND)
                        val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment, contactsDetailFragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                }.show()
            })
        }
    }

    override fun getItemCount(): Int = friendList.size

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        val friendName: TextView = itemView.findViewById(R.id.name_text_view)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
    }
}