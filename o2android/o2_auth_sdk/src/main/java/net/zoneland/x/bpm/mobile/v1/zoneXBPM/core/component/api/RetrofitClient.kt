package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.APIDistributeTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.DownloadBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.DownloadProgressHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.ProgressListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.ProgressResponseBody
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.HttpCacheUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.HttpsTrustManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit


/**
 * Created by fancy on 2017/6/5.
 */

class RetrofitClient private constructor() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: RetrofitClient? = null

        fun instance(): RetrofitClient {
            if (INSTANCE == null) {
                INSTANCE = RetrofitClient()
            }
            return INSTANCE!!
        }
    }


    private val gson: Gson by lazy { GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create() }
    private val helper: APIAddressHelper by lazy { APIAddressHelper.instance() }
    private lateinit var o2HttpClient: OkHttpClient
    private lateinit var o2WebSocketClient: OkHttpClient
    private lateinit var httpClientOutSide: OkHttpClient
    private lateinit var context: Context


    /*在Application onCreate 中初始化 */
    fun init(context: Context) {
        this.context = context
        val httpProtocol = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_HTTP_PROTOCOL_KEY, "")
        o2HttpClient = if (!TextUtils.isEmpty(httpProtocol) && httpProtocol == "https") {
            OkHttpClient.Builder()
                    .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory())
                    .hostnameVerifier { _, _ -> true }
                    .cache(HttpCacheUtil.getOkHttpCacheInstance(context))
                    .addInterceptor(O2Interceptor())
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS).build()
        } else {
            OkHttpClient.Builder()
                    .cache(HttpCacheUtil.getOkHttpCacheInstance(context))
                    .addInterceptor(O2Interceptor())
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS).build()
        }
        httpClientOutSide = OkHttpClient.Builder()
                .cache(HttpCacheUtil.getOkHttpCacheInstance(context))
                .connectTimeout(60, TimeUnit.SECONDS).build()
        o2WebSocketClient = OkHttpClient.Builder()
                //设置读取超时时间
                .readTimeout(3, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(3, TimeUnit.SECONDS)
                //设置连接超时时间
                .connectTimeout(3, TimeUnit.SECONDS)
                .build()
    }


    /**
     * o2 http client
     */
    fun getO2HttpClient() : OkHttpClient? {
        if (::o2HttpClient.isInitialized) {
            return o2HttpClient
        }
        return null
    }




    /**
     * 设置o2服务器的http协议
     */
    fun setO2ServerHttpProtocol(httpProtocol: String) {
        o2HttpClient = if (httpProtocol == "https") {
            OkHttpClient.Builder()
                    .sslSocketFactory(HttpsTrustManager.createSSLSocketFactory())
                    .hostnameVerifier { _, _ -> true }
                    .cache(HttpCacheUtil.getOkHttpCacheInstance(context))
                    .addInterceptor(O2Interceptor())
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS).build()
        } else {
            OkHttpClient.Builder()
                    .cache(HttpCacheUtil.getOkHttpCacheInstance(context))
                    .addInterceptor(O2Interceptor())
                    .retryOnConnectionFailure(true)
                    .connectTimeout(60, TimeUnit.SECONDS).build()
        }
    }

    /**
     * 开启webSocket链接
     */
    fun openWebSocket(listener: WebSocketListener) {
        val request = Request.Builder().url(APIAddressHelper.instance().webSocketUrl()).build()
        o2WebSocketClient.newWebSocket(request, listener)
    }

    /**
     * 关闭webSocket链接
     */
    fun closeWebSocket() {
        o2WebSocketClient.dispatcher().executorService().shutdown()
    }


    fun updateApiService(url: String, builder: OkHttpClient.Builder): PgyUpdateApiService {
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .client(builder.build())
                .build()
        return retrofit.create(PgyUpdateApiService::class.java)
    }

    private val TULING123_BASE_URL = "http://www.tuling123.com/openapi/"

    fun tuling123Service(): Tuling123Service {
        val retrofit = Retrofit.Builder()
                .baseUrl(TULING123_BASE_URL)
                .client(httpClientOutSide)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(Tuling123Service::class.java)
    }

    /**
     * @param baseUrl http://dev.o2oa.io:8888/x_faceset_control/
     */
    fun faceppApiService(baseUrl: String): FaceppApiService {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClientOutSide)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(FaceppApiService::class.java)
    }

    /**
     * 下载皮肤服务
     */
    fun skinDownloadService(url: String, handler: DownloadProgressHandler): SkinDownLoadService {
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .client(progressClient(handler).build())
                .build()
        return retrofit.create(SkinDownLoadService::class.java)
    }

    /**
     * 带进度客户端
     */
    private fun progressClient(handler: DownloadProgressHandler): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        val listener = ProgressListener { progress, total, done ->
            val bean = DownloadBean()
            bean.bytesRead = progress
            bean.contentLength = total
            bean.isDone = done
            handler.sendMessage(bean)
        }
        //添加拦截器，自定义ResponseBody，添加下载进度
        builder.networkInterceptors()
                .add(Interceptor { chain ->
                    val xToken = O2SDKManager.instance().zToken
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                    if (!TextUtils.isEmpty(xToken)) {
                        requestBuilder.addHeader("x-token", xToken)
                    }
                    val originalHttpUrl = original.url()
                    val url = originalHttpUrl.newBuilder().addQueryParameter("o", (Math.random()*100).toString()).build()
                    val request = requestBuilder.addHeader("x-client", O2.DEVICE_TYPE)
                            .method(original.method(), original.body())
                            .url(url).build()

                    val originalResponse = chain.proceed(request)
            originalResponse.newBuilder().body(
                    ProgressResponseBody(originalResponse.body(), listener))
                    .build()
        })

        return builder
    }

    /**
     * o2platform 中心服务器
     * @return
     */
    fun collectApi(): CollectService {
        val retrofit = Retrofit.Builder()
                .baseUrl(O2.O2_COLLECT_URL)
                .client(httpClientOutSide)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(CollectService::class.java)
    }

    /**
     * center服务 获取各个应用地址的服务
     * @param baseUrl
     * @return
     */
    fun api(baseUrl: String): ApiService {
        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(ApiService::class.java)
    }


    /**
     * 门户模块
     */
    fun portalAssembleSurfaceService(): PortalAssembleSurfaceService? {
        return try {
            val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_portal_assemble_surface)
            val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .client(o2HttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
            retrofit.create(PortalAssembleSurfaceService::class.java)
        } catch (e: Exception) {
            null
        }
    }


    /**
     * 新版人员组织管理
     */
    fun organizationAssembleControlApi(): OrganizationAssembleControlAlphaService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_organization_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(OrganizationAssembleControlAlphaService::class.java)

    }


    /**
     * 查询模块
     */
    fun queryAssembleSurfaceServiceAPI(): QueryAssembleSurfaceService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_query_assemble_surface)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(QueryAssembleSurfaceService::class.java)

    }



    /**
     * 人员管理
     * @return
     */
    fun assemblePersonalApi(): OrgAssemblePersonalService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_organization_assemble_personal)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(OrgAssemblePersonalService::class.java)
    }


    /**
     * 认证
     * @return
     */
    fun assembleAuthenticationApi(): OrgAssembleAuthenticationService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_organization_assemble_authentication)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(OrgAssembleAuthenticationService::class.java)
    }

    /**
     * 组织
     * @return
     */
    fun assembleExpressApi(): OrgAssembleExpressService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_organization_assemble_express)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(OrgAssembleExpressService::class.java)
    }

    /**
     * 流程
     * @return
     */
    fun processAssembleSurfaceServiceAPI(): ProcessAssembleSurfaceService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_processplatform_assemble_surface)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(ProcessAssembleSurfaceService::class.java)
    }

    /**
     * 云盘
     * @return
     */
    fun fileAssembleControlApi(): FileAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_file_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(FileAssembleControlService::class.java)
    }

    /**
     * 新版云盘服务
     *
     */
    fun cloudFileControlApi(): CloudFileControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_file_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(CloudFileControlService::class.java)
    }

    /**
     * 会议室管理
     * @return
     */
    fun meetingAssembleControlApi(): MeetingAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_meeting_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(MeetingAssembleControlService::class.java)
    }

    /**
     * 考勤管理
     * @return
     */
    fun attendanceAssembleControlApi(): AttendanceAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_attendance_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(AttendanceAssembleControlService::class.java)
    }

    /**
     * 论坛
     * @return
     */
    fun bbsAssembleControlServiceApi(): BBSAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_bbs_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                //                .addConverterFactory(MyGsonConverterFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(BBSAssembleControlService::class.java)
    }

    /**
     * 热图
     * @return
     */
    fun hotpicAssembleControlServiceApi(): HotpicAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_hotpic_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(HotpicAssembleControlService::class.java)
    }


    /**
     * 内容管理
     * @return
     */
    fun cmsAssembleControlService(): CMSAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_cms_assemble_control)
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(CMSAssembleControlService::class.java)
    }

    /**
     * 公共配置服务
     * @return
     */
    fun organizationAssembleCustomService(): OrganizationAssembleCustomService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_organization_assemble_custom)
        if (TextUtils.isEmpty(url)) {
            throw NullPointerException("没有公共服务模块！！！！")
        }
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofit.create(OrganizationAssembleCustomService::class.java)
    }

    /**
     * 日程
     */
    fun calendarAssembleControlService(): CalendarAssembleControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_calendar_assemble_control)
        val retrofitClient = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofitClient.create(CalendarAssembleControlService::class.java)
    }


    fun jPushControlService(): JPushControlService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_jpush_assemble_control)
        val retrofitClient = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofitClient.create(JPushControlService::class.java)
    }

    /**
     * 消息服务
     */
    fun messageCommunicateService(): MessageCommunicateService {
        val url = helper.getAPIDistribute(APIDistributeTypeEnum.x_message_assemble_communicate)
        val retrofitClient = Retrofit.Builder()
                .baseUrl(url)
                .client(o2HttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        return retrofitClient.create(MessageCommunicateService::class.java)
    }


}