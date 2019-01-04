package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import kotlinx.android.synthetic.main.fragment_main_app.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main.AttendanceMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main.BBSMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.CloudDriveActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index.CMSIndexActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main.MeetingMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.ReadCompletedListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.ReadListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.TaskCompletedListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.TaskListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.AppItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go

/**
 * Created by fancy on 2017/6/9.
 * Copyright © 2017 O2. All rights reserved.
 */

class AppFragment: BaseMVPViewPagerFragment<AppContract.View, AppContract.Presenter>(), AppContract.View {
    override var mPresenter: AppContract.Presenter = AppPresenter()
    override fun layoutResId(): Int = R.layout.fragment_main_app


    val appBeanList = ArrayList<AppItemVO>()
    val adapter: CommonAdapter<AppItemVO> by lazy {
        object : CommonAdapter<AppItemVO>(activity, appBeanList, R.layout.item_app_list) {
            override fun convert(holder: ViewHolder?, app: AppItemVO?) {
                holder?.setText(R.id.app_name_id, app?.appName?:"")
                        ?.setImageViewBackground(R.id.app_id, app?.appImg ?: R.mipmap.ic_todo_more)
            }
        }
    }

    override fun initUI() {
        ApplicationEnum.values().map { appBeanList.add(AppItemVO(it.appName, it.key, it.iconResId)) }

        app_list_id.adapter = adapter
        app_list_id.setOnItemClickListener { _, _, position, _ ->
            when(appBeanList[position].appKey){
                ApplicationEnum.TASK.key -> activity.go<TaskListActivity>()
                ApplicationEnum.TASKCOMPLETED.key -> activity.go<TaskCompletedListActivity>()
                ApplicationEnum.READ.key -> activity.go<ReadListActivity>()
                ApplicationEnum.READCOMPLETED.key -> activity.go<ReadCompletedListActivity>()
                ApplicationEnum.BBS.key -> activity.go<BBSMainActivity>()
                ApplicationEnum.CMS.key -> activity.go<CMSIndexActivity>()
                ApplicationEnum.YUNPAN.key -> activity.go<CloudDriveActivity>()
                ApplicationEnum.MEETING.key -> activity.go<MeetingMainActivity>()
                ApplicationEnum.ATTENDANCE.key -> activity.go<AttendanceMainActivity>()
            }
        }

    }


    override fun lazyLoad() {

    }
}