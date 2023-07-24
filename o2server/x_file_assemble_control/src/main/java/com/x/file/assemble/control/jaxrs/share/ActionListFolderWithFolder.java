package com.x.file.assemble.control.jaxrs.share;

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
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Share;

class ActionListFolderWithFolder extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String shareId, String folderId) throws Exception {
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
			List<String> ids = business.folder2().listSubDirect(folder.getId(), FileStatus.VALID.getName());
			List<Wo> wos = emc.fetch(ids, Wo.copier);
			wos = wos.stream().sorted(Comparator.comparing(Folder2::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Folder2 {

		private static final long serialVersionUID = 6721942171341743439L;

		protected static WrapCopier<Folder2, Wo> copier = WrapCopierFactory.wo(Folder2.class, Wo.class,
				JpaObject.singularAttributeField(Folder2.class, true, true), null);

	}

}