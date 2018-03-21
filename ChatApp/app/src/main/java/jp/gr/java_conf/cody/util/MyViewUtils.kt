package jp.gr.java_conf.cody.util

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.request.target.BitmapImageViewTarget
import jp.gr.java_conf.cody.R

/**
 * Created by daigo on 2018/01/14.
 */
class MyViewUtils(val mContext: Context) {

    companion object {

        @BindingAdapter("loadImageFromUrl")
        fun loadImageFromUrl(view: AppCompatImageView, url: String?) {
            if (url != null) {
                Glide.with(view.context)
                        .load(url)
                        .into(view)
            } else {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.inactive_gray))
            }
        }

        @BindingAdapter("loadImageFromUrl")
        fun loadImageBitmapFromUrl(view: AppCompatImageView, url: String) {
            view.buildDrawingCache()
            Glide.with(view.context)
                    .load(url)
                    .asBitmap()
                    .into(view)
        }

        @BindingAdapter("LoadRoundImage")
        fun loadRoundImage(view: AppCompatImageView, imageUrl: String?) {

            if (imageUrl != null) {
                Glide.with(view.context)
                        .load(imageUrl)
                        .asBitmap()
                        .centerCrop()
                        .into(object : BitmapImageViewTarget(view) {
                            override fun setResource(resource: Bitmap) {
                                val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(view.context.resources, resource)
                                circularBitmapDrawable.isCircular = true
                                view.setImageDrawable(circularBitmapDrawable)
                                resource.recycle()
                                circularBitmapDrawable.bitmap.recycle()
                            }
                        })
            }
        }

    }
}