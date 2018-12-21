package com.x.strategydeploy.assemble.control.attachment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

public class ActionListWithWork extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			KeyworkInfo work = emc.find(workId, KeyworkInfo.class);
			/** 判断work是否存在 */
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
//			WoControl control = business.getControl(effectivePerson, work, WoControl.class);
//			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
//				throw new ExceptionWorkAccessDenied(effectivePerson.getDistinguishedName(), work.getTitle(),
//						work.getId());
//			}
			List<Attachment> os = emc.list(Attachment.class, work.getAttachmentList());
			List<Wo> wos = Wo.copier.copy(os);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getCreateTime)).collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo(Attachment.class, Wo.class, null, Wo.Excludes,
				true);

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

//	public static class WoControl extends WorkControl {
//	}

}
