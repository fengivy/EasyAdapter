package com.example.jianfenghuang.easyadapter.test

import com.ivy.adapter.EasyMultipleViewType

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
class UserViewType : EasyMultipleViewType<User>() {
    override fun initViewType() {
        addViewType("男",UserViewTypeBoy())
        addViewType("女",UserViewTypeGirl())
    }

    override fun markBean(bean: User): Any? {
        return bean.sex
    }
}