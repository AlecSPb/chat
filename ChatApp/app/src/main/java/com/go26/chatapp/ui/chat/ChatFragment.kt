package com.go26.chatapp.ui.chat


import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.FirebaseConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.MessageModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import com.go26.chatapp.adapter.ChatAdapter
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.friendMap
import com.go26.chatapp.model.ChatRoomModel
import com.go26.chatapp.ui.contacts.CommunityMemberFragment


class ChatFragment : Fragment(), View.OnClickListener {
    var newAdapter: ChatAdapter? = null
    var chatRoomModel: ChatRoomModel? = null
    var id: String? = ""
    var progressBar: ProgressBar? = null
    var mFirebaseDatabaseReference: DatabaseReference? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var storage = FirebaseStorage.getInstance()
    var type: String? = ""
    var scrollListener: RecyclerView.OnScrollListener? = null

    val IMAGE_GALLERY_REQUEST = 1
    val IMAGE_CAMERA_REQUEST = 2
    val PLACE_PICKER_REQUEST = 3

    var communityIsPresent: Boolean = false

    //File
    var filePathImageCamera: File? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
        //bottomNavigationView　非表示
        val bottomNavigationView: BottomNavigationView = activity.findViewById(R.id.navigation)
        bottomNavigationView.visibility = View.GONE

        progressBar?.visibility = View.VISIBLE

        // back buttonイベント
        view?.isFocusableInTouchMode = true
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().remove(this).commit()
            }
            return@setOnKeyListener true
        }

        chatRoomModel = arguments.getSerializable("chatRoomModel") as ChatRoomModel
        id = chatRoomModel?.id
        type = chatRoomModel?.type
//        tv_loadmore.setOnClickListener(this)

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        activity.supportActionBar?.title = chatRoomModel?.name

        mLinearLayoutManager = LinearLayoutManager(context)
        mLinearLayoutManager!!.stackFromEnd = true

        progressBar?.visibility = View.VISIBLE
        send_button.setOnClickListener(this)

        MyChatManager.setmContext(context)

        when (type) {
            AppConstants().COMMUNITY_CHAT -> {

                // コミュニティ作成者は退会出来ない
                if (communityMap?.get(id!!)?.members?.get(currentUser?.uid)?.admin == null) {
                    setHasOptionsMenu(true)
                }
                if (id != null) {
                    MyChatManager.fetchCommunityMembersDetails(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            readMessagesFromFirebase()
                            getLastMessageAndUpdateUnreadCount()
                        }

                    }, NetworkConstants().FETCH_COMMUNITY_MEMBERS_DETAILS, id)
                }
            }

            AppConstants().FRIEND_CHAT -> {
                if (id != null) {
                    MyChatManager.fetchFriendMembersDetails(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            readMessagesFromFirebase()
                            getLastMessageAndUpdateUnreadCount()
                        }
                    }, NetworkConstants().FETCH_COMMUNITY_MEMBERS_DETAILS)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.chat_toolbar_item,menu)
        for (i in 0 until menu?.size()!!) {
            val item = menu.getItem(i)
            val spanString = SpannableString(menu.getItem(i).title.toString())
            spanString.setSpan(ForegroundColorSpan(Color.BLACK), 0, spanString.length, 0) //fix the color to white
            item.title = spanString
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.member -> {
                val communityMemberFragment = CommunityMemberFragment.newInstance(id)
                val fragmentManager: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, communityMemberFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                return true
            }
            R.id.leave -> {
                MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {

//                        DataConstants.communityMap?.get(id)?.members?.remove(DataConstants.currentUser?.uid)

                        Toast.makeText(context, "You have been exited from group", Toast.LENGTH_LONG).show()
                        fragmentManager.popBackStack()
                        fragmentManager.beginTransaction().remove(this@ChatFragment).commit()
                    }

                }, id, DataConstants.currentUser?.uid)
                return true
            }
            else -> {
                return false
            }
        }
    }

    fun getLastMessageAndUpdateUnreadCount() {
        MyChatManager.fetchLastMessage(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val lastMessage: MessageModel? = obj as MessageModel
                if (lastMessage != null) {
                    if (type == AppConstants().COMMUNITY_CHAT) {
                        MyChatManager.updateCommunityUnReadCountLastSeenMessageTimestamp(id, lastMessage)
                    } else if (type ==  AppConstants().FRIEND_CHAT) {
                        MyChatManager.updateFriendUnReadCountLastSeenMessageTimestamp(id, lastMessage)
                    }
                }
            }

        }, NetworkConstants().FETCH_MESSAGES, id)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send_button -> {
                val message: String = chat_edit_text.text.toString()
                if (!message.isEmpty()) {
                    sendMessage(message)
                }
            }
        }
    }

    private fun sendMessage(message: String) {

        when (type) {
            AppConstants().FRIEND_CHAT -> {
                sendMessageToFriend(message)
            }

            AppConstants().COMMUNITY_CHAT -> {
                sendMessageToCommunity(message)
            }
        }


    }

    private fun sendMessageToCommunity(message: String) {
        val cal: Calendar = Calendar.getInstance()
        val readStatusTemp: HashMap<String, Boolean> = hashMapOf()

        val messageModel: MessageModel? = MessageModel(message, currentUser?.uid, cal.timeInMillis.toString(),
                read_status = readStatusTemp)

        MyChatManager.sendMessageToACommunity(object : NotifyMeInterface {

            override fun handleData(obj: Any, requestCode: Int?) {
                chat_edit_text.setText("")
                chat_messages_recycler.scrollToPosition(newAdapter?.itemCount!!)
            }

        }, NetworkConstants().SEND_MESSAGE_REQUEST, id, messageModel)
    }

    private fun sendMessageToFriend(message: String) {
        val cal: Calendar = Calendar.getInstance()
        val readStatusTemp: HashMap<String, Boolean> = hashMapOf()

        val messageModel: MessageModel? = MessageModel(message, currentUser?.uid, cal.timeInMillis.toString(),
                read_status = readStatusTemp)

        MyChatManager.sendMessageToAFriend(object : NotifyMeInterface {

            override fun handleData(obj: Any, requestCode: Int?) {
                chat_edit_text.setText("")
                chat_messages_recycler.scrollToPosition(newAdapter?.itemCount!!)
            }

        }, NetworkConstants().SEND_MESSAGE_REQUEST, id, messageModel)
    }

    override fun onStop() {
        super.onStop()
        chat_messages_recycler.removeOnScrollListener(scrollListener)
        getLastMessageAndUpdateUnreadCount()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    private fun readMessagesFromFirebase() {
        var deleteTill = ""
        when (type) {
            AppConstants().COMMUNITY_CHAT -> {
                val currentCommunity = DataConstants.communityMap?.get(id)
                deleteTill = currentCommunity?.members?.get(currentUser?.uid)?.deleteTill!!
            }
            AppConstants().FRIEND_CHAT -> {
                deleteTill = friendMap[id]?.members?.get(currentUser?.uid)?.deleteTill!!
            }
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference

        val itemCount = 10

        val ref: Query = mFirebaseDatabaseReference?.child(FirebaseConstants().MESSAGES)
                ?.child(id)!!

        newAdapter = ChatAdapter(type, context, ref, itemCount, deleteTill, chat_messages_recycler)

        chat_messages_recycler.layoutManager = mLinearLayoutManager
        chat_messages_recycler.adapter = newAdapter
        chat_messages_recycler.scrollToPosition(itemCount)
        send_button.visibility = View.VISIBLE
        progressBar?.visibility = View.INVISIBLE

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isRecyclerViewAtTop() && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    newAdapter?.more()
                }
            }
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {}
        }

        chat_messages_recycler.addOnScrollListener(scrollListener)
    }

    private fun isRecyclerViewAtTop(): Boolean {
        return if (chat_messages_recycler.childCount == 0) true else chat_messages_recycler.getChildAt(0).top == 0
    }

