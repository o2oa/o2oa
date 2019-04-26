package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric

import android.app.Activity
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.hardware.fingerprint.FingerprintManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog


/**
 * Created by fancyLou on 2019/3/17.
 * Copyright © 2019 O2. All rights reserved.
 */


@RequiresApi(Build.VERSION_CODES.M)
class BiometryAuthAPI23(val mActivity: Activity) : IBiometryAuth {

    private val mFingerprintManager: FingerprintManager? by lazy {
        mActivity.getSystemService(FingerprintManager::class.java)
    }
    private val mFmAuthCallback = FingerprintAuthCallback()

    private var mDialog: BiometricPromptDialog? = null
    private var mCancellationSignal: CancellationSignal? = null
    private var mOnBiometryAuthCallback: OnBiometryAuthCallback? = null


    override fun authenticate(cancel: CancellationSignal, callback: OnBiometryAuthCallback) {
        mOnBiometryAuthCallback = callback
        mDialog = BiometricPromptDialog.newInstance()
        mDialog?.setOnBiometricPromptDialogActionCallback(object : BiometricPromptDialog.OnBiometricPromptDialogActionCallback {
            override fun onDialogDismiss() {
                //当dialog消失的时候，包括点击userPassword、点击cancel、和识别成功之后
                if (mCancellationSignal != null && !mCancellationSignal!!.isCanceled) {
                    mCancellationSignal!!.cancel()
                }
            }

            override fun onUsePassword() {
                //一些情况下，用户还可以选择使用密码
                if (mOnBiometryAuthCallback != null) {
                    mOnBiometryAuthCallback?.onUseFallBack()
                }
            }

            override fun onCancel() {
                //点击cancel键
                if (mOnBiometryAuthCallback != null) {
                    mOnBiometryAuthCallback?.onCancel()
                }
            }
        })
        mDialog?.show(mActivity.fragmentManager, "BiometryAuthAPI23")
        mCancellationSignal = cancel
        if (mCancellationSignal == null) {
            mCancellationSignal = CancellationSignal()
        }
        mCancellationSignal?.setOnCancelListener { mDialog?.dismiss() }

        try {
            val cryptoObjectHelper = CryptoObjectHelper()
            mFingerprintManager?.authenticate(
                    cryptoObjectHelper.buildCryptoObject(), mCancellationSignal,
                    0, mFmAuthCallback, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun isHardwareDetected(): Boolean {
        return mFingerprintManager?.isHardwareDetected == true
    }

    fun hasEnrolledFingerprints(): Boolean {
        return  mFingerprintManager?.hasEnrolledFingerprints() == true
    }



    inner class FingerprintAuthCallback : FingerprintManager.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            XLog.error( "onAuthenticationError() called with: errorCode = [$errorCode], errString = [$errString]")
            mDialog?.setState(BiometricPromptDialog.STATE_ERROR)
            mOnBiometryAuthCallback?.onError(errorCode, errString.toString())
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            XLog.error( "onAuthenticationFailed() called")
            mDialog?.setState(BiometricPromptDialog.STATE_FAILED)
            mOnBiometryAuthCallback?.onFailed()
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
            super.onAuthenticationHelp(helpCode, helpString)
            XLog.debug( "onAuthenticationHelp() called with: helpCode = [$helpCode], helpString = [$helpString]")
            mDialog?.setState(BiometricPromptDialog.STATE_FAILED)
            mOnBiometryAuthCallback?.onFailed()

        }

        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            XLog.info(  "onAuthenticationSucceeded: ")
            mDialog?.setState(BiometricPromptDialog.STATE_SUCCEED)
            mOnBiometryAuthCallback?.onSucceeded()

        }
    }



}