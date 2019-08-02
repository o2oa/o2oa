package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.cms.core.entity.tools.filter.term.EqualsTerm;
import com.x.cms.core.entity.tools.filter.term.IsFalseTerm;
import com.x.cms.core.entity.tools.filter.term.IsTrueTerm;

public class ActionListNextPageViewData extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListNextPageViewData.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String lastDocId, Integer pageSize, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		Wo wo = null;
		WoDocument woDocument = new WoDocument();
		List<Document> documentList = null;
		List<Document> searchResultList = new ArrayList<>();
		List<Review> reviewList =  null;
		List<String> viewIdsForCategory = null;
		Long documentCount = 0L;
		CategoryInfo category = null;
		View view = null;
		Boolean check = true;
		Boolean isManager = false;
		Boolean needData = false;
		Document document = null;
		String personName =  effectivePerson.getDistinguishedName();
		List<String> sortableFieldNames = null;
		QueryFilter queryFilter = null;
		
		if( pageSize <= 0 || pageSize == null ){
			pageSize = 12;
		}
		
		if( "(0)".equals( lastDocId ) || StringUtils.isEmpty( lastDocId ) ){
			lastDocId = null;
		}
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null );
		}
		
		if( StringUtils.isEmpty( wi.getCategoryId() ) ) {
			check = false;
			Exception exception = new ExceptionViewDateQueryCategoryIdEmpty();
			result.error( exception );
		}
		
		if( StringUtils.isEmpty( wi.getViewId() ) ) {
			check = false;
			Exception exception = new ExceptionViewDataQueryViewIdEmpty();
			result.error( exception );
		}
		
		try {
			isManager = userManagerService.isManager(request, effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionViewInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ){
			try{
				category = categoryInfoServiceAdv.get( wi.getCategoryId() );
				if( category == null ){
					check = false;
					Exception exception = new ExceptionCategoryNotExists( wi.getCategoryId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据ID查询分类信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				view = viewServiceAdv.get( wi.getViewId() );
				if( view == null ){
					check = false;
					Exception exception = new ExceptionViewNotExists( wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据ID查询列表视图信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			//要对比传和的列表的formId和分类的formId是否一致，如果不一致，则无法正确查询
			if( !category.getFormId().equals( view.getFormId() ) && !category.getReadFormId().equals( view.getFormId() ) ) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess( "分类和列表使用的表单不一致，无法正确查询数据。" );
				result.error( exception );
			}
		}
		
		if( check ){
			try{
				//检查当前Category与View的关联是否存在
				viewIdsForCategory = viewServiceAdv.listViewIdsWithCategoryId( wi.getCategoryId() );
				//判断分类与列表信息是否存在关联，如果不存在，则不允许访问
				if( ListTools.isEmpty( viewIdsForCategory ) || !viewIdsForCategory.contains( wi.getViewId() )) {
					check = false;
					Exception exception = new ExceptionViewAccessDenied( wi.getCategoryId(),  wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据分类ID查询列表视图ID信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){//设置查询条件的默认值，如果不传入则使用列表设置的值
			if( StringUtils.isEmpty( wi.getOrderField() ) ) {
				wi.setOrderField(view.getOrderField());
			}
			if( StringUtils.isEmpty( wi.getOrderType() ) ) {
				wi.setOrderType(view.getOrderType());
			}
			if( StringUtils.isEmpty( wi.getDocStatus() ) ) {
				wi.setDocStatus( "published" );
			}
		}

		if( check ) {
			sortableFieldNames = Arrays.asList( Document.documentFieldNames );
			List<ViewFieldConfig>  fieldConfigs = viewServiceAdv.listFieldConfigByView( view.getId() );
			if( ListTools.isNotEmpty( fieldConfigs )) {
				for( ViewFieldConfig config : fieldConfigs ) {
					if( !sortableFieldNames.contains( config.getFieldName() )) { //存在Document里不存在的列
						needData = true;
						break;
					}
				}
			}
		}
		
		if (check) {
			try {
				queryFilter = wi.getQueryFilter();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess(e, "系统在获取查询条件信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			if( StringUtils.isNotEmpty( lastDocId )) {
				document = documentQueryService.get( lastDocId );
			}
			
			//判断一下，如果排序的列不是Document的常规列
			if( !sortableFieldNames.contains( wi.getOrderField() ) ) {
				//查询该分类下所有可见的DocId
				List<String> viewableDocList = null;
				if( isManager ) {
					documentCount = documentQueryService.countWithConditionOutofPermission( queryFilter );
					viewableDocList = documentQueryService.listIdsByCategoryId( category.getId(), wi.getOrderField(), wi.getOrderField(), 2000 );
				}else {
					documentCount = documentQueryService.countWithConditionInReview( personName, queryFilter );
					viewableDocList = documentQueryService.listDocIdsWithConditionInReview( personName, wi.getOrderField(), wi.getOrderField(), queryFilter, 2000 );
				}
				
				//从Item里查询出2000个排序好的对象，拼成 dataObjList (  docId, sortFieldValue )返回
				List<SimpleItemObj> simpleItems = documentQueryService.listSortObjWithOrderFieldInData( viewableDocList, wi.getOrderField(), wi.getOrderField(), wi.getOrderType() );
			
				if( ListTools.isNotEmpty( simpleItems )) {
					String sequence = null;
					Boolean  pickDocument = false;
					documentList = new ArrayList<>();
					for( SimpleItemObj item : simpleItems ) {				
						//填充Document数据
						sequence = documentQueryService.getSequence( item.getId() );
						if( StringUtils.isNotEmpty( sequence)) {
							if( document != null ) { //说明需要查询后一页
								if( sequence.equalsIgnoreCase( document.getSequence() )) {
									pickDocument = true;//开始往正式输出的列表里添加pageSize个document
								}
							}else {
								pickDocument = true;//取第1页的pageSize个document
							}							
							if( pickDocument ) {
								if( documentList.size() < pageSize ) {
									documentList.add( documentQueryService.get( item.getId() ));
								}else {//数据够一页了
									break;
								}
							}
						}
					}
				}
			}else {//只是根据文档数据进行排序
				if( isManager ) {
					documentCount = documentQueryService.countWithConditionOutofPermission( queryFilter );
					if( Document.isFieldInSequence(wi.getOrderField()) ) {
						//直接从Document忽略权限查询
						searchResultList = documentQueryService.listNextWithConditionOutofPermission( lastDocId, pageSize, wi.getOrderField(), wi.getOrderType(), queryFilter );
					}else {//如果是有序列的字段，使用序列分页，如果不是，则直接查询2000条，内存排序
						documentList = documentQueryService.listNextWithConditionOutofPermission( wi.getOrderField(), wi.getOrderType(), queryFilter, 2000 );
						//循环分页，查询传入的ID所在的位置，向后再查询N条
						if( ListTools.isNotEmpty( documentList )) {
							Boolean add2List = false;
							//放一页到searchResultList中进行返回
							for( Document doc : documentList ) {
								if( StringUtils.isEmpty( lastDocId ) || doc.getId().equalsIgnoreCase( lastDocId ) ) {
									add2List = true;
								}
								if( add2List ) {
									searchResultList.add( doc );
								}
								if( searchResultList.size() >= pageSize ) {
									break;
								}
							}
						}
					}
				}else {
					documentCount = documentQueryService.countWithConditionInReview( personName, queryFilter );
					if( Document.isFieldInSequence(wi.getOrderField()) ) {//如果是有序列的字段，使用序列分页，如果不是，则直接查询2000条，内存排序
						// 从Review表中查询符合条件的对象，并且转换为Document对象列表
						searchResultList = documentQueryService.listNextWithConditionInReview( lastDocId, pageSize, wi.getOrderField(), wi.getOrderType(), personName, queryFilter );
					}else {
						reviewList =  documentQueryService.listNextWithConditionInReview( wi.getOrderField(), wi.getOrderType(), personName, queryFilter, 2000 );
						//循环分页，查询传入的ID所在的位置，向后再查询N条，转换为Document放到searchResultList
						if( ListTools.isNotEmpty( reviewList )) {
							Boolean add2List = false;
							List<String> docIds = new ArrayList<>();
							//放一页到searchResultList中进行返回
							for( Review review : reviewList ) {
								if( StringUtils.isEmpty( lastDocId ) || review.getDocId().equalsIgnoreCase( lastDocId ) ) {
									add2List = true;
								}
								if( add2List ) {
									if( !docIds.contains( review.getDocId() )) {
										docIds.add( review.getDocId() );
										searchResultList.add( documentQueryService.get(  review.getDocId()  ));
										if( searchResultList.size() >= pageSize ) {
											break;
										}
									}
								}
							}
						}
					}
				}	
			}
		}		
		
		if( check ){
			//补充业务数据
			if( searchResultList != null && searchResultList.size() > 0 ){
				for( Document _document : searchResultList ){
					wo = new Wo();
					try {
						woDocument = WoDocument.copier.copy( _document );
						if( woDocument != null ) {
							if( woDocument.getCreatorPerson() != null && !woDocument.getCreatorPerson().isEmpty() ) {
								woDocument.setCreatorPersonShort( woDocument.getCreatorPerson().split( "@" )[0]);
							}
							if( woDocument.getCreatorUnitName() != null && !woDocument.getCreatorUnitName().isEmpty() ) {
								woDocument.setCreatorUnitNameShort( woDocument.getCreatorUnitName().split( "@" )[0]);
							}
							if( woDocument.getCreatorTopUnitName() != null && !woDocument.getCreatorTopUnitName().isEmpty() ) {
								woDocument.setCreatorTopUnitNameShort( woDocument.getCreatorTopUnitName().split( "@" )[0]);
							}
						}
						wo.setDocument( woDocument );
					} catch (Exception e) {
						Exception exception = new ExceptionViewInfoProcess( e, "系统在将文档信息转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					if( needData ) { //存在Document里不存在的列，需要进一步组装Data
						try {
							wo.setData( documentQueryService.getDocumentData( _document ) );
						} catch (Exception e) {
							Exception exception = new ExceptionViewInfoProcess( e, "系统在根据文档信息获取文档数据信息时发生异常。" );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}					
					wos.add( wo );
				}
			}
			result.setCount( documentCount );
			result.setData( wos );
			result.setCount( documentCount );
		}
		
		return result;
	}

	public class Wi {
		
		@FieldDescribe( "是否置顶：ALL|TOP|UNTOP." )
		private String topFlag = "ALL";
		
		@FieldDescribe("需要过滤文档的分类ID")
		private String categoryId;

		@FieldDescribe("需要查询的列表ID")
		private String viewId;
		
		@FieldDescribe("当前查询用于排序的列名，默认以列表配置为主")
		private String orderField;

		@FieldDescribe("当前排序方式：DESC|ASC，默认以列表配置为主")
		private String orderType;
		
		@FieldDescribe("值类别：支持string | date | time | datetime | boolean | boolean | text")
		private String fieldType;
		
		@FieldDescribe("查询文档类型: 全部 | 信息 | 数据 ")
		private String documentType;
		
		@FieldDescribe("需要查询的文档状态：published | draft | archived")
		private String docStatus;
		
		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public String getOrderField() {
			return orderField;
		}

		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getViewId() {
			return viewId;
		}

		public void setViewId(String viewId) {
			this.viewId = viewId;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}
		
		public String getTopFlag() {
			return topFlag;
		}

		public void setTopFlag(String topFlag) {
			this.topFlag = topFlag;
		}

		/**
		 * 根据传入的查询参数，组织一个完整的QueryFilter对象
		 * @return
		 * @throws Exception 
		 */
		public QueryFilter getQueryFilter() throws Exception {
			QueryFilter queryFilter = new QueryFilter();		
			queryFilter.setJoinType( "and" );
			
			if( StringUtils.isNotEmpty( this.getCategoryId() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "categoryId", this.getCategoryId() ) );
			}
			
			//文档类型：全部 | 信息 | 数据
			if( StringUtils.isNotEmpty( this.getDocumentType())) {			
				if( "信息".equals( this.getDocumentType() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
				}else if( "数据".equals( this.getDocumentType() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
				}
			}
			
			//是否置顶：ALL|TOP|UNTOP
			if( StringUtils.isNotEmpty( this.getTopFlag())) {			
				if( "TOP".equals( this.getTopFlag() )) {
					queryFilter.addIsTrueTerm( new IsTrueTerm( "isTop" ) );
				}else if( "UNTOP".equals( this.getDocumentType() )) {
					queryFilter.addIsFalseTerm( new IsFalseTerm( "isTop" ) );
				}
			}

			if( StringUtils.isNotEmpty( this.getDocStatus())) {
				queryFilter.addEqualsTerm( new EqualsTerm( "docStatus", this.getDocStatus() ) );
			}
			
			return queryFilter;
		}

	}
	
	public static class Wo extends GsonPropertyObject {
		
		@FieldDescribe( "文档对象的序列." )
		private String sequence = null;
		
		@FieldDescribe( "文档对象的ID." )
		private String id = null;
		
		@FieldDescribe( "排序列的值." )
		private String orderFieldValue = null;

		@FieldDescribe( "作为输出的CMS文档数据对象." )
		private WoDocument document;

		@FieldDescribe( "文档所有数据信息." )
		private Map<?, ?> data;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
			this.document = document;
		}

		public Map<?, ?> getData() {
			return data;
		}

		public void setData(Map<?, ?> data) {
			this.data = data;
		}

		public String getSequence() {
			return sequence;
		}

		public void setSequence(String sequence) {
			this.sequence = sequence;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrderFieldValue() {
			return orderFieldValue;
		}

		public void setOrderFieldValue(String orderFieldValue) {
			this.orderFieldValue = orderFieldValue;
		}		
	}
	
	public static class WoDocument extends Document {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("创建者姓名（简称）")
		private String creatorPersonShort = null;
		
		@FieldDescribe("创建者所属组织（简称）")
		private String creatorUnitNameShort = null;
		
		@FieldDescribe("创建者顶层组织（简称）")
		private String creatorTopUnitNameShort = null;

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}		
	}
}