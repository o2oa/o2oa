package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

class ActionListWithFolder extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListWithFolder.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String folderId, String orderBy, Boolean desc) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder3 folder = emc.find(folderId, Folder3.class);
			if (null == folder) {
				throw new ExceptionFolderNotExist(folderId);
			}
			String zoneId = business.getSystemConfig().getReadPermissionDown() ? folderId : folder.getZoneId();
			boolean isManager = business.controlAble(effectivePerson);
			if(!isManager){
				boolean isZoneReader = business.folder3().isZoneViewer(zoneId, effectivePerson.getDistinguishedName());
				if(!isZoneReader) {
					throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
				}
			}
			List<String> ids = business.attachment3().listWithFolder(folder.getId(), FileStatus.VALID.getName());
			List<Wo> wos = new ArrayList<>();
			if(ListTools.isNotEmpty(ids)) {
				boolean isAdmin;
				boolean isEditor;
				boolean downloadable;
				if (isManager) {
					isAdmin = true;
					isEditor = true;
					downloadable = true;
				} else {
					isAdmin = business.folder3().isZoneAdmin(folder.getId(), effectivePerson.getDistinguishedName());
					if (isAdmin) {
						isEditor = true;
					} else {
						isEditor = business.folder3().isZoneEditor(folder.getId(), effectivePerson.getDistinguishedName());
					}
					if(isEditor){
						downloadable = true;
					}else{
						downloadable = business.folder3().isZoneReader(folder.getId(), effectivePerson.getDistinguishedName());
					}
				}
				final boolean isAdminUser = isAdmin;
				final boolean isEditorUser = isEditor;
				final boolean downloadableUser = downloadable;
				wos = emc.fetch(ids, Wo.copier);
				wos.forEach(wo -> {
					try {
						setExtendInfo(business, wo, null);
						wo.setIsCreator(wo.getPerson().equals(effectivePerson.getDistinguishedName()));
						wo.setIsAdmin(isAdminUser);
						wo.setIsEditor(isEditorUser);
						if(!isAdminUser && !wo.getPerson().equals(effectivePerson.getDistinguishedName())){
							wo.setIsEditor(false);
						}
						wo.setDownloadable(downloadableUser);
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
				});
				if(Wo.copier.getCopyFields().contains(orderBy)) {
					if (BooleanUtils.isTrue(desc)) {
						SortTools.desc(wos, false, orderBy);
					} else {
						SortTools.asc(wos, false, orderBy);
					}
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WrapAttachment3 {

		private static final long serialVersionUID = 542647033374509304L;

		static WrapCopier<Attachment3, Wo> copier = WrapCopierFactory.wo(Attachment3.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
