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
import com.google.firebase.database.Query
import java.lang.reflect.InvocationTargetException


/**
 * Created by daigo on 2018/01/18.
 */
abstract class InfiniteFirebaseRecyclerAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH> {
    val ADDED = 0
    val CHANGED = 1
    val REMOVED = 2
    val NOTIFY_ALL = 3
    private var modelLayout: Int = 0
    private var modelClass: Class<T>? = null
    private var viewHolderClass: Class<VH>? = null
    var snapshots: InfiniteFirebaseArray? = null
    private var query: Query? = null
    private var startAt: String? = null
    var recyclerView: RecyclerView? = null

    constructor(modelClass: Class<T>,
                         @LayoutRes modelLayout: Int,
                         viewHolderClass: Class<VH>,
                         snapshots: InfiniteFirebaseArray) {
        this.modelClass = modelClass
        this.modelLayout = modelLayout
        this.viewHolderClass = viewHolderClass
        this.snapshots = snapshots
        this.snapshots!!.setOnChangedListener(object : InfiniteFirebaseArray.OnChangedListener {

            override fun onChanged(@InfiniteFirebaseArray.EventType type: Int, index: Int, oldIndex: Int) {
                when (type) {
                    ADDED -> {
                        notifyItemInserted(index)
                        recyclerView?.smoothScrollToPosition(index)
                    }
                    CHANGED -> notifyItemChanged(index)
                    REMOVED -> notifyItemRemoved(index)
                    NOTIFY_ALL -> notifyItemRangeInserted(0, index)

                    else -> throw IllegalStateException("Incomplete case statement")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@InfiniteFirebaseRecyclerAdapter.onCancelled(databaseError)
            }
        })
    }

    constructor(modelClass: Class<T>,
                modelLayout: Int,
                viewHolderClass: Class<VH>,
                ref: Query, itemsPerPage: Int, startAt: String, rv: RecyclerView) : this(modelClass, modelLayout, viewHolderClass, InfiniteFirebaseArray(ref, itemsPerPage, startAt)) {
        query = ref
        this.startAt = startAt
        recyclerView = rv
    }

    fun cleanup() {
        snapshots!!.cleanup()
    }

    fun more() {
        if (snapshots != null) {
            query?.let { snapshots!!.more(it) }
        }
    }

    override fun getItemCount(): Int {
        return snapshots!!.getCount()
    }

    fun getItem(position: Int): T? {
        return parseSnapshot(snapshots!!.getItem(position))
    }

    private fun parseSnapshot(snapshot: DataSnapshot): T? {
        return snapshot.getValue(modelClass)
    }

    fun getRef(position: Int): DatabaseReference {
        return snapshots!!.getItem(position).ref
    }

    override fun getItemId(position: Int): Long {
        return snapshots!!.getItem(position).key.hashCode().toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        try {
            val constructor = viewHolderClass?.getConstructor(View::class.java)
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
        return modelLayout
    }

    protected fun onCancelled(error: DatabaseError) {
        Log.w(TAG, error.toException())
    }

    abstract fun populateViewHolder(viewHolder: VH, model: T?, position: Int)

    companion object {
        private val TAG = InfiniteFirebaseRecyclerAdapter::class.java.simpleName
    }
}