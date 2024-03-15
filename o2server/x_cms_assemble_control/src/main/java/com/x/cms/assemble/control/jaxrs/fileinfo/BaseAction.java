package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
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
