package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.support.v4.app.ActivityCompat.invalidateOptionsMenu
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main_app.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancy on 2017/6/9.
 * Copyright © 2017 O2. All rights reserved.
 */

class AppFragment: BaseMVPViewPagerFragment<MyAppContract.View,MyAppContract.Presenter>(), MyAppContract.View{
    override var mPresenter: MyAppContract.Presenter = MyAppPresenter()
    override fun layoutResId(): Int = R.layout.fragment_main_app

    private val appBeanList = ArrayList<MyAppListObject>()
    private val oldAppBeanList = ArrayList<MyAppListObject>()
    private val myAppBeanList = ArrayList<MyAppListObject>()
    private val oldMyAppBeanList = ArrayList<MyAppListObject>()
    private var isEdit = false
    private val itemTouchHelper = ItemTouchHelper (object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            // 获取触摸响应的方向   包含两个 1.拖动dragFlags 2.侧滑删除swipeFlags
            // 代表只能是向左侧滑删除，当前可以是这样ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT
            val swipeFlags = ItemTouchHelper.LEFT

            // 拖动
            var dragFlags = if (recyclerView.layoutManager is GridLayoutManager) {
                // GridView 样式四个方向都可以
                ItemTouchHelper.UP or ItemTouchHelper.LEFT or
                        ItemTouchHelper.DOWN or ItemTouchHelper.RIGHT
            } else {
                // ListView 样式不支持左右，只支持上下
                ItemTouchHelper.UP or ItemTouchHelper.DOWN
            }

            return makeMovementFlags(dragFlags, swipeFlags)
        }

        /**
         * 拖动的时候不断的回调方法
         */
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            // 获取原来的位置
            val fromPosition = viewHolder.adapterPosition
            // 得到目标的位置
            val targetPosition = target.adapterPosition
            if (fromPosition < targetPosition)
                for (i in fromPosition until targetPosition)
                    Collections.swap(myAppBeanList, i, i + 1)// 改变实际的数据集
            else
                for (i in fromPosition downTo targetPosition + 1)
                    Collections.swap(myAppBeanList, i, i - 1)// 改变实际的数据集

