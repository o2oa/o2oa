package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO

/**
 * Created by fancyLou on 2019-08-20.
 * Copyright © 2019 O2. All rights reserved.
 */

object ContactUnitAndIdentityPickerContract {
    interface View: BaseView {
        fun callbackResult(list: List<NewContactListVO>)
        fun backError(error:String)
    }
    interface Presenter: BasePresenter<View> {
        /**
         * @param parent 上级组织id
         * @param isLoadIdentity 是否加载组织下的身份列表
         */
        fun loadUnitWithParent(parent: String, isLoadIdentity: Boolean, topList: List<String>, orgType: String, dutyList: List<String>)
    }
}