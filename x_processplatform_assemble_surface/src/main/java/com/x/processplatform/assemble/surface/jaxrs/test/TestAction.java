package com.x.processplatform.assemble.surface.jaxrs.test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

@Path("test")
public class TestAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "空表进行group by 报错.", response = Object.class)
	@GET
	@Path("test1")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test1(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Work> root = cq.from(Work.class);
			cq.select(root.get(Work_.id)).groupBy(root.get(Work_.id));
			em.createQuery(cq).getResultList();
			// result.setData(count);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "count group by 只返回一行.", response = Object.class)
	@GET
	@Path("test2")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test2(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try {
			result = new ActionTest2().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "测试null是否能参与比较.", response = Object.class)
	@GET
	@Path("test3")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test3(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try {
			result = new ActionTest3().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "conjunction 和 disjunction 测试.", response = Object.class)
	@GET
	@Path("test4")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test4(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try {
			result = new ActionTest4().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "conjunction 和 disjunction 测试.", response = Object.class)
	@GET
	@Path("test5")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test5(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Process.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Process> cq = cb.createQuery(Process.class);
			Root<Process> root = cq.from(Process.class);
			Predicate p = cb.disjunction();
			p = cb.and(p, cb.notEqual(root.get(Process_.name), "aaa"));
			cq.select(root).where(p);
			List<Process> list = em.createQuery(cq).getResultList();
			result.setData(list);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "测试multislect是否能distinct.", response = Object.class)
	@GET
	@Path("test6")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test6(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Work.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<Work> root = cq.from(Work.class);
			Predicate p = cb.conjunction();
			cq.multiselect(root.get(Work_.process), root.get(Work_.processName), root.get(Work_.activityName))
					.distinct(true).where(p);
			List<Tuple> list = em.createQuery(cq).getResultList();
			result.setData(list);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "测试isMember.", response = Object.class)
	@GET
	@Path("test7")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test7(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Application.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Application> cq = cb.createQuery(Application.class);
			Root<Application> root = cq.from(Application.class);
			List<String> names = new ArrayList<>();
			names.add("林玲(development)");
			names.add("胡起(development)");
			Predicate p = root.get(Application_.availableIdentityList).in(names);
			cq.select(root).where(p);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(cq.toString());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			List<Application> list = em.createQuery(cq).getResultList();
			result.setData(list);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "测试isMember.", response = Object.class)
	@GET
	@Path("test8")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test8(@Context HttpServletRequest request) {
		ActionResult<Object> result = new ActionResult<>();
		try {
			result = new ActionTest8().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
