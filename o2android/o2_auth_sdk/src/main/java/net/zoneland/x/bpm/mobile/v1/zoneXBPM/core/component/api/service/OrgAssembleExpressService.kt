package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityLevelForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.UnitDutyIdentityForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonList
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonUnitList
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.UnitJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable


/**
 * Created by fancy on 2017/6/6.
 */

interface OrgAssembleExpressService {


    /**
     * 上下查询 某个人的所属组织 包括递归
     * @param personList  如： {"personList":["楼国栋"]}
     */
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("jaxrs/unit/list/person/sup/nested")
    fun listUnitPersonSup(@Body personList: RequestBody): Observable<ApiResponse<PersonUnitList>>


    /**
     * 获取下级部门列表
     * @param deptName
     * *
     * @return
     */
    @GET("jaxrs/complex/department/{deptName}")
    fun getDepartmentSubList(@Path("deptName") deptName: String): Observable<ApiResponse<ContactDataBean>>

    /**
     * 查询个人的直接群组对象
     * @param personName 姓名
     * *
     * @return
     */
    @GET("jaxrs/group/list/person/{personName}/sup/direct")
    fun getPersonalGroupList(@Path("personName") personName: String): Observable<ApiResponse<List<PersonalGroupData>>>

    /**
     * 批量获取person的DN
     * @param body {"personList":["4b9df3c7-c0fa-4b64-9f98-034da8bb8f92"]}
     */
    @POST("jaxrs/person/list")
    fun searchPersonDNList(@Body personList: PersonList): Observable<ApiResponse<PersonList>>
    /**
     * 中文姓名模糊查询
     * @param name 姓名
     * *
     * @return
     */
    @GET("jaxrs/person/list/like/{name}")
    fun searchPersonByName(@Path("name") name: String): Observable<ApiResponse<List<SearchPersonData>>>

    /**
     * 分页查询人员
     * 人员选择器使用
     * @param lastId
     * *
     * @param limit
     * *
     * @return
     */
    @GET("jaxrs/person/list/{lastId}/next/{limit}")
    fun searchPersonByPage(@Path("lastId") lastId: String, @Path("limit") limit: Int): Observable<ApiResponse<List<SearchPersonData>>>

    /**
     * 根据拼音或首字母查询通讯录
     * @param name 拼音或者首字母
     * *
     * @return
     */
    @GET("jaxrs/person/list/like/pinyin/{name}")
    fun searchPersonByPinyin(@Path("name") name: String): Observable<ApiResponse<List<SearchPersonData>>>

    /**
     * 查询群组详细
     * 成员列表等
     * @param groupName
     * *
     * @return
     */
    @GET("jaxrs/group/{groupName}")
    fun getGroupInfoData(@Path("groupName") groupName: String): Observable<ApiResponse<PersonalGroupData>>

    /**
     * 获取个人详细信息
     * 包含企业信息
     * @param name
     * *
     * @return
     */
    @GET("jaxrs/complex/person/{name}")
    fun getPersonInfoData(@Path("name") name: String): Observable<ApiResponse<PersonInfoData>>


    /**
     * 获取当前登录用户的部门公司等信息
     * 放入本地存储 方便后续使用
     * @param person
     * *
     * @return
     */
    @GET("jaxrs/department/list/person/{person}")
    fun getLoginPersonDeptAndCompany(@Path("person") person: String): Observable<ApiResponse<List<PersonDeptData>>>


    /**
     * 获取用户属性
     * @param attribute 如： 直接主管
     * *
     * @param person 如： 胡起
     * *
     * @return
     */
    @GET("jaxrs/personattribute/{attribute}/person/{person}")
    fun personattribute(@Path("attribute") attribute: String, @Path("person") person: String): Observable<ApiResponse<PersonattributeData>>

    /**
     * 根据identity获取用户详细
     * @param identity 如：胡起(开发部)
     * *
     * @return
     */
    @GET("jaxrs/person/identity/{identity}")
    fun personIdentity(@Path("identity") identity: String): Observable<ApiResponse<PersonalData>>


    /**
     * 获取用户的身份
     * @param person
     * *
     * @return
     */
    @GET("jaxrs/identity/list/person/{person}")
    fun personIdentityByPerson(@Path("person") person: String): Observable<ApiResponse<List<PersonIdentityData>>>


    /**
     * 根据职务、组织查询身份列表
     * @param body {"nameList":["测试职务"],"unit":"人力资源部@2812170000@U"}
     */
    @POST("jaxrs/unitduty/list/identity/unit/name/object")
    fun identityListByUnitAndDuty(@Body body: UnitDutyIdentityForm): Observable<ApiResponse<List<IdentityJson>>>



    /**
     * 根据身份查询和层级查询组织
     * @param body identity level
     */
    @POST("jaxrs/unit/identity/level/object")
    fun unitByIdentityAndLevel(@Body body: IdentityLevelForm): Observable<ApiResponse<UnitJson>>
}