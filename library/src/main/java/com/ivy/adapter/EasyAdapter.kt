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
    //record class and layout relation
    private val layouts = mutableMapOf<Class<*>, EasyViewType<Any>>()
    //record itemType and layout relation
    private val layoutsReflect = mutableMapOf<Int, EasyViewType<Any>>()
    private val data = mutableListOf<Any>()
    private var autoIncrementItemTypeNum = 0
    private var loadingMoreViewType: EasyLoadingMoreViewType<Any> = EasySimpleLoadingMoreViewType() as EasyLoadingMoreViewType<Any>
    private var rv:RecyclerView? = null
    private var originSpanSizeLookup:GridLayoutManager.SpanSizeLookup? = null

    private val mInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (rv?.layoutManager is GridLayoutManager){
            (rv?.layoutManager as GridLayoutManager).spanSizeLookup = originSpanSizeLookup
        }
        rv = null
    }


    override fun onBindViewHolder(holder: EasyViewHolder, position: Int) {
        val viewType = getItemTypeInterfaceByPosition(position)
        when(viewType) {
           is EasyHeaderAndFooterViewType -> {
                setFullSpanWhenStagger(holder)
               viewType.viewHolder = holder
               viewType.bindViewHolder(-1,viewType.bean?:return,holder)
            }
            is EasyLoadingMoreViewType -> {
                setFullSpanWhenStagger(holder)
                viewType.bindViewHolder(-1, viewType.bean ?: return, holder)
            }
            is EasyEmptyViewType ->{
                setFullSpanWhenStagger(holder)
                val rvHeight = rv?.height?:0
                val height =  rvHeight - getHeaderHeight()%rvHeight
                holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,height)
                viewType.bindViewHolder(-1, viewType.bean?:return,holder)
            }
            else -> {
                viewType.bindViewHolder(position-getHeaderCount(), data[position-getHeaderCount()], holder)
            }
        }
    }

    private fun setFullSpanWhenStagger(holder: EasyViewHolder){
        if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams){
            (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan  = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyViewHolder {
        val itemTypeInterface = layoutsReflect[viewType]?:throw IllegalArgumentException("please invoke addType function")
        return itemTypeInterface.createEasyViewHolder(mInflater,parent)
    }

    override fun getItemCount(): Int {
        return if (data.size == 0&&emptyViewType.size!=0){
            getHeaderCount()+emptyViewType.size+getFooterCount()+(if (isLoadingMore()) 1 else 0)
        }else{
            getHeaderCount()+data.size+getFooterCount()+(if (isLoadingMore()) 1 else 0)
        }
    }


    fun addType(itemTypeInterface: EasyViewType<*>): EasyAdapter {
        itemTypeInterface.context = context
        itemTypeInterface.adapter = this
        val easyViewType: EasyViewType<Any>? = layouts[itemTypeInterface.getBeanClass()]
        if (easyViewType == null){
            layouts.put(itemTypeInterface.getBeanClass(), itemTypeInterface as EasyViewType<Any>)
            if (itemTypeInterface is EasyMultipleViewType){
                itemTypeInterface.easyViewTypeMap.forEach {
                    it.value.context = context
                    it.value.adapter = this
                    layoutsReflect.put(autoIncrementItemTypeNum, it.value as EasyViewType<Any>)
                    it.value.itemViewType = autoIncrementItemTypeNum
                    autoIncrementItemTypeNum++
                }
            }else{
                layoutsReflect.put(autoIncrementItemTypeNum,itemTypeInterface)
                itemTypeInterface.itemViewType = autoIncrementItemTypeNum
                autoIncrementItemTypeNum++
            }
        }
        return this
    }

    private fun removeType(itemTypeInterface: EasyViewType<*>){
        layoutsReflect.remove(itemTypeInterface.itemViewType)
        layouts.remove(itemTypeInterface.getBeanClass())
    }



    override fun getItemViewType(position: Int): Int {
        return getItemTypeInterfaceByPosition(position).itemViewType
    }

    fun getItemTypeInterfaceByPosition(position: Int): EasyViewType<Any> {
        return when {
            isHeaderPosition(position) -> headers[position]
            isLoadingMorePosition(position) -> loadingMoreViewType
            isFooterPosition(position) -> footers[position-getHeaderCount()-if (data.size ==0) emptyViewType.size else data.size]
            else -> {
                if (data.size ==0){
                    return emptyViewType[0]
                }else{
                    val bean = data[position-getHeaderCount()]
                    val viewType = layouts[bean::class.java]?:throw IllegalArgumentException("please invoke addType function")
                    if (viewType is EasyMultipleViewType){
                        viewType.easyViewTypeMap.forEach {
                            if(it.key.invoke(bean)){
                                return it.value
                            }
                        }
                        throw IllegalArgumentException("please invoke addType function")
                    }
                    return viewType
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
            this.notifyItemRemoved(itemCount-1-if (isLoadingMore()) 1 else 0-(getFooterCount()+1)+position)
        }
    }

    override fun onViewDetachedFromWindow(holder: EasyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val easyViewType = layoutsReflect[holder.itemViewType]
        easyViewType?.onViewDetachedFromWindow(holder)
    }

    override fun onViewAttachedToWindow(holder: EasyViewHolder) {
        super.onViewAttachedToWindow(holder)
        val easyViewType = layoutsReflect[holder.itemViewType]
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
        return data.toList()
    }

    fun <T> getDataByPosition(position:Int):T{
        return data[position] as T
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
        return position<getHeaderCount()
    }

    private fun isLoadingMorePosition(position: Int):Boolean{
        return if (data.size == 0){
            position >= getHeaderCount() + emptyViewType.size + getFooterCount()
        }else{
            position >= getHeaderCount() + data.size+getFooterCount()
        }
    }

    private fun isFooterPosition(position: Int):Boolean{
        return if (data.size == 0){
            position>=getHeaderCount()+emptyViewType.size
        }else{
            position>=getHeaderCount()+data.size
        }
    }

    /**
     * single choose
     */
    private var lastSingleRecord:Any? = null

    fun judgeIsSingleChoose(any: Any):Boolean{
        return judgeObjectIsEqual(any,lastSingleRecord)
    }

    fun resetSingleChoose(){
        val position = getPositionByObject(lastSingleRecord?:return)
        lastSingleRecord = null
        if (position !=-1) {
            notifyItemChanged(position)
        }
    }

    fun clickSingleChoose(any: Any) {
        if (any == lastSingleRecord){
            return
        }
        val oldPosition = getPositionByObject(lastSingleRecord)
        val position  = getPositionByObject(any)
        lastSingleRecord = any

        if (position!=-1){
            notifyItemChanged(position)
        }
        if (oldPosition!= -1){
            notifyItemChanged(oldPosition)
        }
    }

    fun getSingleChoose():Any?{
        return lastSingleRecord
    }

    fun getSingleChoosePosition():Int{
        return getPositionByObject(lastSingleRecord)
    }

    fun getPositionByObject(any: Any?):Int{
        val index = data.indexOfFirst { judgeObjectIsEqual(it,any) }
        if (index >= 0){
            return getHeaderCount()+index
        }
        return -1
    }

    /**
     * judge two object is equal,use in getPositionByObject function and single choose
     */
    fun judgeObjectIsEqual(last:Any?, now:Any?):Boolean{
        if (last == null&&now == null){
            return true
        }
        return last?.equals(now)?:false
    }
}