package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_start_process_step_one.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ApplicationData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ProcessInfoData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration


class StartProcessStepOneFragment : BaseMVPFragment<StartProcessStepOneContract.View, StartProcessStepOneContract.Presenter>(), StartProcessStepOneContract.View {

    override var mPresenter: StartProcessStepOneContract.Presenter = StartProcessStepOnePresenter()

    override fun layoutResId(): Int = R.layout.fragment_start_process_step_one

    var currentChooseAppId = ""
    val appList = ArrayList<ApplicationData>()
    val processList = ArrayList<ProcessInfoData>()
    val appAdapter: CommonRecycleViewAdapter<ApplicationData> by lazy {
        object : CommonRecycleViewAdapter<ApplicationData>(activity, appList, R.layout.item_start_process_application) {
            override fun convert(holder: CommonRecyclerViewHolder, t: ApplicationData) {
                holder.setText(R.id.tv_item_start_process_application_name, t.name)
                val back = holder.getView<LinearLayout>(R.id.linear_item_start_process_application_content)
                if (t.id == currentChooseAppId) {
                    back.setBackgroundColor(Color.WHITE)
                } else {
                    back.setBackgroundResource(R.color.z_color_background_normal)
                }
                val icon = holder.getView<CircleImageView>(R.id.image_item_start_process_application_icon)
                val bitmap = BitmapFactory.decodeFile(O2CustomStyle.processDefaultImagePath(activity))
                icon?.setImageBitmap(bitmap)
                icon.tag = t.id
                (activity as StartProcessActivity).loadProcessApplicationIcon(icon, t.id)
            }
        }
    }
    val processAdapter: CommonRecycleViewAdapter<ProcessInfoData> by lazy {
        object : CommonRecycleViewAdapter<ProcessInfoData>(activity, processList, R.layout.item_start_process_application_process) {
            override fun convert(holder: CommonRecyclerViewHolder, t: ProcessInfoData) {
                holder.setText(R.id.tv_item_start_process_application_process_name, t.name)
            }
        }
    }
    val itemDecoration: DividerItemDecoration by lazy { DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST) }

    override fun initUI() {

        (activity as StartProcessActivity).setToolBarTitle(getString(R.string.title_activity_start_process))
        recycler_start_process_application_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_start_process_application_list.addItemDecoration(itemDecoration)
        appAdapter.setOnItemClickListener { _, position ->
            currentChooseAppId = appList[position].id
            mPresenter.loadProcessListByAppId(currentChooseAppId)
            appAdapter.notifyDataSetChanged()
        }
        recycler_start_process_application_list.adapter = appAdapter
        recycler_start_process_application_process_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        processAdapter.setOnItemClickListener { _, position ->
            onProcessItemClick(processList[position])
        }
        recycler_start_process_application_process_list.adapter = processAdapter

        mPresenter.loadApplicationList()
    }


    override fun loadApplicationList(list: List<ApplicationData>) {
        appList.clear()
        appList.addAll(list)
        if (appList.size>0) {
            currentChooseAppId = appList[0].id
            mPresenter.loadProcessListByAppId(currentChooseAppId)
            linear_start_process_content?.visible()
            tv_start_process_empty?.gone()
        }
        appAdapter.notifyDataSetChanged()
    }

    override fun loadApplicationListFail() {
        XToast.toastShort(activity, "获取应用列表失败！")
        appList.clear()
        linear_start_process_content?.gone()
        tv_start_process_empty?.visible()
    }

    override fun loadProcessList(list: List<ProcessInfoData>) {
        processList.clear()
        processList.addAll(list)
        processAdapter.notifyDataSetChanged()
    }

    override fun loadProcessListFail() {
        XToast.toastShort(activity, "获取流程列表失败！")
        processList.clear()
        processAdapter.notifyDataSetChanged()
    }

    private fun onProcessItemClick(processInfoData: ProcessInfoData) {
        val stepTwo = StartProcessStepTwoFragment.newInstance(processInfoData.id, processInfoData.name)
        (activity as StartProcessActivity).addFragment(stepTwo)
    }


}
