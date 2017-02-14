package com.x.organization.assemble.express.jaxrs.company;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("company")
public class CompanyAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Company.class, CompanyAttribute.class);

	@HttpMethodDescribe(value = "按名称和公司.如果返回值不存在则此公司不存在。", response = WrapOutCompany.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try {
			String cacheKey = "getWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompany) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String id = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(id)) {
						Company o = emc.find(id, Company.class);
						if (null != o) {
							wrap = business.company().wrap(o);
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

	@HttpMethodDescribe(value = "根据名称查询人员所在的公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings("unchecked")
	public Response listWithPerson(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						List<String> identityIds = business.identity().listWithPerson(personId);
						if (!identityIds.isEmpty()) {
							List<String> departmentIds = new ArrayList<>();
							for (Identity identity : emc.fetchAttribute(identityIds, Identity.class, "department")) {
								departmentIds.add(identity.getDepartment());
							}
							if (!departmentIds.isEmpty()) {
								List<String> companyIds = new ArrayList<>();
								for (Department department : emc.fetchAttribute(departmentIds, Department.class,
										"company")) {
									companyIds.add(department.getCompany());
								}
								if (!companyIds.isEmpty()) {
									for (Company company : emc.list(Company.class, companyIds)) {
										wraps.add(business.company().wrap(company));
									}
									business.company().sort(wraps);
									cache.put(new Element(cacheKey, wraps));
								}
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

	@HttpMethodDescribe(value = "根据身份名称获取所在部门，再根据部门获取所在公司.", response = WrapOutCompany.class)
	@GET
	@Path("identity/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithIdentity(@PathParam("name") String name) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try {
			String cacheKey = "getWithIdentity#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompany) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String identityId = business.identity().getWithName(name);
					if (StringUtils.isNotEmpty(identityId)) {
						Identity identity = emc.fetchAttribute(identityId, Identity.class, "department");
						if (null != identity) {
							String departmentId = identity.getDepartment();
							if (StringUtils.isNotEmpty(departmentId)) {
								Department department = emc.fetchAttribute(departmentId, Department.class, "company");
								if (null != department) {
									String companyId = department.getCompany();
									if (StringUtils.isNotEmpty(companyId)) {
										Company company = emc.find(companyId, Company.class);
										if (null != company) {
											wrap = business.company().wrap(company);
											cache.put(new Element(cacheKey, wrap));
										}
									}
								}
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

	@HttpMethodDescribe(value = "根据部门名称查找公司.", response = WrapOutCompany.class)
	@GET
	@Path("department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDepartment(@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try {
			String cacheKey = "getWithDepartment#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompany) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						Department department = emc.fetchAttribute(departmentId, Department.class, "company");
						if (null != department) {
							String companyId = department.getCompany();
							if (StringUtils.isNotEmpty(companyId)) {
								Company company = emc.find(companyId, Company.class);
								wrap = business.company().wrap(company);
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
	@HttpMethodDescribe(value = "查询指定公司的直接下级公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						/* 查找公司的下级公司 */
						List<String> companyIds = business.company().listSubDirect(companyId);
						if (!companyIds.isEmpty()) {
							for (Company company : emc.list(Company.class, companyIds)) {
								WrapOutCompany wrap = business.company().wrap(company);
								wraps.add(wrap);
							}
							business.company().sort(wraps);
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
	@HttpMethodDescribe(value = "查询指定公司的嵌套下级公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						/* 查找公司的下级公司 */
						List<String> companyIds = business.company().listSubNested(companyId);
						if (!companyIds.isEmpty()) {
							for (Company company : emc.list(Company.class, companyIds)) {
								WrapOutCompany wrap = business.company().wrap(company);
								wraps.add(wrap);
							}
							business.company().sort(wraps);
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

	@HttpMethodDescribe(value = "查询指定公司的上级公司.", response = WrapOutCompany.class)
	@GET
	@Path("{name}/sup/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSupDirect(@PathParam("name") String name) {
		ActionResult<WrapOutCompany> result = new ActionResult<>();
		WrapOutCompany wrap = null;
		try {
			String cacheKey = "getSupDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCompany) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						Company company = emc.fetchAttribute(companyId, Company.class, "superior");
						if (null != company) {
							String superiorId = company.getSuperior();
							if (StringUtils.isNotEmpty(superiorId)) {
								Company superior = emc.find(superiorId, Company.class);
								wrap = business.company().wrap(superior);
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

	@SuppressWarnings({ "unchecked" })
	@HttpMethodDescribe(value = "递归查询指定公司的上级公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSupNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSupNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> companyIds = business.company().listSupNested(companyId);
						if (!companyIds.isEmpty()) {
							for (Company company : emc.list(Company.class, companyIds)) {
								WrapOutCompany wrap = business.company().wrap(company);
								wraps.add(wrap);
							}
							business.company().sort(wraps);
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

	@HttpMethodDescribe(value = "列示所有公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll() {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		try {
			result = new ActionListAll().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "查找顶层公司.", response = WrapOutCompany.class)
	@GET
	@Path("list/top")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listTop() {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "top";
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyIds = business.company().listTop();
					if (!companyIds.isEmpty()) {
						for (Company company : emc.list(Company.class, companyIds)) {
							WrapOutCompany wrap = business.company().wrap(company);
							wraps.add(wrap);
						}
						business.company().sort(wraps);
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
	@HttpMethodDescribe(value = " 列出指定公司属性值的所有公司", response = WrapOutCompany.class)
	@GET
	@Path("list/companyattribute/{attributeName}/{attributeValue}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompanyAttribute(@PathParam("attributeName") String attributeName,
			@PathParam("attributeValue") String attributeValue) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithCompanyAttribute#" + attributeName + "#" + attributeValue;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyIds = business.company().listWithCompanyAttribute(attributeName,
							attributeValue);
					if (!companyIds.isEmpty()) {
						for (Company company : emc.list(Company.class, companyIds)) {
							WrapOutCompany wrap = business.company().wrap(company);
							wraps.add(wrap);
						}
					}
					business.company().sort(wraps);
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
	@HttpMethodDescribe(value = " 获取拼音首字母开始的Company.", response = WrapOutCompany.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listPinyinInitial#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyIds = business.company().listPinyinInitial(key);
					if (!companyIds.isEmpty()) {
						for (Company company : emc.list(Company.class, companyIds)) {
							WrapOutCompany wrap = business.company().wrap(company);
							wraps.add(wrap);
						}
						business.company().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutCompany.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyIds = business.company().listLikePinyin(key);
					if (!companyIds.isEmpty()) {
						for (Company company : emc.list(Company.class, companyIds)) {
							WrapOutCompany wrap = business.company().wrap(company);
							wraps.add(wrap);
						}
						business.company().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutCompany.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutCompany>> result = new ActionResult<>();
		List<WrapOutCompany> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutCompany>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> companyIds = business.company().listLike(key);
					if (!companyIds.isEmpty()) {
						for (Company company : emc.list(Company.class, companyIds)) {
							WrapOutCompany wrap = business.company().wrap(company);
							wraps.add(wrap);
						}
						business.company().sort(wraps);
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
}