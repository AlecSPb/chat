package com.go26.chatapp.ui.profile


import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import com.bumptech.glide.Glide
import com.go26.chatapp.MyChatManager

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.VISIBLE

        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(tool_bar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)

        // 名前
        name_text_view.text = currentUser?.name

        // 自己紹介
        if (currentUser?.selfIntroduction != null) {
            self_introduction_text_view.visibility = View.VISIBLE
            self_introduction_text_view.text = currentUser?.selfIntroduction
        }

        // 年齢
        if (currentUser?.age != null) {
            age_title_line.visibility = View.VISIBLE

            age_title_text_view.visibility = View.VISIBLE

            age_text_view.visibility = View.VISIBLE
            val age = currentUser?.age.toString() + getString(R.string.age_content)
            age_text_view.text = age

        }

        // 開発経験
        if (currentUser?.developmentExperience != null) {
            experience_title_line.visibility = View.VISIBLE

            experience_title_text_view.visibility = View.VISIBLE

            experience_text_view.visibility = View.VISIBLE
            when (currentUser?.developmentExperience) {
                0 -> {
                    val experience = getString(R.string.experience0)
                    experience_text_view.text = experience
                }
                1 -> {
                    val experience = getString(R.string.experience1)
                    experience_text_view.text = experience
                }
                2 -> {
                    val experience = getString(R.string.experience2)
                    experience_text_view.text = experience
                }
                3 -> {
                    val experience = getString(R.string.experience3)
                    experience_text_view.text = experience
                }
            }
        }

        //　使用言語
        if (currentUser?.programmingLanguage != null) {
            language_title_line.visibility = View.VISIBLE

            language_title_text_view.visibility = View.VISIBLE

            language_text_view.visibility = View.VISIBLE
            language_text_view.text = currentUser?.programmingLanguage
        }

        // 過去に作ったもの
        if (currentUser?.myApps != null) {
            my_apps_title_line.visibility = View.VISIBLE

            my_apps_title_text_view.visibility = View.VISIBLE

            my_apps_text_view.visibility = View.VISIBLE
            val made = currentUser?.myApps
            my_apps_text_view.text = made
        }

        // profile画像
        Glide.with(context)
                .load(currentUser?.imageUrl)
                .into(profile_image_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.profile_toolbar_item, menu)
        for (i in 0 until menu?.size()!!) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            spanString.setSpan(ForegroundColorSpan(Color.BLACK), 0, spanString.length, 0) //fix the color to white
            item.title = spanString
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                val editProfileFragment = EditProfileFragment.newInstance()
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, editProfileFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            R.id.logout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken((R.string.default_web_client_id).toString())
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(this.activity, gso)

                MyChatManager.logout(context, googleSignInClient)
                return true
            }
            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