            myAppEditAdapter.notifyItemMoved(fromPosition, targetPosition)
            return true
        }

        /**
         * 侧滑删除后会回调的方法
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // 获取当前删除的位置
            //val position = viewHolder.adapterPosition
            //mItems.remove(position)
            // adapter 更新notify当前位置删除
            //mAdapter.notifyItemRemoved(position)
        }

        /**
         * 拖动选择状态改变回调
         */
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                // ItemTouchHelper.ACTION_STATE_IDLE 看看源码解释就能理解了
                // 侧滑或者拖动的时候背景设置为灰色
                viewHolder!!.itemView.setBackgroundColor(FancySkinManager.instance().getColor(activity, R.color.z_color_meeting_text))
            }
        }

        /**
         * 回到正常状态的时候回调
         */
        override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder) {
            // 正常默认状态下背景恢复默认
            viewHolder.itemView.setBackgroundColor(0)
            //ViewCompat.setTranslationX(viewHolder.itemView, 0f)
            viewHolder.itemView.translationX = 0f
        }
    })



    override fun initUI() {
        initAllApp()
        initMyApp()
        //绑定拖拽事件
        itemTouchHelper.attachToRecyclerView(my_app_recycler_view)
    }


    override fun lazyLoad() {
        mPresenter.getAllAppList()
        mPresenter.getMyAppList()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_my_app,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        if (isEdit) {
            menu?.findItem(R.id.menu_app_edit)?.title = "完成"
        } else {
            menu?.findItem(R.id.menu_app_edit)?.title = "编辑"
        }
        if (activity is MainActivity) {
            (activity as MainActivity).refreshMenu()
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        isEdit = when (isEdit) {
            false -> {
                my_app_text_view.visibility = View.VISIBLE
                edit_ll_view.visibility = View.VISIBLE
                my_app_rv.visibility = View.GONE
                myAppEditAdapter.notifyDataSetChanged()
                allAppAdapter.notifyDataSetChanged()
                true
            }
            true -> {
                my_app_text_view.visibility = View.GONE
                edit_ll_view.visibility = View.GONE
                my_app_rv.visibility = View.VISIBLE
                mPresenter.addAndDelMyAppList(oldMyAppBeanList, myAppBeanList)
                false
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun initAllApp(){
        all_app_recycler_view.layoutManager = GridLayoutManager(activity,5)
        all_app_recycler_view.adapter = allAppAdapter
        allAppAdapter.setOnItemClickListener { _, position ->
            if (isEdit) {
                if (!appBeanList[position].isClick) {
                    appBeanList[position].isClick = true
                    myAppBeanList.add(appBeanList[position])
                    allAppAdapter.notifyItemChanged(position)
                    myAppEditAdapter.notifyItemInserted(myAppBeanList.size)
                }
            } else {
                IndexFragment.go(appBeanList[position].appId!!, activity, appBeanList[position].appTitle?:"")
            }
        }
    }

    private fun initMyApp(){
        my_app_rv.layoutManager = GridLayoutManager(activity,9)
        my_app_rv.adapter = myAppAdapter

        my_app_recycler_view.layoutManager = GridLayoutManager(activity,5)
        my_app_recycler_view.adapter = myAppEditAdapter
        myAppEditAdapter.setOnItemClickListener { _, position ->
            if (isEdit) {
                appBeanList.forEachIndexed { index, myAppListObject ->
                    if (myAppListObject.appId == myAppBeanList[position].appId) {
                        myAppListObject.isClick = false
                        allAppAdapter.notifyItemChanged(index)
                    }
                }
                myAppBeanList.removeAt(position)
                myAppEditAdapter.notifyItemRemoved(position)
                myAppEditAdapter.notifyItemRangeChanged(position,myAppBeanList.size)
            } else {
                IndexFragment.go(myAppBeanList[position].appId!!,activity, myAppBeanList[position].appTitle?:"")
            }
        }
    }


    override fun setAllAppList(allList: ArrayList<MyAppListObject>) {
        appBeanList.clear()
        oldAppBeanList.clear()
        appBeanList.addAll(allList)
        oldAppBeanList.addAll(allList)
        allAppAdapter.notifyDataSetChanged()
    }

    override fun setMyAppList(myAppList: ArrayList<MyAppListObject>) {
        oldMyAppBeanList.clear()
        oldMyAppBeanList.addAll(myAppList)
        myAppBeanList.clear()
        myAppBeanList.addAll(myAppList)
        for (app: MyAppListObject in appBeanList) {
            for (myApp: MyAppListObject in myAppList){
                if (app.appId == myApp.appId) {
                    app.isClick = true
                    break
                }
            }
        }
        myAppAdapter.notifyDataSetChanged()
    }

    override fun addAndDelMyAppList(isSuccess: Boolean) {
        if (isSuccess) {
            oldMyAppBeanList.clear()
            oldMyAppBeanList.addAll(myAppBeanList)
            myAppAdapter.notifyDataSetChanged()
            myAppEditAdapter.notifyDataSetChanged()
            allAppAdapter.notifyDataSetChanged()
        }
    }

    private val allAppAdapter: CommonRecycleViewAdapter<MyAppListObject> by lazy {
        object : CommonRecycleViewAdapter<MyAppListObject>(activity, appBeanList, R.layout.item_all_app_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MyAppListObject?) {
                val resId = ApplicationEnum.getApplicationByKey(t?.appId)?.iconResId
                if (resId!=null) {
                    holder?.setImageViewResource(R.id.app_id, resId)
                }else {
                    if (t?.appId != null){
                        val portalIconUrl = APIAddressHelper.instance().getPortalIconUrl(t.appId!!)
                        val icon = holder?.getView<ImageView>(R.id.app_id)
                        if (icon !=null) {
                            O2ImageLoaderManager.instance().showImage(icon, portalIconUrl, O2ImageLoaderOptions(placeHolder = R.mipmap.process_default))
                        }
                    }
//                    val bitmap = BitmapFactory.decodeFile(O2CustomStyle.processDefaultImagePath(this@MyAppActivity))
//                    if (bitmap != null) {
//                        holder?.setImageViewBitmap(R.id.app_id, bitmap)
//                    } else {
//                        holder?.setImageViewResource(R.id.app_id, R.mipmap.process_default)
//                    }
                }

                holder?.setText(R.id.app_name_id,t?.appTitle)
                if (isEdit) {
                    val delete = holder?.getView<ImageView>(R.id.delete_app_iv)
                    delete?.visibility = View.VISIBLE
                    if (t!!.isClick){
                        delete?.setImageResource(R.mipmap.icon__app_chose)
                    } else {
                        delete?.setImageResource(R.mipmap.icon_app_add)
                    }
                } else {
                    holder?.getView<ImageView>(R.id.delete_app_iv)?.visibility = View.GONE
                }
            }
        }
    }

    private val myAppEditAdapter: CommonRecycleViewAdapter<MyAppListObject> by lazy {
        object : CommonRecycleViewAdapter<MyAppListObject>(activity, myAppBeanList, R.layout.item_all_app_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MyAppListObject?) {
                val resId = ApplicationEnum.getApplicationByKey(t?.appId)?.iconResId
                if (resId!=null) {
                    holder?.setImageViewResource(R.id.app_id, resId)
                }else {
                    if (t?.appId != null){
                        val portalIconUrl = APIAddressHelper.instance().getPortalIconUrl(t.appId!!)
                        val icon = holder?.getView<ImageView>(R.id.app_id)
                        if (icon !=null) {
                            O2ImageLoaderManager.instance().showImage(icon, portalIconUrl, O2ImageLoaderOptions(placeHolder = R.mipmap.process_default))
                        }
                    }
                }
                if (isEdit) {
                    val delete = holder?.getView<ImageView>(R.id.delete_app_iv)
                    delete?.visibility = View.VISIBLE
                    delete?.setImageResource(R.mipmap.icon_app_del)
                    val text = holder?.getView<TextView>(R.id.app_name_id)
                    text?.visibility = View.VISIBLE
                    text?.text = t?.appTitle
                } else {
                    holder?.getView<ImageView>(R.id.delete_app_iv)?.visibility = View.GONE
                    holder?.getView<TextView>(R.id.app_name_id)?.visibility = View.GONE
                }
            }
        }
    }

    private val myAppAdapter: CommonRecycleViewAdapter<MyAppListObject> by lazy {
        object : CommonRecycleViewAdapter<MyAppListObject>(activity, oldMyAppBeanList, R.layout.item_app_mini) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MyAppListObject?) {
                val resId = ApplicationEnum.getApplicationByKey(t?.appId)?.iconResId
                if (resId!=null) {
                    holder?.setImageViewResource(R.id.app_id, resId)
                }else {
                    if (t?.appId != null){
                        val portalIconUrl = APIAddressHelper.instance().getPortalIconUrl(t.appId!!)
                        val icon = holder?.getView<ImageView>(R.id.app_id)
                        if (icon !=null) {
                            O2ImageLoaderManager.instance().showImage(icon, portalIconUrl, O2ImageLoaderOptions(placeHolder = R.mipmap.process_default))
                        }
                    }
                }
            }
        }
    }
}