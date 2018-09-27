package com.ivy.adapter.paging

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.ivy.adapter.EasyViewHolder

/**
 * Created by jianfeng.huang on 2018/9/25.
 *
 */
class EasyPagingAdapter(val context:Context) : PagedListAdapter<Any,EasyViewHolder>(object : DiffUtil.ItemCallback<Any>(){
    override fun areItemsTheSame(oldItem: Any?, newItem: Any?): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Any?, newItem: Any?): Boolean {
        return false
    } }) {
    override fun onBindViewHolder(holder: EasyViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyViewHolder {
        return EasyViewHolder(View(context))
    }
}