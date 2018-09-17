package com.ivy.adapter
/**
 * Created by jianfeng.huang on 2018/9/12.
 *
 */
class EasySimpleLoadingMoreViewType : EasyLoadingMoreViewType<LoadingMore>() {
    init {
        reset(LoadingMore())
    }

    override fun getLayoutId(): Int {
        return R.layout.view_easy_adpter_loading_more
    }

    override fun loading(bean: LoadingMore, holder: EasyViewHolder) {
        holder.setText(R.id.tv,"加载中...")
    }

    override fun loadingFinish(bean: LoadingMore, holder: EasyViewHolder) {
        holder.setText(R.id.tv,"没有更多数据了")
    }
}