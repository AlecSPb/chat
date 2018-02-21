package jp.gr.java_conf.cody.ui.contacts


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.CommunityContactsAdapter
import jp.gr.java_conf.cody.adapter.FriendContactsAdapter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityList
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.DataConstants.Companion.friendList
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.ui.contacts.requests.RequestsFragment
import kotlinx.android.synthetic.main.fragment_contacts.*


class ContactsFragment : Fragment() {
    var isCompleteToFetchCommunities = false
    var isCompleteToFetchFriends = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews(view)
    }

    override fun onStart() {
        super.onStart()

        MyChatManager.setmContext(context)
        MyChatManager.fetchMyCommunities(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                isCompleteToFetchCommunities = true
                val isValid = obj as Boolean
                if (isValid) {
                    if (!communityList.isEmpty()) {
                        setCommunityContactsAdapter()
                    } else {
                        if (isCompleteToFetchCommunities && isCompleteToFetchFriends && friendList.isEmpty()) {
                            empty_view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, true)

        MyChatManager.fetchMyFriends(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                isCompleteToFetchFriends = true
                val isValid = obj as Boolean
                if (isValid) {
                    if (!friendList.isEmpty()) {
                        setFriendContactsAdapter()
                    } else {
                        if (isCompleteToFetchCommunities && isCompleteToFetchFriends && communityList.isEmpty()) {
                            empty_view.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }, currentUser, NetworkConstants().FETCH_CURRENT_USER_AND_COMMUNITIES_AND_FRIENDS, true)
    }

    private fun setViews(view: View?) {
        //bottomNavigationView　表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.VISIBLE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = getString(R.string.contacts)
        setHasOptionsMenu(true)
    }

    private fun setCommunityContactsAdapter() {
        contacts_scroll_view.visibility = View.VISIBLE
        contacts_community_title_text_view.visibility = View.VISIBLE
        contacts_community_recycler_view.visibility = View.VISIBLE
        contacts_community_recycler_view.layoutManager = LinearLayoutManager(context)
        contacts_community_recycler_view.isNestedScrollingEnabled = false
        contacts_community_recycler_view.adapter = CommunityContactsAdapter(context)
    }

    private fun setFriendContactsAdapter() {
        contacts_scroll_view.visibility = View.VISIBLE
        contacts_friend_title_text_view.visibility = View.VISIBLE
        contacts_friend_recycler_view.visibility = View.VISIBLE
        contacts_friend_recycler_view.layoutManager = LinearLayoutManager(context)
        contacts_friend_recycler_view.isNestedScrollingEnabled = false
        contacts_friend_recycler_view.adapter = FriendContactsAdapter(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.contacts_toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_community -> {
                val newCommunityFragment = NewCommunityFragment.newInstance()
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, newCommunityFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            R.id.requests -> {
                val requestsFragment = RequestsFragment.newInstance()
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, requestsFragment)
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

        fun newInstance(): ContactsFragment {
            val fragment = ContactsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
