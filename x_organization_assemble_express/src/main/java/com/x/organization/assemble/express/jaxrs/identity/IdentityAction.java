package com.x.organization.assemble.express.jaxrs.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapInStringList;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Identity;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("identity")
public class IdentityAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Identity.class);

	@HttpMethodDescribe(value = "根据名称查询Identity,如果返回数据不存在则该Identity不存在。", response = WrapOutIdentity.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutIdentity> result = new ActionResult<>();
		WrapOutIdentity wrap = null;
		try {
			String cacheKey = "getWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutIdentity) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String identityId = business.identity().getWithName(name);
					if (StringUtils.isNotEmpty(identityId)) {
						Identity identity = emc.find(identityId, Identity.class);
						wrap = business.identity().wrap(identity);
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

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据多值名称查询Identity,如果返回数据不存在则该Identity不存在。", response = WrapOutIdentity.class)
	@POST
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithName(WrapInStringList wrapIn) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithName#" + StringUtils.join(wrapIn.getValueList(), ",");
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.identity().listWithName(wrapIn.getValueList());
					List<Identity> identities = emc.list(Identity.class, ids);
					for (Identity o : identities) {
						WrapOutIdentity wrap = business.identity().wrap(o);
						wraps.add(wrap);
					}
				}
				SortTools.order(wraps, true, "name", wrapIn.getValueList());
				cache.put(new Element(cacheKey, wraps));
			}
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据Person名称查询其所有的Identity。", response = WrapOutIdentity.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@PathParam("name") String name) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						List<String> ids = business.identity().listWithPerson(personId);
						for (Identity o : emc.list(Identity.class, ids)) {
							WrapOutIdentity wrap = business.identity().wrap(o);
							wraps.add(wrap);
						}
						business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "根据Department名称查询其所有的Identity。", response = WrapOutIdentity.class)
	@GET
	@Path("list/department/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartmentSubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			String cacheKey = "listWithDepartmentSubDirect#" + name;
			Element element = cache.get(cacheKey);
			List<WrapOutIdentity> wraps = new ArrayList<>();
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotBlank(departmentId)) {
						List<String> ids = business.identity().listWithDepartment(departmentId);
						for (Identity o : emc.list(Identity.class, ids)) {
							WrapOutIdentity wrap = business.identity().wrap(o);
							wraps.add(wrap);
						}
						business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "根据Department名称查询其所有的Identity。", response = WrapOutIdentity.class)
	@GET
	@Path("list/department/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartmentSubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			String cacheKey = "listWithDepartmentSubNested#" + name;
			Element element = cache.get(cacheKey);
			List<WrapOutIdentity> wraps = new ArrayList<>();
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(name);
					if (StringUtils.isNotEmpty(departmentId)) {
						Set<String> departments = new HashSet<>();
						Set<String> ids = new HashSet<>();
						departments.add(departmentId);
						departments.addAll(business.department().listSubNested(departmentId));
						for (String str : departments) {
							ids.addAll(business.identity().listWithDepartment(str));
						}
						for (Identity o : emc.list(Identity.class, ids)) {
							WrapOutIdentity wrap = business.identity().wrap(o);
							wraps.add(wrap);
						}
						business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "根据Company名称查询其所有的Identity。", response = WrapOutIdentity.class)
	@GET
	@Path("list/company/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompanySubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			String cacheKey = "listWithCompanySubDirect#" + name;
			Element element = cache.get(cacheKey);
			List<WrapOutIdentity> wraps = new ArrayList<>();
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						Set<String> ids = new HashSet<>();
						List<String> departments = business.department().listWithCompanySubNested(companyId);
						for (String str : departments) {
							ids.addAll(business.identity().listWithDepartment(str));
						}
						for (Identity o : emc.list(Identity.class, ids)) {
							WrapOutIdentity wrap = business.identity().wrap(o);
							wraps.add(wrap);
						}
						business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "根据Company名称查询其所有的Identity。", response = WrapOutIdentity.class)
	@GET
	@Path("list/company/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithCompanySubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		try {
			String cacheKey = "listWithCompanySubDirect#" + name;
			Element element = cache.get(cacheKey);
			List<WrapOutIdentity> wraps = new ArrayList<>();
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(name);
					if (StringUtils.isNotEmpty(companyId)) {
						Set<String> ids = new HashSet<>();
						Set<String> companies = new HashSet<>();
						companies.add(companyId);
						companies.addAll(business.company().listSubNested(companyId));
						Set<String> departments = new HashSet<>();
						for (String str : companies) {
							departments.addAll(business.department().listWithCompanySubNested(str));
						}
						for (String str : departments) {
							ids.addAll(business.identity().listWithDepartment(str));
						}
						for (Identity o : emc.list(Identity.class, ids)) {
							WrapOutIdentity wrap = business.identity().wrap(o);
							wraps.add(wrap);
						}
						Collections.sort(wraps, new Comparator<WrapOutIdentity>() {
							public int compare(WrapOutIdentity o1, WrapOutIdentity o2) {
								return ObjectUtils.compare(o1.getName(), o2.getName(), true);
							}
						});
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
	@HttpMethodDescribe(value = "在指定范围内(公司和部门)进行模糊查询.", response = WrapOutIdentity.class, request = WrapInCompanySubNestedDepartmentSubNested.class)
	@POST
	@Path("list/company/sub/nested/department/sub/nested/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikeWithCompanySubNestedWithDepartmentSubNested(@PathParam("key") String key,
			WrapInCompanySubNestedDepartmentSubNested wrapIn) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikeWithCompanySubNestedWithDepartmentSubNested#" + key + "#"
					+ StringUtils.join(wrapIn.getCompanyList(), ",") + "#"
					+ StringUtils.join(wrapIn.getDepartmentList(), ",");
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Set<String> departments = new HashSet<>();
					if (null != wrapIn.getCompanyList()) {
						/* 查询指定公司的所有部门，加入到搜索范围 */
						for (String str : wrapIn.getCompanyList()) {
							String companyId = business.company().getWithName(str);
							if (StringUtils.isNotEmpty(companyId)) {
								departments.addAll(business.department().listWithCompanySubNested(companyId));
							}
						}
					}
					if (null != wrapIn.getDepartmentList()) {
						for (String str : wrapIn.getDepartmentList()) {
							String departmentId = business.department().getWithName(str);
							/* 把查询部门加入到搜索范围内 */
							if (StringUtils.isNotEmpty(departmentId)) {
								departments.add(departmentId);
								/* 把查询部门的子部门加入到搜索范围内 */
								departments.addAll(business.department().listSubNested(departmentId));
							}
						}
					}
					List<String> ids = business.identity().listLikeWithDepartment(departments, key);
					for (Identity o : emc.list(Identity.class, ids)) {
						WrapOutIdentity wrap = business.identity().wrap(o);
						wraps.add(wrap);
					}
					business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = " 获取拼音首字母开始的群组.", response = WrapOutIdentity.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listPinyinInitial#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.identity().listPinyinInitial(key);
					for (Identity o : emc.list(Identity.class, ids)) {
						WrapOutIdentity wrap = business.identity().wrap(o);
						wraps.add(wrap);
					}
					business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutIdentity.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.identity().listLikePinyin(key);
					for (Identity o : emc.list(Identity.class, ids)) {
						WrapOutIdentity wrap = business.identity().wrap(o);
						wraps.add(wrap);
					}
					business.identity().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutIdentity.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutIdentity>> result = new ActionResult<>();
		List<WrapOutIdentity> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutIdentity>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.identity().listLike(key);
					for (Identity o : emc.list(Identity.class, ids)) {
						WrapOutIdentity wrap = business.identity().wrap(o);
						wraps.add(wrap);
					}
					business.identity().sort(wraps);
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