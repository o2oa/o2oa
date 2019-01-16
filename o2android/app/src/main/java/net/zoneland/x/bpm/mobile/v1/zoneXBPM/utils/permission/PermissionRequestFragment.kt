package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission

import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import rx.subjects.PublishSubject

/**
 * Created by fancy on 2017/9/11.
 * Copyright © 2017 O2. All rights reserved.
 */


class PermissionRequestFragment : Fragment() {


    val REQUEST_PERMISSION_CODE = 10024
    val mSubjects = HashMap<String, PublishSubject<Permission>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    /**
     * 请求权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissions(permissions: Array<out String>) {
        requestPermissions(permissions, REQUEST_PERMISSION_CODE)
    }

    /**
     * 权限请求反馈处理
     */
    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions == null || grantResults == null) {
            return
        }
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                permissions.mapIndexed { index, permission ->
                    val subject = mSubjects[permission]
                    if (subject != null) {
                        Log.d("permissionFragment", "subject is here $permission")
                        mSubjects.remove(permission)
                        val granted = (grantResults[index] == PackageManager.PERMISSION_GRANTED)
                        subject.onNext(Permission(permission, granted, shouldShowRequestPermissionRationale(permission)))
                        subject.onCompleted()
                    }else {
                        Log.e("permissionFragment", "subject is null $permission")
                    }
                }

            }
        }
    }

    /**
     * 是否有权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun isGranted(permission: String): Boolean {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 是否注册了权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun isRevoked(permission: String): Boolean {
        return activity.packageManager.isPermissionRevokedByPolicy(permission, activity.packageName)
    }


    fun setPublishSubject(permission:String, subject: PublishSubject<Permission>) : PublishSubject<Permission>? {
        return mSubjects.put(permission, subject)
    }

    fun containPublishSubject(permission: String) :Boolean {
        return mSubjects.containsKey(permission)
    }
    fun getPublishSubject(permission: String): PublishSubject<Permission>? {
        return mSubjects[permission]
    }


}