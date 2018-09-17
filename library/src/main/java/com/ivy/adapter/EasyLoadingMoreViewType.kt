package com.ivy.adapter
/**
 * Created by jianfeng.huang on 2018/9/6.
 *
 */
abstract class EasyLoadingMoreViewType<T> : EasyViewType<T>() {
    var bean:T? = null
    var uiStatus = STATUS_LOAD_READY

    fun reset(bean: T){
        this.bean = bean
    }

    override fun convert(position: Int, bean: T, holder: EasyViewHolder) {
        when(uiStatus){
            STATUS_LOAD_READY -> {
                loading(bean,holder)
                onLoadingMoreListener?.onLoadingMore()
                uiStatus = STATUS_LOADING
            }
            STATUS_LOADING ->{
                loading(bean,holder)
            }
            STATUS_LOAD_FINISH -> {
                loadingFinish(bean,holder)
            }
        }
    }

    abstract fun loading(bean: T,holder: EasyViewHolder)
    abstract fun loadingFinish(bean: T,holder: EasyViewHolder)

     fun setLoading(){
        uiStatus = STATUS_LOAD_READY
    }

    fun setLoadingFinish(){
        uiStatus = STATUS_LOAD_FINISH
    }

    companion object {
        const val STATUS_LOAD_READY = 0
        const val STATUS_LOADING = 1
        const val STATUS_LOAD_FINISH= 2
    }

    var onLoadingMoreListener: OnLoadingMoreListener? = null

    interface OnLoadingMoreListener {
        fun onLoadingMore()
    }
}