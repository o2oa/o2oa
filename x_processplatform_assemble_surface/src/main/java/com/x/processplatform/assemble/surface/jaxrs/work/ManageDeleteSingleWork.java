package com.x.processplatform.assemble.surface.jaxrs.work;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;

class ManageDeleteSingleWork extends ActionBase {

	/* 为了和后面的全部删除对应,所以返回的是数组 */
	ActionResult<List<WrapOutId>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutId>> result = new ActionResult<>();
			List<WrapOutId> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class);
			if (null == work) {
				throw new WorkNotExistedException(id);
			}
			/* Process 也可能为空 */
			Process process = business.process().pick(work.getProcess());
			// 需要对这个应用的管理权限
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new ProcessAccessDeniedException(effectivePerson.getName(), process.getId());
			}
			ThisApplication.applications.deleteQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(work.getId(), DefaultCharset.name));
			wraps.add(new WrapOutId(work.getId()));
			result.setData(wraps);
			return result;
		}
	}

}