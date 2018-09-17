package com.example.jianfenghuang.easyadapter.test

import android.view.View
import android.widget.Toast
import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class HouseViewType : EasyViewType<House>() {
    override fun getLayoutId(): Int {
        return R.layout.adapter_house
    }

    override fun convert(position: Int, bean: House, holder: EasyViewHolder) {
        holder.setText(R.id.tv,bean.houseName)
        holder.setBackgroundColor(R.id.iv,bean.houseColor)
        holder.addOnClickListener(R.id.tv)
        holder.addOnLongClickListener(R.id.tv)
    }

    override fun onClickItem(bean: House) {
        super.onClickItem(bean)
        Toast.makeText(context,"房子：${bean.toString()}", Toast.LENGTH_SHORT).show()
    }

    override fun onClickChild(bean: House, view: View) {
        super.onClickChild(bean, view)
        when(view.id){
            R.id.tv ->{
                Toast.makeText(context,"房子名字：${bean.houseName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLongClickChild(bean: House, view: View) {
        super.onLongClickChild(bean, view)
        when(view.id){
            R.id.tv ->{
                Toast.makeText(context,"长按房子名字：${bean.houseName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLongClickItem(bean: House) {
        super.onLongClickItem(bean)
        Toast.makeText(context,"长按房子：${bean.toString()}", Toast.LENGTH_SHORT).show()
    }
}