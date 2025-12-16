package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.assemble.control.entities.FileHistory;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Attachment3_;
import com.x.pan.core.entity.AttachmentVersion;
import com.x.pan.core.entity.FileConfig3;
import com.x.pan.core.entity.FileConfigProperties;
import com.x.pan.core.entity.Folder3;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

	protected static final String EXCEPTION_FLAG = "existed";

	protected void verifyConstraint(Business business, String fileName, String zoneId, long size) throws Exception{
		FileConfig3 config = business.getSystemConfig();
		if (config != null){
			FileConfigProperties properties = config.getProperties();
			String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
			if(properties!=null){
				if(properties.getFileTypeIncludes()!=null && !properties.getFileTypeIncludes().isEmpty()){
					if(!ListTools.contains(properties.getFileTypeIncludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
				if(properties.getFileTypeExcludes()!=null && !properties.getFileTypeExcludes().isEmpty()){
					if(ListTools.contains(properties.getFileTypeExcludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
			}
		}
		Folder3 folder = business.entityManagerContainer().find(zoneId, Folder3.class);
		if(folder.getCapacity() > 0){
			long useSize = business.attachment3().statZoneCapacity(folder.getZoneId());
			long usedCapacity = (useSize + size) / (1024 * 1024);
			if (usedCapacity > folder.getCapacity()) {
				throw new ExceptionCapacityOut(usedCapacity, folder.getCapacity());
			}
		}
	}

	protected void verifyConstraint(Business business, String fileName) throws Exception{
		FileConfig3 config = business.getSystemConfig();
		if (config != null){
			FileConfigProperties properties = config.getProperties();
			String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
			if(properties!=null){
				if(properties.getFileTypeIncludes()!=null && !properties.getFileTypeIncludes().isEmpty()){
					if(!ListTools.contains(properties.getFileTypeIncludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
				if(properties.getFileTypeExcludes()!=null && !properties.getFileTypeExcludes().isEmpty()){
					if(ListTools.contains(properties.getFileTypeExcludes(), fileType)){
						throw new ExceptionAttachmentUploadDenied(fileName);
					}
				}
			}
		}
	}

	public String adjustFileName(Business business, String folderId, String fileName, String status) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName.toLowerCase());
		String base = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		fileName =  base + "-" + DateTools.compact(new Date()) + "." + extension;
		list.add(fileName.toLowerCase());
		list.add(StringTools.uniqueToken()+"."+extension);
		EntityManager em = business.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.lower(root.get(Attachment3_.name)).in(list);
		p = cb.and(p, cb.equal(root.get(Attachment3_.folder), folderId));
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), status));
		cq.select(root.get(Attachment3_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	protected void setExtendInfo(Business business, WrapAttachment3 wo, EffectivePerson effectivePerson) throws Exception {
		wo.setContentType(this.contentType(false, wo.getName()));
		if(effectivePerson!=null) {
			wo.setIsCreator(wo.getPerson().equals(effectivePerson.getDistinguishedName()));
			boolean isManager = business.controlAble(effectivePerson);
			if (isManager) {
				wo.setIsAdmin(true);
				wo.setIsEditor(true);
				wo.setDownloadable(true);
			} else {
				String zoneId = business.getSystemConfig().getReadPermissionDown() ? wo.getFolder() : wo.getZoneId();
				boolean isAdmin = business.folder3().isZoneAdmin(zoneId, effectivePerson.getDistinguishedName());
				if (isAdmin) {
					wo.setIsAdmin(true);
					wo.setIsEditor(true);
				} else {
					wo.setIsEditor(business.folder3().isZoneEditor(zoneId, effectivePerson.getDistinguishedName()));
					if(BooleanUtils.isNotTrue(wo.getIsAdmin()) && !effectivePerson.getDistinguishedName().equals(wo.getPerson())){
						wo.setIsEditor(false);
					}
				}
				if(BooleanUtils.isTrue(wo.getIsEditor())){
					wo.setDownloadable(true);
				}else{
					wo.setDownloadable(business.folder3().isZoneReader(zoneId, effectivePerson.getDistinguishedName()));
				}
			}
		}
	}

	protected List<FileHistory> assembleFileHistory(List<AttachmentVersion> attachmentVersionList, boolean isAttachment3,
													FileConfig3 config, EffectivePerson effectivePerson){
		List<FileHistory> fileHistoryList = new ArrayList<>();
		if(ListTools.isNotEmpty(attachmentVersionList)) {
			for (AttachmentVersion attachment : attachmentVersionList) {
				if(StringUtils.isBlank(attachment.getFileChanges())
						|| StringUtils.isBlank(attachment.getFileDiff())){
					continue;
				}
				FileHistory fileHistory = new FileHistory();
				String person = attachment.getPerson();
				fileHistory.setCreatorId(person);
				fileHistory.setCreatorName(OrganizationDefinition.name(person));
				fileHistory.setCreateTime(attachment.getCreateTime().getTime());
				fileHistory.setVersion(attachment.getFileVersion());
				fileHistory.setChanges(attachment.getFileChanges());
				fileHistory.setDownloadUrl(this.getFileDownloadUrl(config, attachment.getAttachmentId(), isAttachment3,
						effectivePerson, fileHistory.getVersion(), false));
				fileHistory.setDiffUrl(this.getFileDownloadUrl(config, attachment.getAttachmentId(), isAttachment3,
						effectivePerson, fileHistory.getVersion(), true));
				fileHistoryList.add(fileHistory);
			}
		}
		return fileHistoryList;
	}

	/**
	 * 获取资源下载地址
	 * @param config
	 * @param id
	 * @param isAttachment3
	 * @param effectivePerson
	 * @param version
	 * @param isDiff
	 * @return
	 */
	protected String getFileDownloadUrl(FileConfig3 config, String id, boolean isAttachment3, EffectivePerson effectivePerson,
										Integer version, boolean isDiff){
		StringBuffer downloadUrl = new StringBuffer(config.getOfficeViewDownloadUrl());
		try {
			if(!config.getOfficeViewDownloadUrl().endsWith("/")){
				downloadUrl.append("/");
			}
			downloadUrl.append("attachment3/")
					.append(id)
					.append("/download");
			if(version != null && version > 0){
				downloadUrl.append("/version/").append(version);
				if(isDiff){
					downloadUrl.append("/diff");
				}
			}
			downloadUrl.append("?")
					.append(Config.person().getTokenName())
					.append("=")
					.append(URLEncoder.encode(effectivePerson.getToken(), DefaultCharset.charset));
		} catch (Exception e) {
			logger.error(e);
		}
		return downloadUrl.toString();
	}

	protected void saveFile(String id, byte[] bytes, String changes, String diff, EffectivePerson effectivePerson, Map<String, Object> map) throws Exception{
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			boolean isAttachment3 = true;
			String fileName;
			Attachment3 attachment = emc.find(id, Attachment3.class);
			Attachment2 attachment2 = null;
			if (null == attachment) {
				attachment2 = emc.find(id, Attachment2.class);
				if(attachment2 == null) {
					map.put("result", Business.ONLY_OFFICE_ERROR_CODE);
					map.put("msg", "文件不存在");
					return;
				}else{
					if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
						map.put("result", Business.ONLY_OFFICE_ERROR_CODE);
						map.put("msg", "用户无权限编辑");
						return;
					}
					isAttachment3 = false;
					fileName = attachment2.getName();
				}
			}else{
				if(!business.zoneEditable(effectivePerson, attachment.getFolder(), "")){
					map.put("result", Business.ONLY_OFFICE_ERROR_CODE);
					map.put("msg", "用户无权限编辑");
					return;
				}
				fileName = attachment.getName();
			}

			emc.beginTransaction(AttachmentVersion.class);
			emc.beginTransaction(OriginFile.class);
			AttachmentVersion attachmentVersion;
			if(isAttachment3){
				emc.beginTransaction(Attachment3.class);
				attachmentVersion = new AttachmentVersion(effectivePerson.getDistinguishedName(),
					attachment, changes, diff);
			}else{
				emc.beginTransaction(Attachment2.class);
				attachmentVersion = new AttachmentVersion(effectivePerson.getDistinguishedName(), changes, diff);
				attachmentVersion.setAttachmentId(attachment2.getId());
				attachmentVersion.setExtension(attachment2.getExtension());
				attachmentVersion.setName(attachment2.getName());
				attachmentVersion.setFileVersion(attachment2.getFileVersion());
				attachmentVersion.setLength(attachment2.getLength());
				attachmentVersion.setOriginFile(attachment2.getOriginFile());
			}
			emc.persist(attachmentVersion);
			String fileMd5 = StringTools.uniqueToken();
			StorageMapping mapping = ThisApplication.context().storageMappings().random(OriginFile.class);
			OriginFile originFile = new OriginFile(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), fileMd5);
			emc.persist(originFile);
			originFile.saveContent(mapping, bytes, fileName);
			if(isAttachment3) {
				attachment.setOriginFile(originFile.getId());
				attachment.setLength(originFile.getLength());
				attachment.setFileVersion(attachment.getFileVersion() + 1);
				attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				attachment.setLastUpdateTime(new Date());
			}else{
				attachment2.setOriginFile(originFile.getId());
				attachment2.setLength(originFile.getLength());
				attachment2.setFileVersion(attachment2.getFileVersion() + 1);
				attachment2.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				attachment2.setLastUpdateTime(new Date());
			}
			emc.commit();

			CacheManager.notify(Attachment3.class);
			CacheManager.notify(Attachment2.class);
			map.put("result", 10000);
			map.put("msg", "成功");
		}
	}

	public static class WoAttachment extends GsonPropertyObject {
		private String id;
		private String person;
		private String name;
		private String extension;
		private Long length;
		private Date createTime;
		private Date updateTime;
		private String lastUpdatePerson;
		private Integer fileVersion = 1;
		private boolean attachment3 = true;

		public WoAttachment(Attachment3 attachment){
			this.id = attachment.getId();
			this.person = attachment.getPerson();
			this.name = attachment.getName();
			this.extension = attachment.getExtension();
			this.length = attachment.getLength();
			this.createTime = attachment.getCreateTime();
			this.updateTime = attachment.getLastUpdateTime() == null ? attachment.getUpdateTime() : attachment.getLastUpdateTime();
			this.lastUpdatePerson = attachment.getLastUpdatePerson();
			this.fileVersion = attachment.getFileVersion();
		}
		public WoAttachment(Attachment2 attachment){
			this.id = attachment.getId();
			this.person = attachment.getPerson();
			this.name = attachment.getName();
			this.extension = attachment.getExtension();
			this.length = attachment.getLength();
			this.createTime = attachment.getCreateTime();
			this.updateTime = attachment.getUpdateTime();
			this.lastUpdatePerson = attachment.getLastUpdatePerson();
			this.fileVersion = attachment.getFileVersion();
			this.attachment3 = false;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
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

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public String getLastUpdatePerson() {
			return lastUpdatePerson;
		}

		public void setLastUpdatePerson(String lastUpdatePerson) {
			this.lastUpdatePerson = lastUpdatePerson;
		}

		public boolean isAttachment3() {
			return attachment3;
		}

		public void setAttachment3(boolean attachment3) {
			this.attachment3 = attachment3;
		}

		public Integer getFileVersion() {
			return fileVersion;
		}

		public void setFileVersion(Integer fileVersion) {
			this.fileVersion = fileVersion;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}
	}

	public static class WrapAttachment3 extends Attachment3 {

		private static final long serialVersionUID = -7660121712289985792L;

		@FieldDescribe("文件类型")
		private String contentType;

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

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public Boolean getDownloadable() {
			return downloadable;
		}

		public void setDownloadable(Boolean downloadable) {
			this.downloadable = downloadable;
		}
	}
}
