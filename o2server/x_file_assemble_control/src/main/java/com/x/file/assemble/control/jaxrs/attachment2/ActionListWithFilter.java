package com.x.file.assemble.control.jaxrs.attachment2;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;

class ActionListWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithFilter.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String name) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.attachment2().listWithName(effectivePerson.getDistinguishedName(), name);
			List<Wo> wos = Wo.copier.copy(emc.list(Attachment2.class, ids));
			wos.stream().forEach(wo -> {
				try {
					wo.setPath(business.folder2().getSupPath(wo.getFolder()));
				} catch (Exception e) {
					logger.error(e);
				}
			});
			SortTools.desc(wos, false, "createTime");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Attachment2 {

		private static final long serialVersionUID = -531053101150157872L;

		static WrapCopier<Attachment2, Wo> copier = WrapCopierFactory.wo(Attachment2.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("文件路径")
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}
}
