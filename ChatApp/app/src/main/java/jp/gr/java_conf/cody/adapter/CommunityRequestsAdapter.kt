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
import jp.gr.java_conf.cody.model.UserModel
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.ui.contacts.requests.CommunityRequestsFragment
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/24.
 */
class CommunityRequestsAdapter(private val context: Context, private val communityRequestList: MutableList<Pair<String, UserModel>>) : RecyclerView.Adapter<CommunityRequestsAdapter.RequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestViewHolder =
            RequestViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_community_request, parent, false))

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val name = communityRequestList[position].second.name
        holder.userName.text = name

        loadRoundImage(holder.profileImage, communityRequestList[position].second.imageUrl)

        holder.layout.setOnClickListener {
            val communityRequestsFragment = CommunityRequestsFragment.newInstance(position)
            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fragment, communityRequestsFragment)
            fragmentTransaction.commit()
        }
    }

    override fun getItemCount(): Int = communityRequestList.size

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: AppCompatImageView = itemView.findViewById(R.id.profile_image_view)
        val userName: TextView = itemView.findViewById(R.id.name_text_view)
        val layout: RelativeLayout = itemView.findViewById(R.id.parent_layout)
    }
}
