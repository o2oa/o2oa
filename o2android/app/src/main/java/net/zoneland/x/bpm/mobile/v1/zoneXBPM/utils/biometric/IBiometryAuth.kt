package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric

import android.os.CancellationSignal



/**
 * Created by fancyLou on 2019/3/17.
 * Copyright © 2019 O2. All rights reserved.
 */


interface IBiometryAuth {
    fun authenticate(cancel: CancellationSignal,
                     callback: OnBiometryAuthCallback)
}