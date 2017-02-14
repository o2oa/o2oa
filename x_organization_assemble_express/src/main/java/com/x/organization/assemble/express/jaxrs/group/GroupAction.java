package com.x.organization.assemble.express.jaxrs.group;

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
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("group")
public class GroupAction extends AbstractJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Group.class);

	@HttpMethodDescribe(value = "按名称查询Group.", response = WrapOutGroup.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@PathParam("name") String name) {
		ActionResult<WrapOutGroup> result = new ActionResult<>();
		WrapOutGroup wrap = null;
		try {
			String cacheKey = "getWithName#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (WrapOutGroup) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Company */
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						Group group = emc.find(groupId, Group.class);
						if (null != group) {
							wrap = business.group().wrap(group);
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
	@HttpMethodDescribe(value = "根据给定的Group ID获取其所有的直接上级群组.", response = WrapOutGroup.class)
	@GET
	@Path("list/{name}/sup/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSupDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSupDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Company */
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						List<String> ids = business.group().listSupDirect(groupId);
						if (!ids.isEmpty()) {
							for (Group o : emc.list(Group.class, ids)) {
								WrapOutGroup wrap = business.group().wrap(o);
								wraps.add(wrap);
							}
							business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Group ID获取其所有的上级群组.包括嵌套的群组", response = WrapOutGroup.class)
	@GET
	@Path("list/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSupNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSupDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					/* 按名称查找Group */
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						List<String> ids = business.group().listSupNested(groupId);
						for (Group o : emc.list(Group.class, ids)) {
							WrapOutGroup wrap = business.group().wrap(o);
							wraps.add(wrap);
						}
						business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Person ID获取其所在的直接群组对象.", response = WrapOutGroup.class)
	@GET
	@Path("list/person/{name}/sup/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonSupDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPersonSupDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						List<String> ids = business.group().listWithPersonSupDirect(personId);
						for (Group o : emc.list(Group.class, ids)) {
							WrapOutGroup wrap = business.group().wrap(o);
							wraps.add(wrap);
						}
						business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Person name获取其所在的群组对象,包括嵌套的上级群组对象.", response = WrapOutGroup.class)
	@GET
	@Path("list/person/{name}/sup/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPersonSupNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listWithPersonSupNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String personId = business.person().getWithName(name);
					if (StringUtils.isNotEmpty(personId)) {
						Set<String> ids = new HashSet<>();
						for (String str : business.group().listWithPersonSupDirect(personId)) {
							ids.add(str);
							ids.addAll(business.group().listSupNested(str));
						}
						for (Group o : emc.list(Group.class, ids)) {
							WrapOutGroup wrap = business.group().wrap(o);
							wraps.add(wrap);
						}
						business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Person ID获取其所直接包含的群组对象.", response = WrapOutGroup.class)
	@GET
	@Path("list/{name}/sub/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubDirect(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubDirect#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						List<String> ids = business.group().listSubDirect(groupId);
						for (Group o : emc.list(Group.class, ids)) {
							WrapOutGroup wrap = business.group().wrap(o);
							wraps.add(wrap);
						}
						business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据给定的Group ID获取其所包含的下级群组对象,包括嵌套的群组对象.", response = WrapOutGroup.class)
	@GET
	@Path("list/{name}/sub/nested")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubNested(@PathParam("name") String name) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubNested#" + name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					String groupId = business.group().getWithName(name);
					if (StringUtils.isNotEmpty(groupId)) {
						List<String> ids = business.group().listSubNested(groupId);
						for (Group o : emc.list(Group.class, ids)) {
							business.group().wrap(o);
						}
						business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "获取拼音首字母开始的群组.", response = WrapOutGroup.class)
	@GET
	@Path("list/pinyininitial/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPinyinInitial(@PathParam("key") String key) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listSubNested#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.group().listPinyinInitial(key);
					for (Group o : emc.list(Group.class, ids)) {
						WrapOutGroup wrap = business.group().wrap(o);
						wraps.add(wrap);
					}
					business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "根据拼音或者首字母搜索.", response = WrapOutGroup.class)
	@GET
	@Path("list/like/pinyin/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLikePinyin(@PathParam("key") String key) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLikePinyin#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.group().listLikePinyin(key);
					for (Group o : emc.list(Group.class, ids)) {
						WrapOutGroup wrap = business.group().wrap(o);
						wraps.add(wrap);
					}
					business.group().sort(wraps);
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
	@HttpMethodDescribe(value = "进行模糊查询.", response = WrapOutGroup.class)
	@GET
	@Path("list/like/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLike(@PathParam("key") String key) {
		ActionResult<List<WrapOutGroup>> result = new ActionResult<>();
		List<WrapOutGroup> wraps = new ArrayList<>();
		try {
			String cacheKey = "listLike#" + key;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wraps = (List<WrapOutGroup>) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<String> ids = business.group().listLike(key);
					for (Group o : emc.list(Group.class, ids)) {
						WrapOutGroup wrap = business.group().wrap(o);
						wraps.add(wrap);
					}
					business.group().sort(wraps);
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