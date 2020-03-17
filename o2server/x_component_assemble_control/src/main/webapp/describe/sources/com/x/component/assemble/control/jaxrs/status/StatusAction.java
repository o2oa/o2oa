package com.x.component.assemble.control.jaxrs.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.component.assemble.control.Business;
import com.x.component.assemble.control.jaxrs.wrapout.WrapOutComponent;
import com.x.component.core.entity.Component;

@Path("status")
public class StatusAction extends StandardJaxrsAction {

	// @HttpMethodDescribe(value = "获取指定的应用信息.", response = WrapOutStatus.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void list(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<WrapOutStatus> result = new ActionResult<>();
		WrapOutStatus wrap = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new WrapOutStatus();
			wrap.setAllowList(new ArrayList<WrapOutComponent>());
			wrap.setDenyList(new ArrayList<WrapOutComponent>());
			List<String> ids = business.component().listVisiable();
			List<Component> components = new ArrayList<Component>(emc.list(Component.class, ids));
			Collections.sort(components, new Comparator<Component>() {
				public int compare(Component o1, Component o2) {
					return ObjectUtils.compare(o1.getOrderNumber(), o2.getOrderNumber());
				}
			});
			WrapCopier<Component, WrapOutComponent> copier = WrapCopierFactory.wo(Component.class,
					WrapOutComponent.class, null, WrapOutComponent.Excludes);
			for (Component o : components) {
				WrapOutComponent wrapOutComponent = new WrapOutComponent();
				copier.copy(o, wrapOutComponent);
				if (this.allow(o, effectivePerson.getDistinguishedName())) {
					wrap.getAllowList().add(wrapOutComponent);
				} else {
					wrap.getDenyList().add(wrapOutComponent);
				}
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	private boolean allow(Component component, String person) throws Exception {
		if ((component.getAllowList().isEmpty()) && (component.getDenyList().isEmpty())) {
			return true;
		}
		if (component.getAllowList().contains(person)) {
			return true;
		}
		if (component.getDenyList().contains(person)) {
			return false;
		}
		return false;
	}
}