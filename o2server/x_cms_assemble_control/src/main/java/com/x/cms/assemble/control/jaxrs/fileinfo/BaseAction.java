package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.FileTools;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.cms.assemble.control.wrapin.WiAttachment;
import com.x.processplatform.core.entity.content.Attachment;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.Cms;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class BaseAction extends StandardJaxrsAction {

	protected static final String SITE_SEPARATOR = "~";
	protected static final String FILE_SEPARATOR = ",";

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileInfo.class, Document.class);
	protected LogService logService = new LogService();
	protected FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();

	protected boolean checkAllowVisitJob(String person, String job) throws Exception {
		WrapBoolean resp = ThisApplication.context().applications()
				.getQuery(x_processplatform_assemble_surface.class,
						Applications.joinQueryUri("job", job, "allow", "visit", "person", person))
				.getData(WrapBoolean.class);
		return resp.getValue();
	}

	protected boolean checkAllowVisitDoc(String person, String document) throws Exception {
		WrapBoolean resp = ThisApplication.context().applications().getQuery(x_cms_assemble_control.class,
						Applications.joinQueryUri("document", "cipher", document, "permission", "read", "person", person))
				.getData(WrapBoolean.class);
		return resp.getValue();
	}

	protected List<Attachment> checkAttachment(List<WiAttachment> attList, String person, Business business) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(FileInfo.class);
		List<Attachment> attachmentList = new ArrayList<>();
		if (ListTools.isNotEmpty(attList)) {
			Set<String> jobs = new HashSet<>();
			for (WiAttachment w : attList) {
				Attachment o = emc.find(w.getId(), Attachment.class);
				if (null == o) {
					throw new ExceptionEntityNotExist(w.getId(), Attachment.class);
				}
				if(!jobs.contains(o.getJob()) && !checkAllowVisitJob(person,  o.getJob())){
					throw new ExceptionAccessDenied(person, o.getId());
				}
				jobs.add(o.getJob());
				em.detach(o);
				if(StringUtils.isNotBlank(w.getName())){
					FileTools.verifyConstraint(1, w.getName(), null);
					o.setName(w.getName());
				}
				if(StringUtils.isNotBlank(w.getSite())){
					o.setSite(w.getSite());
				}
				attachmentList.add(o);
			}
		}
		return attachmentList;
	}

	protected List<FileInfo> checkFileInfo(List<WiAttachment> attList, String person, Business business) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(FileInfo.class);
		List<FileInfo> fileList = new ArrayList<>();
		if (ListTools.isNotEmpty(attList)) {
			Set<String> docs = new HashSet<>();
			for (WiAttachment w : attList) {
				FileInfo o = emc.find(w.getId(), FileInfo.class);
				if (null == o) {
					throw new ExceptionEntityNotExist(w.getId(), FileInfo.class);
				}

				if (!docs.contains(o.getDocumentId()) && !checkAllowVisitDoc(person,  o.getDocumentId())){
					throw new ExceptionAccessDenied(person, o.getId());
				}
				docs.add(o.getDocumentId());
				em.detach(o);
				if(StringUtils.isNotBlank(w.getName())){
					FileTools.verifyConstraint(1, w.getName(), null);
					o.setName(w.getName());
				}
				if(StringUtils.isNotBlank(w.getSite())){
					o.setSite(w.getSite());
				}
				fileList.add(o);
			}
		}
		return fileList;
	}

	protected FileInfo creteFileInfo(String person, Document document, StorageMapping storage, String name,
			String site) {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension(name);
		FileInfo attachment = new FileInfo();
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

	protected byte[] extensionService(EffectivePerson effectivePerson, FileInfo fileInfo, Cms.DocExtensionEvent event)
			throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setAttachment(fileInfo.getId());
		req.setFileName(fileInfo.getName());
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class,
					fileInfo.getStorage());
			req.setFileBase64(Base64.encodeBase64String(fileInfo.readContent(mapping)));
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	public static class Req {

		private String person;
		private String attachment;
		private String fileName;
		private String fileBase64;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getAttachment() {
			return attachment;
		}

		public void setAttachment(String attachment) {
			this.attachment = attachment;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileBase64() {
			return fileBase64;
		}

		public void setFileBase64(String fileBase64) {
			this.fileBase64 = fileBase64;
		}

	}

	protected boolean read(EffectivePerson effectivePerson, FileInfo fileInfo, List<String> identities,
			List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(fileInfo.getCreatorUid())
				|| (ListTools.isEmpty(fileInfo.getReadIdentityList()) && ListTools.isEmpty(fileInfo.getReadUnitList()))
				|| (ListTools.containsAny(identities, fileInfo.getReadIdentityList())
						|| ListTools.containsAny(units, fileInfo.getReadUnitList()))) {
			value = true;
		}
		return value;
	}

	protected boolean edit(EffectivePerson effectivePerson, FileInfo fileInfo, List<String> identities,
			List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(fileInfo.getCreatorUid())
				|| (ListTools.isEmpty(fileInfo.getEditIdentityList()) && ListTools.isEmpty(fileInfo.getEditUnitList()))
				|| (ListTools.containsAny(identities, fileInfo.getEditIdentityList())
						|| ListTools.containsAny(units, fileInfo.getEditUnitList()))) {
			value = true;
		}
		return value;
	}

	protected boolean control(EffectivePerson effectivePerson, Business business, FileInfo fileInfo,
			List<String> identities, List<String> units) throws Exception {
		boolean value = false;
		if (business.isManager(effectivePerson)) {
			value = true;
		} else if (effectivePerson.isPerson(fileInfo.getCreatorUid())) {
			value = true;
		} else if (ListTools.isEmpty(fileInfo.getControllerUnitList())
				&& ListTools.isEmpty(fileInfo.getControllerIdentityList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, fileInfo.getControllerIdentityList())
					|| ListTools.containsAny(units, fileInfo.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}

	public abstract static class AbstractWoControl extends GsonPropertyObject {

		private static final long serialVersionUID = -6077066445665811296L;

		@FieldDescribe("可读.")
		private Boolean allowRead = false;
		@FieldDescribe("可写.")
		private Boolean allowEdit = false;
		@FieldDescribe("可管理.")
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}
	}

}
