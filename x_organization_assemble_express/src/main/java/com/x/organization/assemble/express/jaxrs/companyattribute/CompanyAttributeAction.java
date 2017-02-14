package com.x.organization.assemble.express.jaxrs.companyattribute;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("companyattribute")
public class CompanyAttributeAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Company.class, CompanyAttribute.class);

	@HttpMethodDescribe(value = "按名称和公司名称查找公司属性.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("{name}/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithCompany(@PathParam("name") String name,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutCompanyAttribute> result = new ActionResult<>();
		WrapOutCompanyAttribute wrap = null;
		try {
			String cacheKey = "getWithNameWithCompany#" + name + "#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompanyAttribute) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						String companyAttributeId = business.companyAttribute().getWithName(name, companyId);
						if (null != companyAttributeId) {
							CompanyAttribute companyAttribute = emc.find(companyAttributeId, CompanyAttribute.class);
							wrap = business.companyAttribute().wrap(companyAttribute);
							cache.put(new Element(cacheKey, wrap));
						}
					}
				}
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "查找公司所有属性.", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("list/company/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompany(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompanyAttribute>> result = new ActionResult<>();
		List<WrapOutCompanyAttribute> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithCompany#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyAttribute>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> companyAttributeIds = business.companyAttribute().listWithCompany(companyId);
						if (!companyAttributeIds.isEmpty()) {
							for (CompanyAttribute companyAttribute : emc.list(CompanyAttribute.class,
									companyAttributeIds)) {
								WrapOutCompanyAttribute wrap = business.companyAttribute().wrap(companyAttribute);
								wraps.add(wrap);
							}
							business.companyAttribute().sort(wraps);
							cache.put(new Element(cacheKey, wraps));
						}
					}
				}
			}
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "列出指定名称的属性。", response = WrapOutCompanyAttribute.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompanyAttribute>> result = new ActionResult<>();
		List<WrapOutCompanyAttribute> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithCompany#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyAttribute>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyAttributeIds = business.companyAttribute().listWithName(name);
					if (!companyAttributeIds.isEmpty()) {
						for (CompanyAttribute companyAttribute : emc.list(CompanyAttribute.class,
								companyAttributeIds)) {
							WrapOutCompanyAttribute wrap = business.companyAttribute().wrap(companyAttribute);
							wraps.add(wrap);
						}
					}
					business.companyAttribute().sort(wraps);
					cache.put(new Element(cacheKey, wraps));
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