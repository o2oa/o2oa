package com.x.organization.assemble.express.jaxrs.setcompanyattribute;

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
import com.x.organization.core.entity.CompanyAttribute;

@Path("setcompanyattribute")
public class SetCompanyAttributeAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "更新当前用户的PersonAttribute属性.")
	@PUT
	@Path("{name}/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setAttributeWithPerson(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("companyName") String companyName, WrapInSetCompanyAttribute wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String companyId = business.person().getWithName(companyName);
			if (StringUtils.isEmpty(companyId)) {
				throw new Exception("company{name:" + companyName + "} not existed.");
			}
			String companyAttributeId = business.companyAttribute().getWithName(name, companyId);
			CompanyAttribute companyAttribute = null;
			emc.beginTransaction(CompanyAttribute.class);
			if (StringUtils.isEmpty(companyAttributeId)) {
				companyAttribute = new CompanyAttribute();
				companyAttribute.setName(name);
				companyAttribute.setCompany(companyId);
				emc.persist(companyAttribute);
			} else {
				companyAttribute = emc.find(companyAttributeId, CompanyAttribute.class);
			}
			companyAttribute.setAttributeList(wrapIn.getAttributeList());
			emc.commit();
			ApplicationCache.notify(CompanyAttribute.class);
			wrap = new WrapOutId(companyAttribute.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}