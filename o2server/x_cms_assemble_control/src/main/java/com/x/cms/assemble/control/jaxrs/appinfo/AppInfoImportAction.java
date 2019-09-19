package com.x.cms.assemble.control.jaxrs.appinfo;

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
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("import")
@JaxrsDescribe("信息发布栏目导入服务")
public class AppInfoImportAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger(AppInfoImportAction.class);

    @JaxrsMethodDescribe(value = "根据别名获取信息栏目信息对象.", action = ActionGetByAlias.class)
    @GET
    @Path("appInfo/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void importAppInfo( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("栏目别名") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<BaseAction.Wo> result = new ActionResult<>();
        try {
            result = new ActionGetByAlias().execute(request, effectivePerson, id);
        } catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionAppInfoProcess(e, "根据指定应用唯一标识查询应用栏目信息对象时发生异常。ALIAS:" + id);
            result.error(exception);
            logger.error(e, effectivePerson, request, null);
        }
        asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
    }
}