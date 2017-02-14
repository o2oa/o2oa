package com.x.organization.assemble.custom.jaxrs.custom;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Custom_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("custom")
public class CustomAction extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Custom.class);

	@HttpMethodDescribe(value = "根据当前的访问用户获取Custom。", response = String.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<String> result = new ActionResult<>();
		String wrap = null;
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			String cacheKey = this.getCustomKey(effectivePerson.getName(), name);
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (String) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Custom o = this.getWithName(emc, effectivePerson.getName(), name);
					if (null != o) {
						wrap = o.getData();
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

	@HttpMethodDescribe(value = "更新指定名称的Custom.", response = WrapOutId.class)
	@PUT
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("name") String name, String wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Custom custom = this.getWithName(emc, effectivePerson.getName(), name);
				emc.beginTransaction(Custom.class);
				if (null != custom) {
					custom.setData(wrapIn);
					emc.check(custom, CheckPersistType.all);
				} else {
					custom = new Custom();
					custom.setPerson(effectivePerson.getName());
					custom.setName(name);
					custom.setData(wrapIn);
					emc.persist(custom, CheckPersistType.all);
				}
				emc.commit();
				/* 仅刷新当前用户的Cache */
				ApplicationCache.notify(Custom.class, this.getCustomKey(effectivePerson.getName(), name));
				wrap = new WrapOutId(custom.getId());
				result.setData(wrap);
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定名称的Custom。", response = WrapOutId.class)
	@DELETE
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Custom o = this.getWithName(emc, effectivePerson.getName(), name);
			if (null != o) {
				emc.beginTransaction(Custom.class);
				emc.remove(o);
				emc.commit();
				/* 仅刷新当前用户的Cache */
				ApplicationCache.notify(Custom.class, this.getCustomKey(effectivePerson.getName(), name));
				wrap = new WrapOutId(o.getId());
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@MethodDescribe("根据Person查找Custom")
	private Custom getWithName(EntityManagerContainer emc, String person, String name) throws Exception {
		try {
			EntityManager em = emc.get(Custom.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Custom> cq = cb.createQuery(Custom.class);
			Root<Custom> root = cq.from(Custom.class);
			Predicate p = cb.equal(root.get(Custom_.person), person);
			p = cb.and(p, cb.equal(root.get(Custom_.name), name));
			List<Custom> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
			if (list.isEmpty()) {
				return null;
			} else {
				return list.get(0);
			}
		} catch (Exception e) {
			throw new Exception("getWithName error.", e);
		}
	}

	@MethodDescribe("根据Person查找Custom")
	private List<String> listName(EntityManagerContainer emc, String person) throws Exception {
		try {
			EntityManager em = emc.get(Custom.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Custom> root = cq.from(Custom.class);
			Predicate p = cb.equal(root.get(Custom_.person), person);
			cq.select(root.get(Custom_.name)).where(p);
			return em.createQuery(cq.where(p)).getResultList();
		} catch (Exception e) {
			throw new Exception("listName error.", e);
		}
	}

	private String getCustomKey(String person, String name) throws Exception {
		return person + "#" + name;
	}
}