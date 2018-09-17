package com.example.jianfenghuang.easyadapter.test

import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyHeaderViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class MainHeaderViewType(val markValue :String = "first") : EasyHeaderViewType<MainHeaderViewType>() {
    override fun convert(bean: MainHeaderViewType, holder: EasyViewHolder) {

    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_header
    }

    override fun markBean(bean: MainHeaderViewType): Any? {
        return markValue
    }

    override fun markValue(): Any? {
        return markValue
    }
}