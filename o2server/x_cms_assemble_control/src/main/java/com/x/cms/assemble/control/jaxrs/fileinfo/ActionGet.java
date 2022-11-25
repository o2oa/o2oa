package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
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

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, String documentId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wrap = ( Wo ) optional.get();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				FileInfo fileInfo = emc.find(id, FileInfo.class);
				if ( null == fileInfo ) {
					throw new ExceptionFileInfoNotExists(id);
				}
				Document document = emc.find( fileInfo.getDocumentId(), Document.class);
				if (null == document) {
					throw new ExceptionDocumentNotExists(fileInfo.getDocumentId());
				}
				Business business = new Business(emc);
				if(!business.isDocumentReader(effectivePerson, document)){
					throw new ExceptionAccessDenied(effectivePerson);
				}
				wrap = Wo.copier.copy( fileInfo );
				CacheManager.put(cacheCategory, cacheKey, wrap );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wo extends FileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo( FileInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}
