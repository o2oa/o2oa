package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.Data;

/**
 * @author sword
 */
public class ActionQueryListDocumentData extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListDocumentData.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
		}
		List<Wo> wos = new ArrayList<>();
		if(wi.getDocIds()!=null && !wi.getDocIds().isEmpty()) {
			wos = getDocumentQueryResult(business, wi.getDocIds(), effectivePerson);
		}
		result.setData(wos);
		return result;
	}

	private List<Wo> getDocumentQueryResult(Business business, List<String> ids, EffectivePerson effectivePerson) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;
		List<Document> docs = documentQueryService.list(ids);
		for (Document document : docs){
				Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), document.getId());
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				if (optional.isPresent()) {
					wo = (Wo)optional.get();
				} else {
					wo = new Wo();
					WoDocument woOutDocument = WoDocument.copier.copy(document);
					wo.setDocId(document.getId());
					wo.setDocument(woOutDocument);
					wo.setData(documentQueryService.getDocumentData(document));
					CacheManager.put(cacheCategory, cacheKey, wo);
				}
				wos.add(wo);
			//}
		}

		return wos;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe( "文档ID." )
		private String docId;

		@FieldDescribe( "作为输出的CMS文档数据对象." )
		private WoDocument document;

		@FieldDescribe( "文档所有数据信息." )
		private Data data;

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
			this.document = document;
		}

		public Data getData() {
			return data;
		}

		public void setData(Data data) {
			this.data = data;
		}

	}

	public static class WoDocument extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null,JpaObject.FieldsInvisible);

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
