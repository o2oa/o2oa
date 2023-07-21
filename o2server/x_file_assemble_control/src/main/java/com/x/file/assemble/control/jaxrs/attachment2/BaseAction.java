package com.x.file.assemble.control.jaxrs.attachment2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileConfig;
import com.x.file.core.entity.open.FileConfigProperties;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;

abstract class BaseAction extends StandardJaxrsAction {

	protected static final String EXCEPTION_FLAG = "existed";

	protected Boolean exist(Business business, String fileName, String folderId, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.name), fileName);
		p = cb.and(p, cb.equal(root.get(Attachment2_.person), person));
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	protected void verifyConstraint(Business business, String person, long size, String fileName) throws Exception{
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileConfig.class);
		Cache.CacheKey cacheKey = new Cache.CacheKey(FileConfig.class, Business.SYSTEM_CONFIG);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		FileConfig config = null;
		if(optional.isPresent()){
			config = (FileConfig)optional.get();
		}else{
			config = business.entityManagerContainer().firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config != null){
				business.entityManagerContainer().get(FileConfig.class).detach(config);
				CacheManager.put(cacheCategory, cacheKey, config);
			}
		}
		if (config != null){
			if(config.getCapacity()!=null && config.getCapacity()>0) {
				long usedCapacity = (business.attachment2().getUseCapacity(person) + size) / (1024 * 1024);
				if (usedCapacity > config.getCapacity()) {
					throw new ExceptionCapacityOut(usedCapacity, config.getCapacity());
				}
			}
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

	public String adjustFileName(Business business, String folderId, String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName);
		String base = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		for (int i = 1; i < 20; i++) {
			list.add(base + i + (StringUtils.isEmpty(extension) ? "" : "." + extension));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = root.get(Attachment2_.name).in(list);
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		cq.select(root.get(Attachment2_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}
}
