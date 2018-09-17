package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
abstract class EastFooterViewType<T> : EasyHeaderAndFooterViewType<T>() {
    override fun notifyView() {
        adapter.notifyItemChanged(adapter.itemCount-1-if (adapter.isLoadingMore()) 1 else 0-(adapter.getFooterCount())+adapter.getFooterPosition(this))
    }
}