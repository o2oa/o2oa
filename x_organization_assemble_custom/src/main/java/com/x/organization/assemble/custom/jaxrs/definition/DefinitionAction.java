package com.x.organization.assemble.custom.jaxrs.definition;

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
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.core.entity.Definition;
import com.x.organization.core.entity.Definition_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("definition")
public class DefinitionAction extends StandardJaxrsAction {

	private Ehcache cache = ApplicationCache.instance().getCache(Definition.class);

	@HttpMethodDescribe(value = "根据name获取Definition。", response = String.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<String> result = new ActionResult<>();
		String wrap = null;
		try {
			String cacheKey = name;
			Element element = cache.get(cacheKey);
			if (null != element) {
				wrap = (String) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Definition o = this.getWithName(emc, name);
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

	@HttpMethodDescribe(value = "更新指定名称的Definition.", response = WrapOutId.class)
	@PUT
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("name") String name, String wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Definition definition = this.getWithName(emc, name);
			emc.beginTransaction(Definition.class);
			if (null != definition) {
				definition.setData(wrapIn);
				emc.check(definition, CheckPersistType.all);
			} else {
				definition = new Definition();
				definition.setData(wrapIn);
				definition.setName(name);
				emc.persist(definition, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(Definition.class);
			wrap = new WrapOutId(definition.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定名称的Definition。", response = WrapOutId.class)
	@DELETE
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Definition o = this.getWithName(emc, name);
			if (null != o) {
				emc.beginTransaction(Definition.class);
				emc.remove(o, CheckRemoveType.all);
				emc.commit();
				ApplicationCache.notify(Definition.class);
				wrap = new WrapOutId(o.getId());
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private Definition getWithName(EntityManagerContainer emc, String name) throws Exception {
		try {
			EntityManager em = emc.get(Definition.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Definition> cq = cb.createQuery(Definition.class);
			Root<Definition> root = cq.from(Definition.class);
			Predicate p = cb.equal(root.get(Definition_.name), name);
			List<Definition> list = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
			if (list.isEmpty()) {
				return null;
			} else {
				return list.get(0);
			}
		} catch (Exception e) {
			throw new Exception("getWithName error.", e);
		}
	}
}