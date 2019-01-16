package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.graphics.Bitmap
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BasePresenter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData

/**
 * Created by fancyLou on 2018/9/17.
 * Copyright © 2018 O2. All rights reserved.
 */

object TaskWorkSubmitDialogContract {

    interface View: BaseView {
        fun submitCallback(result: Boolean, site: String?)
    }
    interface Presenter: BasePresenter<View> {
        fun submit(sign: Bitmap?, data: TaskData?, workId: String, formData: String?)
    }
}