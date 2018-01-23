package com.go26.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.go26.chatapp.constants.DataConstants
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.communityMembersMap
import com.go26.chatapp.constants.DataConstants.Companion.communityMessageMap
import com.go26.chatapp.constants.DataConstants.Companion.userMap
import com.go26.chatapp.constants.FirebaseConstants
import com.go26.chatapp.constants.NetworkConstants
import com.go26.chatapp.constants.PrefConstants
import com.go26.chatapp.model.CommunityModel
import com.go26.chatapp.model.MessageModel
import com.go26.chatapp.model.UserModel
import com.go26.chatapp.ui.LoginActivity
import com.go26.chatapp.util.MyTextUtil
import com.go26.chatapp.util.SecurePrefs
import com.go26.chatapp.util.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.util.*


@SuppressLint("StaticFieldLeak")

/**
 * Created by daigo on 2018/01/14.
 */
object MyChatManager {
    val TAG = "MyChatManager"
    var myChatManager: MyChatManager? = null
    var auth: FirebaseAuth? = FirebaseAuth.getInstance()
    var database: FirebaseDatabase? = FirebaseDatabase.getInstance()
    var authListener: FirebaseAuth.AuthStateListener? = null
    var isFirebaseAuthSuccessfull = false
    var firebaseUserId = ""
    var firebaseDatabaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference
    val gson = Gson()
    var context: Context? = null
    var userRef: DatabaseReference? = firebaseDatabaseReference?.child(FirebaseConstants().USERS)
    var communityRef: DatabaseReference? = firebaseDatabaseReference?.child(FirebaseConstants().COMMUNITY)
    var messageRef: DatabaseReference? = firebaseDatabaseReference?.child(FirebaseConstants().MESSAGES)

    var communityListener: ValueEventListener? = null


    fun setmContext(context: Context) {
        this.context = context
    }

    fun init(context: Context) {
        this.context = context
        setupFirebaseAuth()
        signInToFirebaseAnonymously()
    }

