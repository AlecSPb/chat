package com.go26.chatapp.ui.contacts


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.myFriendsMap
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.fragment_contacts_detail.*


class ContactsDetailFragment : Fragment() {
    var id: String? = null
    var type: String? = null
    private var admin: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        id = arguments.getString("id")
        type = arguments.getString("type")

        return inflater!!.inflate(R.layout.fragment_contacts_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        when (type) {
            AppConstants().COMMUNITY -> {

                val communityModel: CommunityModel? = communityMap!![id]
                loadRoundImage(profile_image_view, communityModel?.imageUrl!!)
                name_text_view.text = communityModel.name
                val locationText = "活動場所: " + communityModel.location
                location_text_view.visibility = View.VISIBLE
                location_text_view.text = locationText
                description_text_view.visibility = View.VISIBLE
                description_text_view.text = communityModel.description

                if (communityModel.members[currentUser?.uid]?.admin != null) {
                    admin = true
                }
            }
            AppConstants().FRIEND -> {
                val friend: UserModel? = myFriendsMap[id]
                loadRoundImage(profile_image_view, friend?.imageUrl!!)
                name_text_view.text = friend.name
                if (friend.programmingLanguage != null) {
                    language_text_view.visibility = View.VISIBLE
                    val language = "使用言語: " + friend.programmingLanguage
                    language_text_view.text = language
                }

                if (friend.whatMade != null) {
                    made_text_view.visibility = View.VISIBLE
                    val made = "過去に作ったアプリ: " + friend.whatMade
                    made_text_view.text = made
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        if (type == AppConstants().COMMUNITY) {
            if (admin) {
                inflater!!.inflate(R.menu.contacts_detail_for_admin_toolbar_item, menu)
            } else {
                inflater!!.inflate(R.menu.contacts_detail_toolbar_item, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.edit_community -> {
                val editCommunityFragment = EditCommunityFragment.newInstance(id!!)
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, editCommunityFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            R.id.leave_community -> {
                MyChatManager.setmContext(context)
                MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        Toast.makeText(context, "You have been exited from group", Toast.LENGTH_LONG).show()
                        fragmentManager.popBackStack()
                        fragmentManager.beginTransaction().remove(this@ContactsDetailFragment).commit()
                    }
                }, id, DataConstants.currentUser?.uid)
                return false
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(id: String?, type: String): ContactsDetailFragment {
            val fragment = ContactsDetailFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
