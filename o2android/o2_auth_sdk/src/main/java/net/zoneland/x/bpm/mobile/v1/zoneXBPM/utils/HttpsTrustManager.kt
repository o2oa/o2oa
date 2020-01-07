package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by fancyLou on 12/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class HttpsTrustManager: X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()


    companion object {
        fun  createSSLSocketFactory(): SSLSocketFactory? {
            return try {
                val sc = SSLContext.getInstance("TLS")
                sc.init(null, arrayOf(HttpsTrustManager()),  SecureRandom())
                sc.socketFactory
            } catch ( e:Exception) {
                null
            }
        }
    }


}