    /**
     * Setup Firebase Auth and Database
     */
    fun setupFirebaseAuth() {
        if (auth == null)
            auth = FirebaseAuth.getInstance()
        if (database == null)
            database = FirebaseDatabase.getInstance()

        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                firebaseUserId = user.uid
                isFirebaseAuthSuccessfull = false
                signInToFirebaseAnonymously()
            }
        }
    }

    /**
     * Sign in to firebase Anonymously
     */
    fun signInToFirebaseAnonymously() {
        setupFirebaseAuth()
        if (!isFirebaseAuthSuccessfull) {
            auth?.signInAnonymously()?.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("", "signInAnonymously", task.exception)
                    isFirebaseAuthSuccessfull = false
                } else {
                    isFirebaseAuthSuccessfull = true
                }
            }?.addOnFailureListener { Log.w("", "signInAnonymously") }
        }

    }

    /*
    * Firebase ref = Firebase(url: "https://<YOUR-FIREBASE-APP>.firebaseio.com");
Firebase userRef = ref.child("user");
Map newUserData = new HashMap();
newUserData.put("age", 30);
newUserData.put("city", "Provo, UT");
userRef.updateChildren(newUserData);
    * */
    //TODO: Update multiple items at once
    /*
    * Firebase ref = new Firebase("https://<YOUR-FIREBASE-APP>.firebaseio.com");
// Generate a new push ID for the new post
Firebase newPostRef = ref.child("posts").push();
String newPostKey = newPostRef.getKey();
// Create the data we want to update
Map newPost = new HashMap();
newPost.put("title", "New Post");
newPost.put("content", "Here is my new post!");
Map updatedUserData = new HashMap();
updatedUserData.put("users/posts/" + newPostKey, true);
updatedUserData.put("posts/" + newPostKey, newPost);
// Do a deep-path update
ref.updateChildren(updatedUserData, new Firebase.CompletionListener() {
   @Override
   public void onComplete(FirebaseError firebaseError, Firebase firebase) {
       if (firebaseError != null) {
           System.out.println("Error updating data: " + firebaseError.getMessage());
       }
   }
});
    * */

    /**
     * Login if node is already present then just update the name and imageurl and don't alter any other field.
     *
     */
    fun loginCreateAndUpdate(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {
        try {
            userRef?.child(userModel?.uid)?.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val p = mutableData.getValue<UserModel>(UserModel::class.java)
                    if (p == null) {
                        mutableData.value = userModel
                    } else {
                        val newUserData: HashMap<String, Any?> = hashMapOf()
                        newUserData.put("imageUrl", userModel?.image_url)
                        newUserData.put("name", userModel?.name)
                        newUserData.put("online", true)
                        userRef?.child(userModel?.uid)?.updateChildren(newUserData)
                    }
                    return Transaction.success(mutableData)

                }

                override fun onComplete(databaseError: DatabaseError?, p1: Boolean, dataSnapshot: DataSnapshot?) {
                    try {
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError)
                        callback?.handleData(true, requestType)
//                        val user: UserModel? = dataSnapshot?.getValue<UserModel>(UserModel::class.java)
//                        fetchCurrentUser(callback, user, requestType)
//                        fetchMyCommunities(callback, requestType, userModel, true)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Function to fetch current user
     */
    fun fetchCurrentUser(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {
        try {
            if (userRef != null && userModel?.uid != null) {
                userRef?.child(userModel.uid)?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        callback?.handleData(false, requestType)
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        val user: UserModel? = p0?.getValue<UserModel>(UserModel::class.java)
                        user?.let {
                            it.online?.let {
                                if (it) {
                                    currentUser = user
                                    SharedPrefManager.getInstance(context!!).savePreferences(PrefConstants().USER_DATA, gson.toJson(currentUser))
                                    fetchMyCommunities(callback, requestType, user, true)
//                                    callback?.handleData(true, requestType)
                                } else {
                                    userRef?.child(user.uid)?.removeEventListener(this)
                                }
                            }
                        }

                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This function gets all the groups in which user is present.
     */
    fun fetchMyCommunities(callback: NotifyMeInterface?, requestType: Int?, userModel: UserModel?, isSingleEvent: Boolean) {

        var i: Int = userModel?.community?.size!!
        if (i == 0) {
//            //No Groups
            callback?.handleData(true, requestType)
        }
        communityListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(communitySnapshot: DataSnapshot) {
                if (communitySnapshot.exists()) {
                    val communityModel: CommunityModel = communitySnapshot.getValue<CommunityModel>(CommunityModel::class.java)!!
                    val memberList: ArrayList<UserModel> = arrayListOf()
                    if (!communityModel.communityDeleted!!) {
                        for (member in communityModel.members) {
                            memberList.add(member.value)
                        }
                        communityMembersMap?.put(communityModel.communityId!!, memberList)
                        communityMessageMap?.put(communityModel.communityId!!, arrayListOf())
                        communityMap?.put(communityModel.communityId!!, communityModel)

                        // fcm
//                        FirebaseMessaging.getInstance().subscribeToTopic(communityModel.communityId!!)
                    }

                }
//                i--
//                if (i <= 0) {
//                    callback?.handleData(true, requestType)
//                }
            }
        }



        for (community in userModel.community) {

            if (community.value) {
//                if (isSingleEvent) {
//                    communityRef?.child(community.key)?.addListenerForSingleValueEvent(communityListener)
//                } else {
//                    communityRef?.child(community.key)?.addValueEventListener(communityListener)
//                }
                communityRef?.child(community.key)?.addValueEventListener(communityListener)
                i--

            } else {
                i--
            }
        }
        if (i <= 0) {
            callback?.handleData(true, requestType)
        }

    }


    fun updateFCMTokenAndDeviceId(context: Context, token: String) {
        var deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        var deviceIdMap: java.util.HashMap<String, String> = hashMapOf()
        deviceIdMap.put(deviceId, token)

        userRef?.child(currentUser?.uid)?.child("deviceIds")?.setValue(deviceIdMap)
    }

    fun logout(context: Context, googleSignInClient: GoogleSignInClient) {
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        val deviceIdMap: MutableMap<String, Any> = hashMapOf()
        val userMap: MutableMap<String, Any?> = hashMapOf()

        deviceIdMap.put(deviceId, "")
        userMap.put("online", false)
        userMap.put("/deviceIds/", deviceIdMap)

        userRef?.child(currentUser?.uid)?.updateChildren(userMap)?.addOnCompleteListener {
            SharedPrefManager.getInstance(context).cleanSharedPreferences()
            currentUser = null

            val auth = FirebaseAuth.getInstance()
            auth.signOut()

            googleSignInClient.signOut()

            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }


    /**
     * This creates the new user node
     *
     */
    fun createOrUpdateUserNode(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {
        try {
            userRef?.child(userModel?.uid)?.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    val p = mutableData.getValue<UserModel>(UserModel::class.java)
                    if (p == null) {
                        mutableData.setValue(userModel)
                    } else {
                        var newUserData: HashMap<String, Any?> = hashMapOf();
                        newUserData.put("imageUrl", userModel?.image_url)
                        newUserData.put("name", userModel?.name)
                        newUserData.put("online", true)
                        userRef?.child(userModel?.uid)?.updateChildren(newUserData)
                    }
                    return Transaction.success(mutableData)

                }

                override fun onComplete(databaseError: DatabaseError?, p1: Boolean, dataSnapshot: DataSnapshot?) {
                    try {
                        Log.d(TAG, "postTransaction:onComplete:" + databaseError)

                        var userModel: UserModel? = dataSnapshot?.getValue<UserModel>(UserModel::class.java)
                        callback?.handleData(userModel!!, requestType)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * This function is called to set user status to offline
     */
    fun goOffline(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {
        userRef?.child(userModel?.uid)?.child(FirebaseConstants().ONLINE)?.setValue(false)
        callback?.handleData(true, requestType)
    }

    /**
     * Get user list from firebase
     */
    fun getAllUsersFromFirebase(callback: NotifyMeInterface?, requestType: Int?) {

        // Making a copy of listener
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnaphot: DataSnapshot) {
                if (dataSnaphot.exists()) {
                    var userList: ArrayList<UserModel> = ArrayList()
                    dataSnaphot.children.forEach { it ->
                        it.getValue<UserModel>(UserModel::class.java)?.let {
                            if (!SecurePrefs(context!!).get(PrefConstants().USER_ID).equals(it.uid)) {
                                userList.add(it)
                            }
                        }
                    }
                    callback?.handleData(userList, requestType)
                }
            }
        }

        userRef?.addValueEventListener(listener)

    }

    /**
     * This function creates a community in the firebase and adds an entry of community id under users and set it to
     * true.
     */
    fun createCommunity(callback: NotifyMeInterface?, community: CommunityModel, requestType: Int?) {

        val communityId = communityRef?.push()?.key
        community.communityId = communityId
        val time = Calendar.getInstance().timeInMillis

        for (user in community.members) {
            user.value.community = hashMapOf()
            user.value.email = null
            user.value.image_url = null
            user.value.name = null
            user.value.online = null
            user.value.unread_community_count = 0
            user.value.last_seen_message_timestamp = time.toString()
            user.value.delete_till = time.toString()
        }

        communityRef?.child(communityId)?.setValue(community)

        for (user in community.members) {
            userRef?.child(user.value.uid)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.setValue(true)
        }

        callback?.handleData(true, requestType)
    }


    /**
     * This function sends messages to a community
     */
    fun sendMessageToACommunity(callback: NotifyMeInterface?, requestType: Int?, communityId: String?,
                                messageModel: MessageModel?) {

        val messageKey = messageRef?.child(communityId)?.push()?.key
        messageModel?.message_id = messageKey

        messageRef?.child(communityId)?.child(messageKey)?.setValue(messageModel)

        communityRef?.child(communityId)?.child(FirebaseConstants().LAST_MESSAGE)?.setValue(messageModel)

        callback?.handleData(true, requestType)

        for (member in communityMap?.get(communityId)?.members!!) {
            if (member.value.uid != currentUser?.uid) {
                communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(member.value.uid)?.
                        runTransaction(object : Transaction.Handler {
                            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                                if (p0 != null) {
                                    Log.d("INC", "Firebase counter increment failed.")
                                } else {
                                    Log.d("INC", "Firebase counter increment succeeded.")
                                }
                            }

                            override fun doTransaction(mutabledata: MutableData?): Transaction.Result {
                                if (mutabledata?.getValue<UserModel>(UserModel::class.java)?.unread_community_count == null) {
                                    var p = mutabledata?.getValue<UserModel>(UserModel::class.java)
                                    p?.unread_community_count = 0
                                    mutabledata?.setValue(p)
                                } else {
                                    var p = mutabledata.getValue<UserModel>(UserModel::class.java)
                                    p?.unread_community_count = p?.unread_community_count as Int + 1
                                    mutabledata.setValue(p)
                                }

                                return Transaction.success(mutabledata)
                            }
                        })
            }
        }

    }


    fun fetchCommunityMembersDetails(callback: NotifyMeInterface?, requestType: Int?, communityId: String?) {
        var i: Int = communityMembersMap?.get(communityId)?.size!!
        for (member in communityMembersMap?.get(communityId)!!) {
            userRef?.child(member.uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    i--
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var userModel: UserModel = snapshot.getValue<UserModel>(UserModel::class.java)!!
                        userMap?.put(userModel.uid!!, userModel)
                        i--
                        if (i == 0) {
                            callback?.handleData(true, requestType)
                        }
                    }
                }

            })
        }


    }


    fun fetchLastMessageFromCommunity(callback: NotifyMeInterface?, requestType: Int?, communityId: String?) {

        /* val clistener = object : ChildEventListener {
             override fun onCancelled(databaseError: DatabaseError) {
                 callback?.handleData(false, requestType)
             }
             override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
             override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
             override fun onChildRemoved(p0: DataSnapshot?) {}
             override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                 if (dataSnapshot.exists()) {
                     //communityMessageMap?.get(communityId)?.clear()
                     dataSnapshot.getValue<MessageModel>(MessageModel::class.java)?.let {
                         communityMessageMap?.get(communityId)?.add(it)
                     }
                     callback?.handleData(true, requestType)
                 } else {
                     callback?.handleData(false, requestType)
                 }
             }
         }*/

        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot?.exists()!!) {

                    var lastMessage: MessageModel = dataSnapshot.children.elementAt(0).getValue<MessageModel>(MessageModel::class.java)!!


                    callback?.handleData(lastMessage, requestType)
                } else {
                    callback?.handleData(MessageModel(), requestType)
                }
            }

        }
        val lastQuery = messageRef?.child(communityId)?.orderByKey()?.limitToLast(1)

        lastQuery?.addListenerForSingleValueEvent(listener)
        // messageRef?.child(communityId)?.addChildEventListener(clistener)
    }

    /**
     * Call this function when user opens any chat groups.
     */
    fun updateUnReadCountLastSeenMessageTimestamp(groupId: String?, lastMessageModel: MessageModel) {

        /* communityRef?.child(communityId)?.child(FirebaseConstants.MEMBERS)?.
                 child(sCurrentUser?.uid)?.child(FirebaseConstants.UNREAD_COMMUNITY_COUNT)?.setValue(0)
         communityRef?.child(communityId)?.child(FirebaseConstants.MEMBERS)?.
                 child(sCurrentUser?.uid)?.child(FirebaseConstants.L_S_M_T)?.setValue(lastMessageModel.timestamp)*/

        val groupMember: HashMap<String, Any?> = hashMapOf()
        groupMember.put(FirebaseConstants().UNREAD_COMMUNITY_COUNT, 0)
        groupMember.put(FirebaseConstants().L_S_M_T, lastMessageModel.timestamp)


        communityRef?.child(groupId)?.child(FirebaseConstants().MEMBERS)?.
                child(currentUser?.uid)?.updateChildren(groupMember);
        lastMessageModel.read_status = hashMapOf()
        communityRef?.child(groupId)?.child(FirebaseConstants().LAST_MESSAGE)?.setValue(lastMessageModel)
    }


    fun fetchAllUserInformation() {
        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.exists()!!) {
                    p0.children.forEach { user ->
                        DataConstants.userMap?.put(user.key/*getValue<UserModel>(UserModel::class.java)?.uid!!*/, user.getValue<UserModel>(UserModel::class.java)!!)
                    }
                }
            }

        })
    }

    fun changeAdminStatusOfUser(callback: NotifyMeInterface?, communityId: String?, userId: String?, isAdmin: Boolean) {
        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userId)?.child(FirebaseConstants().ADMIN)?.setValue(isAdmin)
        callback?.handleData(true, NetworkConstants().CHANGE_ADMIN_STATUS)
    }

    fun removeMemberFromCommunity(callback: NotifyMeInterface?, communityId: String?, userId: String?) {
        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userId)?.removeValue()
        userRef?.child(userId)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.setValue(false)
        callback?.handleData(true, 1)
    }

    fun addMemberToACommunity(callback: NotifyMeInterface?, communityId: String?, userModel: UserModel?) {
        //userRef?.child(userModel?.uid)?.child(FirebaseConstants.COMMUNITY)?.child(communityId)?.setValue(true)

        val time = Calendar.getInstance().timeInMillis

        userModel?.community = hashMapOf()
        userModel?.email = null
        userModel?.image_url = null
        userModel?.name = null
        userModel?.online = null
        userModel?.unread_community_count = 0
        userModel?.last_seen_message_timestamp = time.toString()
        userModel?.delete_till = time.toString()

        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userModel?.uid)?.setValue(userModel)

        userRef?.child(userModel?.uid)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.setValue(true)
        callback?.handleData(true, 1)
    }

    /*
     *
     * Create a groupID of these two by calling getHash(uid1,uid2)
     *
     * Create a community with 2 members, community flag set to false. Then add community id in user1, user2 to be true
     *
     * Then add the message under the MESSAGE->GROUPID.
     */
    fun createOneOnOneChatCommunity(callback: NotifyMeInterface, user2Id: String, user2: UserModel, requestType: Int) {

        val newCommunityId = MyTextUtil().getHash(currentUser?.uid!!, user2Id)


        val community = CommunityModel("", "", newCommunityId, false, false)

        community.members.put(user2Id, user2)
        community.members.put(currentUser?.uid!!, currentUser!!)

        val time = Calendar.getInstance().timeInMillis

        for (user in community.members) {
            user.value.community = hashMapOf()
            user.value.email = null
            user.value.image_url = null
            user.value.name = null
            user.value.online = null
            user.value.unread_community_count = 0
            user.value.last_seen_message_timestamp = time.toString()
            user.value.delete_till = time.toString()
        }

        communityRef?.child(newCommunityId)?.setValue(community)

        for (user in community.members) {
            userRef?.child(user.value.uid)?.child(FirebaseConstants().COMMUNITY)?.child(newCommunityId)?.setValue(true)
        }

        communityMap?.put(community.communityId!!, community)

        callback?.handleData(true, requestType)
    }

    /**
     * Check if a community exists or not
     */
    fun checkIfCommunityExists(callback: NotifyMeInterface, communityId: String, requestType: Int) {
        try {

            communityRef?.child(communityId)?.runTransaction(object : Transaction.Handler {
                override fun doTransaction(p0: MutableData?): Transaction.Result {
                    val p = p0?.getValue<CommunityModel>(CommunityModel::class.java)
                    if (p == null) {

                    }
                    return Transaction.success(p0)
                }

                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                    val p = p2?.getValue<CommunityModel>(CommunityModel::class.java)
                    if (p == null) {
                        callback.handleData(false, requestType)
                    } else {
                        callback.handleData(true, requestType)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This function is used to update the current timestamp to delete chats
     */
    fun deleteCommunityChat(callback: NotifyMeInterface?, communityId: String?) {
        var time = Calendar.getInstance().timeInMillis

        communityRef?.child(communityId!!)?.child("lastMessage")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0?.exists()!!) {
                    var message: MessageModel = p0.getValue<MessageModel>(MessageModel::class.java)!!

                    communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(currentUser?.uid)
                            ?.child(FirebaseConstants().DELETE_TILL_TIMESTAMP)?.setValue(/*message.timestamp.toString()*/time.toString())
                    callback?.handleData(true, NetworkConstants().DELETE_GROUP_CHAT)
                }

            }

        })


    }

    /**
     * This function sets the online status to true under user node. And sets to false when
     * user exits the app.
     */
    fun setOnlinePresence() {
        FirebaseDatabase.getInstance().reference.child(".info/connected")
        val onlineRef = FirebaseDatabase.getInstance().reference.child(".info/connected")
        val currentUserRef = FirebaseDatabase.getInstance().reference.child("/users/" + currentUser?.uid + "/online")
        val lastSeenRef = FirebaseDatabase.getInstance().reference.child("/users/" + currentUser?.uid + "/last_seen_online")

        onlineRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("", "DataSnapshot:" + dataSnapshot)
                if (dataSnapshot.getValue(Boolean::class.java)!!) {
                    currentUserRef?.onDisconnect()?.setValue(false)
                    lastSeenRef?.onDisconnect()?.setValue(Calendar.getInstance().timeInMillis.toString())
                    currentUserRef?.setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("", "DatabaseError:" + databaseError)
            }
        })
    }
}