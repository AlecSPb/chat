package com.go26.chatapp.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.adapter.UserListAdapter
import com.go26.chatapp.adapter.UserSelectionAdapter
import com.go26.chatapp.constants.DataConstants.Companion.selectedUserList
import com.go26.chatapp.constants.DataConstants.Companion.userList
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.UserModel


class CreateCommunityFragment : Fragment(), View.OnClickListener {

    var rvUserList: RecyclerView? = null
    var rvSelectedUser: RecyclerView? = null
    var adapter: UserListAdapter? = null
    var secondaryAdapter: UserSelectionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View? = inflater?.inflate(R.layout.fragment_create_community, container, false)

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews(view)
    }

    private fun setViews(view: View?) {
        MyChatManager.setmContext(context)
        rvUserList = view?.findViewById(R.id.rv_user_list)
        rvSelectedUser = view?.findViewById(R.id.rv_selected_user)

        if (userList?.size == 0) {
            MyChatManager.getAllUsersFromFirebase(object : NotifyMeInterface {
                override fun handleData(`asv`: Any, requestCode: Int?) {
                    userList = `asv` as ArrayList<UserModel>
                    selectedUserList?.clear()
                    rvUserList?.layoutManager = LinearLayoutManager(context)
                    adapter = UserListAdapter(context, userSelectionInterface)
                    rvUserList?.adapter = adapter

                    rvSelectedUser?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    secondaryAdapter = UserSelectionAdapter(context, userRemovedFromSelection)
                    rvSelectedUser?.adapter = secondaryAdapter

                }
            }, NetworkConstants().GET_ALL_USERS_REQUEST)
        } else {
            selectedUserList?.clear()
            rvUserList?.layoutManager = LinearLayoutManager(context)
            adapter = UserListAdapter(context, userSelectionInterface)
            rvUserList?.adapter = adapter

            rvSelectedUser?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            secondaryAdapter = UserSelectionAdapter(context, userRemovedFromSelection)
            rvSelectedUser?.adapter = secondaryAdapter
        }
        val nextText: TextView? = view?.findViewById(R.id.tv_next)
        nextText?.setOnClickListener(this)


    }

    private var userSelectionInterface = object : NotifyMeInterface {
        override fun handleData(obj: Any, requestCode: Int?) {
            when (requestCode) {
                NetworkConstants().USER_REMOVED -> {
                    selectedUserList?.remove(obj as UserModel)
                    secondaryAdapter?.notifyDataSetChanged()
                }
                NetworkConstants().USER_ADDED -> {
                    selectedUserList?.add(obj as UserModel)
                    secondaryAdapter?.notifyDataSetChanged()
                }

            }
            if (selectedUserList?.size!! > 0) {
                rvSelectedUser?.visibility = View.VISIBLE
            } else {
                rvSelectedUser?.visibility = View.GONE
            }
        }
    }

    private var userRemovedFromSelection = object : NotifyMeInterface {
        override fun handleData(obj: Any, requestCode: Int?) {
            when (requestCode) {
                NetworkConstants().USER_REMOVED -> {
                    adapter?.resetView(obj as String)
                }
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_next -> {
                if (selectedUserList?.size!! > 1 && selectedUserList?.size!! <= 6) {
                    val newGroupFragment = NewCommunityFragment.newInstance()
                    val fragmentManager: FragmentManager = activity.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment, newGroupFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    Toast.makeText(context, "Number of members should be more than 2 and less than 7", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    companion object {

        fun newInstance(): CreateCommunityFragment {
            val fragment = CreateCommunityFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
