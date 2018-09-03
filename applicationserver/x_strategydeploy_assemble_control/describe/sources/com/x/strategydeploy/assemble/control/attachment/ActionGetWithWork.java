package com.x.strategydeploy.assemble.control.attachment;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.strategydeploy.assemble.control.Business;
import com.x.strategydeploy.core.entity.Attachment;
import com.x.strategydeploy.core.entity.KeyworkInfo;

public class ActionGetWithWork extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			KeyworkInfo work = emc.find(workId, KeyworkInfo.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			Attachment attachment = emc.find(id, Attachment.class);
			if (null == attachment) {
				throw new ExceptionAttachmentNotExist(id);
			}
//			WoWorkControl workControl = business.getControl(effectivePerson, work, WoWorkControl.class);
//			if (BooleanUtils.isNotTrue(workControl.getAllowVisit())) {
//				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
//						work.getId());
//			}
			if (!work.getAttachmentList().contains(id)) {
				throw new ExceptionMultiReferenced(attachment.getName(), attachment.getId());
			}
			Wo wo = Wo.copier.copy(attachment);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null, Wo.Excludes);

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	}

//	public static class WoWorkControl extends WorkControl {
//	}
}
