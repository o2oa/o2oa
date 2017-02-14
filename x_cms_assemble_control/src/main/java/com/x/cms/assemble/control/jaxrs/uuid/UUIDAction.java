package com.x.cms.assemble.control.jaxrs.uuid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;

/**
 * 该类旨在提供一个服务创建唯一的UNID
 * @author liyi
 *
 */
@Path("uuid")
public class UUIDAction extends AbstractJaxrsAction {
	
	private Logger logger = LoggerFactory.getLogger( UUIDAction.class );
	
	@HttpMethodDescribe(value = "根据随机ID的.", response = JsonElement.class)
	@GET
	@Path("random")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUUID( @Context HttpServletRequest request ) {
		ActionResult<List<String>> result = new ActionResult<>();
		List<String> data = new ArrayList<String>();
		String uuid = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			uuid = UUID.randomUUID().toString();
			data.add( uuid );
			result.setData( data );
		} catch (Exception e) {
			logger.error( "user["+ currentPerson.getName() +"] get a new UUID error！", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}