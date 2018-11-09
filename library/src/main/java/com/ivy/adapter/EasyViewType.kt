package com.ivy.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
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

    open fun initViewHolder(holder: EasyViewHolder){

    }

    fun getBeanClass(): Class<T>{
        return beanClazz
    }

    /**
     * 不同参数的onClickItem方法会同时调用，选择重写一个适合方法即可
     */
    open fun onClickItem(bean: T){}
    open fun onClickItem(bean: T,holder:EasyViewHolder){}

    open fun onClickChild(bean: T,view: View){}
    open fun onClickChild(bean: T,view: View,holder:EasyViewHolder){}

    open fun onLongClickItem(bean: T){}
    open fun onLongClickItem(bean: T,holder:EasyViewHolder){}

    open fun onLongClickChild(bean: T,view: View){}
    open fun onLongClickChild(bean: T,view: View,holder:EasyViewHolder){}

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

    fun getDataPosition(viewHolder: EasyViewHolder):Int{
        val position = viewHolder.layoutPosition
        if (position == RecyclerView.NO_POSITION){
            return position
        }else{
            return position - adapter.getHeaderCount()
        }
    }

    fun updateViewHolder(viewHolder: EasyViewHolder){
        adapter.updateData(viewHolder.layoutPosition)
    }
}