package com.x.cms.assemble.control.jaxrs.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.exception.ExceptionWhen;
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
import com.x.processplatform.core.entity.content.Attachment;

/**
 * 从流程发布一个文档（流程）
 *
 * @author sword
 */
public class ActionPersistPublishByWorkFlow extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionPersistPublishByWorkFlow.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement,
			EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> cloudPictures = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Form form = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			LOGGER.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (StringUtils.isEmpty(wi.getCategoryId())) {
				check = false;
				Exception exception = new ExceptionDocumentCategoryIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get(wi.getCategoryId());
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(wi.getCategoryId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"系统在根据ID查询分类信息时发生异常！ID：" + wi.getCategoryId());
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				appInfo = appInfoServiceAdv.get(categoryInfo.getAppId());
				if (appInfo == null) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists(categoryInfo.getAppId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"系统在根据ID查询应用栏目信息时发生异常！ID：" + categoryInfo.getAppId());
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
			}
		}

		// 查询分类设置的编辑表单
		if (check) {
			if (StringUtils.isEmpty(categoryInfo.getFormId())) {
				check = false;
				Exception exception = new ExceptionCategoryFormIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				form = formServiceAdv.get(categoryInfo.getFormId());
				if (form == null) {
					check = false;
					Exception exception = new ExceptionFormForEditNotExists(categoryInfo.getFormId());
					result.error(exception);
				} else {
					wi.setForm(form.getId());
					wi.setFormName(form.getName());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,
						"系统在根据ID查询编辑表单时发生异常！ID：" + categoryInfo.getFormId());
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
			}
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
						wi.setReadFormId(form.getId());
						wi.setReadFormName(form.getName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e,
							"系统在根据ID查询阅读表单时发生异常！ID：" + categoryInfo.getReadFormId());
					result.error(exception);
					LOGGER.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			wi.setAppId(categoryInfo.getAppId());
			wi.setAppName(appInfo.getAppName());
			wi.setCategoryName(categoryInfo.getCategoryName());
			wi.setCategoryId(categoryInfo.getId());
			wi.setCategoryAlias(categoryInfo.getCategoryAlias());
			wi.setDocumentType(categoryInfo.getDocumentType());
			if (!Document.DOCUMENT_TYPE_DATA.equals(wi.getDocumentType())) {
				wi.setDocumentType(Document.DOCUMENT_TYPE_INFO);
			}
			if (wi.getPictureList() != null && !wi.getPictureList().isEmpty()) {
				wi.setHasIndexPic(true);
			}
		}

		if (check) {
			if (StringUtils.isNotEmpty(wi.getIdentity())) {
				wi.setCreatorIdentity(wi.getIdentity());
				wi.setCreatorPerson(userManagerService.getPersonNameWithIdentity(wi.getIdentity()));
			}
			if (StringUtils.isEmpty(wi.getCreatorPerson())) {
				wi.setCreatorPerson(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(wi.getCreatorIdentity())) {
				if (StringUtils.equals(EffectivePerson.CIPHER, effectivePerson.getDistinguishedName())
						|| StringUtils.equals(Token.defaultInitialManager, effectivePerson.getDistinguishedName())) {
					wi.setCreatorIdentity(effectivePerson.getDistinguishedName());
					wi.setCreatorPerson(effectivePerson.getDistinguishedName());
					wi.setCreatorUnitName(effectivePerson.getDistinguishedName());
					wi.setCreatorTopUnitName(effectivePerson.getDistinguishedName());
				} else {
					// 尝试一下根据当前用户获取用户的第一个身份
					wi.setCreatorIdentity(userManagerService.getMajorIdentityWithPerson(effectivePerson.getDistinguishedName()));
				}
			}
			if (!StringUtils.equals(EffectivePerson.CIPHER, wi.getCreatorIdentity())
					&& !StringUtils.equals(Token.defaultInitialManager, wi.getCreatorIdentity())) {
				if (StringUtils.isNotEmpty(wi.getCreatorIdentity())) {
					wi.setCreatorPerson(userManagerService.getPersonNameWithIdentity(wi.getCreatorIdentity()));
					wi.setCreatorUnitName(userManagerService.getUnitNameByIdentity(wi.getCreatorIdentity()));
					wi.setCreatorTopUnitName(userManagerService.getTopUnitNameByIdentity(wi.getCreatorIdentity()));
				} else {
					Exception exception = new ExceptionPersonHasNoIdentity(wi.getCreatorIdentity());
					result.error(exception);
				}
			}
		}

		if (check) {
			if (StringUtils.isEmpty(wi.getTitle())) {
				wi.setTitle(appInfo.getAppName() + " - " + categoryInfo.getCategoryName() + " - 无标题文档");
			}
		}

		if (check) {
			try {
				wi.setDocStatus(Document.DOC_STATUS_PUBLISH);
				if (wi.getPublishTime() == null) {
					wi.setPublishTime(new Date());
				}
				wi.setPpFormId(wi.getWf_formId());
				document = documentPersistService.save(wi, wi.getDocData(), categoryInfo.getProjection());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在创建文档信息时发生异常！");
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
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
							if(attachment.getOrderNumber()!=null) {
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
				CacheManager.notify(FileInfo.class);
				CacheManager.notify(Document.class);
			}
		}

		if (check) {
			try {
				Wo wo = new Wo();
				wo.setId(document.getId());
				result.setData(wo);
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统将文档状态修改为发布状态时发生异常。Id:" + document.getId());
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				logService.log(emc, wi.getCreatorIdentity(), document.getCategoryAlias() + ":" + document.getTitle(),
						document.getAppId(), document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check && !wi.getSkipPermission()) {
			// 将读者以及作者信息持久化到数据库中
			try {
				documentPersistService.refreshDocumentPermission(document.getId(), wi.getReaderList(),
						wi.getAuthorList());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
				result.error(exception);
				LOGGER.error(e, effectivePerson, request, null);
			}
		}

		CacheManager.notify(Document.class);
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
		if (name.indexOf("\\") > -1) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		if (name.indexOf("/") > -1) {
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

	public static class Wi extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static final WrapCopier<Wi, Document> copier = WrapCopierFactory.wi(Wi.class, Document.class, null,
				JpaObject.FieldsUnmodify);

		@FieldDescribe("文档操作者身份.")
		private String identity = null;

		@FieldDescribe("数据的路径列表.")
		private String[] dataPaths = null;

		@FieldDescribe("启动流程的JobId.")
		private String wf_jobId = null;

		@FieldDescribe("启动流程的WorkId.")
		private String wf_workId = null;

		@FieldDescribe("流程的表单Id.")
		private String wf_formId = null;

		@FieldDescribe("启动流程的附件列表.")
		private String[] wf_attachmentIds = null;

		@FieldDescribe("文档数据.")
		private JsonElement docData = null;

		@FieldDescribe("文档读者.")
		private List<PermissionInfo> readerList = null;

		@FieldDescribe("文档编辑者.")
		private List<PermissionInfo> authorList = null;

		@FieldDescribe("图片列表.")
		private List<String> cloudPictures = null;

		@FieldDescribe("不修改权限（跳过权限设置，保留原来的设置）.")
		private Boolean skipPermission = false;

		public Boolean getSkipPermission() {
			return skipPermission;
		}

		public void setSkipPermission(Boolean skipPermission) {
			this.skipPermission = skipPermission;
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

		public String getWf_formId() {
			return wf_formId;
		}

		public void setWf_formId(String wf_formId) {
			this.wf_formId = wf_formId;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -3298158240217197985L;

	}
}
