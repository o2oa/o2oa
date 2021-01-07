package com.x.processplatform.service.processing.jaxrs.test;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.core.entity.content.Task;

class ActionTest extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends Task {

	}

}