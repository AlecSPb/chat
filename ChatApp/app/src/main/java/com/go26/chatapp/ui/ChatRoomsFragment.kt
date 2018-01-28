package com.go26.chatapp.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.adapter.ChatRoomsAdapter
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.myCommunities
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import kotlinx.android.synthetic.main.fragment_chat_rooms.*
import java.util.*


class ChatRoomsFragment : Fragment() {
    var adapter: ChatRoomsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_chat_rooms, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        setViews()
    }

    override fun onStart() {
        super.onStart()
        setViews()

    }
    private fun setViews() {

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = "Chat"

//        MyChatManager.setmContext(context)
        if (myCommunities.size != 0) {
            fetchMyCommunities()
        }

//        MyChatManager.fetchCurrentUser(object : NotifyMeInterface {
//            override fun handleData(obj: Any, requestCode: Int?) {
//                val isValid: Boolean = obj as Boolean
//                if (isValid) {
//                    fetchMyCommunities()
//                } else {
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }, currentUser?.uid, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS)
    }

    private fun fetchMyCommunities() {
//        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
//            override fun handleData(obj: Any, requestCode: Int?) {
//                myCommunities?.clear()
//                for (communities in communityMap!!) {
//                    if (!communities.value.communities!!) {
//                        if (!communities.value.lastMessage?.sender_id.equals("")) {
//                            myCommunities?.add(communities.value)
//                        }
//                    } else {
//                        myCommunities?.add(communities.value)
//                    }
//
//                }
//                //sort DataConstants.myGroups
//                Collections.sort<CommunityModel>(myCommunities) { o1, o2 ->
//                    if(o1.lastMessage?.timestamp.equals("") && o2.lastMessage?.timestamp.equals("")){
//                        0
//                    }
//                    else if(o1.lastMessage?.timestamp.equals("")){
//                        (o2.lastMessage?.timestamp?.toDouble()!!).toInt()
//                    }else if(o2.lastMessage?.timestamp.equals("")){
//                        (o1.lastMessage?.timestamp?.toDouble()!!).toInt()
//                    }else{
//                        (o2.lastMessage?.timestamp?.toDouble()!! - o1.lastMessage?.timestamp?.toDouble()!!).toInt()
//                    }
//                }
//
//                val recycler: RecyclerView? = view?.findViewById(R.id.rv_main)
//                recycler?.layoutManager = LinearLayoutManager(context)
//                adapter = ChatRoomsAdapter(context)
//                recycler?.adapter = adapter
//            }
//
//        }, NetworkConstants().FETCH_GROUPS, currentUser, isSingleEvent = false)

        //sort DataConstants.myGroups
        Collections.sort<CommunityModel>(myCommunities) { o1, o2 ->
            if(o1.lastMessage?.timestamp.equals("") && o2.lastMessage?.timestamp.equals("")){
                0
            }
            else if(o1.lastMessage?.timestamp.equals("")){
                (o2.lastMessage?.timestamp?.toDouble()!!).toInt()
            }else if(o2.lastMessage?.timestamp.equals("")){
                (o1.lastMessage?.timestamp?.toDouble()!!).toInt()
            }else{
                (o2.lastMessage?.timestamp?.toDouble()!! - o1.lastMessage?.timestamp?.toDouble()!!).toInt()
            }
        }

        val recycler: RecyclerView? = view?.findViewById(R.id.rv_main)
        recycler?.layoutManager = LinearLayoutManager(context)
        adapter = ChatRoomsAdapter(context)
        recycler?.adapter = adapter
    }



    companion object {

        fun newInstance(): ChatRoomsFragment {
            val fragment = ChatRoomsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
