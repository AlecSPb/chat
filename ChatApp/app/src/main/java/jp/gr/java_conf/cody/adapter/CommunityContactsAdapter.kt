package jp.gr.java_conf.cody.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityList
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.ChatRoomModel
import jp.gr.java_conf.cody.ui.chat.ChatRoomsFragment
import jp.gr.java_conf.cody.ui.contacts.ContactsCommunityDetailFragment
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage


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

        loadRoundImage(holder.profileImage, communityList[position].imageUrl)


        holder.layout.setOnClickListener({

            val community = communityList[position]
            MyChatManager.setmContext(context)

            val list = arrayOf(context.getString(R.string.dialog_talk), context.getString(R.string.dialog_detail))
            AlertDialog.Builder(context)
                    .setTitle(community.name)
                    .setItems(list, { _, pos ->
                        when (pos) {
                            0 -> {
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
                            }
                            1 -> {
                                val contactsDetailFragment = ContactsCommunityDetailFragment.newInstance(community.communityId)
                                val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.fragment, contactsDetailFragment)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                        }

                    })
                    .show()
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