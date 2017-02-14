package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutCount;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("person")
public class PersonAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Person.class);

	private BeanCopyTools<Person, WrapOutPerson> copier = BeanCopyToolsBuilder.create(Person.class, WrapOutPerson.class,
			null, WrapOutPerson.Excludes);

	@HttpMethodDescribe(value = "根据人的唯一标识进行查找,如果不存在,返回值不存在。", response = WrapOutPerson.class)
	@GET
	@Path("flag/{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response flag(@PathParam("flag") String flag) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		try {
			if (StringUtils.equalsIgnoreCase(Config.administrator().getName(), flag)) {
				wrap = new WrapOutPerson();
				Config.administrator().copyTo(wrap);
			} else {
				String cacheKey = "flag#" + flag;
				Element element = cache.get(cacheKey);
				if (null != element) {
					wrap = (WrapOutPerson) element.getObjectValue();
				} else {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Person person = emc.flag(flag, Person.class, ExceptionWhen.none, false, "id", "name", "unique",
								"employee");
						if (null != person) {
							wrap = copier.copy(person);
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

	@HttpMethodDescribe(value = "按名称查找人员,如果不存在,返回值不存在。", response = WrapOutPerson.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		try {
			if (StringUtils.equalsIgnoreCase(Config.administrator().getName(), name)) {
				wrap = new WrapOutPerson();
				Config.administrator().copyTo(wrap);
			} else {
				String cacheKey = "getWithName#" + name;
				Element element = cache.get(cacheKey);
				if (null != element) {
					wrap = (WrapOutPerson) element.getObjectValue();
				} else {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						String personId = business.person().getWithName(name);
						if (StringUtils.isNotEmpty(personId)) {
							Person person = emc.find(personId, Person.class);
							wrap = copier.copy(person);
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

	@HttpMethodDescribe(value = "根据Identity的名称查找人员,如果不存在,返回值不存在。", response = WrapOutPerson.class)
	@GET
	@Path("identity/{identityName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithIdentity(@PathParam("identityName") String identityName) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		try {
			if (StringUtils.equalsIgnoreCase(Config.administrator().getId(), identityName)) {
				wrap = new WrapOutPerson();
				Config.administrator().copyTo(wrap);
			} else {
				String cacheKey = "getWithIdentity#" + identityName;
				Element element = cache.get(cacheKey);
				if (null != element) {
					wrap = (WrapOutPerson) element.getObjectValue();
				} else {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						String identityId = business.identity().getWithName(identityName);
						if (StringUtils.isNotEmpty(identityId)) {
							Identity identity = emc.fetchAttribute(identityId, Identity.class, "person");
							if (null != identity && StringUtils.isNotEmpty(identity.getPerson())) {
								Person person = emc.find(identity.getPerson(), Person.class);
								wrap = copier.copy(person);
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

	@HttpMethodDescribe(value = "按名称查找人员,如果不存在,返回值不存在。", response = WrapOutPerson.class)
	@GET
	@Path("credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithCredential(@PathParam("credential") String credential) {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		try {
			if (StringUtils.equalsIgnoreCase(Config.administrator().getName(), credential)) {
				wrap = new WrapOutPerson();
				Config.administrator().copyTo(wrap);
			} else {
				String cacheKey = "getWithCredential#" + credential;
				Element element = cache.get(cacheKey);
				if (null != element) {
					wrap = (WrapOutPerson) element.getObjectValue();
				} else {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						Business business = new Business(emc);
						String personId = business.person().getWithCredential(credential);
						if (StringUtils.isNotEmpty(personId)) {
							Person person = emc.find(personId, Person.class);
							wrap = copier.copy(person);
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

	@HttpMethodDescribe(value = "统计公司成员人员总数，直接人员", response = WrapOutPerson.class)
	@GET
	@Path("count/company/{companyName}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithCompanySubDirect(@PathParam("companyName") String companyName) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		WrapOutCount wrap = new WrapOutCount();
		wrap.setCount(0L);
		try {
			String cacheKey = "countWithCompanySubDirect#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCount) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> departmentIds = business.department().listWithCompanySubNested(companyId);
						List<String> ids = business.identity().listWithDepartment(departmentIds);
						wrap.setCount(business.person().listWithIdentity(ids).size());
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

	@HttpMethodDescribe(value = "统计公司成员人员总数，嵌套子公司人员", response = WrapOutPerson.class)
	@GET
	@Path("count/company/{companyName}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithCompanySubNested(@PathParam("companyName") String companyName) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		WrapOutCount wrap = new WrapOutCount();
		wrap.setCount(0L);
		try {
			String cacheKey = "countWithCompanySubNested#" + companyName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCount) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String companyId = business.company().getWithName(companyName);
					if (StringUtils.isNotEmpty(companyId)) {
						List<String> companyIds = ListTools
								.concreteArrayList(business.company().listSubNested(companyId), true, true, companyId);
						List<String> departmentIds = new ArrayList<>();
						for (String str : companyIds) {
							departmentIds.addAll(business.department().listWithCompanySubNested(str));
						}
						List<String> identityIds = business.identity().listWithDepartment(departmentIds);
						wrap.setCount(business.person().listWithIdentity(identityIds).size());
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

	@HttpMethodDescribe(value = "统计部门成员人员总数，直接人员", response = WrapOutPerson.class)
	@GET
	@Path("count/department/{departmentName}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithDepartmentSubDirect(@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		WrapOutCount wrap = new WrapOutCount();
		wrap.setCount(0L);
		try {
			String cacheKey = "countWithDepartmentSubDirect#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCount) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						List<String> ids = business.identity().listWithDepartment(departmentId);
						wrap.setCount(business.person().listWithIdentity(ids).size());
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

	@HttpMethodDescribe(value = "统计部门成员人员总数，递归人员", response = WrapOutPerson.class)
	@GET
	@Path("count/department/{departmentName}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countWithDepartmentSubNested(@PathParam("departmentName") String departmentName) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		WrapOutCount wrap = new WrapOutCount();
		wrap.setCount(0L);
		try {
			String cacheKey = "countWithDepartmentSubNested#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutCount) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						List<String> departmentIds = new ArrayList<>();
						departmentIds.add(departmentId);
						departmentIds.addAll(business.department().listSubNested(departmentId));
						List<String> ids = business.identity().listWithDepartment(departmentIds);
						wrap.setCount(business.person().listWithIdentity(ids).size());
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
	@HttpMethodDescribe(value = "查找拥有指定personAttribute的个人。", response = WrapOutPerson.class)
	@GET
	@Path("list/personattribute/{personAttribute}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonAttribute(@PathParam("personAttribute") String personAttribute) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPersonAttribute#" + personAttribute;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> personAttributeIds = business.personAttribute().listContainsAttribute(personAttribute);
					List<String> personIds = ListTools.extractProperty(
							emc.fetchAttribute(personAttributeIds, PersonAttribute.class, "person"), "person",
							String.class, true, true);
					if (!personIds.isEmpty()) {
						for (Person person : emc.list(Person.class, personIds)) {
							WrapOutPerson wrap = new WrapOutPerson();
							wrap = copier.copy(person);
							wraps.add(wrap);
						}
						SortTools.asc(wraps, true, "name");
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
	@HttpMethodDescribe(value = "查找部门的个人成员。", response = WrapOutPerson.class)
	@GET
	@Path("list/department/{departmentName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithDepartment(@PathParam("departmentName") String departmentName) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithDepartment#" + departmentName;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String departmentId = business.department().getWithName(departmentName);
					if (StringUtils.isNotEmpty(departmentId)) {
						List<String> ids = business.identity().listWithDepartment(departmentId);
						/* 转换成Person */
						List<Identity> identities = emc.fetchAttribute(ids, Identity.class, "person");
						List<String> personIds = new ArrayList<>();
						for (Identity identity : identities) {
							personIds.add(identity.getPerson());
						}
						if (!personIds.isEmpty()) {
							for (Person person : emc.list(Person.class, personIds)) {
								WrapOutPerson wrap = new WrapOutPerson();
								wrap = copier.copy(person);
								wraps.add(wrap);
							}
							business.person().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Group name,列示直接个人成员.", response = WrapOutPerson.class)
	@GET
	@Path("list/group/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithGroupSubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithGroupSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						Group group = emc.find(groupId, Group.class);
						if (null != group && null != group.getPersonList() && (!group.getPersonList().isEmpty())) {
							for (Person o : emc.list(Person.class, group.getPersonList())) {
								WrapOutPerson wrap = copier.copy(o);
								wraps.add(wrap);
							}
							business.person().sort(wraps);
						}
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
	@HttpMethodDescribe(value = "根据给定的Group ID,列示嵌套的个人成员.", response = WrapOutPerson.class)
	@GET
	@Path("list/group/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithGroupSubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithGroupSubNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						Set<String> ids = new HashSet<>();
						Set<String> groupIds = new HashSet<>();
						groupIds.add(groupId);
						groupIds.addAll(business.group().listSubNested(groupId));
						for (String str : groupIds) {
							Group g = emc.find(str, Group.class);
							ids.addAll(g.getPersonList());
						}
						for (Person o : emc.list(Person.class, ids)) {
							WrapOutPerson wrap = copier.copy(o);
							wraps.add(wrap);
						}
						business.person().sort(wraps);
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
	@HttpMethodDescribe(value = " 获取拼音首字母开始的个人.", response = WrapOutPerson.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listPinyinInitial#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.person().listPinyinInitial(key);
					for (Person o : emc.list(Person.class, ids)) {
						WrapOutPerson wrap = copier.copy(o);
						wraps.add(wrap);
					}
					business.person().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutPerson.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.person().listLikePinyin(key);
					BeanCopyTools<Person, WrapOutPerson> copier = BeanCopyToolsBuilder.create(Person.class,
							WrapOutPerson.class, null, WrapOutPerson.Excludes);
					for (Person o : emc.list(Person.class, ids)) {
						WrapOutPerson wrap = copier.copy(o);
						wraps.add(wrap);
					}
					business.person().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutPerson.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		List<WrapOutPerson> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutPerson>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.person().listLike(key);
					for (Person o : emc.list(Person.class, ids)) {
						WrapOutPerson wrap = copier.copy(o);
						wraps.add(wrap);
					}
					business.person().sort(wraps);
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

	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutPerson.class)
	@GET
	@Path("list/login/recent/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLoginRecent(@PathParam("count") Integer count) {
		ActionResult<List<WrapOutPerson>> result = new ActionResult<>();
		try {
			result = new ActionListLoginRecent().execute(count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}