package com.x.program.center.jaxrs.apppack;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("apppackanony")
@JaxrsDescribe("移动客户端在线打包服务")
public class AppPackAnonymousAction extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(AppPackAnonymousAction.class);



    @JaxrsMethodDescribe(value = "下载app包", action = ActionPackFileDownload.class)
    @GET
    @Path("pack/info/file/download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                         @JaxrsParameterDescribe("app下载标识") @PathParam("id") String id) {
        ActionResult<ActionPackFileDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPackFileDownload().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "查询最新发布的app下载包.", action = ActionLastPackFileInfo.class)
    @GET
    @Path("pack/info/file/last")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void androidPackLastAPk(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionLastPackFileInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLastPackFileInfo().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
