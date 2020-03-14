package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Attachment_;
import com.x.processplatform.core.entity.content.Work;
import net.sf.ehcache.Ehcache;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

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

	public static Ehcache cache = ApplicationCache.instance().getCache(CacheResultObject.class);

	public static class CacheResultObject extends GsonPropertyObject {

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

	public static Ehcache cachePreviewPdf = ApplicationCache.instance().getCache(PreviewPdfResultObject.class);

	public static class PreviewPdfResultObject extends GsonPropertyObject {

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

	public static Ehcache cachePreviewImage = ApplicationCache.instance().getCache(PreviewImageResultObject.class);

	public static class PreviewImageResultObject extends GsonPropertyObject {

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

	public boolean read(Attachment attachment, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getReadIdentityList()) && ListTools.isEmpty(attachment.getReadUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getReadIdentityList())
					|| ListTools.containsAny(units, attachment.getReadUnitList())) {
				value = true;
			}
		}
		return value;
	}

	public boolean edit(Attachment attachment, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getEditIdentityList()) && ListTools.isEmpty(attachment.getEditUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getEditIdentityList())
					|| ListTools.containsAny(units, attachment.getEditUnitList())) {
				value = true;
			}
		}
		return value;
	}

	public boolean control(Attachment attachment, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(attachment.getPerson())) {
			value = true;
		} else if (ListTools.isEmpty(attachment.getControllerUnitList()) && ListTools.isEmpty(attachment.getControllerIdentityList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, attachment.getControllerIdentityList())
					|| ListTools.containsAny(units, attachment.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}
}
