package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

/**
 * Created by fancy on 2017/6/5.
 */


interface BasePresenter<in V : BaseView> {
    fun attachView(view: V)
    fun detachView()
}