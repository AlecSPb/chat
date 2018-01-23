package com.go26.chatapp

import com.go26.chatapp.model.MessageModel
import android.text.TextUtils
import android.support.annotation.IntDef
import com.google.firebase.database.*
import java.lang.annotation.RetentionPolicy


/**
 * Created by daigo on 2018/01/18.
 */
class InfiniteFirebaseArray(ref: Query, private val mNumberPerPage: Int, startat: String) : ChildEventListener, ValueEventListener {

    internal var tempList: MutableList<DataSnapshot> = ArrayList()

    private var mQuery: Query? = null
    var mSnapshots: MutableList<DataSnapshot> = ArrayList()
    private var mIndex = -1
    private var mNextChildKey: String? = null
    private var mEndKey: String? = null
    private var mCount: Int = 0
    private var isDuplicateKey: Boolean = false
    private var mListener: OnChangedListener? = null

    val count: Int
        get() = mSnapshots.size

    val isHasMore: Boolean
        get() {
            var isHasMore = true
            if (mCount < mNumberPerPage || isDuplicateKey) {
                isHasMore = false
            }
//            Logger.d("isHasMore: " + isHasMore)
            return isHasMore
        }


//    @IntDef(ADDED.toLong(), CHANGED.toLong(), REMOVED.toLong(), NOTIFY_ALL.toLong())
//    @Retention(RetentionPolicy.SOURCE)
    annotation class EventType

    interface OnChangedListener {

        fun onChanged(@EventType type: Int, index: Int, oldIndex: Int)

        fun onCancelled(databaseError: DatabaseError)
    }

    init {
        startAt = startat
        initQuery(ref)
    }

    private fun initQuery(ref: Query) {
//        Logger.d("LastChildKey: " + mNextChildKey!!)
//        Logger.d("NumberPerPage: " + mNumberPerPage)
        mQuery = ref.orderByChild("timestamp").startAt(startAt).limitToLast(mNumberPerPage)
        mQuery!!.addChildEventListener(this)
        mCount = 0
        tempList.clear()
    }

    private fun initNextQuery(ref: Query) {
//        Logger.d("LastChildKey: " + mNextChildKey!!)
//        Logger.d("NumberPerPage: " + mNumberPerPage)
        ref.orderByChild("timestamp").limitToLast(5)
                .startAt(startAt).endAt(mEndKey).addListenerForSingleValueEvent(this)
        mCount = 0
        tempList.clear()
    }

    fun cleanup() {
//        Logger.enter()
        //mQuery.removeEventListener(this);
//        Logger.exit()
    }

    fun more(ref: Query) {
        if (/*isHasMore()*/true) {
            initNextQuery(ref)
        }
    }

    fun getItem(index: Int): DataSnapshot {
        return mSnapshots[index]
    }


    override fun onChildAdded(snapshot: DataSnapshot?, previousChildKey: String) {
        if (snapshot == null) {
            return
        }
        mCount++
        if (mCount == 1) {
            mEndKey = snapshot.getValue(MessageModel::class.java)?.timestamp
        }
        mNextChildKey = snapshot.key
        /*if (mCount > mNumberPerPage) {
            return;
        }*/
        if (checkDuplicateKey(mNextChildKey)) {
            isDuplicateKey = true
            return
        }
        /*  tempList.clear();
        tempList.add(snapshot);
        //if (mCount == mNumberPerPage) {
        mSnapshots.addAll(0, tempList);
        mListener.onChanged(ADDED, 0, -1);*/
        //}
        //mSnapshots.addAll(0, tempList);
        tempList.clear()
        tempList.add(snapshot)
        val size = mSnapshots.size
        mSnapshots.addAll(size, tempList)
        notifyChangedListeners(ADDED, mSnapshots.size)
        //notifyChangedListeners(ADDED, 0);

//        Logger.d(mIndex.toString() + " : " + mNextChildKey)
    }

    private fun checkDuplicateKey(nextChildKey: String?): Boolean {
        if (mSnapshots.size > 0) {
            val previousSnapshot = mSnapshots[0]
            val previousChildkey = if (previousSnapshot == null) "" else previousSnapshot.key
            return !TextUtils.isEmpty(previousChildkey) && previousChildkey == nextChildKey
        }
        return false
    }

    fun setOnChangedListener(listener: OnChangedListener) {
        mListener = listener
    }

    private fun notifyChangedListeners(@EventType type: Int, index: Int) {
        notifyChangedListeners(type, index, -1)
    }

    protected fun notifyChangedListeners(@EventType type: Int, index: Int, oldIndex: Int) {
        if (mListener != null) {
            mListener!!.onChanged(type, index, oldIndex)
        }
    }

    protected fun notifyCancelledListeners(databaseError: DatabaseError) {
        if (mListener != null) {
            mListener!!.onCancelled(databaseError)
        }
    }

    private fun getIndexForKey(key: String): Int {
        var index = 0
        for (snapshot in mSnapshots) {
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
            mSnapshots[index] = snapshot
            notifyChangedListeners(CHANGED, index)
        }
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        val index = getIndexForKey(snapshot.key)
        if (index != -1) {
            mSnapshots.removeAt(index)
            notifyChangedListeners(REMOVED, index)
        }
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildKey: String) {

    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
            mCount = 0

            for (currentSnap in dataSnapshot.children) {
                //MessageModel tempModel=dataSnapshot.getChildren().iterator().next().getValue(MessageModel.class);
                if (mCount == 0) {
                    mEndKey = currentSnap.getValue(MessageModel::class.java)?.timestamp
                }
                mNextChildKey = currentSnap.key
                if (!checkDuplicateKey(mNextChildKey)) {
                    tempList.add(currentSnap)
                    mIndex++
                }
                mCount++
            }
            mSnapshots.addAll(0, tempList)
            mListener!!.onChanged(NOTIFY_ALL, 0, -1)

        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        notifyCancelledListeners(databaseError)
    }

    companion object {

        val ADDED = 0
        val CHANGED = 1
        val REMOVED = 2
        val NOTIFY_ALL = 3
        private var startAt: String? = null
    }
}