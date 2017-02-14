package com.x.organization.assemble.express.jaxrs.department;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Department;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("department")
public class DepartmentAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Department.class);

	@HttpMethodDescribe(value = "按名称查找部门.如果返回值不存在则此部门不存在。", response = WrapOutDepartment.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		try {
			String cacheKey = "getWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartment) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						Department department = emc.find(departmentId, Department.class);
						wrap = business.department().wrap(department);
						cache.put(new Element(cacheKey, wrap));
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

	@HttpMethodDescribe(value = "列示所有部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response listAll() {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listAll#";
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					wraps = new ActionList().execute(business);
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

	@HttpMethodDescribe(value = "根据名称查询人员所在的部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response listWithPerson(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						List<String> departmentIds = new ArrayList<>();
						/* 根据人查找Identity */
						List<String> identityIds = business.identity().listWithPerson(personId);
						for (String str : identityIds) {
							/* 根据Identity Id查找 Department */
							departmentIds.add(business.department().getWithIdentity(str));
						}
						for (Department o : emc.list(Department.class, departmentIds)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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

	@HttpMethodDescribe(value = "根据身份名称获取所在部门.", response = WrapOutDepartment.class)
	@GET
	@Path("identity/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithIdentity(@PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		try {
			String cacheKey = "getWithIdentity#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartment) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 根据名称查找Identity */
					String identityId = business.identity().getWithName(name);
					if (StringUtils.isNotEmpty(identityId)) {
						/* 根据 Identity 的 Department 查找 Department */
						String departmentId = business.department().getWithIdentity(identityId);
						if (StringUtils.isNotEmpty(departmentId)) {
							Department o = emc.find(departmentId, Department.class);
							wrap = business.department().wrap(o);
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

	@HttpMethodDescribe(value = "查询指定公司的上级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("{name}/sup/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSupDirect(@PathParam("name") String name) {
		ActionResult<WrapOutDepartment> result = new ActionResult<>();
		WrapOutDepartment wrap = null;
		try {
			String cacheKey = "getSupDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutDepartment) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 根据名称查找Department */
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						/* 查找公司的上级部门 */
						Department department = emc.find(departmentId, Department.class);
						if (null != department) {
							String superiorId = department.getSuperior();
							if (StringUtils.isNotEmpty(superiorId)) {
								Department superior = emc.find(superiorId, Department.class);
								wrap = business.department().wrap(superior);
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
	@HttpMethodDescribe(value = "递归查询指定公司的上级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSupNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSupNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 根据名称查找Department */
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						/* 递归查找部门的上级部门 */
						List<String> superiorIds = business.department().listSupNested(departmentId);
						for (Department o : emc.list(Department.class, superiorIds)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "查询指定部门的下级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 根据名称查找Department */
					String department = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(department)) {
						/* 查找部门的下级部门 */
						List<String> ids = business.department().listSubDirect(department);
						for (Department o : emc.list(Department.class, ids)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "查询指定部门的下级部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 根据名称查找Department */
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						/* 嵌套查找部门的下级部门 */
						List<String> ids = business.department().listSubNested(departmentId);
						for (Department o : emc.list(Department.class, ids)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "查找公司的顶层部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/company/{companyName}/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listTopWithCompany(@PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listTopWithCompany#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Company */
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> ids = business.department().listTopWithCompany(companyId);
						for (Department o : emc.list(Department.class, ids)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "查找公司的部门,包括下属部门和部门的部门.", response = WrapOutDepartment.class)
	@GET
	@Path("list/company/{companyName}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompanySubNested(@PathParam("companyName") String companyName) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listTopWithCompanySubNested#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Company */
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> ids = business.department().listTopWithCompany(companyId);
						for (Department o : emc.list(Department.class, ids)) {
							WrapOutDepartment wrap = business.department().wrap(o);
							wraps.add(wrap);
						}
						business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "列出指定部门属性值的所有部门", response = WrapOutDepartment.class)
	@GET
	@Path("list/departmentAttribute/{name}/{attribute}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartmentAttribute(@PathParam("name") String name,
			@PathParam("attribute") String attribute) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithDepartmentAttribute#" + name + "#" + attribute;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.department().listWithDepartmentAttribute(name, attribute);
					for (Department o : emc.fetchAttribute(ids, Department.class, "name", "display", "superior",
							"company")) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "获取拼音首字母开始的Department.", response = WrapOutDepartment.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listPinyinInitial#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.department().listPinyinInitial(key);
					for (Department o : emc.list(Department.class, ids)) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutDepartment.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.department().listLikePinyin(key);
					for (Department o : emc.list(Department.class, ids)) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					business.department().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutDepartment.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutDepartment>> result = new ActionResult<>();
		List<WrapOutDepartment> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutDepartment>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.department().listLike(key);
					for (Department o : emc.fetchAttribute(ids, Department.class, "name", "display", "superior",
							"company")) {
						WrapOutDepartment wrap = business.department().wrap(o);
						wraps.add(wrap);
					}
					business.department().sort(wraps);
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