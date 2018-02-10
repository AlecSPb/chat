package com.go26.chatapp.viewmodel

import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.go26.chatapp.contract.SearchRootFragmentContract

/**
 * Created by daigo on 2018/01/12.
 */
class SearchRootFragmentViewModel(val view: SearchRootFragmentContract) {
//    val searchWord: ObservableField<String> = ObservableField()
    val searchHint: ObservableField<String> = ObservableField()

    fun setSearchHint(pos: Int) {
        when (pos) {
            0 -> {
                searchHint.set("活動場所で検索")
            }
            1 -> {
                searchHint.set("コミュニティ名で検索")
            }
            2 -> {
                searchHint.set("ユーザーを検索")
            }
        }
    }

    fun onSearchButtonClicked(view: View) {
//        if (searchWord.get() != null) {
//            Log.d("search", searchHint.get())
//            Log.d("word", searchWord.get())
//
//            when (searchHint.get()) {
//                "コミュニティ" -> {
//                    this.view.reloadAdapter(0)
//                }
//                "場所" -> {
//                    this.view.reloadAdapter(1)
//                }
//            }
//        }
    }
}