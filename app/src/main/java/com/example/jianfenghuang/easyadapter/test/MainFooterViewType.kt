package com.example.jianfenghuang.easyadapter.test

import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EastFooterViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 */
class MainFooterViewType() : EastFooterViewType<MainFooterViewType>() {
    override fun convert(bean: MainFooterViewType, holder: EasyViewHolder) {

    }

    override fun getLayoutId(): Int {
        return R.layout.adapter_footer
    }



}