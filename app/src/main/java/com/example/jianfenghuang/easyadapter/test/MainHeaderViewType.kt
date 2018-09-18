package com.example.jianfenghuang.easyadapter.test

import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyHeaderViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class MainHeaderViewType : EasyHeaderViewType<MainHeaderViewType>() {
    override fun convert(bean: MainHeaderViewType, holder: EasyViewHolder) {

    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_header
    }
}