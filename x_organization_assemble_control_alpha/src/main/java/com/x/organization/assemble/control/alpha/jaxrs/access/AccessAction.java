package com.x.organization.assemble.control.alpha.jaxrs.access;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.list.SetUniqueList;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Person;

@Path("access")
public class AccessAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "获取指定用户(id)可以管理的公司.", response = String.class)
	@GET
	@Path("list/person/{id}/control/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<String>> result = new ActionResult<>();
		List<String> wraps = SetUniqueList.setUniqueList(new ArrayList<String>());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			if (effectivePerson.isManager()) {
				/* 如果是管理员可以管理所有公司 */
				wraps.addAll(business.company().listAll());
			} else {
				/* 不是管理员 */
				Person person = emc.find(id, Person.class);
				if (null == person) {
					throw new Exception("person{id:" + id + "} not existed.");
				}
				List<String> ids = business.company().listWithControl(id);
				for (String str : ids) {
					wraps.add(str);
					wraps.addAll(business.company().listSubNested(str));
				}
			}
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}