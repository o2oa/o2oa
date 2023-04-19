package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;

class ActionOnlineInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionOnlineInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}", effectivePerson::getDistinguishedName, () -> id, () -> id);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Business business = new Business(emc);
			FileInfo fileInfo = emc.find(id, FileInfo.class);
			if (null == fileInfo) {
				throw new ExceptionFileInfoNotExists(id);
			}
			Document document = emc.find(fileInfo.getDocumentId(), Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(fileInfo.getDocumentId());
			}
			AppInfo appInfo = emc.find(document.getAppId(), AppInfo.class);
			CategoryInfo categoryInfo = emc.find(document.getCategoryId(), CategoryInfo.class);

			if(business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, document)){
				wo.setCanEdit(true);
				wo.setCanRead(true);
			}else{
				wo.setCanRead(business.isDocumentReader(effectivePerson, document));
			}

			if(BooleanUtils.isTrue(wo.getCanRead())) {
				wo.setId(fileInfo.getId());
				wo.setName(fileInfo.getName());
				wo.setExtension(fileInfo.getExtension());
				wo.setJob(fileInfo.getDocumentId());
				wo.setLength(fileInfo.getLength());
				wo.setCreateTime(fileInfo.getCreateTime());
				wo.setLastUpdateTime(fileInfo.getLastUpdateTime());
				wo.setOwnerId(fileInfo.getCreatorUid());
				wo.setOwnerName(OrganizationDefinition.name(fileInfo.getCreatorUid()));
				wo.setUserId(effectivePerson.getDistinguishedName());
				wo.setUserName(effectivePerson.getName());
			}else{
				wo.setId(id);
			}
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.cms.assemble.control.jaxrs.fileinfo.ActionOnlineEditInfo$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -4628551310520612780L;

		@FieldDescribe("附件ID.")
		private String id;
		@FieldDescribe("文档ID.")
		private String job;
		@FieldDescribe("附件名称.")
		private String name;
		@FieldDescribe("扩展名.")
		private String extension;
		@FieldDescribe("附件大小.")
		private Long length;
		@FieldDescribe("创建用户ID.")
		private String ownerId;
		@FieldDescribe("创建用户名称.")
		private String ownerName;
		@FieldDescribe("当前用户ID.")
		private String userId;
		@FieldDescribe("当前用户名称.")
		private String userName;
		@FieldDescribe("创建时间.")
		private Date createTime;
		@FieldDescribe("最后更新时间.")
		private Date lastUpdateTime;
		@FieldDescribe("当前用户是否可编辑.")
		private Boolean canEdit;
		@FieldDescribe("当前用户是否可阅读.")
		private Boolean canRead;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getLength() {
			return length;
		}

		public void setLength(Long length) {
			this.length = length;
		}

		public String getOwnerId() {
			return ownerId;
		}

		public void setOwnerId(String ownerId) {
			this.ownerId = ownerId;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public void setOwnerName(String ownerName) {
			this.ownerName = ownerName;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Boolean getCanEdit() {
			return canEdit;
		}

		public void setCanEdit(Boolean canEdit) {
			this.canEdit = canEdit;
		}

		public Boolean getCanRead() {
			return canRead;
		}

		public void setCanRead(Boolean canRead) {
			this.canRead = canRead;
		}

		public Date getLastUpdateTime() {
			return lastUpdateTime;
		}

		public void setLastUpdateTime(Date lastUpdateTime) {
			this.lastUpdateTime = lastUpdateTime;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}
	}

}
