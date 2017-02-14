package com.x.base.core.http;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

public class ResponseFactory {

	private static CacheControl defaultCacheControl = CacheControlFactory.getDefault();

	public static Response getDefaultActionResultResponse(ActionResult<?> result) {
		if (result.getType().equals(ActionResult.Type.error)) {
			return Response.serverError().entity(result.toJson()).cacheControl(defaultCacheControl).build();
		} else {
			return Response.ok(result.toJson()).cacheControl(defaultCacheControl).build();
		}
	}
}
