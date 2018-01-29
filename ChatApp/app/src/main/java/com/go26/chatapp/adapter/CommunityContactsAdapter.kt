package com.go26.chatapp.adapter

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.UserRowViewHolder
import com.go26.chatapp.constants.DataConstants.Companion.communityList
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage

/**
 * Created by daigo on 2018/01/14.
 */
class CommunityContactsAdapter(val context: Context) : RecyclerView.Adapter<UserRowViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserRowViewHolder =
            UserRowViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))

    override fun onBindViewHolder(holder: UserRowViewHolder, position: Int) {
        holder.tvName.text = communityList[position].name
        holder.tvName.layout
        holder.tvEmail.visibility = View.GONE
        loadRoundImage(holder.ivProfile, communityList[position].imageUrl!!)


        holder.layout.setOnClickListener({


//            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.fragment, chatFragment)
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
        })


    }

    override fun getItemCount(): Int = communityList.size

}