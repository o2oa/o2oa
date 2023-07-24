package com.x.cms.assemble.control.jaxrs.file;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.File_;

class ActionDownloadWithApp extends StandardJaxrsAction {

	private Cache.CacheCategory cacheCategory = new Cache.CacheCategory(File.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String appInfoFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), flag, appInfoFlag );
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

			if (optional.isPresent()) {
				wo = ((Wo) optional.get());
			} else {
				Business business = new Business(emc);
				AppInfo appInfo = business.getAppInfoFactory().pick(appInfoFlag);
				if (null == appInfo) {
					throw new ExceptionEntityNotExist(appInfoFlag, AppInfo.class);
				}
				String id = this.get(business, appInfo, flag);
				if (StringUtils.isEmpty(id)) {
					throw new ExceptionEntityNotExist(flag, File.class);
				}
				File file = business.fileFactory().pick(id);
				byte[] bs = new byte[] {};
				if (StringUtils.isNotEmpty(file.getData())) {
					bs = Base64.decodeBase64(file.getData());
				}
				wo = new Wo(bs, this.contentType(true, file.getFileName()),
						this.contentDisposition(true, file.getFileName()));
				CacheManager.put(cacheCategory, cacheKey, wo );
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	private String get(Business business, AppInfo appInfo, String flag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.name), flag);
		p = cb.or(p, cb.equal(root.get(File_.alias), flag));
		p = cb.or(p, cb.equal(root.get(File_.id), flag));
		p = cb.and(p, cb.equal(root.get(File_.appId), appInfo.getId()));
		List<String> list = em.createQuery(cq.select(root.get(File_.id)).where(p)).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

}