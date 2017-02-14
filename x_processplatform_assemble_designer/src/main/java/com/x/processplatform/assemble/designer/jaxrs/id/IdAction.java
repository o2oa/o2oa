package com.x.processplatform.assemble.designer.jaxrs.id;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("id")
public class IdAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "创建用于ID值的UUID", response = WrapOutId.class)
	@GET
	@Path("{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public Response get(@PathParam("count") Integer count) {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		try {
			List<WrapOutId> list = new ArrayList<>();
			if (count > 0 && count < 200) {
				for (int i = 0; i < count; i++) {
					WrapOutId wrap = new WrapOutId(JpaObject.createId());
					list.add(wrap);
				}
			}
			result.setData(list);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}