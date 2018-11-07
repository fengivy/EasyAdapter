package com.example.jianfenghuang.easyadapter

import android.arch.paging.PagedList
import android.arch.paging.PositionalDataSource
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import com.ivy.adapter.EasyAdapter
import com.ivy.adapter.EasyItemClickListener
import com.ivy.adapter.EasyLoadingMoreViewType
import com.example.jianfenghuang.easyadapter.test.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var adatpter: EasyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = EasyAdapter(this)
                .addHeader(MainHeaderViewType())
                .addFooter(MainFooterViewType())
                .addType(UserViewType(this))
                .addType(HouseViewType(this))
                .setEmptyView(EmptyViewType())
                //.openLoadingMore(true)
        rv.layoutManager = LinearLayoutManager(this)
        /*val layoutManager = GridLayoutManager(this,3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                if (position%4==0){
                    return 3
                }else{
                    return 1
                }
            }
        }
        rv.layoutManager = layoutManager*/
        rv.addOnItemTouchListener(EasyItemClickListener())
        rv.adapter = adapter
        //adapter.resetData(getLoadingValue())
        adapter.setOnLoadingMoreListener(object : EasyLoadingMoreViewType.OnLoadingMoreListener{
            override fun onLoadingMore() {
                Handler().postDelayed({
                    if (position >=3){
                        adapter.loadingMoreFinish()
                    }else{
                        adapter.addData(getLoadingValue())
                        adapter.loadingMoreFinish(false)
                        position++
                    }
                },1000)
            }
        })
        val user= mutableListOf<User>()
        adapter.resetData(user)

        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            adapter.resetData(null)
            position = 0
            adapter.openLoadingMore(true)
        }
    }

    private fun getLoadingValue():List<Any> {
        val list = mutableListOf<Any>()
        list.add(User("小红",18,"女"))
        list.add(House("大房子", Color.RED))
        list.add(User("小明",18,"男"))
        list.add(User("小明",17,""))
        list.add(User("小明",17,"男"))
        list.add(House("二房子",Color.BLUE))
        list.add(User("小刘",15,"男"))
        list.add(House("三房子",Color.GREEN))
        list.add(User("小白",16,"女"))
        list.add(House("小房子",Color.YELLOW))
        return list
    }

    var position = 0
}
