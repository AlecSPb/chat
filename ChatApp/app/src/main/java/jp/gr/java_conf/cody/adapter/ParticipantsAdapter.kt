package jp.gr.java_conf.cody.adapter

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class ParticipantsAdapter(var callback: NotifyMeInterface, var type: String) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ParticipantsViewHolder =
            ParticipantsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_invited_community_member, parent, false))

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        val user = DataConstants.selectedUserList[position]

        try {
            holder.userName.text = user.name

            loadRoundImage(holder.profileImage, user.imageUrl)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getItemCount(): Int = DataConstants.selectedUserList.size

    class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view)
        val userName: TextView = itemView.findViewById(R.id.user_name_text_view)
    }
}