package com.go26.chatapp.ui.contacts


import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.CommunityModel
import kotlinx.android.synthetic.main.fragment_edit_community_description.*


class EditCommunityDescriptionFragment : Fragment() {
    private var id: String? = null
    private var community: CommunityModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        id = arguments.getString("id")
        community = communityMap!![id!!]

        return inflater!!.inflate(R.layout.fragment_edit_community_description, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = getString(R.string.edit_description)
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

        if (communityMap!![id]?.description != null) {
            description_edit_text.setText(communityMap!![id]?.description)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.edit_finish_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.edit_finish -> {
                var isValid = true
                var errorMessage = ""

                val communityModel = CommunityModel(communityId = community?.communityId)

                val description = description_edit_text.text.toString()
                if (description.isBlank()) {
                    isValid = false
                    errorMessage = getString(R.string.blank)
                } else {
                    communityModel.description = description
                }

                if (isValid) {
                    MyChatManager.setmContext(context)
                    MyChatManager.updateCommunityDescription(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            if (communityMap!![id!!]?.description == description) {
                                fragmentManager.popBackStack()
                                fragmentManager.beginTransaction().remove(this@EditCommunityDescriptionFragment).commit()
                            } else {
                                var count = 0
                                val handler = Handler()

                                handler.postDelayed(object : Runnable {
                                    override fun run() {
                                        count ++
                                        if (count > 30) {
                                            Toast.makeText(context, "更新に失敗しました。アプリを再起動してください。", Toast.LENGTH_SHORT).show()
                                            return
                                        }
                                        if (communityMap!![id!!]?.description == description) {
                                            fragmentManager.popBackStack()
                                            fragmentManager.beginTransaction().remove(this@EditCommunityDescriptionFragment).commit()
                                        } else {
                                            handler.postDelayed(this, 100)
                                        }
                                    }
                                }, 100)
                            }
                        }
                    }, communityModel, NetworkConstants().UPDATE_INFO)
                } else {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(id: String): EditCommunityDescriptionFragment {
            val fragment = EditCommunityDescriptionFragment()
            val args = Bundle()
            args.putString("id", id)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
