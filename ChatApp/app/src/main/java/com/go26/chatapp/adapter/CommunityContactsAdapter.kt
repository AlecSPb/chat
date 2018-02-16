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
import com.go26.chatapp.constants.DataConstants.Companion.communityList
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.ui.chat.ChatRoomsFragment
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import com.go26.chatapp.ui.contacts.ContactsCommunityDetailFragment


/**
 * Created by daigo on 2018/01/14.
 */
class CommunityContactsAdapter(val context: Context) : RecyclerView.Adapter<CommunityContactsAdapter.ContactsViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactsViewHolder =
            ContactsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_community, parent, false))

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.communityName.text = communityList[position].name

        holder.location.visibility = View.GONE

        val memberCount = "メンバー: " + communityList[position].memberCount?.toString() + "人"
        holder.memberCount.text = memberCount

        loadRoundImage(holder.profileImage, communityList[position].imageUrl!!)


        holder.layout.setOnClickListener({

            val community = communityList[position]
            val list: MutableList<String> = mutableListOf("トーク", "詳細")

            MaterialDialog.Builder(context).title(community.name!!).items(list).itemsCallback { _, _, _, text ->
                MyChatManager.setmContext(context)
                if (text.toString() == "トーク") {
                    val chatRoomModel = ChatRoomModel(community.communityId!!, community.name!!, community.imageUrl!!,
                            community.lastMessage?.message!!, community.members[currentUser?.uid]?.unreadCount!!, AppConstants().COMMUNITY_CHAT)
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
                                }, currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
                            }
                        }
                    }, currentUser?.uid!!, chatRoomModel, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)
                } else if (text.toString() == "詳細") {
                    val contactsDetailFragment = ContactsCommunityDetailFragment.newInstance(community.communityId)
                    val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment, contactsDetailFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }.show()
        })
    }

    override fun getItemCount(): Int = communityList.size

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        val communityName: TextView = itemView.findViewById(R.id.name_text_view)
        val location: TextView = itemView.findViewById(R.id.location_text_view)
        val memberCount: TextView = itemView.findViewById(R.id.member_count_text_view)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
    }
}