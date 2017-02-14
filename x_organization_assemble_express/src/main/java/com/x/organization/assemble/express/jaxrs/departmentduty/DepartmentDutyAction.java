package com.x.organization.assemble.express.jaxrs.departmentduty;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("departmentduty")
public class DepartmentDutyAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(DepartmentDuty.class);

	@HttpMethodDescribe(value = "按名称和部门名称查找公司职务.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("{name}/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithDepartment(@PathParam("name") String name,
			@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutDepartmentDuty> result = new ActionResult<>();
		WrapOutDepartmentDuty wrap = null;
		try {
			String cacheKey = "getWithNameWithDepartment#" + name + "#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartmentDuty) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Department */
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						/* 查找DepartmentDuty */
						String departmentDutyId = business.departmentDuty().getWithName(name, departmentId);
						if (StringUtils.isNotEmpty(departmentDutyId)) {
							DepartmentDuty departmentDuty = emc.find(departmentDutyId, DepartmentDuty.class);
							if (null != departmentDuty) {
								wrap = business.departmentDuty().wrap(departmentDuty);
								cache.put(new Element(cacheKey, wrap));
							}
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
	@HttpMethodDescribe(value = "查找部门所有职务.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/department/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartment(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithDepartment#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartmentDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Department */
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						/* 查找DepartmentDuty */
						List<String> list = business.departmentDuty().listWithDepartment(departmentId);
						for (DepartmentDuty o : emc.list(DepartmentDuty.class, list)) {
							WrapOutDepartmentDuty wrap = business.departmentDuty().wrap(o);
							wraps.add(wrap);
						}
						business.departmentDuty().sort(wraps);
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
	@HttpMethodDescribe(value = "列出指定名称的属性.", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartmentDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.departmentDuty().listWithName(name);
					for (DepartmentDuty o : emc.list(DepartmentDuty.class, ids)) {
						WrapOutDepartmentDuty wrap = business.departmentDuty().wrap(o);
						wraps.add(wrap);
					}
					business.departmentDuty().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Identity，列示其所有的DepartmentDuty", response = WrapOutDepartmentDuty.class)
	@GET
	@Path("list/identity/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithIdentity(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentDuty>> result = new ActionResult<>();
		List<WrapOutDepartmentDuty> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithIdentity#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartmentDuty>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.departmentDuty().listWithIdentity(name);
					for (DepartmentDuty o : emc.list(DepartmentDuty.class, ids)) {
						WrapOutDepartmentDuty wrap = business.departmentDuty().wrap(o);
						wraps.add(wrap);
					}
					business.departmentDuty().sort(wraps);
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