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
    //记录adapter的itemViewType
    var itemViewType = -1
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
    fun bindViewHolder(position: Int, bean: T, holder: EasyViewHolder){
        convert(position,bean,holder,adapter.judgeIsSingleChoose(bean!!))
    }

    abstract fun getLayoutId():Int
    abstract fun convert(position:Int,bean :T,holder: EasyViewHolder,isChoose: Boolean = false)


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

    open fun onClickItem(bean: T){}

    open fun onClickChild(bean: T,view: View){}

    open fun onLongClickItem(bean: T){}

    open fun onLongClickChild(bean: T,view: View){}

    open fun onViewDetachedFromWindow(viewHolder: EasyViewHolder){

    }

    open fun onViewAttachedToWindow(holder: EasyViewHolder){

    }

    open fun isOpenSingleChooseByItem():Boolean{
        return false
    }

    open fun isOpenSingleChooseByChild(viewId:Int):Boolean{
        return false
    }
}