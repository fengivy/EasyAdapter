package com.example.jianfenghuang.easyadapter.test

import android.widget.TextView
import android.widget.Toast
import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class UserViewTypeBoy : EasyViewType<User>() {
    override fun getLayoutId(): Int {
        return R.layout.adapter_user
    }

    override fun convert(position: Int, bean: User, holder: EasyViewHolder, isChoose: Boolean) {
        holder.setText(R.id.tvName,"${bean.name}--${if (isChoose) "choose" else ""}")
        holder.setText(R.id.tvAge,"${bean.age}")
        holder.getView<TextView>(R.id.tvAge).setOnClickListener {
            Toast.makeText(context,"我22岁",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickItem(bean: User) {
        super.onClickItem(bean)
        //Toast.makeText(context,"用户：${bean.toString()}",Toast.LENGTH_SHORT).show()
    }

    override fun isOpenSingleChooseByItem(): Boolean {
        return true
    }

}