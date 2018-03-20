package jp.gr.java_conf.cody.ui.chat


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import jp.gr.java_conf.cody.MyChatManager
import jp.gr.java_conf.cody.NotifyMeInterface
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.adapter.ChatRoomsAdapter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.currentUser
import jp.gr.java_conf.cody.constants.FirebaseConstants
import jp.gr.java_conf.cody.constants.NetworkConstants
import jp.gr.java_conf.cody.model.ChatRoomModel
import kotlinx.android.synthetic.main.fragment_chat_rooms.*


class ChatRoomsFragment : Fragment() {
    var adapter: ChatRoomsAdapter? = null
    private var isBackStack: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (arguments.getBoolean("fromContacts") && !isBackStack) {
            val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
            bottomNavigationView.menu.findItem(R.id.navigation_contacts).isChecked = true

            isBackStack = true

            val chatRoomModel = arguments.getSerializable("chatRoomModel") as ChatRoomModel
            val chatFragment = ChatFragment.newInstance(chatRoomModel)
            val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, chatFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return inflater!!.inflate(R.layout.fragment_chat_rooms, container, false)
    }

    override fun onStart() {
        super.onStart()
        setViews()

    }

    private fun setViews() {
        //bottomNavigationView　表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.menu.findItem(R.id.navigation_chat).isChecked = true
        bottomNavigationView.visibility = View.VISIBLE

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = getString(R.string.chat)

        val recycler: RecyclerView? = view?.findViewById(R.id.chat_rooms_recycler_view)
        recycler?.layoutManager = LinearLayoutManager(context)

        MyChatManager.setmContext(context)
        MyChatManager.isChatRoomExist(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val isValid = obj as Boolean
                if (isValid) {
                    val ref: Query = FirebaseDatabase.getInstance().reference.child(FirebaseConstants().USERS).child(currentUser?.uid).child(FirebaseConstants().CHAT_ROOMS)
                    adapter = ChatRoomsAdapter(context, ref)
                    recycler?.visibility = View.VISIBLE
                    recycler?.adapter = adapter
                } else {
                    empty_view.visibility = View.VISIBLE
                }
            }
        }, NetworkConstants().CHECK_CHAT_ROOMS_EXISTS)

    }

    companion object {

        fun newInstance(fromContacts: Boolean, chatRoomModel: ChatRoomModel?): ChatRoomsFragment {
            val fragment = ChatRoomsFragment()
            val args = Bundle()
            args.putBoolean("fromContacts", fromContacts)
            args.putSerializable("chatRoomModel", chatRoomModel)

            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
