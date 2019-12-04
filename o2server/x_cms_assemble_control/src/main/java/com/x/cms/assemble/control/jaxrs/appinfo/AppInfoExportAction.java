package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("export")
@JaxrsDescribe("信息发布栏目导出服务")
public class AppInfoExportAction extends StandardJaxrsAction {

    private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());
	private static  Logger logger = LoggerFactory.getLogger(AppInfoExportAction.class);

    @JaxrsMethodDescribe(value = "根据ID导出栏目信息对象所有设计.", action = ActionAppInfoExport.class)
    @GET
    @Path("appInfo/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void exportAppInfo( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("栏目ID") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionAppInfoExport.Wo> result = new ActionResult<>();
        try {
            result = ((ActionAppInfoExport)proxy.getProxy(ActionAppInfoExport.class)).execute( request, effectivePerson, id );
        } catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionAppInfoProcess(e, "根据指定ID导出栏目信息对象时发生异常。ID:" + id );
            result.error(exception);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}