package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.service.FileCommonService;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.BooleanUtils;

abstract class BaseAction extends StandardJaxrsAction {

	protected FileCommonService fileCommonService = new FileCommonService();

	protected void setExtendInfo(Business business, AbstractWoFolder wo, String person, boolean isManager) throws Exception {
		wo.setAttachmentCount(business.attachment3().countWithFolder(wo.getId()));
		wo.setFolderCount(business.folder3().countSubDirect(wo.getId()));
		if(person!=null) {
			wo.setIsCreator(wo.getPerson().equals(person));
			if (isManager) {
				wo.setIsAdmin(true);
				wo.setIsEditor(true);
				wo.setDownloadable(true);
			} else {
				boolean isAdmin = business.folder3().isZoneAdmin(wo.getId(), person);
				if (isAdmin) {
					wo.setIsAdmin(true);
					wo.setIsEditor(true);
				} else {
					wo.setIsEditor(business.folder3().isZoneEditor(wo.getId(), person));
				}
				if(BooleanUtils.isTrue(wo.getIsEditor())){
					wo.setDownloadable(true);
				}else{
					wo.setDownloadable(business.folder3().isZoneReader(wo.getId(), person));
				}
			}
		}

	}

	public static class AbstractWoFolder extends Folder3 {

		private static final long serialVersionUID = -3416878548938205004L;

		@FieldDescribe("附件数量")
		private Long attachmentCount;
		@FieldDescribe("目录数量")
		private Long folderCount;
		@FieldDescribe("是否管理员")
		private Boolean isAdmin = false;
		@FieldDescribe("是否编辑着")
		private Boolean isEditor = false;
		@FieldDescribe("是否创建着")
		private Boolean isCreator = false;
		@FieldDescribe("是否可下载")
		private Boolean downloadable = false;

		public Boolean getIsAdmin() {
			return isAdmin;
		}

		public void setIsAdmin(Boolean isAdmin) {
			this.isAdmin = isAdmin;
		}

		public Boolean getIsEditor() {
			return isEditor;
		}

		public void setIsEditor(Boolean isEditor) {
			this.isEditor = isEditor;
		}

		public Boolean getIsCreator() {
			return isCreator;
		}

		public void setIsCreator(Boolean isCreator) {
			this.isCreator = isCreator;
		}

		public Long getAttachmentCount() {
			return attachmentCount;
		}

		public void setAttachmentCount(Long attachmentCount) {
			this.attachmentCount = attachmentCount;
		}

		public Long getFolderCount() {
			return folderCount;
		}

		public void setFolderCount(Long folderCount) {
			this.folderCount = folderCount;
		}

		public Boolean getDownloadable() {
			return downloadable;
		}

		public void setDownloadable(Boolean downloadable) {
			this.downloadable = downloadable;
		}
	}

}
