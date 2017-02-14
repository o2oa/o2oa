package com.x.organization.assemble.express.jaxrs.setdepartmentattribute;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.DepartmentAttribute;

@Path("setdepartmentattribute")
public class SetDepartmentAttributeAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "更新当前用户的PersonAttribute属性.")
	@PUT
	@Path("{name}/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setAttributeWithPerson(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("departmentName") String departmentName, WrapInSetDepartmentAttribute wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String departmentId = business.person().getWithName(departmentName);
			if (StringUtils.isEmpty(departmentId)) {
				throw new Exception("department{name:" + departmentName + "} not existed.");
			}
			String departmentAttributeId = business.departmentAttribute().getWithName(name, departmentId);
			DepartmentAttribute departmentAttribute = null;
			emc.beginTransaction(DepartmentAttribute.class);
			if (StringUtils.isEmpty(departmentAttributeId)) {
				departmentAttribute = new DepartmentAttribute();
				departmentAttribute.setName(name);
				departmentAttribute.setDepartment(departmentId);
				emc.persist(departmentAttribute);
			} else {
				departmentAttribute = emc.find(departmentAttributeId, DepartmentAttribute.class);
			}
			departmentAttribute.setAttributeList(wrapIn.getAttributeList());
			emc.commit();
			ApplicationCache.notify(DepartmentAttribute.class);
			wrap = new WrapOutId(departmentAttribute.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}