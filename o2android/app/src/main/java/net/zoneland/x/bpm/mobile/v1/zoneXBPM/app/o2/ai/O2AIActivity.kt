package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.baidu.android.tts.InitConfig
import com.baidu.android.tts.MySyntherizer
import com.baidu.android.tts.OfflineResource
import com.baidu.android.voicedemo.control.MyRecognizer
import com.baidu.speech.asr.SpeechConstant
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.readystatesoftware.systembartint.SystemBarTintManager
import kotlinx.android.synthetic.main.activity_o2_ai.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog
import java.io.IOException

class O2AIActivity : AppCompatActivity(), O2AIContract.View {

    companion object {
        val HANDLER_ACTION_SPEECH_FINISH = 0
        val HANDLER_ACTION_SPEECH_ERROR = 1
        val HANDLER_ACTION_LISTEN_FINISH = 2
        val HANDLER_ACTION_LISTEN_ERROR = 3

    }
    private val mPresenter = O2AIPresenter()
    private val handler = Handler {
        val what = it.what
        val message = it.obj
        when (what) {
            HANDLER_ACTION_SPEECH_FINISH -> {
                mPresenter.speakFinish(message as String)
            }
            HANDLER_ACTION_SPEECH_ERROR -> {
                mPresenter.speakError(message as String)
            }
            HANDLER_ACTION_LISTEN_FINISH -> {
                mPresenter.listenFinish(message as String)
            }
            HANDLER_ACTION_LISTEN_ERROR -> {
                XLog.error("message:$message")
                mPresenter.listenError()
            }
            else -> {
                XLog.info("what:$what, message:$message")
            }
        }
        return@Handler true
    }
    private var myRecognizer: MyRecognizer? = null
    private val statusListener: AsrRecognizeStatusListener by lazy { AsrRecognizeStatusListener(handler) }
    private var synthesizer: MySyntherizer? = null
    private var initStatus = false
    private var loadingDialog: LoadingDialog? = null

    fun showLoadingDialog() {
        if (loadingDialog==null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }
    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }


    override fun getContext(): Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o2_ai)
        showLoadingDialog()
        val tintManager = SystemBarTintManager(this)
        tintManager.isStatusBarTintEnabled = true
        tintManager.setStatusBarTintResource(R.color.z_color_black)
        mPresenter.attachView(this)
        image_o2_ai_back.setOnClickListener { finish() }
        tv_o2_ai_voice_transaction.text = ""
        val audioService = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            XToast.toastShort(this, "你的手机好像没有开启声音，请打开声音！")
        }
    }

    override fun onResume() {
        super.onResume()
        if (!initStatus) {
            Handler().postDelayed({
                try {
                    myRecognizer = MyRecognizer(this, statusListener)
                } catch (e: RuntimeException) {
                    XLog.error("", e)
                }
                initTTS()
                hideLoadingDialog()
                circle_ripple.visible()
                mPresenter.reInitial()
                speak("你好：${O2SDKManager.instance().cName}，需要我为您做些什么?",  mPresenter.generateListenCommand())
                initStatus = true
            }, 800)
        }else {
            mPresenter.reInitial()
            speak("你好：${O2SDKManager.instance().cName}，需要我为您做些什么?",  mPresenter.generateListenCommand())
        }
    }

    override fun onPause() {
        super.onPause()
        myRecognizer?.cancel()
    }

    override fun onDestroy() {
        releaseRecord()
        synthesizer?.release()
        mPresenter.detachView()
        super.onDestroy()

    }

    override fun beginListen() {
        XLog.info("beginListen....................")
        circle_ripple.start()
        startRecord()
    }

    override fun speak(message:String, id: String) {
        XLog.info("speak ......$message")
        tv_o2_ai_voice_transaction.text = message
        circle_ripple.stop()
        stopRecord()
        synthesizer?.speak(message, id)
    }


    override fun finishAI() {
        finish()
    }



    private fun startRecord() {
        XLog.info("start Record.......................")
        val param = HashMap<String, Any>().apply {
            put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false)
        }
        for (entry in param.entries) {
            XLog.info("key:" + entry.key + ", value:" + entry.value)
        }
        myRecognizer?.start(param)
    }

    private fun stopRecord() {
        myRecognizer?.stop()
    }

    private fun releaseRecord() {
        myRecognizer?.release()
    }



    private fun initTTS() {
        val listener = TTSListener(handler)
        val config = InitConfig(O2App.instance.BAIDU_APP_ID, O2App.instance.BAIDU_APP_KEY,
                O2App.instance.BAIDU_SECRET_KEY, TtsMode.MIX, getParams(), listener)
        synthesizer = MySyntherizer(this, config)
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    private fun getParams(): Map<String, String> {
        val params = java.util.HashMap<String, String>()
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params[SpeechSynthesizer.PARAM_SPEAKER] = "0"
        // 设置合成的音量，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_VOLUME] = "9"
        // 设置合成的语速，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_SPEED] = "5"
        // 设置合成的语调，0-9 ，默认 5
        params[SpeechSynthesizer.PARAM_PITCH] = "5"

        params[SpeechSynthesizer.PARAM_MIX_MODE] = SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线发音选择，VOICE_FEMALE即为离线女声发音。
        // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
        // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        val offlineResource = createOfflineResource(OfflineResource.VOICE_FEMALE)
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        if (offlineResource != null) {
            params[SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE] = offlineResource.textFilename
            params[SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE] = offlineResource.modelFilename
        }
        return params
    }


    private fun createOfflineResource(voiceType: String): OfflineResource? {
        var offlineResource: OfflineResource? = null
        try {
            offlineResource = OfflineResource(this, voiceType)
        } catch (e: IOException) {
            // IO 错误自行处理
            e.printStackTrace()
            XLog.error("【error】:copy files from assets failed." + e.message)
        }

        return offlineResource
    }
}
