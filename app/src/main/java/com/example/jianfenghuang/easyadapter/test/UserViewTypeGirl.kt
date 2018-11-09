package com.example.jianfenghuang.easyadapter.test

import android.widget.Toast
import com.example.jianfenghuang.easyadapter.R
import com.ivy.adapter.EasyViewHolder
import com.ivy.adapter.EasyViewType

/**
 * Created by jianfeng.huang on 2018/9/5.
 *
 */
class UserViewTypeGirl : EasyViewType<User>() {
    override fun getLayoutId(): Int {
        return R.layout.adapter_user_girl
    }

    override fun convert(position: Int, bean: User, holder: EasyViewHolder, isChoose: Boolean) {
        holder.setText(R.id.tvName,"${bean.name}--${if (isChoose) "choose" else ""}")
        holder.setText(R.id.tvAge,"${bean.age}")
    }

    override fun onClickItem(bean: User) {
        super.onClickItem(bean)

    }

    override fun onClickItem(bean: User, holder: EasyViewHolder) {
        super.onClickItem(bean, holder)
        Toast.makeText(context,"用户：${bean.toString()}", Toast.LENGTH_SHORT).show()
    }
}