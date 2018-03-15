package jp.gr.java_conf.cody.ui.profile


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.circulardialog.CDialog
import com.example.circulardialog.extras.CDConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.webianks.easy_feedback.EasyFeedback
import de.psdev.licensesdialog.LicensesDialogFragment
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface

import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.ui.LoginActivity
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadRoundImage
import jp.gr.java_conf.cody.util.NetUtils
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
            val notices = Notices()

            notices.addNotice(Notice(getString(R.string.licenses_dialog), "", getString(R.string.licenses_dialog_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.easy_feedback), "", getString(R.string.easy_feedback_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.matisse), "", getString(R.string.matisse_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.android_image_cropper), "", getString(R.string.android_image_cropper_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.glide), "", getString(R.string.glide_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.secure_preferences), "", getString(R.string.secure_preferences_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.google_gson), "", getString(R.string.google_gson_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.indicator), "", getString(R.string.indicator_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.circular_dialogs), "", "", ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.toggle), "", getString(R.string.toggle_copyright), ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.toggle_button_group), "", "", ApacheSoftwareLicense20()))
            notices.addNotice(Notice(getString(R.string.welcome), "", getString(R.string.welcome_copyright), ApacheSoftwareLicense20()))
            
            LicensesDialogFragment.Builder(context)
                    .setNotices(notices)
                    .setShowFullLicenseText(false)
                    .setIncludeOwnLicense(false)
                    .build()
                    .show(fragmentManager, null)
        }

        // logout
        logout_text_view.setOnClickListener {
            // オンライン出ないとログアウトできないようにする
            if (NetUtils(context).isOnline()) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken((R.string.default_web_client_id).toString())
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(this.activity, gso)

                MyChatManager.logout(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        activity.finish()
                    }
                }, context, googleSignInClient)
            } else {
                CDialog(context)
                        .createAlert(getString(R.string.connection_alert), CDConstants.WARNING, CDConstants.MEDIUM)
                        .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)
                        .setDuration(2000)
                        .setTextSize(CDConstants.NORMAL_TEXT_SIZE)
                        .show()
            }
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
