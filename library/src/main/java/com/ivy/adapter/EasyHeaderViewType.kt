package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
abstract class EasyHeaderViewType<T> : EasyHeaderAndFooterViewType<T>() {
    override fun notifyView() {
        adapter.notifyItemChanged(adapter.getHeaderPosition(this))
    }
}