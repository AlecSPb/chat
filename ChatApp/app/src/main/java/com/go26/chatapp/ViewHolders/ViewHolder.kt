package com.go26.chatapp.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.go26.chatapp.R

/**
 * Created by daigo on 2018/01/18.
 */
class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var llParent = itemView.findViewById(R.id.ll_parent) as LinearLayout
    var name = itemView.findViewById(R.id.name) as TextView
    var timestamp = itemView.findViewById(R.id.timestamp) as TextView
    var rlName = itemView.findViewById(R.id.rl_name) as RelativeLayout
    var message = itemView.findViewById(R.id.message) as TextView
}