package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/4.
 *
 */
data class LayoutType(val itemTypeInterface: EasyViewType<Any>) {
    val beanType = mutableMapOf<Any?, EasyViewType<Any>>()
}