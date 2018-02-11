package com.go26.chatapp.ui.profile


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import com.go26.chatapp.MyChatManager

import com.go26.chatapp.R
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.util.MyViewUtils.Companion.loadRoundImage
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
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = "MyPage"
        setHasOptionsMenu(true)

        name_text_view.text = currentUser?.name

        if (currentUser?.programmingLanguage != null) {
            language_text_view.visibility = View.VISIBLE
            val language = "使用言語: " + currentUser?.programmingLanguage
            language_text_view.text = language
        } else {
            language_text_view.visibility = View.GONE
        }

        if (currentUser?.whatMade != null) {
            made_title_text_view.visibility = View.VISIBLE
            made_text_view.visibility = View.VISIBLE
            val made = currentUser?.whatMade
            made_text_view.text = made
        } else {
            made_text_view.visibility = View.GONE
        }

        loadRoundImage(profile_image_view, currentUser?.imageUrl!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.profile_toolbar_item,menu)
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
