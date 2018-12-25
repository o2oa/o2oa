package com.x.file.assemble.control.jaxrs.editor;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			for (String str : business.attachment().listPersonWithEditor(effectivePerson.getDistinguishedName())) {
				Wo wo = new Wo();
				wo.setName(str);
				wo.setValue(str);
				wo.setCount(
						business.attachment().countWithPersonWithEditor(str, effectivePerson.getDistinguishedName()));
				wos.add(wo);
			}
			result.setData(wos);
			return result;
		}
	}

	public class Wo extends GsonPropertyObject {

		@FieldDescribe("名称")
		private String name;

		@FieldDescribe("值")
		private String value;

		@FieldDescribe("数量")
		private Long count;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
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
