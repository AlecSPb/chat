package jp.gr.java_conf.cody.ui.search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import jp.gr.java_conf.cody.R
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityActivityFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityFeatureFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.communityMemberCountFilter
import jp.gr.java_conf.cody.constants.DataConstants.Companion.filterCount
import kotlinx.android.synthetic.main.activity_search_filter.*

class SearchFilterActivity : AppCompatActivity() {

    private var featureFilter: Int = 0
    private var memberCountFilter: Boolean = false
    private var activityFilter: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_filter)

        setViews()
    }

    private fun setViews() {
        //actionbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        this.setSupportActionBar(toolbar)
        this.supportActionBar?.setDisplayShowTitleEnabled(false)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cancel)

        // feature
        when (communityFeatureFilter) {
            0 -> {
                feature_toggle_button.check(R.id.feature_default)
            }
            1 -> {
                feature_toggle_button.check(R.id.feature1)
            }
            2 -> {
                feature_toggle_button.check(R.id.feature2)
            }
            3 -> {
                feature_toggle_button.check(R.id.feature3)
            }
            4 -> {
                feature_toggle_button.check(R.id.feature4)
            }
            else -> {
                feature_toggle_button.check(R.id.feature_default)
            }
        }

        feature_toggle_button.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.feature_default -> {
                    featureFilter = 0
                }
                R.id.feature1 -> {
                    featureFilter = 1
                }
                R.id.feature2 -> {
                    featureFilter = 2
                }
                R.id.feature3 -> {
                    featureFilter = 3
                }
                R.id.feature4 -> {
                    featureFilter = 4
                }
            }
        }

        // memberCount
        member_count_switch.isOn = communityMemberCountFilter
        member_count_switch.setOnToggledListener { _, isOn ->
            memberCountFilter = isOn
        }

        // activity
        activity_switch.isOn = communityActivityFilter
        activity_switch.setOnToggledListener { _, isOn ->
            activityFilter = isOn
        }

        // filter button
        filter_button.setOnClickListener {
            filterCount = 0
            communityFeatureFilter = featureFilter
            communityMemberCountFilter = memberCountFilter
            communityActivityFilter = activityFilter

            if (featureFilter != 0) {
                filterCount = filterCount + 1
            }
            if (memberCountFilter) {
                filterCount = filterCount + 1
            }
            if (activityFilter) {
                filterCount = filterCount + 1
            }
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
