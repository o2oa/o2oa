package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric

import android.app.Activity
import android.os.Build
import android.os.CancellationSignal
import android.app.KeyguardManager
import android.content.Context




/**
 * Created by fancyLou on 2019/3/17.
 * Copyright © 2019 O2. All rights reserved.
 */


class BiometryManager {

    private var mActivity: Activity
    private var mImpl : IBiometryAuth? = null

    constructor(activity: Activity) {
        mActivity = activity
        if (isAboveApi23()) {
            mImpl =  BiometryAuthAPI23(activity)
        }
    }

    fun authenticate(callback: OnBiometryAuthCallback) {
        mImpl?.authenticate(CancellationSignal(), callback)
    }

    fun authenticate(cancel: CancellationSignal,
                     callback: OnBiometryAuthCallback) {
        mImpl?.authenticate(cancel, callback)
    }

    fun isBiometricPromptEnable(): Boolean {
        return (isAboveApi23()
                && isHardwareDetected()
                && hasEnrolledFingerprints()
                && isKeyguardSecure())
    }

    private fun hasEnrolledFingerprints(): Boolean {
        return if (isAboveApi23()) {
            (mImpl as BiometryAuthAPI23).hasEnrolledFingerprints()
        } else {
            false
        }
    }

    private fun isHardwareDetected(): Boolean {
        return if (isAboveApi23()) {
            (mImpl as BiometryAuthAPI23).isHardwareDetected()
        } else {
            false
        }
    }


    private fun isKeyguardSecure(): Boolean {
        val keyguardManager = mActivity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardSecure

    }
    private fun isAboveApi23(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }




}








interface OnBiometryAuthCallback {

    fun onUseFallBack()

    fun onSucceeded()

    fun onFailed()

    fun onError(code: Int, reason: String)

    fun onCancel()

}