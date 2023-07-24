package com.x.file.assemble.control.jaxrs.folder;

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
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Folder;
import com.x.file.core.entity.personal.Folder_;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean exist(Business business, EffectivePerson effectivePerson, String name, String superior,
			String excludeId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(Folder_.name), name));
		p = cb.and(p, cb.equal(root.get(Folder_.superior), StringUtils.trimToEmpty(superior)));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Folder_.id), excludeId));
		}
		cq.select(cb.count(root)).where(p);
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected void setCount(Business business, AbstractWoFolder wo) throws Exception {
		List<String> ids = business.attachment().listWithFolder(wo.getId());
		long count = 0;
		long size = 0;
		for (Attachment o : business.entityManagerContainer().fetch(ids, Attachment.class,
				ListTools.toList(Attachment.length_FIELDNAME))) {
			count++;
			size += o.getLength();
		}
		wo.setAttachmentCount(count);
		wo.setSize(size);
		wo.setFolderCount(business.folder().countSubDirect(wo.getId()));
	}

	public static class AbstractWoFolder extends Folder {

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
