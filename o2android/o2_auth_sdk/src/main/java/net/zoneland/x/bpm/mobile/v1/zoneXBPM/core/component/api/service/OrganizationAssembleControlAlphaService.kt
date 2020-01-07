package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.ApiResponse
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.group.O2Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonListLikeForm
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.UnitJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.unit.UnitListForm
import retrofit2.http.*
import rx.Observable

/**
 * Created by fancy on 2017/7/10.
 * Copyright © 2017 O2. All rights reserved.
 */


interface OrganizationAssembleControlAlphaService {

    /**
     * 列示顶层组织
     */
    @GET("jaxrs/unit/list/top")
    fun unitListTop() : Observable<ApiResponse<List<UnitJson>>>

    /**
     * 列示某组织下的子组织列表
     * @param unit 组织
     */
    @GET("jaxrs/unit/list/{unit}/sub/direct")
    fun unitSubDirectList(@Path("unit") unit: String): Observable<ApiResponse<List<UnitJson>>>

    /**
     * 根据请求id返回组织列表
     * @param body {"unitList":[]}
     */
    @POST("jaxrs/unit/list")
    fun unitList(@Body body: UnitListForm): Observable<ApiResponse<List<UnitJson>>>


    /**
     * 根据组织类型查询组织， 如果没有组织类型 用上面两个接口 ，有组织类型就用这个接口
     * body：{"type":"一级部门","unitList":[]}
     */
    @PUT("jaxrs/unit/list/unit/type")
    fun unitListByType(@Body body: UnitListForm): Observable<ApiResponse<List<UnitJson>>>

    /**
     * 根据给定的人员,列示其所有的身份.
     * @param person 人员
     */
    @GET("jaxrs/identity/list/person/{person}")
    fun identityListWithPerson(@Path("person") person:String): Observable<ApiResponse<List<IdentityJson>>>

    /**
     * 列示某组织下的身份列表
     * @param unit 组织
     */
    @GET("jaxrs/identity/list/unit/{unit}")
    fun identityListWithUnit(@Path("unit") unit:String): Observable<ApiResponse<List<IdentityJson>>>


    /**
     * 个人详细信息
     */
    @GET("jaxrs/person/{person}")
    fun person(@Path("person") person: String): Observable<ApiResponse<PersonJson>>

    /**
     * 模糊查询
     * key 支持 中文 拼音等
     */
    @PUT("jaxrs/person/list/like")
    fun personListLike(@Body body: PersonListLikeForm): Observable<ApiResponse<List<PersonJson>>>


    /**
     * 	查找人员所在的群组,包括嵌套的下级群组.
     */
    @GET("jaxrs/group/list/person/{person}/sup/nested")
    fun groupListWithPerson(@Path("person") person: String): Observable<ApiResponse<List<O2Group>>>

    /**
     * 分页查询群组列表
     */
    @GET("jaxrs/group/list/{lastId}/next/{pageSize}")
    fun groupListByPage(@Path("lastId") lastId: String, @Path("pageSize") pageSize: Int): Observable<ApiResponse<List<O2Group>>>

    /**
     * 分页查询人员
     */
    @GET("jaxrs/person/list/{lastId}/next/{pageSize}")
    fun personListByPage(@Path("lastId") lastId: String, @Path("pageSize") pageSize: Int): Observable<ApiResponse<List<PersonJson>>>

}