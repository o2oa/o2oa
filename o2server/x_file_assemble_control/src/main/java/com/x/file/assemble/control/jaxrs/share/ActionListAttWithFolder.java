package com.x.file.assemble.control.jaxrs.share;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionListAttWithFolder extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson,String shareId, String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Share share = emc.find(shareId, Share.class);
			if (null == share) {
				throw new ExceptionAttachmentNotExist(shareId);
			}
			Folder2 folder = emc.find(folderId, Folder2.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(folderId);
			}
			if(!"password".equals(share.getShareType())) {
				if(!hasPermission(business,effectivePerson,share)){
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			List<String> ids = business.attachment2().listWithFolder(folder.getId(),"正常");
			List<Wo> wos = Wo.copier.copy(emc.list(Attachment2.class, ids));
			SortTools.asc(wos, false, "name");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Attachment2 {

		private static final long serialVersionUID = -531053101150157872L;

		static WrapCopier<Attachment2, Wo> copier = WrapCopierFactory.wo(Attachment2.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
