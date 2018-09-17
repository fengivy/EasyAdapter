package com.ivy.adapter

/**
 * Created by jianfeng.huang on 2018/9/10.
 *
 */
abstract class EasyEmptyViewType<T> : EasyViewType<T>() {
    var bean:T? = null


    override fun convert(position: Int, bean: T, holder: EasyViewHolder) {
        convert(this.bean?:return, holder)
    }


    abstract fun convert(bean:T ,holder: EasyViewHolder)

    fun resetBean(bean:T?){
        this.bean = bean

    }
}