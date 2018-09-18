package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/18.
 *
 */
abstract class EasyMultipleViewType<T> : EasyViewType<T>() {
    val easyViewTypeMap = HashMap<Any,EasyViewType<T>>()

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

    fun addViewType(any: Any,viewType: EasyViewType<T>){
        easyViewTypeMap.put(any,viewType)
    }

    /**
     * 使用bean的哪个字段进行判断
     */
    abstract fun markBean(bean: T):Any?
}