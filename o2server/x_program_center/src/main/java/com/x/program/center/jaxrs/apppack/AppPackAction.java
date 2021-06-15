package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */

@Path("apppack")
@JaxrsDescribe("移动客户端打包服务")
public class AppPackAction extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(AppPackAction.class);

    @JaxrsMethodDescribe(value = "检测打包服务器.", action = ActionConnectPackServer.class)
    @GET
    @Path("server/connect")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void connect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionConnectPackServer.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConnectPackServer().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "获取最近一次打包信息.", action = ActionPackInfo.class)
    @GET
    @Path("pack/info/{token}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void packInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("token") @PathParam("token") String token) {
        ActionResult<ActionPackInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPackInfo().execute(token);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "发起 android app 打包.", action = ActionAndroidPack.class)
    @POST
    @Path("pack/info/android/start")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void androidPackStart(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                 @JaxrsParameterDescribe("token") @FormDataParam("token") String token,
                                 @JaxrsParameterDescribe("appName") @FormDataParam("appName") String appName,
                                 @JaxrsParameterDescribe("o2ServerProtocol") @FormDataParam("o2ServerProtocol") String o2ServerProtocol,
                                 @JaxrsParameterDescribe("o2ServerHost") @FormDataParam("o2ServerHost") String o2ServerHost,
                                 @JaxrsParameterDescribe("o2ServerPort") @FormDataParam("o2ServerPort") String o2ServerPort,
                                 @JaxrsParameterDescribe("o2ServerContext") @FormDataParam("o2ServerContext") String o2ServerContext,
                           @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
                           @JaxrsParameterDescribe("附件标识") @FormDataParam(FILE_FIELD) final byte[] bytes,
                           @JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition){
        ActionResult<ActionAndroidPack.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAndroidPack().execute(token, appName, o2ServerProtocol, o2ServerHost, o2ServerPort, o2ServerContext, fileName, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
