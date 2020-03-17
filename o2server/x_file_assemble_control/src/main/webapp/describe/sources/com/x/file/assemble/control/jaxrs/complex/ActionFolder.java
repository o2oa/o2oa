package com.x.file.assemble.control.jaxrs.complex;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

class ActionFolder extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Folder folder = emc.fetch(id, Folder.class, ListTools.toList(Folder.person_FIELDNAME));
			if (null == folder) {
				throw new ExceptionFolderNotExist(id);
			}
			if (!StringUtils.equals(folder.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionFolderAccessDenied(effectivePerson, folder);
			}
			List<WoAttachment> woAttachments = WoAttachment.copier
					.copy(emc.list(Attachment.class, business.attachment().listWithFolder(folder.getId())));
			List<WoFolder> woFolders = WoFolder.copier.copy(emc.list(Folder.class, business.folder()
					.listWithPersonWithSuperior(effectivePerson.getDistinguishedName(), folder.getId())));
			SortTools.asc(woAttachments, false, "name");
			SortTools.asc(woFolders, false, "name");
			Wo wo = new Wo();
			wo.setAttachmentList(woAttachments);
			wo.setFolderList(woFolders);
			result.setData(wo);
			return result;
		}
	}

	public class Wo extends GsonPropertyObject {

		List<WoAttachment> attachmentList;
		List<WoFolder> folderList;

		public List<WoAttachment> getAttachmentList() {
			return attachmentList;
		}

		public void setAttachmentList(List<WoAttachment> attachmentList) {
			this.attachmentList = attachmentList;
		}

		public List<WoFolder> getFolderList() {
			return folderList;
		}

		public void setFolderList(List<WoFolder> folderList) {
			this.folderList = folderList;
		}

	}

	public static class WoAttachment extends Attachment {

		private static final long serialVersionUID = -531053101150157872L;

		static WrapCopier<Attachment, WoAttachment> copier = WrapCopierFactory.wo(Attachment.class, WoAttachment.class,
				null, JpaObject.FieldsInvisible);

		private String contentType;

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

	}

	public static class WoFolder extends Folder {

		private static final long serialVersionUID = -3416878548938205004L;

		static WrapCopier<Folder, WoFolder> copier = WrapCopierFactory.wo(Folder.class, WoFolder.class, null,
				JpaObject.FieldsInvisible);

		private Long attachmentCount;
		private Long size;
		private Long folderCount;

		public Long getAttachmentCount() {
			return attachmentCount;
		}

		public void setAttachmentCount(Long attachmentCount) {
			this.attachmentCount = attachmentCount;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		public Long getFolderCount() {
			return folderCount;
		}

		public void setFolderCount(Long folderCount) {
			this.folderCount = folderCount;
		}

	}

}
