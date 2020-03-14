package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionInfo;
import com.x.cms.assemble.control.jaxrs.permission.element.PermissionName;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.element.Form;

/**
 * 直接发布文档内容
 * @author O2LEE
 *
 */
public class ActionPersistPublishContent extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistPublishContent.class);

	@AuditLog(operation = "发布文档")
	protected ActionResult<Wo> execute( HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<FileInfo> cloudPictures = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Form form = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if (check) {
			if ( StringUtils.isEmpty(wi.getCategoryId())) {
				check = false;
				Exception exception = new ExceptionDocumentCategoryIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				categoryInfo = categoryInfoServiceAdv.get( wi.getCategoryId() );
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(wi.getCategoryId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e,"系统在根据ID查询分类信息时发生异常！ID：" + wi.getCategoryId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				appInfo = appInfoServiceAdv.get( categoryInfo.getAppId() );
				if (appInfo == null) {
					check = false;
					Exception exception = new ExceptionAppInfoNotExists(categoryInfo.getAppId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID查询应用栏目信息时发生异常！ID：" + categoryInfo.getAppId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 查询分类设置的编辑表单
		if (check) {
			if ( StringUtils.isEmpty(categoryInfo.getFormId())) {
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
				logger.error(e, effectivePerson, request, null);
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
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			wi.setDocumentType( categoryInfo.getDocumentType() );
			wi.setAppId(categoryInfo.getAppId());
			wi.setAppName(appInfo.getAppName());
			wi.setCategoryName(categoryInfo.getCategoryName());
			wi.setCategoryId(categoryInfo.getId());
			wi.setCategoryAlias(categoryInfo.getCategoryAlias());
			if( StringUtils.isEmpty( wi.getDocumentType() ) ) {
				wi.setDocumentType( categoryInfo.getDocumentType() );
			}
			if( !"信息".equals(wi.getDocumentType()) && !"数据".equals( wi.getDocumentType() )) {
				wi.setDocumentType( "信息" );
			}
			if (wi.getPictureList() != null && !wi.getPictureList().isEmpty()) {
				wi.setHasIndexPic(true);
			}
		}

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				if (StringUtils.isEmpty( wi.getCreatorIdentity() )) {
					if( "cipher".equalsIgnoreCase( effectivePerson.getDistinguishedName() )) {
						wi.setCreatorIdentity("cipher");
						wi.setCreatorPerson("cipher");
						wi.setCreatorUnitName("cipher");
						wi.setCreatorTopUnitName("cipher");
					}else if ("xadmin".equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
						wi.setCreatorIdentity("xadmin");
						wi.setCreatorPerson("xadmin");
						wi.setCreatorUnitName("xadmin");
						wi.setCreatorTopUnitName("xadmin");
					}else {
						//尝试一下根据当前用户获取用户的第一个身份
						wi.setCreatorIdentity(userManagerService.getMajorIdentityWithPerson( effectivePerson.getDistinguishedName()) );
					}
				}
				
				if ( !StringUtils.equals(  "cipher", wi.getCreatorIdentity() ) && !StringUtils.equals(  "xadmin", wi.getCreatorIdentity() )) {
					//说明是指定的发布者，并不使用cipher和xadmin代替
					if (StringUtils.isNotEmpty( wi.getCreatorIdentity() )) {
						wi.setCreatorPerson( userManagerService.getPersonNameWithIdentity( wi.getCreatorIdentity() ) );
						wi.setCreatorUnitName( userManagerService.getUnitNameByIdentity( wi.getCreatorIdentity() ) );
						wi.setCreatorTopUnitName( userManagerService.getTopUnitNameByIdentity( wi.getCreatorIdentity() ) );
					}else {
						Exception exception = new ExceptionPersonHasNoIdentity(wi.getCreatorIdentity());
						result.error(exception);
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			if ( StringUtils.isEmpty(wi.getTitle())) {
				wi.setTitle( appInfo.getAppName() + " - " + categoryInfo.getCategoryName() + " - 无标题文档" );
			}
		}

		if (check) {
			try {
				JsonElement docData = XGsonBuilder.instance().toJsonTree(wi.getDocData(), Map.class);
				wi.setDocStatus("published");
				if( wi.getPublishTime() == null ) {
					wi.setPublishTime(new Date());
				}
				document = documentPersistService.save(wi.copier.copy(wi), docData );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在创建文档信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {				
				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统将文档状态修改为发布状态时发生异常。Id:" + document.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				logService.log(emc, wi.getCreatorIdentity(),
						document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(),
						document.getCategoryId(), document.getId(), "", "DOCUMENT", "发布");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		// 处理文档的云文档图片信息
		if (check) {
			try {
				cloudPictures = fileInfoServiceAdv.getCloudPictureList(document.getId());
				if (cloudPictures == null) {
					cloudPictures = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询文档云图片信息时发生异常！ID:" + document.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 检查是否有需要删除的图片
			if (cloudPictures != null && !cloudPictures.isEmpty()) {
				boolean isExists = false;
				for (FileInfo picture : cloudPictures) {
					isExists = false;
					if (wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty()) {
						for (String cloudPictureId : wi.getCloudPictures()) {
							if( picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
								isExists = true;
							}
						}
					}
					if (!isExists) {
						try {
							fileInfoServiceAdv.deleteFileInfo(picture.getId());
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "系统在删除文档云图片信息时发生异常！ID:" + picture.getId());
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}

		if (check) {
			// 检查是否有需要新添加的云图片信息
			if (wi.getCloudPictures() != null && !wi.getCloudPictures().isEmpty()) {
				boolean isExists = false;
				int index = 0;
				for (String cloudPictureId : wi.getCloudPictures()) {
					index++;
					isExists = false;
					for (FileInfo picture : cloudPictures) {
						if (picture.getCloudId() != null && picture.getCloudId().equalsIgnoreCase(cloudPictureId)) {
							isExists = true;
							fileInfoServiceAdv.updatePictureIndex(picture.getId(), index);
						}
					}
					if (!isExists) {
						try {
							// 说明原来的文件中不存在，需要添加一个新的云图片
							fileInfoServiceAdv.saveCloudPicture(cloudPictureId, document, index);
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "系统在新增文档云图片信息时发生异常！CLOUD_ID:" + cloudPictureId);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}

		if ( check && !wi.getSkipPermission() ) {
			//将读者以及作者信息持久化到数据库中
			try {
				documentPersistService.refreshDocumentPermission( document.getId(), wi.getReaderList(), wi.getAuthorList() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在核对文档访问管理权限信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		//判断是否需要发送通知消息
		if (check) {
			try {
				Boolean notify = false;
				if( categoryInfo.getSendNotify() == null ) {
					if( StringUtils.equals("信息", categoryInfo.getDocumentType()) ) {
						notify = true;
					}						
				}else {
					if( categoryInfo.getSendNotify() ) {
						notify = true;
					}
				}
				if( notify ){
					logger.info("try to add notify object to queue for document:" + document.getTitle() );
					ThisApplication.queueSendDocumentNotify.send( document );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "根据ID查询分类信息对象时发生异常。Flag:" + document.getCategoryId()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
				
		ApplicationCache.notify(Document.class);
		return result;
	}

	public static class Wi {
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier<Wi, Document> copier = WrapCopierFactory.wi( Wi.class, Document.class, null, null);

		@FieldDescribe( "文档操作者身份." )
		private String identity = null;
		
		@FieldDescribe( "数据的路径列表." )
		private String[] dataPaths = null;
		
		@FieldDescribe( "启动流程的JobId." )
		private String wf_jobId = null;
		
		@FieldDescribe( "启动流程的WorkId." )
		private String wf_workId = null;
		
		@FieldDescribe( "启动流程的附件列表." )
		private String[] wf_attachmentIds = null;	
		
		@FieldDescribe( "文档数据JSON对象." )
		private Map<?, ?> docData = null;
		
		@FieldDescribe( "文档读者，Json数组，权限对象需要包含四个属性:<br/>permission权限类别：读者|阅读|作者|管理,  <br/>permissionObjectType使用者类别：所有人|组织|人员|群组, <br/>permissionObjectCode使用者编码：所有人|组织编码|人员UID|群组编码, <br/>permissionObjectName使用者名称：所有人|组织名称|人员名称|群组名称" )
		private List<PermissionInfo> readerList = null;
		
		@FieldDescribe( "文档编辑者, ，Json数组，权限对象需要包含四个属性:<br/>permission权限类别：读者|阅读|作者|管理,  <br/>permissionObjectType使用者类别：所有人|组织|人员|群组, <br/>permissionObjectCode使用者编码：所有人|组织编码|人员UID|群组编码, <br/>permissionObjectName使用者名称：所有人|组织名称|人员名称|群组名称" )
		private List<PermissionInfo> authorList = null;
		
		private List<String> cloudPictures = null;
		
		@FieldDescribe( "不修改权限（跳过权限设置，保留原来的设置）， True|False." )
		private Boolean skipPermission  = false;
		
		@FieldDescribe("文档摘要，70字以内")
		private String summary;

		@FieldDescribe("文档标题，70字以内")
		private String title;

		@FieldDescribe("文档类型，跟随分类类型，信息（默认） | 数据")
		private String documentType = "信息";

		private String appId;

		private String appName;

		private String appAlias;

		@FieldDescribe("分类ID")
		private String categoryId;

		private String categoryName;

		private String categoryAlias;

		private String form;

		private String formName;

		private String importBatchName;

		private String readFormId;

		private String readFormName;

		private String creatorPerson;

		private String creatorIdentity;

		private String creatorUnitName;

		private String creatorTopUnitName;

		@FieldDescribe("文档状态: published | draft | checking | error")
		private String docStatus = "draft";

		private String description = null;

		private Long viewCount = 0L;

		private Long commendCount = 0L;

		private Long commentCount = 0L;
		
		private Date publishTime;

		private Date modifyTime;

		@FieldDescribe("是否置顶")
		private Boolean isTop = false;

		private Boolean hasIndexPic = false;

		private Boolean reviewed = false;

		private String sequenceTitle = "";

		private String sequenceAppAlias = "";

		private String sequenceCategoryAlias = "";

		private String sequenceCreatorPerson = "";

		private String sequenceCreatorUnitName = "";

		private List<String> readPersonList;

		private List<String> readUnitList;

		private List<String> readGroupList;

		private List<String> authorPersonList;

		private List<String> authorUnitList;

		private List<String> authorGroupList;

		private List<String> remindPersonList;

		private List<String> remindUnitList;

		private List<String> remindGroupList;

		private List<String> managerList;

		private List<String> pictureList;
		
		
		
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

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getAppAlias() {
			return appAlias;
		}

		public void setAppAlias(String appAlias) {
			this.appAlias = appAlias;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getCategoryAlias() {
			return categoryAlias;
		}

		public void setCategoryAlias(String categoryAlias) {
			this.categoryAlias = categoryAlias;
		}

		public String getForm() {
			return form;
		}

		public void setForm(String form) {
			this.form = form;
		}

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}

		public String getImportBatchName() {
			return importBatchName;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}

		public String getReadFormId() {
			return readFormId;
		}

		public void setReadFormId(String readFormId) {
			this.readFormId = readFormId;
		}

		public String getReadFormName() {
			return readFormName;
		}

		public void setReadFormName(String readFormName) {
			this.readFormName = readFormName;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public String getCreatorIdentity() {
			return creatorIdentity;
		}

		public void setCreatorIdentity(String creatorIdentity) {
			this.creatorIdentity = creatorIdentity;
		}

		public String getCreatorUnitName() {
			return creatorUnitName;
		}

		public void setCreatorUnitName(String creatorUnitName) {
			this.creatorUnitName = creatorUnitName;
		}

		public String getCreatorTopUnitName() {
			return creatorTopUnitName;
		}

		public void setCreatorTopUnitName(String creatorTopUnitName) {
			this.creatorTopUnitName = creatorTopUnitName;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Long getViewCount() {
			return viewCount;
		}

		public void setViewCount(Long viewCount) {
			this.viewCount = viewCount;
		}

		public Long getCommendCount() {
			return commendCount;
		}

		public void setCommendCount(Long commendCount) {
			this.commendCount = commendCount;
		}

		public Long getCommentCount() {
			return commentCount;
		}

		public void setCommentCount(Long commentCount) {
			this.commentCount = commentCount;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public Date getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(Date modifyTime) {
			this.modifyTime = modifyTime;
		}

		public Boolean getIsTop() {
			return isTop;
		}

		public void setIsTop(Boolean isTop) {
			this.isTop = isTop;
		}

		public Boolean getHasIndexPic() {
			return hasIndexPic;
		}

		public void setHasIndexPic(Boolean hasIndexPic) {
			this.hasIndexPic = hasIndexPic;
		}

		public Boolean getReviewed() {
			return reviewed;
		}

		public void setReviewed(Boolean reviewed) {
			this.reviewed = reviewed;
		}

		public String getSequenceTitle() {
			return sequenceTitle;
		}

		public void setSequenceTitle(String sequenceTitle) {
			this.sequenceTitle = sequenceTitle;
		}

		public String getSequenceAppAlias() {
			return sequenceAppAlias;
		}

		public void setSequenceAppAlias(String sequenceAppAlias) {
			this.sequenceAppAlias = sequenceAppAlias;
		}

		public String getSequenceCategoryAlias() {
			return sequenceCategoryAlias;
		}

		public void setSequenceCategoryAlias(String sequenceCategoryAlias) {
			this.sequenceCategoryAlias = sequenceCategoryAlias;
		}

		public String getSequenceCreatorPerson() {
			return sequenceCreatorPerson;
		}

		public void setSequenceCreatorPerson(String sequenceCreatorPerson) {
			this.sequenceCreatorPerson = sequenceCreatorPerson;
		}

		public String getSequenceCreatorUnitName() {
			return sequenceCreatorUnitName;
		}

		public void setSequenceCreatorUnitName(String sequenceCreatorUnitName) {
			this.sequenceCreatorUnitName = sequenceCreatorUnitName;
		}

		public List<String> getReadPersonList() {
			return readPersonList;
		}

		public void setReadPersonList(List<String> readPersonList) {
			this.readPersonList = readPersonList;
		}

		public List<String> getReadUnitList() {
			return readUnitList;
		}

		public void setReadUnitList(List<String> readUnitList) {
			this.readUnitList = readUnitList;
		}

		public List<String> getReadGroupList() {
			return readGroupList;
		}

		public void setReadGroupList(List<String> readGroupList) {
			this.readGroupList = readGroupList;
		}

		public List<String> getAuthorPersonList() {
			return authorPersonList;
		}

		public void setAuthorPersonList(List<String> authorPersonList) {
			this.authorPersonList = authorPersonList;
		}

		public List<String> getAuthorUnitList() {
			return authorUnitList;
		}

		public void setAuthorUnitList(List<String> authorUnitList) {
			this.authorUnitList = authorUnitList;
		}

		public List<String> getAuthorGroupList() {
			return authorGroupList;
		}

		public void setAuthorGroupList(List<String> authorGroupList) {
			this.authorGroupList = authorGroupList;
		}

		public List<String> getRemindPersonList() {
			return remindPersonList;
		}

		public void setRemindPersonList(List<String> remindPersonList) {
			this.remindPersonList = remindPersonList;
		}

		public List<String> getRemindUnitList() {
			return remindUnitList;
		}

		public void setRemindUnitList(List<String> remindUnitList) {
			this.remindUnitList = remindUnitList;
		}

		public List<String> getRemindGroupList() {
			return remindGroupList;
		}

		public void setRemindGroupList(List<String> remindGroupList) {
			this.remindGroupList = remindGroupList;
		}

		public List<String> getManagerList() {
			return managerList;
		}

		public void setManagerList(List<String> managerList) {
			this.managerList = managerList;
		}

		public List<String> getPictureList() {
			return pictureList;
		}

		public void setPictureList(List<String> pictureList) {
			this.pictureList = pictureList;
		}

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

		public void setReaderList(List<PermissionInfo> readerList) {
			this.readerList = readerList;
		}

		public String[] getDataPaths() {
			if( dataPaths != null && dataPaths.length == 1 && dataPaths[0].equals("null")){
				return null;
			}
			return dataPaths;
		}

		public void setDataPaths(String[] dataPaths) {
			this.dataPaths = dataPaths;
		}

		public Map<?, ?> getDocData() {
			return docData;
		}

		public void setDocData(Map<?, ?> docData) {
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

		public List<PermissionInfo> getAuthorList() {
			return authorList;
		}

		public void setAuthorList(List<PermissionInfo> authorList) {
			this.authorList = authorList;
		}

	}
	
	public static class Wo extends WoId {

	}
}