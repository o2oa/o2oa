package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */


interface OrgAssembleAuthenticationService{


    /**
     * 登录状态
     */
    @GET("jaxrs/authentication/mode")
    fun loginMode(): Observable<ApiResponse<LoginModeData>>

    /**
     * 登陆
     * @param json
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/authentication")
    fun login(@Body json: Map<String, String>): Observable<ApiResponse<AuthenticationInfoJson>>


    /**
     * 人脸识别的时候登录： body = {"client":"oa","token":"6OH6cis+8Y9yrqBReLOB+XLCpatZAyVY+PY0cPj301s="}
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/sso")
    fun sso(@Body body: Map<String, String>): Observable<ApiResponse<AuthenticationInfoJson>>


    /**
     * 检查当前用户
     * @return
     */
    @GET("jaxrs/authentication")
    fun who(@Header("x-token") token: String): Observable<ApiResponse<AuthenticationInfoJson>>

    /**
     * 用手机验证码登录
     * @param json credential=xxxx,codeAnswer=xxxx,使用短信验证码登录.
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/authentication/code")
    fun loginWithPhoneCode(@Body json: Map<String, String>): Observable<ApiResponse<AuthenticationInfoJson>>


    /**
     * 获取登录验证码
     * @param credential
     * *
     * @return
     */
    @GET("jaxrs/authentication/code/credential/{credential}")
    fun getPhoneCode(@Path("credential") credential: String): Observable<ApiResponse<ValueData>>


    /**
     * 获取图片验证码
     */
    @GET("jaxrs/authentication/captcha/width/{width}/height/{height}")
    fun getCaptchaCodeImg(@Path("width") width: Int, @Path("height") height: Int): Observable<ApiResponse<CaptchaImgData>>


    /**
     * 用图片验证码登录
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/authentication/captcha")
    fun loginWithCaptchaCode(@Body form:LoginWithCaptchaForm): Observable<ApiResponse<AuthenticationInfoJson>>


    /**
     * 扫一扫 确认登录
     * @param meta
     * *
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/authentication/bind/meta/{meta}")
    fun scanConfirmWebLogin(@Path("meta") meta: String): Observable<ApiResponse<AuthenticationInfoJson>>

    /**
     * 获取RSA 加密公钥
     */
    @GET("jaxrs/authentication/captchaRSAPublicKey")
    fun getRSAPublicKey(): Observable<ApiResponse<RSAPublicKeyData>>


    /**
     * 登出
     * @return
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @DELETE("jaxrs/authentication")
    fun logout(): Observable<ApiResponse<IdData>>


}