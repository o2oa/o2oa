package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionManageListRelative extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListRelative.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Work work = emc.find(id, Work.class, ExceptionWhen.not_found);
			Process process = business.process().pick(work.getProcess());
			Application application = business.application().pick(work.getApplication());
			// 需要对这个应用的管理权限
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, application, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> ids = this.listRelative(business, work);
			for (String str : ids) {
				Work o = emc.find(str, Work.class);
				Wo wo = Wo.copier.copy(o);
				wo.setControl(new WorkControlBuilder(effectivePerson, business, o).enableAll().build());
				wos.add(wo);
			}
			SortTools.asc(wos, "createTime");
			result.setData(wos);
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

	public static class Wo extends Work {

		private static final long serialVersionUID = -5668264661685818057L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<Work, Wo> copier = WrapCopierFactory.wo(Work.class, Wo.class, null, Excludes);

		@FieldDescribe("排序号")
		private Long rank;

		@FieldDescribe("权限")
		private Control control;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

}