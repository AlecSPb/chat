package com.go26.chatapp.ui.contacts


import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.fragment_edit_community.*


class EditCommunityFragment : Fragment() {
    private var communityModel: CommunityModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val id = arguments.getString("id")
        communityModel = communityMap!![id]

        return inflater!!.inflate(R.layout.fragment_edit_community, container, false)
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

        // focus
        edit_community_layout.setOnTouchListener{ _, _ ->
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(edit_community_layout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            edit_community_layout.requestFocus()
            return@setOnTouchListener true
        }

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        loadRoundImage(profile_image_view, communityModel?.imageUrl!!)
        community_name_edit_text.setText(communityModel?.name)
        location_edit_text.setText(communityModel?.location)
        description_edit_text.setText(communityModel?.description)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_community_toolbar_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.finish_edit -> {
                val name = community_name_edit_text.text.toString()
                val description = description_edit_text.text.toString()
                val location = location_edit_text.text.toString()
                val community = CommunityModel(communityId = communityModel?.communityId, name = name, description = description, location = location)

                MyChatManager.setmContext(context)
                MyChatManager.updateCommunityInfo(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        Toast.makeText(context, "You have been exited from group", Toast.LENGTH_LONG).show()
                        fragmentManager.popBackStack()
                        fragmentManager.beginTransaction().remove(this@EditCommunityFragment).commit()
                    }
                }, community, NetworkConstants().UPDATE_INFO)
                return false
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(id: String): EditCommunityFragment {
            val fragment = EditCommunityFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
