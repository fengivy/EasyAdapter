package com.ivy.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import java.util.LinkedHashSet

/**
 * Created by jianfeng.huang on 2018/9/4.
 *
 */
class EasyViewHolder(view:View) : RecyclerView.ViewHolder(view) {
    private val views = SparseArray<View>()
    public val childClickViewIds = LinkedHashSet<Int>()
    public val childLongClickViewIds = LinkedHashSet<Int>()


    fun setText(viewId: Int, value: CharSequence): EasyViewHolder {
        val view = this.getView<View>(viewId) as TextView
        view.text = value
        return this
    }

    fun setText(viewId: Int, @StringRes strId: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as TextView
        view.setText(strId)
        return this
    }

    fun setImageResource(viewId: Int, @DrawableRes imageResId: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as ImageView
        view.setImageResource(imageResId)
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): EasyViewHolder {
        val view = this.getView<View>(viewId)
        view?.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(viewId: Int, @DrawableRes backgroundRes: Int): EasyViewHolder {
        val view = this.getView<View>(viewId)
        view?.setBackgroundResource(backgroundRes)
        return this
    }

    fun setTextColor(viewId: Int, textColor: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as TextView
        view.setTextColor(textColor)
        return this
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable): EasyViewHolder {
        val view = this.getView<View>(viewId) as ImageView
        view.setImageDrawable(drawable)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap): EasyViewHolder {
        val view = this.getView<View>(viewId) as ImageView
        view.setImageBitmap(bitmap)
        return this
    }

    fun setAlpha(viewId: Int, value: Float): EasyViewHolder {
        this.getView<View>(viewId)?.alpha = value
        return this
    }

    fun setVisible(viewId: Int,visibility:Int): EasyViewHolder {
        val view = this.getView<View>(viewId)
        view?.visibility = visibility
        return this
    }

    fun setProgress(viewId: Int, progress: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as ProgressBar
        view.progress = progress
        return this
    }

    fun setProgress(viewId: Int, progress: Int, max: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as ProgressBar
        view.max = max
        view.progress = progress
        return this
    }

    fun setMax(viewId: Int, max: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as ProgressBar
        view.max = max
        return this
    }

    fun setRating(viewId: Int, rating: Float): EasyViewHolder {
        val view = this.getView<View>(viewId) as RatingBar
        view.rating = rating
        return this
    }

    fun setRating(viewId: Int, rating: Float, max: Int): EasyViewHolder {
        val view = this.getView<View>(viewId) as RatingBar
        view.max = max
        view.rating = rating
        return this
    }

    fun setTag(viewId: Int, tag: Any): EasyViewHolder {
        val view = this.getView<View>(viewId)
        view?.tag = tag
        return this
    }

    fun setTag(viewId: Int, key: Int, tag: Any): EasyViewHolder {
        val view = this.getView<View>(viewId)
        view?.setTag(key, tag)
        return this
    }

    fun addOnClickListener(viewId: Int){
        childClickViewIds.add(viewId)
    }

    fun addOnLongClickListener(viewId: Int){
        childLongClickViewIds.add(viewId)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(viewId: Int): T? {
        var view: T? = this.views.get(viewId) as T?
        if (view == null) {
            view = this.itemView.findViewById(viewId)
            this.views.put(viewId, view)
        }
        return view
    }
}