package com.x.organization.assemble.express.jaxrs.role;

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

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutRole;
import com.x.organization.core.entity.Role;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("role")
public class RoleAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Role.class);

	@HttpMethodDescribe(value = "按名称查找人员,如果不存在,返回值不存在。", response = WrapOutRole.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutRole> result = new ActionResult<WrapOutRole>();
		WrapOutRole wrap = null;
		try {
			String cacheKey = "getWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutRole) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String role = business.role().getWithName(name);
					if (StringUtils.isNotEmpty(role)) {
						Role o = emc.find(role, Role.class);
						wrap = business.role().wrap(o);
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
	@HttpMethodDescribe(value = "根据给定的Person name,获取其拥有的角色对象.", response = WrapOutRole.class)
	@GET
	@Path("list/person/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPerson(@PathParam("name") String name) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPerson#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutRole>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						Set<String> ids = new HashSet<>();
						ids.addAll(business.role().listWithPerson(personId));
						Set<String> groups = new HashSet<>();
						for (String str : business.group().listWithPersonSupDirect(personId)) {
							groups.add(str);
							groups.addAll(business.group().listSupNested(str));
						}
						for (String str : groups) {
							ids.addAll(business.role().listWithGroup(str));
						}
						for (Role o : emc.list(Role.class, ids)) {
							WrapOutRole wrap = business.role().wrap(o);
							wraps.add(wrap);
						}
						business.role().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Group ID,获取其拥有的角色对象.", response = WrapOutRole.class)
	@GET
	@Path("list/group/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithGroup(@PathParam("name") String name) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithGroup#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutRole>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String group = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(group)) {
						Set<String> ids = new HashSet<>();
						Set<String> groups = new HashSet<>();
						groups.add(group);
						groups.addAll(business.group().listSupNested(group));
						for (String str : groups) {
							ids.addAll(business.role().listWithGroup(str));
						}
						for (Role o : emc.list(Role.class, ids)) {
							WrapOutRole wrap = business.role().wrap(o);
							wraps.add(wrap);
						}
						business.role().sort(wraps);
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
	@HttpMethodDescribe(value = "获取拼音首字母开始的Role.", response = WrapOutRole.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try {
			String cacheKey = "listPinyinInitial#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutRole>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.role().listPinyinInitial(key);
					for (Role o : emc.list(Role.class, ids)) {
						WrapOutRole wrap = business.role().wrap(o);
						wraps.add(wrap);
					}
					business.role().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutRole.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutRole>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.role().listLikePinyin(key);
					for (Role o : emc.list(Role.class, ids)) {
						WrapOutRole wrap = business.role().wrap(o);
						wraps.add(wrap);
					}
					business.role().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutRole.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutRole>> result = new ActionResult<>();
		List<WrapOutRole> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutRole>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.role().listLike(key);
					for (Role o : emc.list(Role.class, ids)) {
						WrapOutRole wrap = business.role().wrap(o);
						wraps.add(wrap);
					}
					business.role().sort(wraps);
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