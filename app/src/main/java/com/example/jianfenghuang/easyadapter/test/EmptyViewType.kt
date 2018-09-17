package com.example.jianfenghuang.easyadapter.test

import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyEmptyViewType
import com.ivy.adapter.EasyViewHolder

/**
 * Created by jianfeng.huang on 2018/9/10.
 *
 */
class EmptyViewType : EasyEmptyViewType<String>() {
    override fun convert(bean: String, holder: EasyViewHolder) {

    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_empty
    }

    override fun onViewAttachedToWindow(holder: EasyViewHolder) {
        super.onViewAttachedToWindow(holder)
        println("---------------attach emptyView")
    }

    override fun onViewDetachedFromWindow(viewHolder: EasyViewHolder) {
        super.onViewDetachedFromWindow(viewHolder)
        println("---------------detached emptyView")
    }
}