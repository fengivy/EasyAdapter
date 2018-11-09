package com.example.jianfenghuang.easyadapter.test

import android.app.Activity
import com.ivy.adapter.EasyMultipleViewType

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
class UserViewType(val activity: Activity) : EasyMultipleViewType<User>() {
    init {
        addViewType({ it.sex == "男" },UserViewTypeBoy())
        addViewType({ bean -> bean.sex == "女" },UserViewTypeGirl())
        addViewType({ _ -> true },UserViewTypeNoneSex())

    }
}