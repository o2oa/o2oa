package com.x.cms.assemble.control.jaxrs.document;

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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		if(wi.getDocIds()!=null) {
			List<String> unitNames = userManagerService.listUnitNamesWithPerson(effectivePerson.getDistinguishedName());
			List<String> groupNames = userManagerService.listGroupNamesByPerson(effectivePerson.getDistinguishedName());
			for(String id : wi.getDocIds()){
				Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), id, effectivePerson.getDistinguishedName());
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
				if (optional.isPresent()) {
					wos.add((Wo)optional.get());
				} else {
					Wo wo = getDocumentQueryResult(business,id, effectivePerson, unitNames, groupNames);
					if(wo != null) {
						wos.add(wo);
						CacheManager.put(cacheCategory, cacheKey, wo);
					}
				}
			}
		}
		result.setData(wos);
		return result;			
	}

	/**
	 * 获取需要返回的文档信息对象
	 * @param id
	 * @param effectivePerson
	 * @return
	 */
	private Wo getDocumentQueryResult(Business business, String id, EffectivePerson effectivePerson, List<String> unitNames, List<String> groupNames) throws Exception {
		Wo wo = new Wo();
		Document document = documentQueryService.view( id, effectivePerson );
		if ( document != null ){
			if(this.hasReadPermission(business, document, unitNames, groupNames, effectivePerson, null)) {
				WoDocument woOutDocument = WoDocument.copier.copy(document);
				wo.setDocId(id);
				wo.setDocument(woOutDocument);
				if (woOutDocument != null) {
					wo.setData(documentQueryService.getDocumentData(document));
				}
			}else{
				return null;
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
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null,JpaObject.FieldsInvisible);

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