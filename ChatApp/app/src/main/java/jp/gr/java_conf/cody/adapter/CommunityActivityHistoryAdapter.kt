package jp.gr.java_conf.cody.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.Query
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.model.CommunityActivityModel
import java.util.*

/**
 * Created by daigo on 2018/03/12.
 */
class CommunityActivityHistoryAdapter(var context: Context, ref: Query) :
        FirebaseRecyclerAdapter<CommunityActivityModel, CommunityActivityHistoryAdapter.ViewHolder>(
                CommunityActivityModel::class.java, R.layout.item_activity_history,
                CommunityActivityHistoryAdapter.ViewHolder::class.java, ref) {

    override fun populateViewHolder(viewHolder: ViewHolder?, model: CommunityActivityModel?, position: Int) {

        viewHolder?.activityContent?.text = model?.activityContents
        viewHolder?.activityLocation?.text = model?.location

        val cal = Calendar.getInstance()
        cal.timeInMillis = model?.date?.toLong()!!
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        viewHolder?.activityDate?.text = String.format("%d / %02d / %02d", year, month+1, day)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var activityContent: TextView = itemView.findViewById(R.id.activity_content_text_view)
        var activityLocation: TextView = itemView.findViewById(R.id.activity_location_text_view)
        var activityDate: TextView = itemView.findViewById(R.id.activity_date_text_view)
    }
}