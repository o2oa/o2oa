package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ProcessPlatform;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionFileNameInvalid;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Attachment_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	protected static final String OFD_ATT_KEY = ".ofd";

	protected static final String SITE_SEPARATOR = "~";

	protected static final String FILE_SEPARATOR = ",";

	public static class WiExtraParam {
		private String site;

		private String fileName;

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

	}

	public static class CacheResultObject extends GsonPropertyObject {

		private static final long serialVersionUID = -1071169661372205135L;

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	public static class PreviewPdfResultObject extends GsonPropertyObject {

		private static final long serialVersionUID = 7589263971880126815L;

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	public static class PreviewImageResultObject extends GsonPropertyObject {

		private static final long serialVersionUID = 2119185075125829853L;

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	public static class ReqAttachment extends GsonPropertyObject {

		private static final long serialVersionUID = -3590703578295962581L;

		private String id;
		private String name;
		private String site;
		private Boolean isSoftCopy;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSite() {
			return site;
		}

		public void setSite(String site) {
			this.site = site;
		}

		public Boolean getSoftCopy() {
			return isSoftCopy;
		}

		public void setSoftCopy(Boolean softCopy) {
			isSoftCopy = softCopy;
		}
	}

	public String adjustFileName(Business business, String job, String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName);
		String base = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		for (int i = 1; i < 50; i++) {
			list.add(base + i + (StringUtils.isEmpty(extension) ? "" : "." + extension));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = root.get(Attachment_.name).in(list);
		p = cb.and(p, cb.equal(root.get(Attachment_.job), job));
		cq.select(root.get(Attachment_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public boolean read(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units, Business business) throws Exception {
		boolean value = false;
		if (ListTools.isEmpty(attachment.getReadIdentityList()) && ListTools.isEmpty(attachment.getReadUnitList())) {
			value = true;
		} else if (ListTools.containsAny(identities, attachment.getReadIdentityList())
				|| ListTools.containsAny(units, attachment.getReadUnitList())) {
			value = true;
		} else {
			value = this.edit(attachment, effectivePerson, identities, units, business);
		}
		return value;
	}

	public boolean edit(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units, Business business) throws Exception {
		boolean value = false;
		if (ListTools.isEmpty(attachment.getEditIdentityList()) && ListTools.isEmpty(attachment.getEditUnitList())) {
			value = true;
		} else if (ListTools.containsAny(identities, attachment.getEditIdentityList())
				|| ListTools.containsAny(units, attachment.getEditUnitList())) {
			value = true;
		} else {
			value = this.control(attachment, effectivePerson, identities, units, business);
		}
		return value;
	}

	public boolean control(Attachment attachment, EffectivePerson effectivePerson, List<String> identities,
			List<String> units, Business business) throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getControllerUnitList())
				&& ListTools.isEmpty(attachment.getControllerIdentityList())) {
			value = true;
		} else if (ListTools.containsAny(identities, attachment.getControllerIdentityList())
				|| ListTools.containsAny(units, attachment.getControllerUnitList())) {
			value = true;
		} else if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson,
				attachment.getApplication(), attachment.getProcess()))) {
			value = true;
		}
		return value;
	}

	protected CompletableFuture<Boolean> checkJobControlFuture(EffectivePerson effectivePerson, String job) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());
	}

	protected CompletableFuture<Boolean> checkControlFuture(EffectivePerson effectivePerson, String flag) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, flag).enableAllowVisit().build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());
	}

	/**
	 * 判断附件是否符合大小、文件类型的约束
	 *
	 * @param size
	 * @param fileName
	 * @param callback
	 * @throws Exception
	 */
	protected void verifyConstraint(long size, String fileName, String callback) throws Exception {
		if (!StringTools.isFileName(fileName)) {
			throw new ExceptionFileNameInvalid(fileName);
		}
		String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
		if(StringUtils.isBlank(fileType)){
			throw new ExceptionFileNameInvalid(fileName);
		}
		if (Config.general().getAttachmentConfig().getFileSize() != null
				&& Config.general().getAttachmentConfig().getFileSize() > 0) {
			size = size / (1024 * 1024);
			if (size > Config.general().getAttachmentConfig().getFileSize()) {
				if (StringUtils.isNotEmpty(callback)) {
					throw new ExceptionAttachmentInvalidCallback(callback, fileName,
							Config.general().getAttachmentConfig().getFileSize());
				} else {
					throw new ExceptionAttachmentInvalid(fileName,
							Config.general().getAttachmentConfig().getFileSize());
				}
			}
		}
		if ((Config.general().getAttachmentConfig().getFileTypeIncludes() != null
				&& !Config.general().getAttachmentConfig().getFileTypeIncludes().isEmpty())
				&& (!ListTools.contains(Config.general().getAttachmentConfig().getFileTypeIncludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
		if ((Config.general().getAttachmentConfig().getFileTypeExcludes() != null
				&& !Config.general().getAttachmentConfig().getFileTypeExcludes().isEmpty())
				&& (ListTools.contains(Config.general().getAttachmentConfig().getFileTypeExcludes(), fileType))) {
			if (StringUtils.isNotEmpty(callback)) {
				throw new ExceptionAttachmentInvalidCallback(callback, fileName);
			} else {
				throw new ExceptionAttachmentInvalid(fileName);
			}
		}
	}

	protected byte[] read(EffectivePerson effectivePerson, StorageMapping mapping, Work work,
			WorkCompleted workCompleted, Attachment attachment) throws Exception {
		byte[] bytes = null;
		if (work != null) {
			Optional<ProcessPlatform.WorkExtensionEvent> event = Config.processPlatform().getExtensionEvents()
					.getWorkAttachmentDownloadEvents()
					.bind(work.getApplication(), work.getProcess(), work.getActivity());
			if (event.isPresent()) {
				bytes = this.extensionService(effectivePerson, attachment, event.get());
			}
		} else if (workCompleted != null) {
			Optional<ProcessPlatform.WorkCompletedExtensionEvent> event = Config.processPlatform().getExtensionEvents()
					.getWorkCompletedAttachmentDownloadEvents()
					.bind(workCompleted.getApplication(), workCompleted.getProcess());
			if (event.isPresent()) {
				bytes = this.extensionService(effectivePerson, attachment, event.get());
			}
		}
		if (bytes == null) {
			bytes = attachment.readContent(mapping);
		}
		return bytes;
	}

	protected byte[] extensionService(EffectivePerson effectivePerson, Attachment attachment,
			ProcessPlatform.WorkExtensionEvent event) throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setAttachment(attachment.getId());
		req.setFileName(attachment.getName());
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			req.setFileBase64(Base64.encodeBase64String(attachment.readContent(mapping)));
			bytes = CipherConnectionAction.postBinary(effectivePerson.getDebugger(), event.getUrl(), req);
		}
		return bytes;
	}

	protected byte[] extensionService(EffectivePerson effectivePerson, Attachment attachment,
			ProcessPlatform.WorkCompletedExtensionEvent event) throws Exception {
		byte[] bytes = null;
		Req req = new Req();
		req.setPerson(effectivePerson.getDistinguishedName());
		req.setAttachment(attachment.getId());
		req.setFileName(attachment.getName());
		if (StringUtils.isNotEmpty(event.getCustom())) {
			bytes = ThisApplication.context().applications().postQueryBinary(event.getCustom(), event.getUrl(), req);
		} else {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
					attachment.getStorage());
			req.setFileBase64(Base64.encodeBase64String(attachment.readContent(mapping)));
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
}
