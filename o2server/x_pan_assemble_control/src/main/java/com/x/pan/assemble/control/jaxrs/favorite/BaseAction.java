package com.x.pan.assemble.control.jaxrs.favorite;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.service.FileCommonService;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZonePermission;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

	protected FileCommonService fileCommonService = new FileCommonService();

		protected void setCount(Business business, AbstractWoFolder wo) throws Exception {
			List<String> ids = business.attachment2().listWithFolder(wo.getId(), FileStatus.VALID.getName());
			long count = 0;
			long size = 0;
			for (Attachment3 o : business.entityManagerContainer().fetch(ids, Attachment3.class,
					ListTools.toList(Attachment3.length_FIELDNAME))) {
				count++;
				size += o.getLength();
			}
			wo.setAttachmentCount(count);
			wo.setSize(size);
			wo.setFolderCount(business.folder2().countSubDirect(wo.getId()));
		}

		public static class AbstractWoFolder extends Folder3 {

			private static final long serialVersionUID = -3416878548938205004L;

			@FieldDescribe("附件数量")
			private Long attachmentCount;
			@FieldDescribe("字节数")
			private Long size;
			@FieldDescribe("目录数量")
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

		public static class Zone extends Folder3 {

			@FieldDescribe("是否是管理员")
			private Boolean isAdmin = false;
			@FieldDescribe("权限列表")
			private List<WoZonePermission> zonePermissionList;
			@FieldDescribe("使用容量")
			private Long usedCapacity;
			@FieldDescribe("是否是共享区")
			private Boolean isZone = true;

			public Boolean getIsAdmin() {
				return isAdmin;
			}

			public void setIsAdmin(Boolean isAdmin) {
				this.isAdmin = isAdmin;
			}

			public List<WoZonePermission> getZonePermissionList() {
				return zonePermissionList;
			}

			public void setZonePermissionList(List<WoZonePermission> zonePermissionList) {
				this.zonePermissionList = zonePermissionList;
			}

			public Long getUsedCapacity() {
				return usedCapacity;
			}

			public void setUsedCapacity(Long usedCapacity) {
				this.usedCapacity = usedCapacity;
			}

			public Boolean getIsZone() {
				return isZone;
			}

			public void setIsZone(Boolean isZone) {
				this.isZone = isZone;
			}
		}

		public static class WoZonePermission extends ZonePermission {

		protected static WrapCopier<ZonePermission, WoZonePermission> copier = WrapCopierFactory.wo(ZonePermission.class, WoZonePermission.class,
				JpaObject.singularAttributeField(ZonePermission.class, true, true), null);
	}

}
