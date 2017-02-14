package com.x.processplatform.assemble.designer.jaxrs.applicationcategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationCategory;

@Path("applicationcategory")
public class ApplicationCategoryAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "获取应用分类信息并统计同一分类的数量.", response = WrapOutApplicationCategory.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutApplicationCategory>> result = new ActionResult<>();
		List<WrapOutApplicationCategory> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			for (String str : business.application().listApplicationCategoryWithPerson(effectivePerson)) {
				WrapOutApplicationCategory wrap = new WrapOutApplicationCategory();
				wrap.setApplicationCategory(str);
				wrap.setCount(business.application().countWithPersonWithApplicationCategory(effectivePerson, str));
				wraps.add(wrap);
			}
			this.sort(wraps);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void sort(List<WrapOutApplicationCategory> list) {
		Collections.sort(list, new Comparator<WrapOutApplicationCategory>() {
			public int compare(WrapOutApplicationCategory o1, WrapOutApplicationCategory o2) {
				return ObjectUtils.compare(o1.getApplicationCategory(), o2.getApplicationCategory(), true);
			}
		});
	}
}