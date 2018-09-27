package com.example.jianfenghuang.easyadapter.test

import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyViewType

/**
 * Created by jianfeng.huang on 2018/9/27.
 */
class UserViewTypeNoneSex : EasyViewType<User>() {
    override fun getLayoutId(): Int {
        return R.layout.adapter_user_no_sex
    }

    override fun convert(position: Int, bean: User, holder: EasyViewHolder, isChoose: Boolean) {

    }

    override fun initViewHolder(holder: EasyViewHolder) {

    }
}