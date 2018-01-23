package com.go26.chatapp.adapter

import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.support.design.widget.BottomNavigationView

/**
 * Created by daigo on 2018/01/10.
 */
@BindingMethods(
        BindingMethod(
                type = BottomNavigationView::class,
                attribute = "app:onNavigationItemSelected",
                method = "setOnNavigationItemSelectedListener"
        )
)
object DataBindingAdapter {
}