package com.ivy.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup



/**
 * Created by jianfeng.huang on 2018/9/4.
 *
 */
class EasyAdapter(private val context:Context) : RecyclerView.Adapter<EasyViewHolder>() {
    //用于记录class和布局的关系
    private val layouts = mutableMapOf<Class<*>, LayoutType>()
    //用于记录itemType和布局的关系
    private val layoutsReflect = mutableMapOf<Int, EasyViewType<Any>>()
    private val viewTypeReflect = mutableMapOf<EasyViewType<Any>,Int>()
    private val data = mutableListOf<Any>()
    private var autoIncrementItemTypeNum = 0
    private var loadingMoreViewType: EasyLoadingMoreViewType<Any> = EasySimpleLoadingMoreViewType() as EasyLoadingMoreViewType<Any>
    private var rv:RecyclerView? = null
    private var originSpanSizeLookup:GridLayoutManager.SpanSizeLookup? = null

    private val mInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        rv = recyclerView
        val layoutManager = recyclerView?.layoutManager
        if (layoutManager is GridLayoutManager){
            originSpanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when{
                        isHeaderPosition(position) -> layoutManager.spanCount
                        isLoadingMorePosition(position) -> layoutManager.spanCount
                        isFooterPosition(position) -> layoutManager.spanCount
                        else -> {
                            return if (data.size ==0){
                                layoutManager.spanCount
                            }else{
                                originSpanSizeLookup?.getSpanSize(position-getHeaderCount())?:1
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (rv?.layoutManager is GridLayoutManager){
            (rv?.layoutManager as GridLayoutManager).spanSizeLookup = originSpanSizeLookup
        }
        rv = null
    }


    override fun onBindViewHolder(holder: EasyViewHolder, position: Int) {
        when {
            isHeaderPosition(position) -> {
                setFullSpanWhenStagger(holder)
                val headerViewType = headers[position]
                headerViewType.viewHolder = holder
                headerViewType.convert(-1,headerViewType.bean?:return,holder)
            }
            isLoadingMorePosition(position) -> {
                setFullSpanWhenStagger(holder)
                loadingMoreViewType.convert(position, loadingMoreViewType.bean ?: return, holder)
            }
            isFooterPosition(position) -> {
                setFullSpanWhenStagger(holder)
                val footerViewType = footers[position-headers.size - if (data.size == 0) emptyViewType.size else data.size]
                footerViewType.viewHolder = holder
                footerViewType.convert(-1,footerViewType.bean?:return,holder)
            }
            else -> {
                if (data.size == 0){
                    setFullSpanWhenStagger(holder)
                    val rvHeight = rv?.height?:0
                    val height =  rvHeight - getHeaderHeight()%rvHeight
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,height)
                    emptyViewType[0].convert(-1, emptyViewType[0].bean?:return,holder)
                }else{
                    getItemTypeInterfaceByPosition(position).convert(position-headers.size, data[position-headers.size], holder)
                }
            }
        }
    }

    private fun setFullSpanWhenStagger(holder: EasyViewHolder){
        if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams){
            (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan  = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyViewHolder {
        val itemTypeInterface = layoutsReflect[viewType]?:throw IllegalArgumentException("请调用addType方法")
        return itemTypeInterface.createEasyViewHolder(mInflater,parent)
    }

    override fun getItemCount(): Int {
        return if (data.size == 0&&emptyViewType.size!=0){
            headers.size+emptyViewType.size+footers.size+(if (isLoadingMore()) 1 else 0)
        }else{
            headers.size+data.size+footers.size+(if (isLoadingMore()) 1 else 0)
        }
    }


    fun addType(itemTypeInterface: EasyViewType<*>): EasyAdapter {
        itemTypeInterface.context = context
        itemTypeInterface.adapter = this
        val layoutTypes: LayoutType? = layouts[itemTypeInterface.getBeanClass()]
        if (layoutTypes == null){
            //设置layoutType对象
            val layoutType = LayoutType(itemTypeInterface as EasyViewType<Any>)
            layoutType.beanType.put(itemTypeInterface.markValue(), itemTypeInterface)

            layouts.put(itemTypeInterface.getBeanClass(),layoutType)
            layoutsReflect.put(autoIncrementItemTypeNum,itemTypeInterface)
            viewTypeReflect.put(itemTypeInterface,autoIncrementItemTypeNum)
            autoIncrementItemTypeNum++
        }else{
            if (layoutTypes.beanType[itemTypeInterface.markValue()] ==null){
                layoutTypes.beanType.put(itemTypeInterface.markValue(),itemTypeInterface as EasyViewType<Any>)
                layoutsReflect.put(autoIncrementItemTypeNum,itemTypeInterface)
                viewTypeReflect.put(itemTypeInterface,autoIncrementItemTypeNum)
                autoIncrementItemTypeNum++
            }
        }
        return this
    }

    private fun removeType(itemTypeInterface: EasyViewType<*>){
        val itemTypeNum = viewTypeReflect[itemTypeInterface] ?: return
        viewTypeReflect.remove(itemTypeInterface)
        layoutsReflect.remove(itemTypeNum)
        val layoutType = layouts[itemTypeInterface.getBeanClass()]?:return
        if (layoutType.beanType.isEmpty()){
            layouts.remove(itemTypeInterface.getBeanClass())
        }else{
            layoutType.beanType.remove(itemTypeInterface.markValue())
        }
    }



    override fun getItemViewType(position: Int): Int {
        return viewTypeReflect[getItemTypeInterfaceByPosition(position)] ?:throw IllegalArgumentException("请调用addType方法:$position")
    }

    fun getItemTypeInterfaceByPosition(position: Int): EasyViewType<Any> {
        return when {
            isHeaderPosition(position) -> headers[position]
            isLoadingMorePosition(position) -> loadingMoreViewType as EasyViewType<Any>
            isFooterPosition(position) -> footers[position-headers.size-if (data.size ==0) emptyViewType.size else data.size]
            else -> {
                if (data.size ==0){
                    return emptyViewType[0]
                }else{
                    val bean = data[position-headers.size]
                    val layoutType: LayoutType = layouts[bean::class.java]?:throw IllegalArgumentException("请调用addType方法")
                    layoutType.beanType[layoutType.itemTypeInterface.markBean(bean)] ?:throw IllegalArgumentException("请调用addType方法")
                }
            }
        }
    }


    fun resetData(list: List<Any>?) {
        data.clear()
        data.addAll(list?: listOf())
        notifyDataSetChanged()
    }

    fun addData(any: Any,index:Int){
        data.add(index,any)
        if (data.size == 1){
            this.notifyDataSetChanged()
        }else{
            this.notifyItemInserted(getHeaderCount()+index)
        }
    }

    fun addData(list:List<Any>?){
        if (list?.isNotEmpty() == true){
            data.addAll(list)
            if(data.size == list.size){
                this.notifyDataSetChanged()
            }else{
                this.notifyItemRangeInserted(itemCount-list.size - getFooterCount()-(if(isLoadingMore()) 1 else 0),list.size)
            }
        }
    }


    fun updateData(position:Int){
        if (position>=0 &&position<data.size){
            this.notifyItemChanged(getHeaderCount()+position)
        }
    }

    fun updateData(bean:Any){
        val position = data.indexOfFirst { it == bean }
        updateData(position)
    }

    fun remove(any: Any){
        val index = data.indexOfFirst { it == any }
        if (index >= 0){
            data.removeAt(index)
            notifyItemRemoved(getHeaderCount()+index)
        }
    }



    private fun notifyLoadingMore(){
        if (isLoadingMore()){
            notifyItemChanged(itemCount-1)
        }
    }

    private val headers = mutableListOf<EasyHeaderAndFooterViewType<Any>>()
    fun addHeader(itemTypeInterface: EasyHeaderAndFooterViewType<*>): EasyAdapter {
        addType(itemTypeInterface)
        headers.add(itemTypeInterface as EasyHeaderAndFooterViewType<Any>)
        return this
    }

    fun removeHeader(itemTypeInterface: EasyHeaderAndFooterViewType<*>){
        removeType(itemTypeInterface)
        val position = headers.indexOf(itemTypeInterface)
        if (position >= 0){
            headers.removeAt(position)
            this.notifyItemRemoved(position)
        }
    }

    fun getHeaderPosition(itemTypeInterface: EasyViewType<*>):Int{
        return headers.indexOfFirst { it == itemTypeInterface }
    }

    private val footers = mutableListOf<EasyHeaderAndFooterViewType<Any>>()
    fun addFooter(itemTypeInterface: EasyHeaderAndFooterViewType<*>): EasyAdapter {
        addType(itemTypeInterface)
        footers.add(itemTypeInterface as EasyHeaderAndFooterViewType<Any>)
        return this
    }

    fun removeFooter(itemTypeInterface: EasyHeaderAndFooterViewType<*>){
        removeType(itemTypeInterface)
        val position = footers.indexOf(itemTypeInterface)
        if (position >= 0){
            footers.removeAt(position)
            this.notifyItemRemoved(itemCount-1-if (isLoadingMore()) 1 else 0-(footers.size+1)+position)
        }
    }

    override fun onViewDetachedFromWindow(holder: EasyViewHolder?) {
        super.onViewDetachedFromWindow(holder)
        val easyViewType = layoutsReflect[holder?.itemViewType?:return]
        easyViewType?.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: EasyViewHolder?) {
        super.onViewAttachedToWindow(holder)
        val easyViewType = layoutsReflect[holder?.itemViewType?:return]
        easyViewType?.onViewAttachedToWindow(holder)
    }


    private val emptyViewType = mutableListOf<EasyEmptyViewType<Any>>()
    fun setEmptyView(itemTypeInterface: EasyEmptyViewType<*>): EasyAdapter {
        addType(itemTypeInterface)
        emptyViewType.clear()
        emptyViewType.add(itemTypeInterface as EasyEmptyViewType<Any>)
        return this
    }

    private fun getHeaderHeight():Int{
        var height = 0
        headers.forEach {
            height+= it.viewHolder?.itemView?.measuredHeight?:0
        }
        return height
    }

    fun getFooterPosition(itemTypeInterface: EasyViewType<*>):Int{
        return footers.indexOfFirst { it == itemTypeInterface }
    }

    fun getData() :List<Any>{
        return data
    }

    fun getHeaderCount(): Int {
        return headers.size
    }

    fun getFooterCount(): Int {
        return footers.size
    }

    fun isLoadingMore():Boolean{
        return isOpenLoadingMore
    }


    private var isOpenLoadingMore = false
    fun setLoadingMoreViewType(viewType: EasyLoadingMoreViewType<*>): EasyAdapter {
        removeType(loadingMoreViewType)
        this.loadingMoreViewType = viewType as EasyLoadingMoreViewType<Any>
        addType(loadingMoreViewType)
        return this
    }

    fun openLoadingMore(isOpen: Boolean): EasyAdapter {
        if (isOpen){
            addType(loadingMoreViewType)
            loadingMoreViewType.setLoading()
            notifyLoadingMore()
        }else{
            notifyLoadingMore()
        }
        isOpenLoadingMore = isOpen
        return this
    }

    fun loadingMoreFinish(isLoadAllDataFinish:Boolean = true){
        if (isLoadingMore()){
            if (isLoadAllDataFinish){
                loadingMoreViewType.setLoadingFinish()
                notifyLoadingMore()
            }else{
                loadingMoreViewType.setLoading()
                notifyLoadingMore()
            }
        }
    }

    fun setOnLoadingMoreListener(loadingMoreListener: EasyLoadingMoreViewType.OnLoadingMoreListener) {
        loadingMoreViewType.onLoadingMoreListener  =  loadingMoreListener
    }

    private fun isHeaderPosition(position: Int):Boolean{
        return position<headers.size
    }

    private fun isLoadingMorePosition(position: Int):Boolean{
        return if (data.size == 0){
            position >= headers.size + emptyViewType.size + footers.size
        }else{
            position >= headers.size + data.size+footers.size
        }
    }

    private fun isFooterPosition(position: Int):Boolean{
        return if (data.size == 0){
            position>=headers.size+emptyViewType.size
        }else{
            position>=headers.size+data.size
        }
    }
}