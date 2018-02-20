package jp.gr.java_conf.cody.adapter

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.database.Query
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.AppConstants
import jp.gr.java_conf.cody.constants.DataConstants.Companion.userMap
import jp.gr.java_conf.cody.model.MessageModel
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.util.MyTextUtil
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage
import jp.gr.java_conf.cody.util.SharedPrefManager

/**
 * Created by daigo on 2018/02/12.
 */
class ChatAdapter(var type: String?, var context: Context, ref: Query, itemsPerPage: Int, deleteTill: String, chat_messages_recycler: RecyclerView)  :
        InfiniteFirebaseRecyclerAdapter<MessageModel, ChatAdapter.ViewHolder>(
                MessageModel::class.java, R.layout.item_chat_row,
                ChatAdapter.ViewHolder::class.java, ref, itemsPerPage, deleteTill, chat_messages_recycler) {

    var currentUser: UserModel = SharedPrefManager.getInstance(context).savedUserModel!!

    override fun populateViewHolder(viewHolder: ViewHolder, model: MessageModel?, position: Int) {
        val chatMessage = model!!
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        if (chatMessage.sender_id.toString() == currentUser.uid) {
            viewHolder.llParent.gravity = Gravity.END
            viewHolder.profileImage.visibility = View.GONE
            viewHolder.name.visibility = View.GONE
            lp.gravity = Gravity.END
            viewHolder.message.layoutParams = lp
        } else {
            viewHolder.llParent.gravity = Gravity.START
            viewHolder.profileImage.visibility = View.VISIBLE

            val member = userMap?.get(chatMessage.sender_id!!)
            // 退会した人はnull
            if (member != null) {
                loadRoundImage(viewHolder.profileImage, member.imageUrl!!)

                val name = member.name
                if (name != null && type == AppConstants().COMMUNITY_CHAT) {
                    viewHolder.name.visibility = View.VISIBLE
                    viewHolder.name.text = name
                } else {
                    viewHolder.name.visibility = View.GONE
                }
            } else {
                viewHolder.profileImage.visibility = View.GONE
                viewHolder.name.visibility = View.VISIBLE
                viewHolder.name.text = "退会済み"
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
        var llParent = itemView.findViewById(R.id.ll_parent) as RelativeLayout
        var profileImage = itemView.findViewById(R.id.profile_image_view) as AppCompatImageView
        var name = itemView.findViewById(R.id.name) as TextView
        var timestamp = itemView.findViewById(R.id.timestamp) as TextView
        var message = itemView.findViewById(R.id.message) as TextView
    }
}