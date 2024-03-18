package com.x.cms.assemble.control.jaxrs.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.query.DocumentNotify;
import com.x.processplatform.core.entity.content.Attachment;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.element.Form;
import org.w3c.dom.DocumentType;

/**
 * 保存文档
 *
 * @author O2LEE
 *
 */
public class ActionPersistSaveDocument extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPersistSaveDocument.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement,
			EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Document document = Wi.copier.copy(wi);
		document.setId(wi.getId());
		String identity = wi.getIdentity();
		if (StringUtils.isBlank(identity)) {
			identity = userManagerService.getPersonIdentity(effectivePerson.getDistinguishedName(), identity);
		}

		if (StringUtils.isEmpty(wi.getCategoryId())) {
			throw new ExceptionDocumentCategoryIdEmpty();
		}

		CategoryInfo categoryInfo;
		AppInfo appInfo = null;

		Document oldDocument = documentQueryService.get(document.getId());
		if (oldDocument != null) {
			categoryInfo = categoryInfoServiceAdv.get(oldDocument.getCategoryId());
			appInfo = appInfoServiceAdv.get(oldDocument.getAppId());
		} else {
			categoryInfo = categoryInfoServiceAdv.get(wi.getCategoryId());
			if (categoryInfo != null) {
				appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
			}
		}

		if (categoryInfo == null) {
			throw new ExceptionCategoryInfoNotExists(wi.getCategoryId());
		}
		if (appInfo == null) {
			throw new ExceptionAppInfoNotExists(categoryInfo.getAppId());
		}

		Business business = new Business(null);
		if (!business.isDocumentEditor(effectivePerson, appInfo, categoryInfo, oldDocument)) {
			throw new ExceptionAccessDenied(effectivePerson, document);
		}

		// 查询分类设置的编辑表单
		if (StringUtils.isEmpty(categoryInfo.getFormId())) {
			throw new ExceptionCategoryFormIdEmpty();
		}

		Form form = formServiceAdv.get(categoryInfo.getFormId());
		if (form == null) {
			throw new ExceptionFormForEditNotExists(categoryInfo.getFormId());
		} else {
			document.setForm(form.getId());
			document.setFormName(form.getName());
		}

		if (check) {
			if (categoryInfo.getReadFormId() != null && !categoryInfo.getReadFormId().isEmpty()) {
				try {
					form = formServiceAdv.get(categoryInfo.getReadFormId());
					if (form == null) {
						check = false;
						Exception exception = new ExceptionFormForReadNotExists(categoryInfo.getReadFormId());
						result.error(exception);
					} else {
						document.setReadFormId(form.getId());
						document.setReadFormName(form.getName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"系统在根据ID查询阅读表单时发生异常！ID：" + categoryInfo.getReadFormId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			document.setAppId(appInfo.getId());
			document.setAppAlias(appInfo.getAppAlias());
			document.setAppName(appInfo.getAppName());
			document.setCategoryName(categoryInfo.getCategoryName());
			document.setCategoryAlias(categoryInfo.getCategoryAlias());
			document.setDocumentType(categoryInfo.getDocumentType());
			if(!Document.DOCUMENT_TYPE_DATA.equals( document.getDocumentType() )) {
				document.setDocumentType( Document.DOCUMENT_TYPE_INFO );
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if (StringUtils.isNotEmpty(identity)) {
					document.setCreatorIdentity(identity);
				}

				if (StringUtils.isEmpty(document.getCreatorIdentity())) {
					if (StringUtils.equals(EffectivePerson.CIPHER, effectivePerson.getDistinguishedName())
							|| StringUtils.equals(Token.defaultInitialManager,
									effectivePerson.getDistinguishedName())) {
						document.setCreatorIdentity(effectivePerson.getDistinguishedName());
						document.setCreatorPerson(effectivePerson.getDistinguishedName());
						document.setCreatorUnitName(effectivePerson.getDistinguishedName());
						document.setCreatorTopUnitName(effectivePerson.getDistinguishedName());
					} else {
						// 尝试一下根据当前用户获取用户的第一个身份
						document.setCreatorIdentity(
								userManagerService.getMajorIdentityWithPerson(effectivePerson.getDistinguishedName()));
					}
				}

				if (!StringUtils.equals(EffectivePerson.CIPHER, document.getCreatorIdentity())
						&& !StringUtils.equals(Token.defaultInitialManager, document.getCreatorIdentity())) {
					if (StringUtils.isNotEmpty(document.getCreatorIdentity())) {
						document.setCreatorPerson(
								userManagerService.getPersonNameWithIdentity(document.getCreatorIdentity()));
						document.setCreatorUnitName(
								userManagerService.getUnitNameByIdentity(document.getCreatorIdentity()));
						document.setCreatorTopUnitName(
								userManagerService.getTopUnitNameByIdentity(document.getCreatorIdentity()));
					} else {
						Exception exception = new ExceptionPersonHasNoIdentity(document.getCreatorIdentity());
						result.error(exception);
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			if (DocumentStatus.PUBLISHED.getValue().equals(wi.getDocStatus())
					|| DocumentStatus.WAIT_PUBLISH.getValue().equals(wi.getDocStatus())) {
				if (document.getPublishTime() == null) {
					document.setPublishTime(new Date());
				}
			}
		}

		if (check) {
			if (StringUtils.isEmpty(document.getTitle())) {
				document.setTitle(appInfo.getAppName() + " - " + categoryInfo.getCategoryName() + " - 无标题文档");
			}
		}

		if (check) {
			try {
				document.getProperties().setCloudPictures(wi.getCloudPictures());
				document.getProperties().setDocumentNotify(wi.documentNotify);
				document = documentPersistService.save(document, wi.getDocData(), categoryInfo.getProjection());
				CacheManager.notify(Document.class);

				Wo wo = new Wo();
				wo.setId(document.getId());
				result.setData(wo);

				// 检查是否需要删除热点图片
				try {
					ThisApplication.queueDocumentUpdate.send(document);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在创建文档信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 从流程管理中复制所有的附件到CMS
		if (check) {
			if (wi.getWf_attachmentIds() != null && wi.getWf_attachmentIds().length > 0) {
				FileInfo fileInfo = null;
				Attachment attachment = null;
				StorageMapping mapping_attachment = null;
				StorageMapping mapping_fileInfo = null;
				InputStream input = null;
				byte[] attachment_content = null;
				for (String attachmentId : wi.getWf_attachmentIds()) {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						document = emc.find(document.getId(), Document.class, ExceptionWhen.not_found);
						attachment = emc.find(attachmentId, Attachment.class, ExceptionWhen.not_found);
						if (attachment != null) {
							emc.beginTransaction(FileInfo.class);
							emc.beginTransaction(Document.class);

							mapping_attachment = ThisApplication.context().storageMappings().get(Attachment.class,
									attachment.getStorage());
							attachment_content = attachment.readContent(mapping_attachment);

							mapping_fileInfo = ThisApplication.context().storageMappings().random(FileInfo.class);
							fileInfo = concreteFileInfo(effectivePerson.getDistinguishedName(), document,
									mapping_fileInfo, attachment.getName(), attachment.getSite());
							input = new ByteArrayInputStream(attachment_content);
							fileInfo.saveContent(mapping_fileInfo, input, attachment.getName(),
									Config.general().getStorageEncrypt());
							if (attachment.getOrderNumber() != null) {
								fileInfo.setSeqNumber(attachment.getOrderNumber());
							}
							fileInfo.setName(attachment.getName());
							emc.check(document, CheckPersistType.all);
							emc.persist(fileInfo, CheckPersistType.all);

							emc.commit();
						}
					} catch (Throwable th) {
						th.printStackTrace();
						result.error(th);
					} finally {
						if (input != null) {
							input.close();
						}
					}
				}
			}
		}

		if (check) {
			try {// 将读者以及作者信息持久化到数据库中
				if (oldDocument == null || wi.getReaderList() != null || wi.getAuthorList() != null) {
					documentPersistService.refreshDocumentPermission(document.getId(), wi.getReaderList(),
							wi.getAuthorList());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	private FileInfo concreteFileInfo(String person, Document document, StorageMapping storage, String name,
			String site) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension(name);
		FileInfo attachment = new FileInfo();
		if (StringUtils.isEmpty(extension)) {
			throw new Exception("file extension is empty.");
		} else {
			fileName = fileName + "." + extension;
		}
		if (name.indexOf("\\") > 0) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		if (name.indexOf("/") > 0) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		attachment.setCreateTime(new Date());
		attachment.setLastUpdateTime(new Date());
		attachment.setExtension(extension);
		attachment.setName(name);
		attachment.setFileName(fileName);
		attachment.setStorage(storage.getName());
		attachment.setAppId(document.getAppId());
		attachment.setCategoryId(document.getCategoryId());
		attachment.setDocumentId(document.getId());
		attachment.setCreatorUid(person);
		attachment.setSite(site);
		attachment.setFileHost("");
		attachment.setFilePath("");
		attachment.setFileType("ATTACHMENT");

		return attachment;
	}

	public static class Wi {

		@FieldDescribe("ID，非必填，更新时必填写，不然就是新增文档")
		private String id;

		@FieldDescribe("文档标题，<font style='color:red'>必填</font>")
		private String title;

		@FieldDescribe("客体密级标识")
		private Integer objectSecurityClearance;

		@FieldDescribe("分类ID，<font style='color:red'>必填</font>")
		private String categoryId;

		@FieldDescribe("文档操作者身份，如果不传入则取登录者信息。")
		private String identity = null;

		@FieldDescribe("文档摘要，非必填")
		private String summary;

		@FieldDescribe("文档状态: published | draft | waitPublish，非必填，默认为draft")
		private String docStatus = "draft";

		@FieldDescribe("文档发布时间")
		private Date publishTime;

		@FieldDescribe("首页图片列表，非必填")
		private List<String> pictureList = null;

		@FieldDescribe("数据的路径列表，非必填")
		private String[] dataPaths = null;

		@FieldDescribe("启动流程的JobId，非必填")
		private String wf_jobId = null;

		@FieldDescribe("启动流程的WorkId，非必填")
		private String wf_workId = null;

		@FieldDescribe("启动流程的附件列表，非必填")
		private String[] wf_attachmentIds = null;

		@FieldDescribe("文档数据，非必填")
		private JsonElement docData = null;

		@FieldDescribe("文档读者，非必填：{'permission':'读者', 'permissionObjectType':'组织', 'permissionObjectCode':'组织全称', 'permissionObjectName':'组织全称'}")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("文档编辑者，非必填：{'permission':'读者', 'permissionObjectType':'组织', 'permissionObjectCode':'组织全称', 'permissionObjectName':'组织全称'}")
		private List<PermissionInfo> authorList = null;

		@FieldDescribe("定时发布文档消息提醒对象(参数同消息发布接口对象).")
		private DocumentNotify documentNotify;

		@FieldDescribe("图片列表，非必填")
		private List<String> cloudPictures = null;

		@FieldDescribe("不修改权限（跳过权限设置，保留原来的设置），非必填")
		private Boolean skipPermission = false;

		@FieldDescribe("业务数据String值01.")
		private String stringValue01;

		@FieldDescribe("业务数据String值02.")
		private String stringValue02;

		@FieldDescribe("业务数据String值03.")
		private String stringValue03;

		private String importBatchName;

		public static final WrapCopier<Wi, Document> copier = WrapCopierFactory.wi(Wi.class, Document.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public List<String> getPictureList() {
			return pictureList;
		}

		public void setPictureList(List<String> pictureList) {
			this.pictureList = pictureList;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public List<PermissionInfo> getReaderList() {
			return readerList;
		}

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

		public String[] getDataPaths() {
			if (dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")) {
				return null;
			}
			return dataPaths;
		}

		public void setDataPaths(String[] dataPaths) {
			this.dataPaths = dataPaths;
		}

		public JsonElement getDocData() {
			return docData;
		}

		public void setDocData(JsonElement docData) {
			this.docData = docData;
		}

		public String getWf_jobId() {
			return wf_jobId;
		}

		public String getWf_workId() {
			return wf_workId;
		}

		public String[] getWf_attachmentIds() {
			return wf_attachmentIds;
		}

		public void setWf_jobId(String wf_jobId) {
			this.wf_jobId = wf_jobId;
		}

		public void setWf_workId(String wf_workId) {
			this.wf_workId = wf_workId;
		}

		public void setWf_attachmentIds(String[] wf_attachmentIds) {
			this.wf_attachmentIds = wf_attachmentIds;
		}

		public List<String> getCloudPictures() {
			return cloudPictures;
		}

		public void setCloudPictures(List<String> cloudPictures) {
			this.cloudPictures = cloudPictures;
		}

		public Boolean getSkipPermission() {
			return skipPermission;
		}

		public void setSkipPermission(Boolean skipPermission) {
			this.skipPermission = skipPermission;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public String getStringValue01() {
			return stringValue01;
		}

		public void setStringValue01(String stringValue01) {
			this.stringValue01 = stringValue01;
		}

		public String getStringValue02() {
			return stringValue02;
		}

		public void setStringValue02(String stringValue02) {
			this.stringValue02 = stringValue02;
		}

		public String getStringValue03() {
			return stringValue03;
		}

		public void setStringValue03(String stringValue03) {
			this.stringValue03 = stringValue03;
		}

		public DocumentNotify getDocumentNotify() {
			return documentNotify;
		}

		public void setDocumentNotify(DocumentNotify documentNotify) {
			this.documentNotify = documentNotify;
		}

		public String getImportBatchName() {
			return importBatchName;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}

		public Integer getObjectSecurityClearance() {
			return objectSecurityClearance;
		}

		public void setObjectSecurityClearance(Integer objectSecurityClearance) {
			this.objectSecurityClearance = objectSecurityClearance;
		}
	}

	public static class Wo extends WoId {

	}
}
