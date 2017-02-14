package com.x.organization.assemble.express.jaxrs.companyduty;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.list.SetUniqueList;
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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.CompanyDuty;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("companyduty")
public class CompanyDutyAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(CompanyDuty.class);

	@HttpMethodDescribe(value = "按名称和公司名称查找公司职务.", response = WrapOutCompanyDuty.class)
	@GET
	@Path("{name}/company/{companyName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithCompany(@PathParam("name") String name,
			@PathParam("companyName") String companyName) {
		ActionResult<WrapOutCompanyDuty> result = new ActionResult<>();
		WrapOutCompanyDuty wrap = null;
		try {
			String cacheKey = "getWithNameWithCompany#" + name + "#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompanyDuty) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						String companyDutyId = business.companyDuty().getWithName(name, companyId);
						if (StringUtils.isNotEmpty(companyDutyId)) {
							CompanyDuty companyDuty = emc.find(companyDutyId, CompanyDuty.class);
							wrap = business.companyDuty().wrap(companyDuty);
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
	@HttpMethodDescribe(value = "查找公司所有职务.", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/company/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompany(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithCompany#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> companyDutyIds = business.companyDuty().listWithCompany(companyId);
						for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
							WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
							wraps.add(wrap);
						}
						business.companyDuty().sort(wraps);
						cache.put(new Element(cacheKey, wraps));
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
	@HttpMethodDescribe(value = "列出指定名称的属性。", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyDutyIds = business.companyDuty().listWithName(name);
					for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
						WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
						wraps.add(wrap);
					}
					business.companyDuty().sort(wraps);
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

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据给定的Identity列示其所有的CompanyDuty", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/identity/{identityName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithIdentity(@PathParam("identityName") String identityName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithIdentity#" + identityName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyDutyIds = business.companyDuty().listWithIdentity(identityName);
					for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
						WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
						wraps.add(wrap);
					}
					business.companyDuty().sort(wraps);
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

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据给定的Identity列示其所有的CompanyDuty", response = WrapOutCompanyDuty.class)
	@GET
	@Path("list/person/{personName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@PathParam("personName") String personName) {
		ActionResult<List<WrapOutCompanyDuty>> result = new ActionResult<>();
		List<WrapOutCompanyDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + personName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompanyDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(personName);
					if (StringUtils.isNotEmpty(personId)) {
						List<String> identityIds = business.identity().listWithPerson(personId);
						if (!identityIds.isEmpty()) {
							List<String> companyDutyIds = SetUniqueList.setUniqueList(new ArrayList<String>());
							for (String identityId : identityIds) {
								companyDutyIds.addAll(business.companyDuty().listWithIdentity(identityId));
							}
							if (!companyDutyIds.isEmpty()) {
								for (CompanyDuty companyDuty : emc.list(CompanyDuty.class, companyDutyIds)) {
									WrapOutCompanyDuty wrap = business.companyDuty().wrap(companyDuty);
									wraps.add(wrap);
								}
								business.companyDuty().sort(wraps);
								cache.put(new Element(cacheKey, wraps));
							}
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
}