package com.x.base.core.project.jaxrs;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.CacheControlFactory;

public class ResponseFactory {

	private static CacheControl defaultCacheControl = CacheControlFactory.getDefault();

	private static final String Content_Disposition = "Content-Disposition";
	private static final String Content_Type = "Content-Type";

	public static <T> Response getDefaultActionResultResponse(ActionResult<T> result) {
		if (result.getType().equals(ActionResult.Type.error)) {
			return Response.serverError().entity(result.toJson()).cacheControl(defaultCacheControl).build();
		} else {
			if ((null != result.getData()) && (result.getData() instanceof FileWo)) {
				FileWo wo = (FileWo) result.getData();
				return Response.ok(wo.getBytes()).header(Content_Disposition, wo.getContentDisposition())
						.header(Content_Type, wo.getContentType()).build();
			} else {
				return Response.ok(result.toJson()).cacheControl(defaultCacheControl).build();
			}
		}
	}
}
