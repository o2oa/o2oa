package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;

/**
 * @author sword
 */
public class ActionQueryListDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListDocument.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		List<Wo> wos = new ArrayList<>();
		if(ListTools.isNotEmpty(wi.getDocIds())) {
			Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), gson.toJson(wi.docIds));
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				wos = (List<Wo>)optional.get();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					List<Document> documentList = emc.fetch(wi.getDocIds(), Document.class);
					for (Document document : documentList) {
						Wo wo = Wo.copier.copy(document);
						if (StringUtils.isNoneBlank(document.getIndexPics())) {
							wo.setPictureList(ListTools.toList(document.getIndexPics().split(",")));
						}
						wos.add(wo);
					}
					if(wos.size() > 0) {
						CacheManager.put(cacheCategory, cacheKey, wos);
					}
				}
			}
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends WrapOutDocumentList {

		public static final WrapCopier<Document, Wo> copier = WrapCopierFactory.wo( Document.class, Wo.class, null,JpaObject.FieldsInvisible);

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe( "文档id" )
		private List<String> docIds = new ArrayList<>();

		public List<String> getDocIds() {
			return docIds;
		}

		public void setDocIds(List<String> docIds) {
			this.docIds = docIds;
		}
	}
}
