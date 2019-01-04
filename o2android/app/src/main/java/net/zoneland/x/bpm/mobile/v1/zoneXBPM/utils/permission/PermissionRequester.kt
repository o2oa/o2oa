package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission

import android.app.Activity
import android.os.Build
import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by fancy on 2017/9/11.
 * Copyright © 2017 O2. All rights reserved.
 */


class PermissionRequester(activity: Activity) {

    val PERMISSION_FRAGMENT_TAG = "PermissionRequester"

    val fragment: PermissionRequestFragment by lazy {
        var myFragment = activity.fragmentManager.findFragmentByTag(PERMISSION_FRAGMENT_TAG)
        if (myFragment == null) {
            val newFragment = PermissionRequestFragment()
            val manager = activity.fragmentManager
            manager.beginTransaction()
                    .add(newFragment, PERMISSION_FRAGMENT_TAG)
                    .commitAllowingStateLoss()
            manager.executePendingTransactions()
            newFragment
        } else {
            myFragment as PermissionRequestFragment
        }
    }

    fun request(vararg permissions: String): Observable<PermissionResult> {
        val size = permissions.size
        if (size < 1) {
            throw IllegalArgumentException("request Permission is empty!!!")
        }
        return Observable.just("").compose(transformer(permissions))
    }

    fun requestEach(vararg permissions: String): Observable<Permission> {
        if (permissions.size < 1) {
            throw IllegalArgumentException("request Permission is empty!!!")
        }
        return Observable.just("").compose(transformerEach(permissions))
    }

    private fun transformer(permissions: Array<out String>): Observable.Transformer<in String, out PermissionResult> {
        return Observable.Transformer<String, PermissionResult> {
            _ ->
            requestTrigger(permissions)
                    .toList()
                    //.buffer(permissions.size)
                    .flatMap { list ->
                        if (list.isEmpty()) {
                            return@flatMap Observable.empty<PermissionResult>()
                        } else {
                            var granted = true
                            var shouldShowRequestPermissionRationale = true
                            val deniedList = ArrayList<String>()
                            list.filter { !it.granted }.map {
                                granted = false
                                if(!it.shouldShowRequestPermissionRationale){
                                    shouldShowRequestPermissionRationale = false
                                }
                                deniedList.add(it.name)
                            }
                            return@flatMap Observable.just(PermissionResult(granted, shouldShowRequestPermissionRationale, deniedList))
                        }
                    }
        }
    }


    private fun transformerEach(permissions: Array<out String>): Observable.Transformer<String, Permission> {
        return Observable.Transformer<String, Permission> { _ ->
            requestTrigger(permissions)
        }
    }

    private fun requestTrigger(permissions: Array<out String>): Observable<Permission> {
        return Observable.just("").flatMap { _ ->
            requestImplementation(permissions)
        }
    }

    private fun requestImplementation(permissions: Array<out String>): Observable<Permission> {
        val list = ArrayList<Observable<Permission>>(permissions.size)
        val unRequestPermissionList = ArrayList<String>()
        for (permission in permissions) {
            if (isGranted(permission)) {
                list.add(Observable.just(Permission(permission, true, false)))
                continue
            }

            if (isRevoked(permission)) {
                list.add(Observable.just(Permission(permission, false, false)))
                continue
            }
            var subject: PublishSubject<Permission>? = fragment.getPublishSubject(permission)
            if (subject == null) {
                unRequestPermissionList.add(permission)
                subject = PublishSubject.create()
                fragment.setPublishSubject(permission, subject)
            }
            list.add(subject!!)
        }
        if (!unRequestPermissionList.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(unRequestPermissionList.toTypedArray())
            }
        }
        return Observable.concat(Observable.from(list))
    }

    private fun isGranted(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return fragment.isGranted(permission)
        } else {
            return true
        }
    }

    private fun isRevoked(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return fragment.isRevoked(permission)
        } else {
            return false
        }
    }


}