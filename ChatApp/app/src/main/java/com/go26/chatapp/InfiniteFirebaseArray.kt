package com.go26.chatapp

import com.go26.chatapp.model.MessageModel
import android.text.TextUtils
import com.google.firebase.database.*


/**
 * Created by daigo on 2018/01/18.
 */
class InfiniteFirebaseArray(ref: Query, private val numberPerPage: Int, private val startAt: String) : ChildEventListener, ValueEventListener {
    val ADDED = 0
    val CHANGED = 1
    val REMOVED = 2
    val NOTIFY_ALL = 3

    private var tempList: MutableList<DataSnapshot> = mutableListOf()

    private var query: Query? = null
    var snapShots: MutableList<DataSnapshot> = mutableListOf()
    private var nextChildKey: String? = null
    private var endKey: String? = null
    private var count: Int = 0
    private var isDuplicateKey: Boolean = false
    private var listener: OnChangedListener? = null

//    @IntDef(ADDED.toLong(), CHANGED.toLong(), REMOVED.toLong(), NOTIFY_ALL.toLong())
//    @Retention(RetentionPolicy.SOURCE)
    annotation class EventType

    interface OnChangedListener {

        fun onChanged(@EventType type: Int, index: Int, oldIndex: Int)

        fun onCancelled(databaseError: DatabaseError)
    }

    init {
        initQuery(ref)
    }

    private fun initQuery(ref: Query) {
        query = ref.orderByChild("timestamp").startAt(startAt).limitToLast(numberPerPage)
        query!!.addChildEventListener(this)
        count = 0
        tempList.clear()
    }

    private fun initNextQuery(ref: Query) {
        ref.orderByChild("timestamp").limitToLast(5)
                .startAt(startAt).endAt(endKey).addListenerForSingleValueEvent(this)
        count = 0
        tempList.clear()
    }

    fun cleanup() {
//        Logger.enter()
        //query.removeEventListener(this);
//        Logger.exit()
    }

    fun more(ref: Query) {
        if (isHasMore()) {
            initNextQuery(ref)
        }
    }

    fun getCount(): Int = snapShots.size

    fun getItem(index: Int): DataSnapshot {
        return snapShots[index]
    }


    override fun onChildAdded(snapshot: DataSnapshot?, previousChildKey: String?) {
        if (snapshot == null) {
            return
        }
        count++
        if (count == 1) {
            endKey = snapshot.getValue(MessageModel::class.java)?.timestamp
        }
        nextChildKey = snapshot.key
        /*if (count > numberPerPage) {
            return;
        }*/
        if (checkDuplicateKey(nextChildKey)) {
            isDuplicateKey = true
            return
        }
        /*  tempList.clear();
        tempList.add(snapshot);
        //if (count == numberPerPage) {
        snapShots.addAll(0, tempList);
        listener.onChanged(ADDED, 0, -1);*/
        //}
        //snapShots.addAll(0, tempList);
        tempList.clear()
        tempList.add(snapshot)
        val size = snapShots.size
        snapShots.addAll(size, tempList)
        notifyChangedListeners(ADDED, snapShots.size)
        //notifyChangedListeners(ADDED, 0);

//        Logger.d(mIndex.toString() + " : " + nextChildKey)
    }

    private fun checkDuplicateKey(nextChildKey: String?): Boolean {
        if (snapShots.size > 0) {
            val previousSnapshot = snapShots[0]
            val previousChildkey = previousSnapshot.key
            return !TextUtils.isEmpty(previousChildkey) && previousChildkey == nextChildKey
        }
        return false
    }

    private fun isHasMore(): Boolean {
        var isHasMore = true
        if (count < numberPerPage || isDuplicateKey) {
            isHasMore = false
        }
        return isHasMore
    }

    fun setOnChangedListener(listener: OnChangedListener) {
        this.listener = listener
    }

    private fun notifyChangedListeners(@EventType type: Int, index: Int) {
        notifyChangedListeners(type, index, -1)
    }

    private fun notifyChangedListeners(@EventType type: Int, index: Int, oldIndex: Int) {
        if (listener != null) {
            listener!!.onChanged(type, index, oldIndex)
        }
    }

    private fun notifyCancelledListeners(databaseError: DatabaseError) {
        if (listener != null) {
            listener!!.onCancelled(databaseError)
        }
    }

    private fun getIndexForKey(key: String): Int {
        var index = 0
        for (snapshot in snapShots) {
            if (snapshot.key.equals(key, ignoreCase = true)) {
                return index
            } else {
                index++
            }
        }
        return -1
    }

    override fun onChildChanged(snapshot: DataSnapshot, s: String) {
        val index = getIndexForKey(snapshot.key)
        if (index != -1) {
            snapShots[index] = snapshot
            notifyChangedListeners(CHANGED, index)
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        val index = getIndexForKey(snapshot.key)
        if (index != -1) {
            snapShots.removeAt(index)
            notifyChangedListeners(REMOVED, index)
        }
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildKey: String) {

    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
            count = 0

            for (currentSnap in dataSnapshot.children) {
                //MessageModel tempModel=dataSnapshot.getChildren().iterator().next().getValue(MessageModel.class);
                if (count == 0) {
                    endKey = currentSnap.getValue(MessageModel::class.java)?.timestamp
                }
                nextChildKey = currentSnap.key
                if (!checkDuplicateKey(nextChildKey)) {
                    tempList.add(currentSnap)
                }
                count++
            }
            snapShots.addAll(0, tempList)
//            listener!!.onChanged(NOTIFY_ALL, 0, -1)
            listener!!.onChanged(NOTIFY_ALL, tempList.size, -1)

        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        notifyCancelledListeners(databaseError)
    }
}