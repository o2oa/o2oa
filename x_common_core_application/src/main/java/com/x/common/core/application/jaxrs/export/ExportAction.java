package com.x.common.core.application.jaxrs.export;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.zip.JarTools;
import com.x.common.core.application.AbstractThisApplication;
import com.x.common.core.application.jaxrs.AbstractJaxrsAction;

@Path("export")
public class ExportAction extends AbstractJaxrsAction {

	@GET
	@Path("{token}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将配置文件打包导出.")
	public Response export(@Context HttpServletRequest request, @PathParam("token") String token) {
		ActionResult<String> result = new ActionResult<>();
		String wrap = null;
		try {
			if (!StringUtils.equals(AbstractThisApplication.center.getCipher(), token)) {
				throw new Exception("error token.");
			}
			byte[] bytes = JarTools.jar(AbstractThisApplication.webApplicationDirectory + "/WEB-INF/classes/META-INF");
			wrap = Base64.encodeBase64String(bytes);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}