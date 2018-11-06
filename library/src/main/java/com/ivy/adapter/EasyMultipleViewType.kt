package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
abstract class EasyMultipleViewType<T> : EasyViewType<T>() {
    val easyViewTypeMap = LinkedHashMap<(bean:T)->Boolean,EasyViewType<T>>()
    init {
        initViewType()
    }
    /**
     * please use addViewType() method addviewType
     */
    @Deprecated("this method has a error in kotlin,please invoke addViewType() in init{}")
    open fun initViewType(){}

    override fun getLayoutId(): Int {
        return 0
    }

    override fun convert(position: Int, bean: T, holder: EasyViewHolder, isChoose: Boolean) {

    }

    fun addViewType(func:(bean:T)->Boolean,viewType: EasyViewType<T>):EasyMultipleViewType<T>{
        easyViewTypeMap.put(func,viewType)
        return this
    }

}