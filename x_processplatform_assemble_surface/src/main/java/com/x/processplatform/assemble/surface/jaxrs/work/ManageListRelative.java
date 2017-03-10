package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWork;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;

class ManageListRelative extends ActionBase {

	ActionResult<List<WrapOutWork>> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutWork>> result = new ActionResult<>();
			List<WrapOutWork> wraps = new ArrayList<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class, ExceptionWhen.not_found);
			Process process = business.process().pick(work.getProcess());
			// 需要对这个应用的管理权限
			if (!business.process().allowControl(effectivePerson, process)) {
				throw new ProcessAccessDeniedException(effectivePerson.getName(), process.getId());
			}
			List<String> ids = this.listRelative(business, work);
			for (String str : ids) {
				Work o = emc.find(str, Work.class);
				Control control = business.getControlOfWorkList(effectivePerson, o);
				WrapOutWork wrap = workOutCopier.copy(o);
				wrap.setControl(control);
				wraps.add(wrap);
			}
			SortTools.asc(wraps, "createTime");
			/* 添加权限 */
			result.setData(wraps);
			return result;
		}
	}

	private List<String> listRelative(Business business, Work work) throws Exception {
		List<String> list = business.work().listWithJob(work.getJob());
		List<String> ids = new ArrayList<>();
		for (String str : list) {
			// 排除自己
			if (!StringUtils.equals(work.getId(), str)) {
				ids.add(str);
			}
		}
		return ids;
	}
}