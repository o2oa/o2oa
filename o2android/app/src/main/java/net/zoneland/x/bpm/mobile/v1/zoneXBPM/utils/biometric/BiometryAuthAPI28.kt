package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric

import android.app.Activity
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi

/**
 * Created by fancyLou on 2019/3/17.
 * Copyright © 2019 O2. All rights reserved.
 */



//@RequiresApi(Build.VERSION_CODES.P)
class BiometryAuthAPI28(mActivity: Activity): IBiometryAuth {


    override fun authenticate(cancel: CancellationSignal, callback: OnBiometryAuthCallback) {

    }

}