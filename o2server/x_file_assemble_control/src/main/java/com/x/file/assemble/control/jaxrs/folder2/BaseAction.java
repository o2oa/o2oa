package com.x.file.assemble.control.jaxrs.folder2;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.service.FileCommonService;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Folder2_;

abstract class BaseAction extends StandardJaxrsAction {

	protected FileCommonService fileCommonService = new FileCommonService();

	protected boolean exist(Business business, EffectivePerson effectivePerson, String name, String superior,
			String excludeId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Folder2_.name), name));
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), StringUtils.trimToEmpty(superior)));
		p = cb.and(p, cb.equal(root.get(Folder2_.status), FileStatus.VALID.getName()));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Folder2_.id), excludeId));
		}
		cq.select(cb.count(root)).where(p);
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected void setCount(Business business, AbstractWoFolder wo) throws Exception {
		List<String> ids = business.attachment2().listWithFolder(wo.getId(),FileStatus.VALID.getName());
		long count = 0;
		long size = 0;
		for (Attachment2 o : business.entityManagerContainer().fetch(ids, Attachment2.class,
				ListTools.toList(Attachment2.length_FIELDNAME))) {
			count++;
			size += o.getLength();
		}
		wo.setAttachmentCount(count);
		wo.setSize(size);
		wo.setFolderCount(business.folder2().countSubDirect(wo.getId()));
	}

	public static class AbstractWoFolder extends Folder2 {

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

}
