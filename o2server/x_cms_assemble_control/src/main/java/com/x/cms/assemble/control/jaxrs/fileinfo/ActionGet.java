package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			String documentId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), id, effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			result.setData(wo);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				FileInfo fileInfo = emc.find(id, FileInfo.class);
				if (null == fileInfo) {
					throw new ExceptionFileInfoNotExists(id);
				}
				Document document = emc.find(fileInfo.getDocumentId(), Document.class);
				if (null == document) {
					throw new ExceptionDocumentNotExists(fileInfo.getDocumentId());
				}
				Business business = new Business(emc);
				List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
				List<String> units = business.organization().unit().listWithPerson(effectivePerson);
				if (!business.isDocumentReader(effectivePerson, document)) {
					throw new ExceptionAccessDenied(effectivePerson, document);
				}
				boolean canRead = this.read(effectivePerson, fileInfo, identities, units);
				if (!canRead) {
					throw new ExceptionAccessDenied(effectivePerson, fileInfo);
				}
				wo = Wo.copier.copy(fileInfo);
				wo.getControl().setAllowRead(true);
				boolean canControl = this.control(effectivePerson, business, fileInfo, identities, units);
				if (canControl) {
					wo.getControl().setAllowControl(true);
					wo.getControl().setAllowEdit(true);
				} else {
					boolean canEdit = this.edit(effectivePerson, fileInfo, identities, units);
					wo.getControl().setAllowEdit(canEdit);
				}
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
		}
		return result;
	}

	public static class Wo extends FileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static final WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo(FileInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

	}

	public static class WoControl extends AbstractWoControl {

		private static final long serialVersionUID = 3365013438984004717L;

	}
}
