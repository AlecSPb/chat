package jp.gr.java_conf.cody.adapter

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
import com.google.firebase.database.Query
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.model.ChatRoomModel
import jp.gr.java_conf.cody.ui.chat.ChatFragment
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class ChatRoomsAdapter(val context: Context, ref: Query) : FirebaseRecyclerAdapter<ChatRoomModel, ChatRoomsAdapter.ViewHolder>(
        ChatRoomModel::class.java, R.layout.item_user,
        ChatRoomsAdapter.ViewHolder::class.java, ref)  {

    override fun populateViewHolder(viewHolder: ViewHolder?, model: ChatRoomModel?, position: Int) {
        if (model?.type == AppConstants().FRIEND_CHAT) {
            val name = model.name
            viewHolder?.name?.text = name
        } else {
            viewHolder?.name?.text = model?.name
        }

        if (model?.lastMessage != null && model.lastMessage != "") {
            viewHolder?.message?.text = model.lastMessage
        } else {
            viewHolder?.message?.text = context.getString(R.string.no_message)
        }

        if (model?.unreadCount!! > 0) {
            viewHolder?.unreadCount?.visibility = View.VISIBLE
            viewHolder?.unreadCount?.text = model.unreadCount.toString()
        } else {
            viewHolder?.unreadCount?.visibility = View.GONE
        }

        loadRoundImage(viewHolder?.profileImage!!, model.imageUrl)

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
        var profileImage = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        var name = itemView.findViewById(R.id.user_name_text_view) as TextView
        var message = itemView.findViewById(R.id.message_text_view) as TextView
        var layout = itemView.findViewById(R.id.parent_layout) as RelativeLayout
        var unreadCount = itemView.findViewById(R.id.unread_count_text_view) as TextView
    }
}