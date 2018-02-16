package com.go26.chatapp.ui.contacts


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import com.bumptech.glide.Glide

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.model.CommunityModel
import kotlinx.android.synthetic.main.fragment_edit_community.*


class EditCommunityFragment : Fragment() {
    private var id: String? = null
    private var communityModel: CommunityModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        id = arguments.getString("id")
        communityModel = communityMap!![id!!]

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
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
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

        // 名前
        name_text_view.text = communityModel?.name

        // コミュニティの説明
        if (communityModel?.description != null) {
            description_text_view.visibility = View.VISIBLE
            description_text_view.text = communityModel?.description
        }

        // 活動場所
        if (communityModel?.location != null) {
            location_text_view.visibility = View.VISIBLE
            location_text_view.text = communityModel?.location
        }

        // profile画像
        Glide.with(context)
                .load(communityModel?.imageUrl)
                .into(profile_image_view)

        setButtonClickListener()
    }

    private fun setButtonClickListener() {
        name_edit_button.setOnClickListener {
            val editCommunityNameFragment = EditCommunityNameFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityNameFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        description_edit_button.setOnClickListener {
            val editCommunityDescriptionFragment = EditCommunityDescriptionFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityDescriptionFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        location_edit_button.setOnClickListener {
            val editCommunityLocationFragment = EditCommunityLocationFragment.newInstance(id!!)
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left)
            fragmentTransaction.replace(R.id.fragment, editCommunityLocationFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_toolbar_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }

            R.id.select_photo -> {
                return true
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
