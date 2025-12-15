package com.x.pan.assemble.control.factory;

import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.pan.assemble.control.AbstractFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3;
import com.x.pan.core.entity.Attachment3_;
import com.x.pan.core.entity.AttachmentVersion;
import com.x.pan.core.entity.AttachmentVersion_;
import com.x.pan.core.entity.FileStatusEnum;
import com.x.pan.core.entity.ZonePermission;
import com.x.pan.core.entity.ZonePermission_;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class Attachment3Factory extends AbstractFactory {

	public Attachment3Factory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.equal(root.get(Attachment3_.folder), Business.TOP_FOLD));
		cq.select(root.get(Attachment3_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithFolder(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.folder), folder);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment3_.status), status));
		}
		cq.select(root.get(Attachment3_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countWithFolder(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.folder), id);
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), FileStatus.VALID.getName()));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithName(String person, String name) throws Exception {
		List<String> list = business().getUserInfo(person);
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		EntityManager subEm = this.entityManagerContainer().get(ZonePermission.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaBuilder subCb = subEm.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.status), FileStatus.VALID.getName());
		String key = StringTools.escapeSqlLikeKey(name);
		p = cb.and(p, cb.like(root.get(Attachment3_.name), "%" + key + "%", StringTools.SQL_ESCAPE_CHAR));

		Subquery<ZonePermission> subQuery = cq.subquery(ZonePermission.class);
		Root<ZonePermission> subRoot = subQuery.from(subEm.getMetamodel().entity(ZonePermission.class));
		subQuery.select(subRoot);
		Path path = business().getSystemConfig().getReadPermissionDown() ? root.get(Attachment3_.folder) : root.get(Attachment3_.zoneId);
		Predicate p_permission = subCb.equal(subRoot.get(ZonePermission_.zoneId), path);
		p_permission = subCb.and(p_permission, subRoot.get(ZonePermission_.name).in(list));
		subQuery.where(p_permission);
		p = cb.and(p, cb.exists(subQuery));

		cq.select(root.get(Attachment3_.id)).where(p).orderBy(cb.desc(root.get(Attachment3_.lastUpdateTime)));
		return em.createQuery(cq).setMaxResults(50).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public long getUseCapacity(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), FileStatus.VALID.getName()));
		cq.select(cb.sum(root.get(Attachment3_.length))).where(p);
		Long sum = em.createQuery(cq).getSingleResult();
		return sum == null ? 0 : sum;
	}

	public List<Attachment3> listWithFolder2(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment3> cq = cb.createQuery(Attachment3.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.folder), folder);
		if (StringUtils.isNotBlank(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment3_.status), status));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public boolean exist(String fileName, String folderId, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(cb.lower(root.get(Attachment3_.name)), fileName.toLowerCase());
		p = cb.and(p, cb.equal(root.get(Attachment3_.folder), folderId));
		if(StringUtils.isNotBlank(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment3_.status), status));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public long statZoneCapacity(String zoneId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.zoneId), zoneId);
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), FileStatusEnum.VALID.getName()));
		cq.select(cb.sum(root.get(Attachment3_.length))).where(p);
		Long sum = em.createQuery(cq).getSingleResult();
		return sum == null ? 0 : sum;
	}

	public String adjustFileName(String folderId, String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName);
		String base = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		for (int i = 1; i < 10; i++) {
			list.add(base + i + (StringUtils.isEmpty(extension) ? "" : "." + extension));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = this.entityManagerContainer().get(Attachment3.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment3> root = cq.from(Attachment3.class);
		Predicate p = cb.equal(root.get(Attachment3_.folder), folderId);
		p = cb.and(p, root.get(Attachment3_.name).in(list));
		p = cb.and(p, cb.equal(root.get(Attachment3_.status), FileStatusEnum.VALID.getName()));
		cq.select(root.get(Attachment3_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public AttachmentVersion getVersion(String id, Integer version) throws Exception{
		EntityManager em = this.entityManagerContainer().get(AttachmentVersion.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttachmentVersion> cq = cb.createQuery(AttachmentVersion.class);
		Root<AttachmentVersion> root = cq.from(AttachmentVersion.class);
		Predicate p = cb.equal(root.get(AttachmentVersion_.attachmentId), id);
		p = cb.and(p, cb.equal(root.get(AttachmentVersion_.fileVersion), version));
		List<AttachmentVersion> list = em.createQuery(cq.where(p)).getResultList();
		return ListTools.isEmpty(list) ? null : list.get(0);
	}

}
