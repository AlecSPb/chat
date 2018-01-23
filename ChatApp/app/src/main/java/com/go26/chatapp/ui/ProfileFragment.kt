package com.go26.chatapp.ui


import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import com.go26.chatapp.MyChatManager

import com.go26.chatapp.R
import com.go26.chatapp.databinding.FragmentProfileBinding
import com.go26.chatapp.viewmodel.ProfileFragmentViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class ProfileFragment : Fragment() {

    lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        viewModel = ProfileFragmentViewModel(context)
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewModel = viewModel

        return binding.root
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
