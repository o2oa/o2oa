package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * Created by fancyLou on 2019-08-09.
 * Copyright © 2019 O2. All rights reserved.
 */


object ContactPersonGroupActivityContract {
    interface View : BaseView {
        fun callbackResult(list: List<NewContactListVO>)
        fun backError(error:String)
    }

    interface Presenter : BasePresenter<View> {
        /**
         * @param mode 查询模式 group还是person
         */
        fun findListByPage(mode: String, lastId:String)
    }
}