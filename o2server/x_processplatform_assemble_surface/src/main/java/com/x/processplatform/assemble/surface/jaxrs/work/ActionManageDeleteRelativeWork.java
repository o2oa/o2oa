package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageDeleteRelativeWork extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageDeleteRelativeWork.class);

	/* 为了和后面的全部删除对应,所以返回的是数组 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(id);
			}
			/* Process 也可能为空 */
			Process process = business.process().pick(work.getProcess());
			Application application = business.application().pick(work.getApplication());
			// 需要对这个应用的管理权限
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<Wo> wos = ThisApplication.context().applications()
					.deleteQuery(x_processplatform_service_processing.class,
							"job/" + URLEncoder.encode(work.getJob(), DefaultCharset.name))
					.getDataAsList(Wo.class);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -7848336872352149678L;

	}
}