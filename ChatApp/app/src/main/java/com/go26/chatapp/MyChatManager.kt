package com.go26.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import com.go26.chatapp.constants.*
import com.go26.chatapp.constants.DataConstants.Companion.communityList
import com.go26.chatapp.constants.DataConstants.Companion.currentUser
import com.go26.chatapp.constants.DataConstants.Companion.communityMap
import com.go26.chatapp.constants.DataConstants.Companion.communityMembersMap
import com.go26.chatapp.constants.DataConstants.Companion.communityMessageMap
import com.go26.chatapp.constants.DataConstants.Companion.communityRequestsList
import com.go26.chatapp.constants.DataConstants.Companion.communityRequestsMap
import com.go26.chatapp.constants.DataConstants.Companion.foundCommunityListByLocation
import com.go26.chatapp.constants.DataConstants.Companion.foundCommunityListByName
import com.go26.chatapp.constants.DataConstants.Companion.foundUserList
import com.go26.chatapp.constants.DataConstants.Companion.friendList
import com.go26.chatapp.constants.DataConstants.Companion.friendRequests
import com.go26.chatapp.constants.DataConstants.Companion.friendRequestsMap
import com.go26.chatapp.constants.DataConstants.Companion.myCommunities
import com.go26.chatapp.constants.DataConstants.Companion.myCommunityRequests
import com.go26.chatapp.constants.DataConstants.Companion.myCommunityRequestsMap
import com.go26.chatapp.constants.DataConstants.Companion.myFriendRequests
import com.go26.chatapp.constants.DataConstants.Companion.myFriendRequestsMap
import com.go26.chatapp.constants.DataConstants.Companion.myFriends
import com.go26.chatapp.constants.DataConstants.Companion.myFriendsMap
import com.go26.chatapp.constants.DataConstants.Companion.popularCommunityList
import com.go26.chatapp.constants.DataConstants.Companion.userMap
import com.go26.chatapp.model.*
import com.go26.chatapp.ui.LoginActivity
import com.go26.chatapp.util.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
    var friendRef: DatabaseReference? = firebaseDatabaseReference?.child(FirebaseConstants().FRIENDS)

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
//                        newUserData.put("imageUrl", userModel?.imageUrl)
//                        newUserData.put("name", userModel?.name)
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
    fun fetchCurrentUser(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?, isSingleEvent: Boolean) {
        try {
            if (userRef != null && userModel?.uid != null) {
                val listenerForSingle = object : ValueEventListener {
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
                                    callback?.handleData(true, requestType)
                                } else {
                                    userRef?.child(user.uid)?.removeEventListener(this)
                                }
                            }
                        }

                    }
                }

                val listener = object : ValueEventListener {
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
                                } else {
                                    userRef?.child(user.uid)?.removeEventListener(this)
                                }
                            }
                        }
                    }
                }

                if (isSingleEvent) {
                    userRef?.child(userModel.uid)?.addListenerForSingleValueEvent(listenerForSingle)
                } else {
                    userRef?.child(userModel.uid)?.addValueEventListener(listener)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This function gets all the groups in which user is present.
     */
    fun fetchMyCommunities(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?, isSingleEvent: Boolean) {
        var communityCount = 0
        var now = 0

        val listenerForSingle = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(communitySnapshot: DataSnapshot) {
                if (communitySnapshot.exists()) {
                    val communityModel: CommunityModel = communitySnapshot.getValue<CommunityModel>(CommunityModel::class.java)!!
                    //
                    // 退会した場合とコミュニティがなくなった場合除外する処理を加える
                    if (currentUser?.communities != null) {
                        var isMyCommunity = false
                        for (myCommunity in currentUser?.communities!!) {
                            isMyCommunity = (myCommunity.key == communityModel.communityId)
                            if (isMyCommunity) break
                        }
                        if (isMyCommunity) {
                            if (!communityModel.communityDeleted!!) {
                                communityList.add(communityModel)
                            }
                        }
                    }
                    now += 1
                    if (now == communityCount) {
                        callback?.handleData(true, requestType)
                    }
                }
            }
        }

        communityListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(communitySnapshot: DataSnapshot) {
                if (communitySnapshot.exists()) {
                    val communityModel: CommunityModel = communitySnapshot.getValue<CommunityModel>(CommunityModel::class.java)!!
                    val memberList: ArrayList<UserModel> = arrayListOf()
                    //
                    //
                    // 退会した場合とコミュニティがなくなった場合除外する処理を加える
                    if (currentUser?.communities != null) {
                        var isMyCommunity = false
                        for (myCommunity in currentUser?.communities!!) {
                            isMyCommunity = (myCommunity.key == communityModel.communityId)
                            if (isMyCommunity) break
                        }
                        if (isMyCommunity) {
                            if (!communityModel.communityDeleted!!) {
                                for (member in communityModel.members) {
                                    memberList.add(member.value)
                                }
                                communityMembersMap?.put(communityModel.communityId!!, memberList)
                                communityMessageMap?.put(communityModel.communityId!!, arrayListOf())
                                communityMap?.put(communityModel.communityId!!, communityModel)

                                myCommunities.clear()
                                if (communityMap != null) {
                                    myCommunities = communityMap?.values?.toMutableList()!!
                                }
                                // 自分がadminの場合 communityのjoin requestを保存
                                if (communityModel.members[currentUser?.uid]?.admin != null) {
                                    fetchCommunityRequests(communityModel)
                                }

                                // lastMessageが変わったらchatRoomを作る
                                if (communityModel.lastMessage?.timestamp != null) {
                                    if (communityModel.lastMessage?.timestamp!! >= communityModel.members[currentUser?.uid]?.joinTime!!) {
                                        val chatRoomModel = ChatRoomModel(communityModel.communityId!!, communityModel.name!!, communityModel.imageUrl!!,
                                                communityModel.lastMessage?.message!!, communityModel.members[currentUser?.uid]?.unreadCount!!, AppConstants().COMMUNITY_CHAT)
                                        userRef?.child(currentUser?.uid)?.child(FirebaseConstants().CHAT_ROOMS)?.child(chatRoomModel.id)?.setValue(chatRoomModel)
                                    }
                                }

                                // fcm
//                        FirebaseMessaging.getInstance().subscribeToTopic(communityModel.id!!)
                            } else {
                                communityRef?.child(communityModel.communityId)?.removeEventListener(communityListener)
                            }
                        }
                    } else {
                        communityRef?.child(communityModel.communityId)?.removeEventListener(communityListener)
                    }
                }
            }
        }

        val myCommunitiesListenerForSingle = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myCommunitiesSnapshot: DataSnapshot) {
                communityList.clear()
                if (myCommunitiesSnapshot.exists()) {
                    communityCount = myCommunitiesSnapshot.children.count()
                    myCommunitiesSnapshot.children.forEach { it ->
                        communityRef?.child(it.key)?.addListenerForSingleValueEvent(listenerForSingle)
                    }
                }
            }
        }

        val myCommunitiesListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myCommunitiesSnapshot: DataSnapshot) {
                communityMembersMap?.clear()
                communityMessageMap?.clear()
                communityMap?.clear()
                myCommunities.clear()
                if (myCommunitiesSnapshot.exists()) {
                    myCommunitiesSnapshot.children.forEach { it ->
                        communityRef?.child(it.key)?.removeEventListener(communityListener)
                        communityRef?.child(it.key)?.addValueEventListener(communityListener)
                    }
                }
                callback?.handleData(true, requestType)
            }
        }

        if (isSingleEvent) {
            userRef?.child(userModel?.uid)?.child(FirebaseConstants().COMMUNITY)?.addListenerForSingleValueEvent(myCommunitiesListenerForSingle)
        } else {
            userRef?.child(userModel?.uid)?.child(FirebaseConstants().COMMUNITY)?.addValueEventListener(myCommunitiesListener)
        }
    }

    fun fetchMyFriends(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?, isSingleEvent: Boolean) {

        var friendCount = 0
        var now = 0

        val listenerForSingle = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val friendModel: FriendModel = dataSnapshot.getValue<FriendModel>(FriendModel::class.java)!!

                    if (currentUser?.friends != null) {
                        var isMyFriend = false
                        for (myFriend in currentUser?.friends!!) {
                            isMyFriend = (myFriend.key == friendModel.friendId)
                            if (isMyFriend) break
                        }
                        if (isMyFriend) {
                            if (!friendModel.friendDeleted!!) {
                                friendList.add(friendModel)
                            }
                        }
                    }
                    now += 1
                    if (now == friendCount) {
                        callback?.handleData(true, requestType)
                    }
                }
            }
        }

        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val friendModel: FriendModel = dataSnapshot.getValue<FriendModel>(FriendModel::class.java)!!

                    if (currentUser?.friends != null) {
                        var isMyFriend = false
                        for (myFriend in currentUser?.friends!!) {
                            isMyFriend = (myFriend.key == friendModel.friendId)
                            if (isMyFriend) break
                        }
                        if (isMyFriend) {
                            if (!friendModel.friendDeleted!!) {
                                var friendId: String
                                for (f in friendModel.members) {
                                    if (f.key != currentUser?.uid) {
                                        friendId = f.key

                                        userRef?.child(friendId)?.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError?) {}

                                            override fun onDataChange(p0: DataSnapshot?) {
                                                val friend: UserModel? = p0?.getValue<UserModel>(UserModel::class.java)
                                                myFriendsMap.put(friend?.uid!!, friend)
                                                myFriends.clear()
                                                myFriends = myFriendsMap.values.toMutableList()

                                                // lastMessageが変わったらchatRoomを作る
                                                if (friendModel.lastMessage?.timestamp != null) {
                                                    if (friendModel.lastMessage?.timestamp!! >= friendModel.members[currentUser?.uid]?.joinTime!!) {
                                                        val chatRoomModel = ChatRoomModel(friendModel.friendId!!, friend.name!!, friend.imageUrl!!,
                                                                friendModel.lastMessage?.message!!, friendModel.members[currentUser?.uid]?.unreadCount!!, AppConstants().FRIEND_CHAT)
                                                        userRef?.child(currentUser?.uid)?.child(FirebaseConstants().CHAT_ROOMS)?.child(chatRoomModel.id)?.setValue(chatRoomModel)
                                                    }
                                                }
                                            }
                                        })
                                    }
                                }

                            } else {
                                friendRef?.child(friendModel.friendId)?.removeEventListener(this)
                            }
                        }
                    } else {
                        friendRef?.child(friendModel.friendId)?.removeEventListener(this)
                    }
                }
            }
        }

        val myFriendsListenerForSingle = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myFriendsSnapshot: DataSnapshot) {
                friendList.clear()
                if (myFriendsSnapshot.exists()) {
                    friendCount = myFriendsSnapshot.children.count()
                    myFriendsSnapshot.children.forEach { it ->
                        friendRef?.child(it.key)?.addListenerForSingleValueEvent(listenerForSingle)
                    }
                }
            }
        }

        val myFriendsListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myFriendsSnapshot: DataSnapshot) {
                myFriendsMap.clear()
                myFriends.clear()
                if (myFriendsSnapshot.exists()) {
                    myFriendsSnapshot.children.forEach { it ->
                        friendRef?.child(it.key)?.removeEventListener(listener)
                        friendRef?.child(it.key)?.addValueEventListener(listener)
                    }
                }
                callback?.handleData(true, requestType)
            }
        }

        if (isSingleEvent) {
            userRef?.child(userModel?.uid)?.child(FirebaseConstants().FRIENDS)?.addListenerForSingleValueEvent(myFriendsListenerForSingle)
        } else {
            userRef?.child(userModel?.uid)?.child(FirebaseConstants().FRIENDS)?.addValueEventListener(myFriendsListener)
        }
    }

    fun fetchFriendRequests(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {

        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user: UserModel? = dataSnapshot.getValue<UserModel>(UserModel::class.java)
                    if (user != null) {
                        if (currentUser?.friendRequests != null) {
                            var isFriendRequests = false
                            for (request in currentUser?.friendRequests!!) {
                                isFriendRequests = (request.key == user.uid)
                                if (isFriendRequests) break
                            }

                            if (isFriendRequests) {
                                friendRequestsMap[user.uid!!] = user

                            } else {
                                friendRequestsMap.remove(user.uid)
                                // friendRequestsにない場合 リスナーを外す
                                userRef?.child(user.uid)?.removeEventListener(this)
                            }
                        } else {
                            friendRequestsMap.remove(user.uid)
                            // friendRequestsにない場合 リスナーを外す
                            userRef?.child(user.uid)?.removeEventListener(this)
                        }
                        friendRequests.clear()
                        friendRequests = friendRequestsMap.values.toMutableList()
                    }
                }
            }
        }

        val friendRequestsListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(friendRequestsSnapshot: DataSnapshot) {
                friendRequestsMap.clear()
                friendRequests.clear()
                if (friendRequestsSnapshot.exists()) {
                    friendRequestsSnapshot.children.forEach { it ->
                        userRef?.child(it.key)?.removeEventListener(listener)
                        userRef?.child(it.key)?.addValueEventListener(listener)
                    }
                }
//                callback?.handleData(true, requestType)
            }
        }

        userRef?.child(userModel?.uid)?.child(FirebaseConstants().FRIEND_REQUESTS)?.addValueEventListener(friendRequestsListener)

    }

    fun fetchMyFriendRequests(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {

        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user: UserModel? = dataSnapshot.getValue<UserModel>(UserModel::class.java)
                    if (user != null) {
                        if (currentUser?.myFriendRequests != null) {
                            var isMyFriendRequests = false
                            for (request in currentUser?.myFriendRequests!!) {
                                isMyFriendRequests = (request.key == user.uid)
                                if (isMyFriendRequests) break
                            }
                            if (isMyFriendRequests) {
                                myFriendRequestsMap[user.uid!!] = user
                                myFriendRequests.clear()
                                myFriendRequests = myFriendRequestsMap.values.toMutableList()
                            } else {
                                userRef?.child(user.uid)?.removeEventListener(this)
                            }
                        }
                    }
                }
            }
        }

        val myFriendRequestsListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myFriendRequestsSnapshot: DataSnapshot) {
                myFriendRequestsMap.clear()
                myFriendRequests.clear()
                if (myFriendRequestsSnapshot.exists()) {
                    myFriendRequestsSnapshot.children.forEach { it ->
                        userRef?.child(it.key)?.removeEventListener(listener)
                        userRef?.child(it.key)?.addValueEventListener(listener)
                    }
                }
//                callback?.handleData(true, requestType)
            }
        }

        userRef?.child(userModel?.uid)?.child(FirebaseConstants().MY_FRIEND_REQUESTS)?.addValueEventListener(myFriendRequestsListener)

    }

    fun fetchMyCommunityRequests(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {

        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val community: CommunityModel? = dataSnapshot.getValue<CommunityModel>(CommunityModel::class.java)
                    if (community != null) {
                        var isMyCommunityRequests = false
                        for (request in currentUser?.myCommunityRequests!!) {
                            isMyCommunityRequests = (request.key == community.communityId)
                            if (isMyCommunityRequests) break
                        }
                        if (isMyCommunityRequests) {
                            myCommunityRequestsMap[community.communityId!!] = community
                            myCommunityRequests.clear()
                            myCommunityRequests = myCommunityRequestsMap.values.toMutableList()
                        } else {
                            communityRef?.child(community.communityId)?.removeEventListener(this)
                        }
                    }
                }
            }
        }

        val myCommunityRequestsListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(myCommunityRequestsSnapshot: DataSnapshot) {
                myCommunityRequestsMap.clear()
                myCommunityRequests.clear()
                if (myCommunityRequestsSnapshot.exists()) {
                    myCommunityRequestsSnapshot.children.forEach { it ->
                        communityRef?.child(it.key)?.removeEventListener(listener)
                        communityRef?.child(it.key)?.addValueEventListener(listener)
                    }
                }
//                callback?.handleData(true, requestType)
            }
        }

        userRef?.child(userModel?.uid)?.child(FirebaseConstants().MY_COMMUNITY_REQUESTS)?.addValueEventListener(myCommunityRequestsListener)

    }

    fun fetchCommunityRequests(communityModel: CommunityModel?) {

        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user: UserModel? = dataSnapshot.getValue<UserModel>(UserModel::class.java)
                    if (user != null) {
                        var isCommunityRequests = false
                        for (community in myCommunities) {
                            for (request in community.joinRequests) {
                                isCommunityRequests = (request.key == user.uid)
                                if (isCommunityRequests) break
                            }
                        }
                        if (isCommunityRequests) {
                            if (communityRequestsMap[communityModel?.communityId] == null) {
                                communityRequestsMap[communityModel?.communityId!!] = mutableListOf(user)
                            } else {
                                for (requestUser in communityRequestsMap[communityModel?.communityId]!!) {
                                    if (requestUser.uid == user.uid) {
                                        communityRequestsMap[communityModel?.communityId]?.remove(requestUser)
                                    }
                                }
                                communityRequestsMap[communityModel?.communityId]?.add(user)
                            }

                            for (communityRequest in communityRequestsList) {
                                if (communityRequest.first == communityModel?.communityId) {
                                    communityRequestsList.remove(communityRequest)
                                }
                            }
                            for (communityRequest in communityRequestsMap[communityModel?.communityId]!!) {
                                val pair = Pair(communityModel?.communityId!!, communityRequest)
                                communityRequestsList.add(pair)
                            }
                        } else {
                            userRef?.child(user.uid)?.removeEventListener(this)
                        }
                    }
                }
            }
        }

        val communityRequestsListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("", "")
            }

            override fun onDataChange(communityRequestsSnapshot: DataSnapshot) {
                communityRequestsMap.clear()
                communityRequestsList.clear()
                if (communityRequestsSnapshot.exists()) {
                    communityRequestsSnapshot.children.forEach { it ->
                        userRef?.child(it.key)?.removeEventListener(listener)
                        userRef?.child(it.key)?.addValueEventListener(listener)
                    }
                }
            }
        }
        communityRef?.child(communityModel?.communityId)?.child(FirebaseConstants().JOIN_REQUESTS)?.addListenerForSingleValueEvent(communityRequestsListener)
    }

    fun fetchPopularCommunity(callback: NotifyMeInterface?, requestType: Int?) {
        var communityCount = 0

        // communityの数を取得
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    communityCount = dataSnapshot.children.count()
                    queryPopularCommunity(callback, communityCount, requestType)
                }
            }
        }
        communityRef?.addListenerForSingleValueEvent(listener)
    }

    private fun queryPopularCommunity(callback: NotifyMeInterface?, communityCount: Int, requestType: Int?) {
        popularCommunityList.clear()
        var limit = 10
        var now = 0

        if (communityCount < limit) {
            limit = communityCount
        }

        communityRef?.orderByChild(FirebaseConstants().MEMBER_COUNT)?.limitToLast(limit)?.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                val community = dataSnapshot?.getValue<CommunityModel>(CommunityModel::class.java)
                if (community != null) {
                    popularCommunityList.add(community)
                    now += 1
                }
                if (limit == now) {
                    callback?.handleData(true, requestType)
                }
            }
        })
    }

    fun searchCommunityName(callback: NotifyMeInterface?, searchWords: String, requestType: Int?) {
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    foundCommunityListByName.clear()
                    dataSnapshot.children.forEach { it ->
                        it.getValue<CommunityModel>(CommunityModel::class.java)?.let {
                            // 自分が所属しているコミュニティは除外
                            var isMyCommunity = false
                            if (myCommunities.size != 0) {
                                for (community: CommunityModel in myCommunities) {
                                    isMyCommunity = (community.communityId == it.communityId)
                                    if (isMyCommunity) break
                                }
                            }
                            if (searchWords == it.name && !isMyCommunity) {
                                foundCommunityListByName.add(it)
                            }
                        }
                    }
                    callback?.handleData(true, requestType)
                }
            }
        }

        communityRef?.addListenerForSingleValueEvent(listener)
    }

    fun searchCommunityLocation(callback: NotifyMeInterface?, searchWords: String, requestType: Int?) {
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    foundCommunityListByLocation.clear()
                    dataSnapshot.children.forEach { it ->
                        it.getValue<CommunityModel>(CommunityModel::class.java)?.let {
                            // 自分が所属しているコミュニティは除外
                            var isMyCommunity = false
                            if (myCommunities.size != 0) {
                                for (community: CommunityModel in myCommunities) {
                                    isMyCommunity = (community.communityId == it.communityId)
                                    if (isMyCommunity) break
                                }
                            }
                            if (searchWords == it.location && !isMyCommunity) {
                                foundCommunityListByLocation.add(it)
                            }
                        }
                    }
                    callback?.handleData(true, requestType)
                }
            }
        }

        communityRef?.addListenerForSingleValueEvent(listener)
    }

    fun searchUserName(callback: NotifyMeInterface?, searchWords: String, requestType: Int?) {
        // Making a copy of listener
        val listener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    foundUserList.clear()
                    dataSnapshot.children.forEach { it ->
                        it.getValue<UserModel>(UserModel::class.java)?.let {
                            // フレンド除外
                            if (currentUser?.friends != null) {
                                var isMyFriends = false
                                for (friend in myFriends) {
                                    isMyFriends = (friend.uid == it.uid)
                                    if (isMyFriends) break
                                }
                                if (!isMyFriends) {
                                    // 自分は除外
                                    if (searchWords == it.name && it.uid != currentUser?.uid) {
                                        foundUserList.add(it)
                                    }
                                }
                            } else {
                                // 自分は除外
                                if (searchWords == it.name && it.uid != currentUser?.uid) {
                                    foundUserList.add(it)
                                }
                            }
                        }
                    }
                    callback?.handleData(true, requestType)
                }
            }
        }

        userRef?.addListenerForSingleValueEvent(listener)
    }

    fun sendCommunityJoinRequest(callback: NotifyMeInterface?, user: UserModel, community: CommunityModel, requestType: Int?) {

        communityRef?.child(community.communityId)?.child(FirebaseConstants().JOIN_REQUESTS)?.child(user.uid)?.setValue(true)
        userRef?.child(user.uid)?.child(FirebaseConstants().MY_COMMUNITY_REQUESTS)?.child(community.communityId)?.setValue(true)

        callback?.handleData(true, requestType)
    }

    fun sendFriendRequest(callback: NotifyMeInterface?, currentUser: UserModel, user: UserModel, requestType: Int?) {

        userRef?.child(user.uid)?.child(FirebaseConstants().FRIEND_REQUESTS)?.child(currentUser.uid)?.setValue(true)
        userRef?.child(currentUser.uid)?.child(FirebaseConstants().MY_FRIEND_REQUESTS)?.child(user.uid)?.setValue(true)

        callback?.handleData(true, requestType)
    }

    fun confirmCommunityJoinRequest(callback: NotifyMeInterface?, uid: String, communityId: String, requestType: Int?) {
        communityRef?.child(communityId)?.child(FirebaseConstants().JOIN_REQUESTS)?.child(uid)?.removeValue()
        userRef?.child(uid)?.child(FirebaseConstants().MY_COMMUNITY_REQUESTS)?.child(communityId)?.removeValue()

        if (communityRequestsMap.isNotEmpty()) {
            for (user in communityRequestsMap[communityId]!!) {
                if (user.uid == uid) {
                    addMemberToACommunity(callback, communityId, user)
                }
            }
        }
    }

    fun disconfirmCommunityJoinRequest(callback: NotifyMeInterface?, uid: String, communityId: String, requestType: Int?) {
        communityRef?.child(communityId)?.child(FirebaseConstants().JOIN_REQUESTS)?.child(uid)?.removeValue()
        userRef?.child(uid)?.child(FirebaseConstants().MY_COMMUNITY_REQUESTS)?.child(communityId)?.removeValue()

        callback?.handleData(true, requestType)
    }

    fun confirmFriendRequest(callback: NotifyMeInterface?, myId: String, userId: String, friendModel: FriendModel, requestType: Int?) {
        userRef?.child(userId)?.child(FirebaseConstants().MY_FRIEND_REQUESTS)?.child(myId)?.removeValue()
        userRef?.child(myId)?.child(FirebaseConstants().FRIEND_REQUESTS)?.child(userId)?.removeValue()

        createFriend(callback, friendModel, requestType)
    }

    fun disconfirmFriendRequest(callback: NotifyMeInterface?, myId: String, userId: String, requestType: Int?) {

        userRef?.child(userId)?.child(FirebaseConstants().MY_FRIEND_REQUESTS)?.child(myId)?.removeValue()
        userRef?.child(myId)?.child(FirebaseConstants().FRIEND_REQUESTS)?.child(userId)?.removeValue()

        callback?.handleData(true, requestType)
    }

    fun hasChatRoom(callback: NotifyMeInterface?, uid: String, chatRoomModel: ChatRoomModel, requestType: Int?) {
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback?.handleData(true, requestType)
                } else {
                    callback?.handleData(false, requestType)
                }
            }
        }

        userRef?.child(uid)?.child(FirebaseConstants().CHAT_ROOMS)?.child(chatRoomModel.id)?.addListenerForSingleValueEvent(listener)
    }

    fun createChatRoom(callback: NotifyMeInterface?, uid: String, chatRoomModel: ChatRoomModel, requestType: Int?) {
        userRef?.child(uid)?.child(FirebaseConstants().CHAT_ROOMS)?.child(chatRoomModel.id)?.setValue(chatRoomModel)
        callback?.handleData(true, requestType)
    }

    fun updateUserInfo(callback: NotifyMeInterface?, userModel: UserModel?, requestType: Int?) {
        val updateMap: HashMap<String, Any> = hashMapOf()
        updateMap.put(FirebaseConstants().NAME, userModel?.name!!)
        updateMap.put(FirebaseConstants().IMAGE_URL, userModel.imageUrl!!)
        if (userModel.programmingLanguage != null) {
            updateMap.put(FirebaseConstants().PROGRAMMING_LANGUAGE, userModel.programmingLanguage!!)
        }
        if (userModel.whatMade != null) {
            updateMap.put(FirebaseConstants().WHAT_MADE, userModel.whatMade!!)
        }
        userRef?.child(userModel.uid)?.updateChildren(updateMap)

        userRef?.child(userModel.uid)?.child(FirebaseConstants().FRIENDS)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach{ it ->
                        friendRef?.child(it.key)?.child(FirebaseConstants().MEMBERS)?.child(userModel.uid)?.updateChildren(updateMap)

                    }
                } else {
                    callback?.handleData(false, requestType)
                }
            }
        })
        callback?.handleData(true, requestType)
    }

    fun updateCommunityInfo(callback: NotifyMeInterface?, communityModel: CommunityModel?, requestType: Int?) {
        val updateMap: HashMap<String, Any> = hashMapOf()
        updateMap.put(FirebaseConstants().NAME, communityModel?.name!!)
        updateMap.put(FirebaseConstants().IMAGE_URL, communityModel.imageUrl!!)
        updateMap.put(FirebaseConstants().DESCRIPTION, communityModel.description!!)
        updateMap.put(FirebaseConstants().LOCATION, communityModel.location!!)
        communityRef?.child(communityModel.communityId)?.updateChildren(updateMap)
        callback?.handleData(true, requestType)
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
                        var newUserData: HashMap<String, Any?> = hashMapOf()
                        newUserData.put("imageUrl", userModel?.imageUrl)
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
     * This function creates a communities in the firebase and adds an entry of communities id under users and set it to
     * true.
     */
    fun createCommunity(callback: NotifyMeInterface?, community: CommunityModel, requestType: Int?) {

        val communityId = communityRef?.push()?.key
        community.communityId = communityId
        val time = Calendar.getInstance().timeInMillis

        for (user in community.members) {
            user.value.communities = hashMapOf()
            user.value.email = null
            user.value.imageUrl = null
            user.value.name = null
            user.value.online = null
            user.value.friends.clear()
            user.value.myFriendRequests.clear()
            user.value.friendRequests.clear()
            user.value.unreadCount = 0
            user.value.joinTime = time.toString()
            user.value.lastSeenMessageTimestamp = time.toString()
            user.value.deleteTill = time.toString()
        }

        community.memberCount = community.members.size

        communityRef?.child(communityId)?.setValue(community)

        for (user in community.members) {
            userRef?.child(user.value.uid)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.setValue(true)
        }

        callback?.handleData(true, requestType)
    }

    private fun createFriend(callback: NotifyMeInterface?, friendModel: FriendModel, requestType: Int?) {

        val friendId = friendRef?.push()?.key
        friendModel.friendId = friendId
        val time = Calendar.getInstance().timeInMillis

        for (user in friendModel.members) {
            user.value.email = null
            user.value.online = null
            user.value.friends.clear()
            user.value.communities.clear()
            user.value.myFriendRequests.clear()
            user.value.friendRequests.clear()
            user.value.unreadCount = 0
            user.value.joinTime = time.toString()
            user.value.lastSeenMessageTimestamp = time.toString()
            user.value.age = null
            user.value.deleteTill = time.toString()
        }

        friendRef?.child(friendId)?.setValue(friendModel)

        for (user in friendModel.members) {
            userRef?.child(user.value.uid)?.child(FirebaseConstants().FRIENDS)?.child(friendId)?.setValue(true)
        }

        callback?.handleData(true, requestType)
    }


    /**
     * This function sends messages to a communities
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
                                if (mutabledata?.getValue<UserModel>(UserModel::class.java)?.unreadCount == null) {
                                    var p = mutabledata?.getValue<UserModel>(UserModel::class.java)
                                    p?.unreadCount = 0
                                    mutabledata?.setValue(p)
                                } else {
                                    var p = mutabledata.getValue<UserModel>(UserModel::class.java)
                                    p?.unreadCount = p?.unreadCount as Int + 1
                                    mutabledata.setValue(p)
                                }

                                return Transaction.success(mutabledata)
                            }
                        })
            }
        }

    }

    fun sendMessageToAFriend(callback: NotifyMeInterface?, requestType: Int?, friendId: String?,
                                messageModel: MessageModel?) {

        val messageKey = messageRef?.child(friendId)?.push()?.key
        messageModel?.message_id = messageKey

        messageRef?.child(friendId)?.child(messageKey)?.setValue(messageModel)

        friendRef?.child(friendId)?.child(FirebaseConstants().LAST_MESSAGE)?.setValue(messageModel)

        callback?.handleData(true, requestType)

        for (member in myFriendsMap) {
            if (member.value.uid != currentUser?.uid) {
                friendRef?.child(friendId)?.child(FirebaseConstants().MEMBERS)?.child(member.value.uid)?.
                        runTransaction(object : Transaction.Handler {
                            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                                if (p0 != null) {
                                    Log.d("INC", "Firebase counter increment failed.")
                                } else {
                                    Log.d("INC", "Firebase counter increment succeeded.")
                                }
                            }

                            override fun doTransaction(mutabledata: MutableData?): Transaction.Result {
                                if (mutabledata?.getValue<UserModel>(UserModel::class.java)?.unreadCount == null) {
                                    val p = mutabledata?.getValue<UserModel>(UserModel::class.java)
                                    p?.unreadCount = 0
                                    mutabledata?.setValue(p)
                                } else {
                                    val p = mutabledata.getValue<UserModel>(UserModel::class.java)
                                    p?.unreadCount = p?.unreadCount as Int + 1
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
        userMap?.clear()
        if (i == 0) {
            callback?.handleData(false, requestType)
        }
        for (member in communityMembersMap?.get(communityId)!!) {
            userRef?.child(member.uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    i--
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel: UserModel = snapshot.getValue<UserModel>(UserModel::class.java)!!
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

    fun fetchFriendMembersDetails(callback: NotifyMeInterface?, requestType: Int?) {
        var i: Int = myFriends.size
        userMap?.clear()
        if (i == 0) {
            callback?.handleData(false, requestType)
        }
        for (member in myFriends) {
            userRef?.child(member.uid)?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    i--
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel: UserModel = snapshot.getValue<UserModel>(UserModel::class.java)!!
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

    fun fetchLastMessage(callback: NotifyMeInterface?, requestType: Int?, id: String?) {

        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot?.exists()!!) {

                    val lastMessage: MessageModel = dataSnapshot.children.elementAt(0).getValue<MessageModel>(MessageModel::class.java)!!


                    callback?.handleData(lastMessage, requestType)
                } else {
                    callback?.handleData(MessageModel(), requestType)
                }
            }

        }
        val lastQuery = messageRef?.child(id)?.orderByKey()?.limitToLast(1)

        lastQuery?.addListenerForSingleValueEvent(listener)
    }

    fun updateCommunityUnReadCountLastSeenMessageTimestamp(communityId: String?, lastMessageModel: MessageModel) {
        var isMyCommunity = false
        if (currentUser?.communities != null) {
            for (community in currentUser?.communities!!) {
                isMyCommunity = (community.key == communityId)
                if (isMyCommunity) break
            }
            if (isMyCommunity) {
                val communityMember: HashMap<String, Any?> = hashMapOf()
                communityMember.put(FirebaseConstants().UNREAD_MESSAGE_COUNT, 0)
                communityMember.put(FirebaseConstants().L_S_M_T, lastMessageModel.timestamp)
                communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(currentUser?.uid)?.updateChildren(communityMember)

                lastMessageModel.read_status = hashMapOf()
                communityRef?.child(communityId)?.child(FirebaseConstants().LAST_MESSAGE)?.setValue(lastMessageModel)
            }
        }
    }

    fun updateFriendUnReadCountLastSeenMessageTimestamp(friendId: String?, lastMessageModel: MessageModel) {
        var isMyFriend = false
        if (currentUser?.friends != null) {
            for (friend in currentUser?.friends!!) {
                isMyFriend = (friend.key == friendId)
                if (isMyFriend) break
            }
            if (isMyFriend) {
                val me: HashMap<String, Any?> = hashMapOf()
                me.put(FirebaseConstants().UNREAD_MESSAGE_COUNT, 0)
                me.put(FirebaseConstants().L_S_M_T, lastMessageModel.timestamp)
                friendRef?.child(friendId)?.child(FirebaseConstants().MEMBERS)?.child(currentUser?.uid)?.updateChildren(me)

                lastMessageModel.read_status = hashMapOf()
                friendRef?.child(friendId)?.child(FirebaseConstants().LAST_MESSAGE)?.setValue(lastMessageModel)
            }
        }
    }

    fun changeAdminStatusOfUser(callback: NotifyMeInterface?, communityId: String?, userId: String?, isAdmin: Boolean) {
        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userId)?.child(FirebaseConstants().ADMIN)?.setValue(isAdmin)
        callback?.handleData(true, NetworkConstants().CHANGE_ADMIN_STATUS)
    }

    fun removeMemberFromCommunity(callback: NotifyMeInterface?, communityId: String?, userId: String?) {
        userRef?.child(userId)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.removeValue()
        userRef?.child(userId)?.child(FirebaseConstants().CHAT_ROOMS)?.child(communityId)?.removeValue()
        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userId)?.removeValue()

        communityRef?.child(communityId)?.
                runTransaction(object : Transaction.Handler {
                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (p0 != null) {
                            Log.d("INC", "Firebase member counter increment failed.")
                        } else {
                            Log.d("INC", "Firebase member counter increment succeeded.")
                            callback?.handleData(true, 1)
                        }
                    }

                    override fun doTransaction(mutabledata: MutableData?): Transaction.Result {
                        if (mutabledata?.getValue<CommunityModel>(CommunityModel::class.java)?.memberCount != null) {
                            val community = mutabledata.getValue<CommunityModel>(CommunityModel::class.java)
                            community?.memberCount = community?.memberCount as Int - 1
                            mutabledata.value = community
                        }
                        return Transaction.success(mutabledata)
                    }
                })
    }

    private fun addMemberToACommunity(callback: NotifyMeInterface?, communityId: String?, userModel: UserModel?) {
        //userRef?.child(userModel?.uid)?.child(FirebaseConstants.COMMUNITY)?.child(id)?.setValue(true)

        val time = Calendar.getInstance().timeInMillis

        userModel?.communities = hashMapOf()
        userModel?.myFriendRequests?.clear()
        userModel?.friendRequests?.clear()
        userModel?.myCommunityRequests?.clear()
        userModel?.email = null
        userModel?.imageUrl = null
        userModel?.name = null
        userModel?.online = null
        userModel?.friends?.clear()
        userModel?.unreadCount = 0
        userModel?.joinTime = time.toString()
        userModel?.lastSeenMessageTimestamp = time.toString()
        userModel?.deleteTill = time.toString()

        communityRef?.child(communityId)?.child(FirebaseConstants().MEMBERS)?.child(userModel?.uid)?.setValue(userModel)
        userRef?.child(userModel?.uid)?.child(FirebaseConstants().COMMUNITY)?.child(communityId)?.setValue(true)

        communityRef?.child(communityId)?.
                runTransaction(object : Transaction.Handler {
                    override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (p0 != null) {
                            Log.d("INC", "Firebase member counter increment failed.")
                        } else {
                            Log.d("INC", "Firebase member counter increment succeeded.")
                            callback?.handleData(true, 1)
                        }
                    }

                    override fun doTransaction(mutabledata: MutableData?): Transaction.Result {
                        if (mutabledata?.getValue<CommunityModel>(CommunityModel::class.java)?.memberCount != null) {
                            val community = mutabledata.getValue<CommunityModel>(CommunityModel::class.java)
                            community?.memberCount = community?.memberCount as Int + 1
                            mutabledata.value = community
                        }
                        return Transaction.success(mutabledata)
                    }
                })
    }

    /**
     * Check if a communities exists or not
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
        val lastSeenRef = FirebaseDatabase.getInstance().reference.child("/users/" + currentUser?.uid + "/lastSeenOnline")

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