//    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//
//        val storageRef = storage.getReferenceFromUrl(NetworkConstants.URL_STORAGE_REFERENCE).child(NetworkConstants.FOLDER_STORAGE_IMG)
//
//        if (requestCode == IMAGE_GALLERY_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                val selectedImageUri = data.data
//                if (selectedImageUri != null) {
//                    sendFileFirebase(storageRef, selectedImageUri)
//                } else {
//                    //URI IS NULL
//                }
//            }
//        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                if (filePathImageCamera != null && filePathImageCamera!!.exists()) {
//                    val imageCameraRef = storageRef.child(filePathImageCamera!!.getName() + "_camera")
//                    sendFileFirebase(imageCameraRef, filePathImageCamera!!)
//                } else {
//                    //IS NULL
//                }
//            }
//        } else if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                val place = PlacePicker.getPlace(this, data)
//                if (place != null) {
//                    val latLng = place.latLng
//                    val mapModel = LocationModel(latLng.latitude.toString() + "", latLng.longitude.toString() + "")
//                    //val chatModel = MessageModel(tfUserModel.getUserId(), ffUserModel.getUserId(), ffUserModel, Calendar.getInstance().time.time.toString() + "", mapModel)
//                    // mFirebaseDatabaseReference.child(deedId).child(CHAT_REFERENCE).child(seekerProviderKey).push().setValue(chatModel)
//                } else {
//                    //PLACE IS NULL
//                }
//            }
//        }
//
//    }


//    private fun sendFileFirebase(storageReference: StorageReference?, file: File) {
//        if (storageReference != null) {
//            val uploadTask = storageReference.putFile(Uri.fromFile(file))
//            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
//                Log.i("", "onSuccess sendFileFirebase")
//                val downloadUrl = taskSnapshot.downloadUrl
//                val fileModel = FileModel("img", downloadUrl!!.toString(), file.name, file.length().toString() + "")
//                //  val chatModel = MessageModel(tfUserModel.getUserId(), ffUserModel.getUserId(), ffUserModel, Calendar.getInstance().time.time.toString() + "", fileModel)
//                //  mFirebaseDatabaseReference.child(deedId).child(CHAT_REFERENCE).child(seekerProviderKey).push().setValue(chatModel)
//            }
//        } else {
//            //IS NULL
//        }
//
//    }
//
//
//    private fun sendFileFirebase(storageReference: StorageReference?, file: Uri) {
//        if (storageReference != null) {
//            val name = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
//            val imageGalleryRef = storageReference.child(name + "_gallery")
//            val uploadTask = imageGalleryRef.putFile(file)
//            uploadTask.addOnFailureListener { e -> Log.e("", "onFailure sendFileFirebase " + e.message) }.addOnSuccessListener { taskSnapshot ->
//                Log.i("", "onSuccess sendFileFirebase")
//                val downloadUrl = taskSnapshot.downloadUrl
//                val fileModel = FileModel("img", downloadUrl!!.toString(), name, "")
//                //   val chatModel = MessageModel(tfUserModel.getUserId(), ffUserModel.getUserId(), ffUserModel, Calendar.getInstance().time.time.toString() + "", fileModel)
//                // mFirebaseDatabaseReference.child(deedId).child(CHAT_REFERENCE).child(seekerProviderKey).push().setValue(chatModel)
//            }
//        } else {
//            //IS NULL
//        }
//
//    }


    companion object {

        fun newInstance(chatRoomModel: ChatRoomModel): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            args.putSerializable("chatRoomModel", chatRoomModel)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
