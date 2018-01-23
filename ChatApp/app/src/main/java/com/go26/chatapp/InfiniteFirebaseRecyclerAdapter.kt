package com.go26.chatapp

import com.google.firebase.database.DatabaseError
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import android.support.v7.widget.RecyclerView
import android.support.annotation.LayoutRes
import android.util.Log
import android.view.View
import com.go26.chatapp.InfiniteFirebaseArray.Companion.ADDED
import com.go26.chatapp.InfiniteFirebaseArray.Companion.CHANGED
import com.go26.chatapp.InfiniteFirebaseArray.Companion.NOTIFY_ALL
import com.go26.chatapp.InfiniteFirebaseArray.Companion.REMOVED
import com.google.firebase.database.Query
import java.lang.reflect.InvocationTargetException


/**
 * Created by daigo on 2018/01/18.
 */
abstract class InfiniteFirebaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH> {

    protected var mModelLayout: Int = 0
    internal var mModelClass: Class<T>? = null
    internal var mViewHolderClass: Class<VH>? = null
    var mSnapshots: InfiniteFirebaseArray? = null
    private var mQuery: Query? = null
    private var startat: String? = null
    var recyclerView: RecyclerView? = null

    internal constructor(modelClass: Class<T>,
                         @LayoutRes modelLayout: Int,
                         viewHolderClass: Class<VH>,
                         snapshots: InfiniteFirebaseArray) {
        mModelClass = modelClass
        mModelLayout = modelLayout
        mViewHolderClass = viewHolderClass
        mSnapshots = snapshots
        mSnapshots!!.setOnChangedListener(object : InfiniteFirebaseArray.OnChangedListener {

            override fun onChanged(@InfiniteFirebaseArray.EventType type: Int, index: Int, oldIndex: Int) {
//                Logger.d("EventType: " + type)
//                Logger.d("Index: " + index)
                when (type) {
                    ADDED -> {
                        notifyItemInserted(index)
                        recyclerView?.smoothScrollToPosition(index)
                    }
                    CHANGED -> notifyItemChanged(index)
                    REMOVED -> notifyItemRemoved(index)
                    NOTIFY_ALL ->

                        notifyDataSetChanged()
                    else -> throw IllegalStateException("Incomplete case statement")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@InfiniteFirebaseRecyclerAdapter.onCancelled(databaseError)
            }
        })
    }

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     * instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     * combination of `limit()`, `startAt()`, and `endAt()`.
     */
    constructor(modelClass: Class<T>,
                modelLayout: Int,
                viewHolderClass: Class<VH>,
                ref: Query, itemsPerPage: Int, startAt: String, rv: RecyclerView) : this(modelClass, modelLayout, viewHolderClass, InfiniteFirebaseArray(ref, itemsPerPage, startAt)) {
        mQuery = ref
        startat = startAt
        recyclerView = rv
    }

    fun cleanup() {
        mSnapshots!!.cleanup()
    }

    fun more() {
        if (mSnapshots != null) {
            mQuery?.let { mSnapshots!!.more(it) }
        }
    }

    override fun getItemCount(): Int {
        return mSnapshots!!.count
    }

    fun getItem(position: Int): T? {
        return parseSnapshot(mSnapshots!!.getItem(position))
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected fun parseSnapshot(snapshot: DataSnapshot): T? {
        return snapshot.getValue(mModelClass)
    }

    fun getRef(position: Int): DatabaseReference {
        return mSnapshots!!.getItem(position).getRef()
    }

    override fun getItemId(position: Int): Long {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots!!.getItem(position).key.hashCode().toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        try {
            val constructor = mViewHolderClass?.getConstructor(View::class.java)
            return constructor!!.newInstance(view)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val model = getItem(position)
        populateViewHolder(viewHolder, model, position)
    }

    override fun getItemViewType(position: Int): Int {
        return mModelLayout
    }

    /**
     * This method will be triggered in the event that this listener either failed at the server,
     * or is removed as a result of the security and Firebase Database rules.
     *
     * @param error A description of the error that occurred
     */
    protected fun onCancelled(error: DatabaseError) {
        Log.w(TAG, error.toException())
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     *
     *
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    protected abstract fun populateViewHolder(viewHolder: VH, model: T?, position: Int)

    companion object {
        private val TAG = InfiniteFirebaseRecyclerAdapter::class.java.simpleName
    }
}