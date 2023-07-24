package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.Data;

public class ActionQueryGetDocumentData extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryGetDocumentData.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = getDocumentQueryResult(id);
		result.setData(wo);
		return result;
	}

	private Wo getDocumentQueryResult(String id) throws Exception {
		Wo wo = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wo = (Wo)optional.get();
		} else {
			Document document = documentQueryService.get(id);
			wo = new Wo();
			if(document!=null) {
				WoDocument woOutDocument = WoDocument.copier.copy(document);
				wo.setDocId(document.getId());
				wo.setDocument(woOutDocument);
				wo.setData(documentQueryService.getDocumentData(document));
				CacheManager.put(cacheCategory, cacheKey, wo);
			}
		}
		return wo;
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

}
