package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
abstract class EasyMultipleViewType<T> : EasyViewType<T>() {
    val easyViewTypeMap = HashMap<(bean:T)->Boolean,EasyViewType<T>>()

    init {
        this.initViewType()
    }

    /**
     * please use addViewType() method addviewType
     */
    abstract fun initViewType()

    override fun getLayoutId(): Int {
        return 0
    }

    override fun convert(position: Int, bean: T, holder: EasyViewHolder, isChoose: Boolean) {

    }

    fun addViewType(func:(bean:T)->Boolean,viewType: EasyViewType<T>){
        easyViewTypeMap.put(func,viewType)
    }
}