package jp.gr.java_conf.cody.ui.profile


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.*
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.util.MyViewUtils.Companion.loadImageFromUrl
import kotlinx.android.synthetic.main.fragment_profile_detail.*


class ProfileDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_profile_detail, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(tool_bar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        loadImageFromUrl(profile_image_view, currentUser?.imageUrl!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.profile_toolbar_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                fragmentManager.beginTransaction().remove(this).commit()
                fragmentManager.popBackStack()
                return true
            }
            R.id.edit -> {
                val editProfileFragment = EditProfileFragment.newInstance()
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, editProfileFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }

            else -> {
                return false
            }
        }
    }

    companion object {

        fun newInstance(): ProfileDetailFragment {
            val fragment = ProfileDetailFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
