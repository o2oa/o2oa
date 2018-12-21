package com.x.processplatform.assemble.designer.jaxrs.process.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.x.processplatform.core.entity.element.ManualMode;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;

@Path("process/demo/simple")
public class DemoSimpleAction extends StandardJaxrsAction {

	// @HttpMethodDescribe(value = "创建一个简单流程.", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post_demo(@Context HttpServletRequest request, WrapInDemoSimple wrapIn) {
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
			Process process = new Process();
			wrapIn.copyTo(process, "id");
			process.setCreatorPerson(effectivePerson.getDistinguishedName());
			process.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			process.setLastUpdateTime(new Date());
			List<SliceJpaObject> elements = new ArrayList<>();
			Begin begin = new Begin();
			begin.setName("start");
			elements.add(begin);
			Manual manual1 = new Manual();
			manual1.setName("draft");
			manual1.setManualMode(ManualMode.single);
			elements.add(manual1);
			Manual manual2 = new Manual();
			manual2.setName("audit");
			manual2.setManualMode(ManualMode.single);
			elements.add(manual2);
			End end = new End();
			end.setName("end");
			elements.add(end);
			Route route_begin_manual1 = new Route();
			route_begin_manual1.setActivityType(ActivityType.manual);
			route_begin_manual1.setActivity(manual1.getId());
			begin.setRoute(route_begin_manual1.getId());
			elements.add(route_begin_manual1);
			Route route_manual1_manual2 = new Route();
			route_manual1_manual2.setName("send to audit");
			route_manual1_manual2.setActivityType(ActivityType.manual);
			route_manual1_manual2.setActivity(manual2.getId());
			manual1.setRouteList(new ArrayList<String>());
			manual1.getRouteList().add(route_manual1_manual2.getId());
			elements.add(route_manual1_manual2);
			Route route_manual2_end = new Route();
			route_manual2_end.setActivityType(ActivityType.end);
			route_manual2_end.setActivity(end.getId());
			manual2.setRouteList(new ArrayList<String>());
			manual2.getRouteList().add(route_manual2_end.getId());
			elements.add(route_manual2_end);
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Route.class);
			emc.persist(process, CheckPersistType.all);
			for (SliceJpaObject o : elements) {
				PropertyUtils.setProperty(o, "process", process.getId());
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

}
