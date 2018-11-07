package com.ivy.adapter

import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class EasyItemClickListener : RecyclerView.SimpleOnItemTouchListener() {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent?): Boolean {
        gesture?:let {
            gesture = GestureDetector(rv.context,object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val view = rv.findChildViewUnder(e.x,e.y)
                    if ( view!= null){
                        val viewHolder = rv.getChildViewHolder(view) as EasyViewHolder
                        val ids = viewHolder.childClickViewIds
                        val position = rv.getChildViewHolder(view).adapterPosition
                        val adapter = rv.adapter as EasyAdapter
                        ids.forEachIndexed { _, it ->
                            val child = viewHolder.getView<View>(it)
                            if (child!=null&&child.isEnabled&&judgeIsTouchInView(child,e)){
                                dealClick(position,adapter,false,child)
                                return true
                            }
                        }
                        dealClick(position,adapter,true,null)
                    }
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    super.onLongPress(e)
                    val view = rv.findChildViewUnder(e.x,e.y)
                    if ( view!= null){
                        val viewHolder = rv.getChildViewHolder(view) as EasyViewHolder
                        val ids = viewHolder.childLongClickViewIds
                        val position = rv.getChildViewHolder(view).adapterPosition
                        val adapter = rv.adapter as EasyAdapter
                        ids.forEach {
                            val child = viewHolder.getView<View>(it)
                            if (child!=null&&child.isEnabled&&judgeIsTouchInView(child,e)){
                                dealLongClick(position,adapter,false,child)
                                return
                            }
                        }
                        dealLongClick(position,adapter,true,null)
                    }
                }
            })
        }
        //return gesture?.onTouchEvent(e)?:false
        gesture?.onTouchEvent(e)
        return false
    }
    private var gesture:GestureDetector? = null
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent?) {

    }

    /**
     * 判断点击位置是否落在view上
     */
    private fun judgeIsTouchInView(view: View, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        return if (view.visibility != View.VISIBLE) {
            false
        } else {
            view.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]
            ev.rawX >= x.toFloat() && ev.rawX <= (x + view.width).toFloat() && ev.rawY >= y.toFloat() && ev.rawY <= (y + view.height).toFloat()
        }
    }

    private fun dealClick(position:Int, adapter: EasyAdapter, isClickItem:Boolean, child:View ?= null){
        val viewTypeInterface = adapter.getItemTypeInterfaceByPosition(position)
        if (viewTypeInterface is EasyHeaderAndFooterViewType){
            if (isClickItem){
                viewTypeInterface.onClickItem(viewTypeInterface.bean?:return)
            }else{
                viewTypeInterface.onClickChild(viewTypeInterface.bean?:return,child?:return)
            }
        }else if (viewTypeInterface is EasyEmptyViewType){
            if (isClickItem){
                viewTypeInterface.onClickItem(viewTypeInterface.bean?:return)
            }else{
                viewTypeInterface.onClickChild(viewTypeInterface.bean?:return,child?:return)
            }
        }else{
            if (isClickItem){
                viewTypeInterface.onClickItem(adapter.getData<Any>()[position-adapter.getHeaderCount()])
                if (viewTypeInterface.isOpenSingleChooseByItem()) {
                    adapter.clickSingleChoose(adapter.getData<Any>()[position - adapter.getHeaderCount()])
                }
            }else{
                viewTypeInterface.onClickChild(adapter.getData<Any>()[position-adapter.getHeaderCount()],child?:return)
                if (viewTypeInterface.isOpenSingleChooseByChild(child.id)){
                    adapter.clickSingleChoose(adapter.getData<Any>()[position-adapter.getHeaderCount()])
                }
            }
        }
    }

    private fun dealLongClick(position:Int, adapter: EasyAdapter, isClickItem:Boolean, child:View ?= null){
        val viewTypeInterface = adapter.getItemTypeInterfaceByPosition(position)
        if (viewTypeInterface is EasyHeaderAndFooterViewType){
            if (isClickItem){
                viewTypeInterface.onLongClickItem(viewTypeInterface.bean?:return)
            }else{
                viewTypeInterface.onLongClickChild(viewTypeInterface.bean?:return,child?:return)
            }
        }else if (viewTypeInterface is EasyEmptyViewType){
            if (isClickItem){
                viewTypeInterface.onLongClickItem(viewTypeInterface.bean?:return)
            }else{
                viewTypeInterface.onLongClickChild(viewTypeInterface.bean?:return,child?:return)
            }
        }else{
            if (isClickItem){
                viewTypeInterface.onLongClickItem(adapter.getData<Any>()[position-adapter.getHeaderCount()])
            }else{
                viewTypeInterface.onLongClickChild(adapter.getData<Any>()[position-adapter.getHeaderCount()],child?:return)
            }
        }
    }
}