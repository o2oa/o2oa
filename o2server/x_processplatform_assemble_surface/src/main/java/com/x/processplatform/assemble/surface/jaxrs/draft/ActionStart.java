package com.x.processplatform.assemble.surface.jaxrs.draft;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionCreateWi;

class ActionStart extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionStart.class);

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<JsonElement> result = new ActionResult<>();
		Process process = null;
		Draft draft = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			draft = emc.find(id, Draft.class);
			if (null == draft) {
				throw new ExceptionEntityNotExist(id, Draft.class);
			}
			if ((!effectivePerson.isPerson(draft.getPerson())) && (!business
					.ifPersonCanManageApplicationOrProcess(effectivePerson, draft.getApplication(), draft.getProcess()))) {
				throw new ExceptionAccessDenied(effectivePerson, draft);
			}
			Application application = business.application().pick(draft.getApplication());
			if (null == application) {
				throw new ExceptionEntityNotExist(draft.getApplication(), Application.class);
			}
			process = business.process().pick(draft.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(draft.getProcess(), Process.class);
			}
			if (StringUtils.isNotEmpty(process.getEdition()) && BooleanUtils.isFalse(process.getEditionEnable())) {
				process = business.process().pickEnabled(process.getApplication(), process.getEdition());
			}

		}
		ActionCreateWi req = new ActionCreateWi();

		req.setData(XGsonBuilder.instance().toJsonTree(draft.getProperties().getData()));
		req.setIdentity(draft.getIdentity());
		req.setLatest(false);
		req.setTitle(draft.getTitle());

		// 创建工作
		JsonElement jsonElement = ThisApplication.context().applications()
				.postQuery(x_processplatform_assemble_surface.class,
						Applications.joinQueryUri("work", "process", process.getId()), req, process.getId())
				.getData();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			draft = emc.find(id, Draft.class);
			emc.beginTransaction(Draft.class);
			emc.remove(draft, CheckRemoveType.all);
			emc.commit();
		}

		result.setData(jsonElement);
		return result;
	}

}
