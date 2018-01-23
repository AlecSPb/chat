package com.go26.chatapp.ui


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import com.go26.chatapp.InfiniteFirebaseRecyclerAdapter
import com.go26.chatapp.MyChatManager
import com.go26.chatapp.NotifyMeInterface

import com.go26.chatapp.R
import com.go26.chatapp.ViewHolders.ViewHolder
import com.go26.chatapp.adapter.ChatRecyclerAdapter
import com.go26.chatapp.constants.AppConstants
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.myCommunities
import com.go26.chatapp.constants.FirebaseConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.model.MessageModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.util.MyTextUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*
import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import com.go26.chatapp.constants.DataConstants.Companion.communityMap


class ChatFragment : Fragment(), View.OnClickListener {
    var adapter: ChatRecyclerAdapter? = null
    var communityId: String? = ""
    var position: Int? = 0
    var progressBar: ProgressBar? = null
    var mFirebaseDatabaseReference: DatabaseReference? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var storage = FirebaseStorage.getInstance()
    var type: String? = ""

    val IMAGE_GALLERY_REQUEST = 1
    val IMAGE_CAMERA_REQUEST = 2
    val PLACE_PICKER_REQUEST = 3

    var user2: UserModel = UserModel()
    var user2Id: String = ""

    var communityIsPresent: Boolean = false

