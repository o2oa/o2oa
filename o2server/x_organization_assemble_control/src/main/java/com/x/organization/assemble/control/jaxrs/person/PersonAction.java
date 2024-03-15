package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("person")
@JaxrsDescribe("个人操作")
public class PersonAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(PersonAction.class);

    @JaxrsMethodDescribe(value = "获取个人,附带身份,身份所在的组织,个人所在群组,个人拥有角色.", action = ActionGet.class)
    @GET
    @Path("{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "创建个人.", action = ActionCreate.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionCreate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCreate().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "更新个人.", action = ActionEdit.class)
    @PUT
    @Path("{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "更新个人mockputtopost.", action = ActionEdit.class)
    @POST
    @Path("{flag}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "删除个人.", action = ActionDelete.class)
    @DELETE
    @Path("{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除个人mockdeletetoget.", action = ActionDelete.class)
    @GET
    @Path("{flag}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除个人关联信息（身份、角色等），但保留人员（企业离退休人员需要能登录平台但不能做业务处理）.", action = ActionReserveDelete.class)
    @DELETE
    @Path("{flag}/reserve")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void reserveDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionReserveDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionReserveDelete().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除个人关联信息（身份、角色等），但保留人员（企业离退休人员需要能登录平台但不能做业务处理）.", action = ActionReserveDelete.class)
    @GET
    @Path("{flag}/reserve/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void reserveDeleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionReserveDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionReserveDelete().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示个人,下一页.", action = ActionListNext.class)
    @GET
    @Path("list/{flag}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListNext().execute(effectivePerson, flag, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示个人,上一页.", action = ActionListPrev.class)
    @GET
    @Path("list/{flag}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPrev().execute(effectivePerson, flag, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据给定的群组,列示直接个人成员.", action = ActionListSubDirectWithGroup.class)
    @GET
    @Path("list/group/{groupFlag}/sub/direct")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listSubDirectWithGroup(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("群组标识") @PathParam("groupFlag") String groupFlag) {
        ActionResult<List<ActionListSubDirectWithGroup.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListSubDirectWithGroup().execute(effectivePerson, groupFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根根据给定的群组,列示嵌套的个人成员.", action = ActionListSubNestedWithGroup.class)
    @GET
    @Path("list/group/{groupFlag}/sub/nested")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listSubNestedWithGroup(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("群组标识") @PathParam("groupFlag") String groupFlag) {
        ActionResult<List<ActionListSubNestedWithGroup.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListSubNestedWithGroup().execute(effectivePerson, groupFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根根据给定的角色,列示个人成员.", action = ActionListWithRole.class)
    @GET
    @Path("list/role/{roleFlag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithRole(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("角色标识") @PathParam("roleFlag") String roleFlag) {
        ActionResult<List<ActionListWithRole.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithRole().execute(effectivePerson, roleFlag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取拼音首字母开始的个人.", action = ActionListPinyinInitial.class)
    @PUT
    @Path("list/pinyininitial")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPinyinInitial(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListPinyinInitial.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPinyinInitial().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "获取拼音首字母开始的个人mockputtopost.", action = ActionListPinyinInitial.class)
    @POST
    @Path("list/pinyininitial/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPinyinInitialMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListPinyinInitial.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPinyinInitial().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据名称进行模糊查询.", action = ActionListLike.class)
    @PUT
    @Path("list/like")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listLike(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListLike.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListLike().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据名称进行模糊查询mockputtopost.", action = ActionListLike.class)
    @POST
    @Path("list/like/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listLikeMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListLike.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListLike().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据拼音或者首字母进行模糊查询.", action = ActionListLikePinyin.class)
    @PUT
    @Path("list/like/pinyin")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listLikePinyin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListLikePinyin.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListLikePinyin().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "根据拼音或者首字母进行模糊查询mockputtopost.", action = ActionListLikePinyin.class)
    @POST
    @Path("list/like/pinyin/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listLikePinyinMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionListLikePinyin.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListLikePinyin().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "更新指定个人的密码.", action = ActionSetPassword.class)
    @PUT
    @Path("{flag}/set/password")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionSetPassword.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSetPassword().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "更新指定个人的密码mockputtopost.", action = ActionSetPassword.class)
    @POST
    @Path("{flag}/set/password/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPasswordMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionSetPassword.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSetPassword().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "重置个人的密码.", action = ActionResetPassword.class)
    @GET
    @Path("{flag}/reset/password")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识") @PathParam("flag") String flag) {
        ActionResult<ActionResetPassword.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionResetPassword().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "校验password密码等级.", action = ActionCheckPassword.class)
    @GET
    @Path("check/password/{password}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void checkPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("校验密码") @PathParam("password") String password) {
        ActionResult<ActionCheckPassword.Wo> result = new ActionResult<>();
        try {
            result = new ActionCheckPassword().execute(password);
        } catch (Throwable th) {
            th.printStackTrace();
            result.error(th);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取个人头像.", action = ActionGetIcon.class)
    @GET
    @Path("{flag}/icon")
    @Consumes(MediaType.APPLICATION_JSON)
    public void getIconWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识或者身份标志") @PathParam("flag") String flag) {
        ActionResult<ActionGetIcon.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetIcon().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "设置个人的头像.", action = ActionSetIcon.class)
    @PUT
    @Path("{flag}/icon")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void setIcon(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识") @PathParam("flag") String flag,
            @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("头像文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionSetIcon.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSetIcon().execute(effectivePerson, flag, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "设置个人的头像mockputtopost.", action = ActionSetIcon.class)
    @POST
    @Path("{flag}/icon/mockputtopost")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void setIconMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("个人标识") @PathParam("flag") String flag,
            @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("头像文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionSetIcon.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSetIcon().execute(effectivePerson, flag, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "锁定用户.", action = ActionDoLock.class)
    @POST
    @Path("lock/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lockPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionDoLock.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDoLock().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }

    @JaxrsMethodDescribe(value = "用户解锁，用于管理员解锁登录多次失败被锁定的用户.", action = ActionUnlock.class)
    @GET
    @Path("unlock/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unlockPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionUnlock.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUnlock().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }

    @JaxrsMethodDescribe(value = "设置用户密码到期时间，用于管理员设置用户的密码到期时间.", action = ActionSetPasswordExpiredTime.class)
    @GET
    @Path("{flag}/set/password/expired/time/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPasswordExpiredTime(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("过期时间") @PathParam("date") String date) {
        ActionResult<ActionSetPasswordExpiredTime.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSetPasswordExpiredTime().execute(effectivePerson, flag, date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }

    @JaxrsMethodDescribe(value = "禁用用户.", action = ActionDoBan.class)
    @POST
    @Path("ban/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void banPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                           @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag, JsonElement jsonElement) {
        ActionResult<ActionDoBan.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDoBan().execute(effectivePerson, flag, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }

    @JaxrsMethodDescribe(value = "用户解禁.", action = ActionUnban.class)
    @POST
    @Path("unban/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void unbanPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("人员标识") @PathParam("flag") String flag) {
        ActionResult<ActionUnban.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUnban().execute(effectivePerson, flag);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }

    @JaxrsMethodDescribe(value = "分页查询用户信息.", action = ActionListFilterPaging.class)
    @POST
    @Path("list/filter/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listFilterPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                                 @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListFilterPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListFilterPaging().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "分页查询删除(注销)用户信息.", action = ActionListDeletePaging.class)
    @POST
    @Path("list/delete/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listDeletePaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                                 @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListDeletePaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListDeletePaging().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

}
