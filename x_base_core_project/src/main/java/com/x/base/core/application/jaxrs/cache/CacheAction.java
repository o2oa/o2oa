package com.x.base.core.application.jaxrs.cache;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.cache.ClearCacheRequest;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;

@Path("cache")
public class CacheAction extends AbstractJaxrsAction {

	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "处理Cache刷新信息.", request = ClearCacheRequest.class, response = WrapOutString.class)
	public Response remove(@Context ServletContext servletContext, @Context HttpServletRequest request,
			ClearCacheRequest clearCacheRequest) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		try {
			Object o = servletContext.getAttribute(com.x.base.core.project.Context.class.getName());
			if (null != o) {
				com.x.base.core.project.Context cxt = (com.x.base.core.project.Context) o;
				if (null != cxt.clearCacheRequestQueue()) {
					cxt.clearCacheRequestQueue().send(clearCacheRequest);
				} else {
					ApplicationCache.receive(clearCacheRequest);
				}
			} else {
				ApplicationCache.receive(clearCacheRequest);
			}
			wrap.setValue(clearCacheRequest.getClassName());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}