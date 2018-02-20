package jp.gr.java_conf.cody.ui.profile


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.webianks.easy_feedback.EasyFeedback
import jp.gr.java_conf.cody.MyChatManager

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    private var isFirst = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        isFirst = arguments.getBoolean("isFirst")
        return inflater!!.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.VISIBLE

        // 名前
        name_text_view.text = currentUser?.name

        // プロフィール画像
        loadRoundImage(profile_image_view, currentUser?.imageUrl!!)

        if (isFirst && currentUser?.selfIntroduction == null && currentUser?.programmingLanguage == null && currentUser?.myApps == null) {
            first_text_view.visibility = View.VISIBLE
        }

        // プロフィール詳細へ
        profile_layout.setOnClickListener {
            val profileDetailFragment = ProfileDetailFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, profileDetailFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // フィードバック
        feedback_text_view.setOnClickListener {
            EasyFeedback.Builder(context)
                    .withEmail(getString(R.string.email))
                    .withSystemInfo()
                    .build()
                    .start()
        }

        // プライバシーポリシー
        privacy_policy_text_view.setOnClickListener {
            val privacyPolicyFragment = PrivacyPolicyFragment.newInstance()
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, privacyPolicyFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // ライセンス
        license_text_view.setOnClickListener {

        }

        // logout
        logout_text_view.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken((R.string.default_web_client_id).toString())
                    .requestEmail()
                    .build()

            val googleSignInClient = GoogleSignIn.getClient(this.activity, gso)

            MyChatManager.logout(context, googleSignInClient)
        }
    }

    companion object {

        fun newInstance(isFirst: Boolean): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putBoolean("isFirst", isFirst)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
