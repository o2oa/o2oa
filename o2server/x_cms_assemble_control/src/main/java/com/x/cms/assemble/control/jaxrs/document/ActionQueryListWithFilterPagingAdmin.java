package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.Review_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;
import com.x.cms.core.express.tools.filter.QueryFilter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用
 */
public class ActionQueryListWithFilterPagingAdmin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListWithFilterPagingAdmin.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, Integer page, Integer size, JsonElement jsonElement, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Long total = 0L;
		List<Wo> wos = new ArrayList<>();
		Business business = new Business(null);
		if(!business.isManager(effectivePerson)){
			result.setCount(0L);
			result.setData(wos);
			return result;
		}

		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );

		if( StringUtils.isEmpty( wi.getDocumentType() )) {
			wi.setDocumentType( "信息" );
		}

		if( StringUtils.isEmpty( wi.getOrderField() )) {
			wi.setOrderField( "createTime" );
		}

		if( StringUtils.isEmpty( wi.getOrderType() )) {
			wi.setOrderType( "DESC" );
		}

		if( ListTools.isEmpty( wi.getStatusList() )) {
			List<String> status = new ArrayList<>();
			status.add( "published" );
			wi.setStatusList( status );
		}

		QueryFilter queryFilter = wi.getQueryFilter();

		List<DocumentWo> docWos = new ArrayList<>();
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			EntityManager em = emc.get(Document.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Document> cq = cb.createQuery(Document.class);
			Root<Document> root = cq.from(Document.class);
			Predicate p = CriteriaBuilderTools.composePredicateWithQueryFilter(Document_.class, cb, null, root, queryFilter);

			if("asc".equalsIgnoreCase(wi.getOrderType())){
				docWos = emc.fetchAscPaging(Document.class, DocumentWo.copier, p, page, size, wi.getOrderField());
			}else {
				docWos = emc.fetchDescPaging(Document.class, DocumentWo.copier, p, page, size, wi.getOrderField());
			}
			total = emc.count(Document.class, p);
		}

		Wo wo = null;
		for( DocumentWo documentWo : docWos ) {
			try {
				wo = Wo.copier.copy( documentWo );
				if( wo.getCreatorPerson() != null && !wo.getCreatorPerson().isEmpty() ) {
					wo.setCreatorPersonShort( wo.getCreatorPerson().split( "@" )[0]);
				}
				if( wo.getCreatorUnitName() != null && !wo.getCreatorUnitName().isEmpty() ) {
					wo.setCreatorUnitNameShort( wo.getCreatorUnitName().split( "@" )[0]);
				}
				if( wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName().isEmpty() ) {
					wo.setCreatorTopUnitNameShort( wo.getCreatorTopUnitName().split( "@" )[0]);
				}
				if( wi.getNeedData() ) {
					//需要组装数据
					wo.setData( documentQueryService.getDocumentData( documentWo ) );
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
			wos.add( wo );
		}

		result.setCount(total);
		result.setData(wos);
		return result;
	}

	public class DocumentCacheForFilter {

		private Long total = 0L;
		private List<Wo> documentList = null;

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getDocumentList() {
			return documentList;
		}

		public void setDocumentList(List<Wo> documentList) {
			this.documentList = documentList;
		}
	}

	public static class Wi extends WrapInDocumentFilter{

	}

	public static class DocumentWo extends Document{
		static WrapCopier<Document, DocumentWo> copier = WrapCopierFactory.wo(Document.class, DocumentWo.class,
				JpaObject.singularAttributeField(Document.class, true, true), null);
	}

	public static class Wo extends WrapOutDocumentList {

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Document, Wo> copier = WrapCopierFactory.wo( Document.class, Wo.class, null,JpaObject.FieldsInvisible);

	}
}
