package com.x.organization.assemble.express.jaxrs.departmentattribute;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("departmentattribute")
public class DepartmentAttributeAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(DepartmentAttribute.class);

	@HttpMethodDescribe(value = "按名称和部门名称查找部门属性.", response = WrapOutDepartmentAttribute.class)
	@GET
	@Path("{name}/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithNameWithDepartment(@PathParam("name") String name,
			@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutDepartmentAttribute> result = new ActionResult<>();
		WrapOutDepartmentAttribute wrap = null;
		try {
			String cacheKey = "getWithNameWithDepartment#" + name + "#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartmentAttribute) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						String departmentAttributeId = business.departmentAttribute().getWithName(name,
								departmentId);
						if (null != departmentAttributeId) {
							DepartmentAttribute departmentAttribute = emc.find(departmentAttributeId,
									DepartmentAttribute.class);
							wrap = business.departmentAttribute().wrap(departmentAttribute);
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
	@HttpMethodDescribe(value = "查找部门所有属性.", response = WrapOutDepartmentAttribute.class)
	@GET
	@Path("list/department/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartment(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentAttribute>> result = new ActionResult<>();
		List<WrapOutDepartmentAttribute> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithDepartment#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartmentAttribute>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						List<String> departmentAttributeIds = business.departmentAttribute()
								.listWithDepartment(departmentId);
						if (!departmentAttributeIds.isEmpty()) {
							for (DepartmentAttribute o : emc.list(DepartmentAttribute.class, departmentAttributeIds)) {
								WrapOutDepartmentAttribute wrap = business.departmentAttribute().wrap(o);
								wraps.add(wrap);
							}
							business.departmentAttribute().sort(wraps);
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
	@HttpMethodDescribe(value = "列出指定名称的属性。", response = WrapOutDepartmentAttribute.class)
	@GET
	@Path("list/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartmentAttribute>> result = new ActionResult<>();
		List<WrapOutDepartmentAttribute> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartmentAttribute>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> departmentAttributeIds = business.departmentAttribute().listWithName(name);
					if (!departmentAttributeIds.isEmpty()) {
						for (DepartmentAttribute departmentAttribute : emc.list(DepartmentAttribute.class,
								departmentAttributeIds)) {
							WrapOutDepartmentAttribute wrap = business.departmentAttribute().wrap(departmentAttribute);
							wraps.add(wrap);
						}
					}
					business.departmentAttribute().sort(wraps);
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