package com.ivy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.ParameterizedType

/**
 * Created by jianfeng.huang on 2018/9/4.
 *
 */
abstract class EasyViewType<T> {
    lateinit var adapter: EasyAdapter
    lateinit var context:Context
    private val beanClazz:Class<T> by lazy {
        val genericSuperclass = this@EasyViewType::class.java.genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            val actualTypeArguments =genericSuperclass.actualTypeArguments
            if (actualTypeArguments.isNotEmpty()) {
                return@lazy actualTypeArguments[0] as Class<T>
            }else{
                throw IllegalArgumentException("请设置泛型")
            }
        }
        throw IllegalArgumentException("请设置泛型")
    }

    abstract fun getLayoutId():Int
    abstract fun convert(position:Int,bean :T,holder: EasyViewHolder)

    fun createEasyViewHolder(inflate:LayoutInflater,parent: ViewGroup): EasyViewHolder {
        val viewHolder = EasyViewHolder(inflate.inflate(getLayoutId(), parent, false))
        initViewHolder(viewHolder)
        return viewHolder
    }

    private fun initViewHolder(holder: EasyViewHolder){

    }

    fun getBeanClass(): Class<T>{
        return beanClazz
    }
    open fun markBean(bean: T):Any?{
        return null
    }

    open fun markValue():Any?{
        return null
    }

    open fun onClickItem(bean: T){}

    open fun onClickChild(bean: T,view: View){}

    open fun onLongClickItem(bean: T){}

    open fun onLongClickChild(bean: T,view: View){}

    open fun onViewDetachedFromWindow(viewHolder: EasyViewHolder){

    }

    open fun onViewAttachedToWindow(holder: EasyViewHolder){

    }
}