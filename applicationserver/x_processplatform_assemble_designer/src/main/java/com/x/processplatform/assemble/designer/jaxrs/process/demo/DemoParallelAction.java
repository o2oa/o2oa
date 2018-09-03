package com.x.processplatform.assemble.designer.jaxrs.process.demo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.PropertyUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.core.entity.element.Route;

@Path("process/demo/split")
public class DemoParallelAction extends StandardJaxrsAction {

	// @JaxrsMethodDescribe(value = "创建一个带拆分节点的简单流程.", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post_demo(@Context HttpServletRequest request, WrapInDemoSplit wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Application application = emc.find(wrapIn.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(wrapIn.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Merge.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Route.class);
			Process process = new Process();
			wrapIn.copyTo(process);
			if (processExistedWithNameApplication(emc, process.getName(), process.getApplication())) {
				throw new Exception("process{name:" + process.getName() + ", application:" + process.getApplication()
						+ "} already existed.");
			}
			List<SliceJpaObject> elements = new ArrayList<>();
			Begin begin = new Begin();
			begin.setName("start");
			elements.add(begin);
			Manual manual1 = new Manual();
			manual1.setName("draft");
			elements.add(manual1);
			Parallel parallel = new Parallel();
			parallel.setName("parallel to one and tow");
			elements.add(parallel);
			Manual manual2 = new Manual();
			manual2.setName("auditOne");
			elements.add(manual2);
			Manual manual3 = new Manual();
			manual3.setName("auditTwo");
			elements.add(manual3);
			Merge merge = new Merge();
			merge.setName("merge one and tow");
			elements.add(merge);
			Manual manual4 = new Manual();
			manual4.setName("check");
			elements.add(manual4);
			End end = new End();
			end.setName("end");
			elements.add(end);
			Route route_begin_manual1 = new Route();
			route_begin_manual1.setActivityType(ActivityType.manual);
			route_begin_manual1.setActivity(manual1.getId());
			begin.setRoute(route_begin_manual1.getId());
			elements.add(route_begin_manual1);
			Route route_manual1_parallel = new Route();
			route_manual1_parallel.setName("send to split");
			route_manual1_parallel.setActivityType(ActivityType.split);
			route_manual1_parallel.setActivity(parallel.getId());
			manual1.getRouteList().add(route_manual1_parallel.getId());
			elements.add(route_manual1_parallel);
			Route route_parallel_manual2 = new Route();
			route_parallel_manual2.setName("to manual2");
			route_parallel_manual2.setActivityType(ActivityType.manual);
			route_parallel_manual2.setActivity(manual2.getId());
			parallel.getRouteList().add(route_parallel_manual2.getId());
			elements.add(route_parallel_manual2);
			Route route_parallel_manual3 = new Route();
			route_parallel_manual3.setName("to manual3");
			route_parallel_manual3.setActivityType(ActivityType.manual);
			route_parallel_manual3.setActivity(manual3.getId());
			parallel.getRouteList().add(route_parallel_manual3.getId());
			elements.add(route_parallel_manual3);
			Route route_manual2_merge = new Route();
			route_manual2_merge.setName("manual2 to merge");
			route_manual2_merge.setActivityType(ActivityType.merge);
			route_manual2_merge.setActivity(merge.getId());
			manual2.getRouteList().add(route_manual2_merge.getId());
			elements.add(route_manual2_merge);
			Route route_manual3_merge = new Route();
			route_manual3_merge.setName("manual3 to merge");
			route_manual3_merge.setActivityType(ActivityType.merge);
			route_manual3_merge.setActivity(merge.getId());
			manual3.getRouteList().add(route_manual3_merge.getId());
			elements.add(route_manual3_merge);
			Route route_merge_manual4 = new Route();
			route_merge_manual4.setName("send to check");
			route_merge_manual4.setActivityType(ActivityType.manual);
			route_merge_manual4.setActivity(manual4.getId());
			merge.setRoute(route_merge_manual4.getId());
			elements.add(route_merge_manual4);
			Route route_manual4_end = new Route();
			route_manual4_end.setName("finished");
			route_manual4_end.setActivityType(ActivityType.end);
			route_manual4_end.setActivity(end.getId());
			manual4.getRouteList().add(route_manual4_end.getId());
			elements.add(route_manual4_end);
			emc.persist(process, CheckPersistType.all);
			for (SliceJpaObject o : elements) {
				PropertyUtils.setProperty(o, "process", process.getId());
				PropertyUtils.setProperty(o, "distributeFactor", process.getDistributeFactor());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			result.setData(new WrapOutId(process.getId()));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private boolean processExistedWithNameApplication(EntityManagerContainer emc, String name, String application)
			throws Exception {
		EntityManager em = emc.get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Process> root = cq.from(Process.class);
		Predicate predicate = cb.equal(root.get(Process_.application), application);
		predicate = cb.and(predicate, cb.equal(root.get(Process_.name), name));
		cq.select(cb.count(root));
		Long count = em.createQuery(cq.where(predicate)).getSingleResult();
		return (count > 0);
	}
}
