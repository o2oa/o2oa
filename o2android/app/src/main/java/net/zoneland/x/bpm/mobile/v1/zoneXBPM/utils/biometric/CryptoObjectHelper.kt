package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric

import android.os.Build
import android.support.annotation.RequiresApi
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.hardware.fingerprint.FingerprintManager
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator


/**
 * Created by fancyLou on 2019/3/17.
 * Copyright © 2019 O2. All rights reserved.
 */

@RequiresApi(Build.VERSION_CODES.M)
class CryptoObjectHelper {

    // This can be key name you want. Should be unique for the app.
    val KEY_NAME = "net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.biometric.CryptoObjectHelper"

    // We always use this keystore on Android.
    val KEYSTORE_NAME = "AndroidKeyStore"

    // Should be no need to change these values.
    val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    val TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING


    var _keystore: KeyStore

    init {
        _keystore = KeyStore.getInstance(KEYSTORE_NAME)
        _keystore.load(null)
    }

    @Throws(Exception::class)
    fun buildCryptoObject(): FingerprintManager.CryptoObject {
        val cipher = createCipher(true)
        return FingerprintManager.CryptoObject(cipher)
    }

    @Throws(Exception::class)
    fun createCipher(retry: Boolean): Cipher {
        val key = getKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        try {
            cipher.init(Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE, key)
        } catch (e: KeyPermanentlyInvalidatedException) {
            _keystore.deleteEntry(KEY_NAME)
            if (retry) {
                createCipher(false)
            } else {
                throw Exception("Could not create the cipher for fingerprint authentication.", e)
            }
        }

        return cipher
    }

    @Throws(Exception::class)
    fun getKey(): Key {

        if (!_keystore.isKeyEntry(KEY_NAME)) {
            createKey()
        }

        return _keystore.getKey(KEY_NAME, null)
    }

    @Throws(Exception::class)
    fun createKey() {
        val keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME)
        val keyGenSpec = KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(ENCRYPTION_PADDING)
                .setUserAuthenticationRequired(true)
                .build()
        keyGen.init(keyGenSpec)
        keyGen.generateKey()
    }
}