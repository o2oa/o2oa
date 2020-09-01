package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

class ActionDownloadWorkInfo extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDownloadWorkInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Work work = emc.find(workId, Work.class);
			if (work == null) {
				WorkCompleted workCompleted = emc.find(workId, WorkCompleted.class);
				if (null == workCompleted) {
					throw new Exception("workId: " + workId + " not exist in work or workCompleted");
				}
				if (!business.readable(effectivePerson, workCompleted)) {
					throw new ExceptionWorkCompletedAccessDenied(effectivePerson.getDistinguishedName(),
							workCompleted.getTitle(), workCompleted.getId());
				}
			} else {
				if (!business.readable(effectivePerson, work)) {
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
			}
			Wo wo = null;
			CacheCategory cacheCategory = new CacheCategory(CacheResultObject.class);
			CacheKey cacheKey = new CacheKey(flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				CacheResultObject ro = (CacheResultObject) optional.get();
				wo = new Wo(ro.getBytes(), this.contentType(false, ro.getName()),
						this.contentDisposition(false, ro.getName()));
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
}
