package com.x.cms.assemble.control.jaxrs.uuid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 该类旨在提供一个服务创建唯一的UNID
 * 
 * @author liyi
 *
 */
@Path("uuid")
public class UUIDAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(UUIDAction.class);

	// @HttpMethodDescribe(value = "根据随机ID的.", response = JsonElement.class)
	@GET
	@Path("random")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUUID( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<String>> result = new ActionResult<>();
		List<String> data = new ArrayList<String>();
		String uuid = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			uuid = UUID.randomUUID().toString();
			data.add(uuid);
			result.setData(data);
		} catch (Exception e) {
			logger.warn("user[" + currentPerson.getDistinguishedName() + "] get a new UUID error！", e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}