    //File
    var filePathImageCamera: File? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setViews()
    }

    private fun setViews() {
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

        communityId = arguments.getString(AppConstants().COMMUNITY_ID)
        position = arguments.getInt(AppConstants().POSITION)
        type = arguments.getString(AppConstants().CHAT_TYPE)
        tv_loadmore.setOnClickListener(this)

        //actionbar
        val toolbar: Toolbar? = view?.findViewById(R.id.toolbar)
        val activity: AppCompatActivity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(true)
        // コミュニティ作成者は退会出来ない
        if (communityMap?.get(communityId!!)?.members?.get(currentUser?.uid)?.admin == null) {
            setHasOptionsMenu(true)
        }

        MyChatManager.setmContext(context)

        when (type) {
            AppConstants().COMMUNITY_CHAT -> {

                if (communityId != null) {
                    activity.supportActionBar?.title = myCommunities?.get(position!!)?.name
                    mLinearLayoutManager = LinearLayoutManager(context)
                    mLinearLayoutManager!!.setStackFromEnd(true)
                    //  chat_messages_recycler.layoutManager = mLinearLayoutManager

                    progressBar?.visibility = View.VISIBLE
                    btnSend.setOnClickListener(this)

                    MyChatManager.fetchCommunityMembersDetails(object : NotifyMeInterface {
                        override fun handleData(obj: Any, requestCode: Int?) {
                            readMessagesFromFirebase(communityId!!)
                            getLastMessageAndUpdateUnreadCount()
                        }

                    }, NetworkConstants().FETCH_GROUP_MEMBERS_DETAILS, communityId)
                }


            }

            AppConstants().ONE_ON_ONE_CHAT -> {
                mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager!!.setStackFromEnd(true)
                btnSend.setOnClickListener(this)

                btnSend.visibility = View.GONE
                user2Id = arguments.getString(AppConstants().USER_ID)
                val userModel = DataConstants.userMap?.get(user2Id)
                activity.supportActionBar?.title = userModel?.name

//                checkIfGroupExistsOrNot();

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
            R.id.leave -> {
                MyChatManager.setmContext(context)
                MyChatManager.removeMemberFromCommunity(object : NotifyMeInterface {
                    override fun handleData(obj: Any, requestCode: Int?) {

                        DataConstants.communityMap?.get(communityId)?.members?.remove(DataConstants.currentUser?.uid)

                        Toast.makeText(context, "You have been exited from group", Toast.LENGTH_LONG).show()
                        fragmentManager.beginTransaction().remove(this@ChatFragment).commit()
                    }

                }, communityId, DataConstants.currentUser?.uid)
                return true
            }
            else -> {
                return false
            }
        }
    }

    /**
     * Check if group exists, if exists then download the chat data.
     * If group doesn't exists, then wait till the first message is sent.
     *
     *
     * When first message is sent.
     *
     * Check if user2 node is present or not, if not create user2 node. Then,
     *
     * Create a groupID of these two by calling getHash(uid1,uid2)
     *
     * Create a group with 2 members, group flag set to false. Then add group id in user1, user2 to be true
     *
     * Then add the message under the MESSAGE->GROUPID.
     */
//    private fun checkIfGroupExistsOrNot() {
//        communityId = MyTextUtil().getHash(currentUser?.uid!!, user2Id)
//
//        MyChatManager.checkIfCommunityExists(object : NotifyMeInterface {
//            override fun handleData(obj: Any, requestCode: Int?) {
//                if (obj as Boolean) {
//                    //Exists so fetch the data
//                    readMessagesFromFirebase(communityId!!)
//                    tv_last_seen.visibility = View.VISIBLE
//                    user2 = DataConstants.userMap?.get(user2Id)!!
//                    if (user2.online!!) {
//                        tv_last_seen.setText("Online")
//                    } else if (user2.last_seen_online != null) {
//                        tv_last_seen.setText(MyTextUtil().getTimestamp(user2.last_seen_online!!.toLong()))
//                    } else {
//                        tv_last_seen.visibility = View.GONE
//                    }
//                    communityIsPresent = true
//                } else {
//                    //Doesn't exists wait till first message is sent (Do nothing)
//                    user2 = DataConstants.userMap?.get(user2Id)!!
//                    MyChatManager.createOrUpdateUserNode(object : NotifyMeInterface {
//                        override fun handleData(obj: Any, requestCode: Int?) {
//                            user2 = obj as UserModel
//                            //createCommunityOfTwo(user2, null)
//                        }
//                    }, user2, NetworkConstants().CREATE_USER_NODE)
//                }
//            }
//
//        }, communityId!!, NetworkConstants().CHECK_GROUP_EXISTS)
//
//
//    }


    fun getLastMessageAndUpdateUnreadCount() {
        MyChatManager.fetchLastMessageFromCommunity(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                val lastMessage: MessageModel? = obj as MessageModel
                if (lastMessage != null) {
                    MyChatManager.updateUnReadCountLastSeenMessageTimestamp(communityId, lastMessage)
                }
            }

        }, NetworkConstants().FETCH_MESSAGES, communityId)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                val message: String = et_chat.text.toString()
                if (!message.isEmpty()) {
                    sendMessage(message!!)
                }
            }

            R.id.tv_loadmore -> {
                /* newAdapter?.more()
                 tv_loadmore.visibility = View.GONE*/
            }

        }
    }

    /**
     * Check if group exists, if exists then download the chat data.
     * If group doesn't exists, then wait till the first message is sent.
     *
     *
     * When first message is sent.
     *
     * Check if user2 node is present or not, if not create user2 node. Then,
     *
     * Create a groupID of these two by calling getHash(uid1,uid2)
     *
     * Create a group with 2 members, group flag set to false. Then add group id in user1, user2 to be true
     *
     * Then add the message under the MESSAGE->GROUPID.
     */
    fun sendMessage(message: String) {

        when (type) {
            AppConstants().ONE_ON_ONE_CHAT -> {
                if (communityIsPresent) {
                    sendMessageToCommunity(message)
                } else {
                    /* if (DataConstants.userMap?.containsKey(user2Id)!!) {
                         user2 = DataConstants.userMap?.get(user2Id)!!
                         //createCommunityOfTwo(sCurrentUser, user2)
                     } else {
                         //User2 node is not there so create one.
                         user2.email = "sky.wall.treasure@gmail.com"
                         user2.name = "Sky Wall Treasure"
                         user2.image_url = "https://lh6.googleusercontent.com/-x8DMU8TwQWU/AAAAAAAAAAI/AAAAAAAALQA/waA51g0k3GA/s96-c/photo.jpg"
                         user2.uid = user2Id
                         user2.group = hashMapOf()*/


                }
            }

            AppConstants().COMMUNITY_CHAT -> {
                sendMessageToCommunity(message)
            }
        }


    }

    private fun createCommunityOfTwo(user2: UserModel, message: String?) {

        MyChatManager.createOneOnOneChatCommunity(object : NotifyMeInterface {
            override fun handleData(obj: Any, requestCode: Int?) {
                communityIsPresent = true
                readMessagesFromFirebase(communityId!!)
                btnSend.visibility = View.VISIBLE
                if (message != null) {
                    sendMessageToCommunity(message)
                }


            }

        }, user2Id, user2, NetworkConstants().CREATE_ONE_ON_ONE_GROUP)
    }

    private fun sendMessageToCommunity(message: String) {
        val cal: Calendar = Calendar.getInstance()
        val read_status_temp: HashMap<String, Boolean> = hashMapOf()

        /*   for (member in groupMembersMap?.get(communityId!!)!!) {
               if (member.uid == sCurrentUser?.uid) {
                   read_status_temp.put(member.uid!!, true)
               } else {
                   read_status_temp.put(member.uid!!, false)
               }
           }*/

        val messageModel: MessageModel? = MessageModel(message, currentUser?.uid, cal.timeInMillis.toString(),
                read_status = read_status_temp)

        MyChatManager.sendMessageToACommunity(object : NotifyMeInterface {

            override fun handleData(obj: Any, requestCode: Int?) {
                et_chat.setText("")
                adapter?.notifyDataSetChanged()
                chat_messages_recycler.scrollToPosition(adapter?.itemCount!!)
//                Handler().postDelayed({
//
//                    adapter?.notifyDataSetChanged()
//                    chat_messages_recycler.scrollToPosition(adapter?.itemCount!!)
//
//                }, 1000)

            }

        }, NetworkConstants().SEND_MESSAGE_REQUEST, communityId, messageModel)
    }

    override fun onStop() {
        super.onStop()
        getLastMessageAndUpdateUnreadCount()
    }


    var newAdapter: InfiniteFirebaseRecyclerAdapter<MessageModel, ViewHolder>? = null

    private fun readMessagesFromFirebase(communityId: String) {
        val currentCommunity = DataConstants.communityMap?.get(communityId)
        var time = Calendar.getInstance().timeInMillis
        var deleteTill: String = currentCommunity?.members?.get(currentUser?.uid)?.delete_till!!
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference

        val itemCount = 10

        val ref: Query = mFirebaseDatabaseReference?.child(FirebaseConstants().MESSAGES)
                ?.child(communityId)!!

        adapter = ChatRecyclerAdapter(communityId, context, ref)

//        newAdapter = object : InfiniteFirebaseRecyclerAdapter<MessageModel, ViewHolder>(MessageModel::class.java, R.layout.item_chat_row, ViewHolder::class.java, ref, itemCount, deleteTill, chat_messages_recycler) {
//            override fun populateViewHolder(viewHolder: ViewHolder?, model: MessageModel?, position: Int) {
//                val viewHolder = viewHolder as ViewHolder
//                val chatMessage = model!!
//
//                if (chatMessage.sender_id.toString() == currentUser?.uid) {
//                    viewHolder.llParent.gravity = Gravity.END
////                    viewHolder.llChild.background =
////                            ContextCompat.getDrawable(context, R.drawable.chat_bubble_grey_sender)
//                    viewHolder.name.text = "You"
//                } else {
//                    viewHolder.llParent.gravity = Gravity.START
//                    viewHolder.name.text = DataConstants.userMap?.get(chatMessage.sender_id!!)?.name
////                    viewHolder.llChild.background = ContextCompat.getDrawable(viewHolder.llParent.context, R.drawable.chat_bubble_grey)
//                }
//                viewHolder.message.text = chatMessage.message
//                try {
//                    viewHolder.timestamp.text = MyTextUtil().getTimestamp(chatMessage.timestamp?.toLong()!!)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//
//
//                viewHolder.rlName.layoutParams.width = viewHolder.message.layoutParams.width
//            }
//
//            override fun getItemCount(): Int {
//                return super.getItemCount()
//            }
//
//        }


        chat_messages_recycler.setLayoutManager(mLinearLayoutManager)
        //chat_messages_recycler.setAdapter(firebaseAdapter)
        chat_messages_recycler.adapter = adapter
        chat_messages_recycler.scrollToPosition(itemCount)
        btnSend.visibility = View.VISIBLE
        progressBar?.visibility = View.INVISIBLE


        chat_messages_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (IsRecyclerViewAtTop() && newState == RecyclerView.SCROLL_STATE_IDLE) {

//                    newAdapter?.more()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {


            }
        })


    }

    private fun IsRecyclerViewAtTop(): Boolean {
        return if (chat_messages_recycler.getChildCount() == 0) true else chat_messages_recycler.getChildAt(0).getTop() == 0
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

        fun newInstance(communityId: String, chatType: String,
                        userId: String, position: Int): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            args.putString(AppConstants().COMMUNITY_ID, communityId)
            args.putString(AppConstants().CHAT_TYPE, chatType)
            args.putString(AppConstants().USER_ID, userId)
            args.putInt(AppConstants().POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
