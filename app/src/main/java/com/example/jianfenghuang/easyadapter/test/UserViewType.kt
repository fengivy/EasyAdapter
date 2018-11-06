package com.example.jianfenghuang.easyadapter.test

import com.ivy.adapter.EasyMultipleViewType

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
class UserViewType : EasyMultipleViewType<User>() {
    override fun initViewType() {
        addViewType({ bean -> bean.sex == "男" },UserViewTypeBoy())
        addViewType({ bean -> bean.sex == "女" },UserViewTypeGirl())
        addViewType({ _ -> true },UserViewTypeNoneSex())
    }
}