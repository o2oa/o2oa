package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.cms.core.entity.tools.filter.term.InTerm;
import com.x.cms.core.entity.tools.filter.term.NotInTerm;

public class ActionQueryListNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();		
		Long total = 0L;
		Wi wi = null;
		List<Wo> wos = new ArrayList<>();
		List<Document> documentList = null;
		List<Document> searchResultList = new ArrayList<>();
		List<Review> reviewList =  null;
		Boolean check = true;
		Boolean isManager = false;
		String personName = effectivePerson.getDistinguishedName();
		QueryFilter queryFilter = null;
		
		if ( count == 0 ) { count = 20; }
		if ( StringUtils.isEmpty( id ) || "(0)".equals( id ) ) { id = null; }
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if ( wi == null ) { wi = new Wi(); }
		
		if( StringUtils.isEmpty( wi.getDocumentType() )) {
			wi.setDocumentType( "信息" );
		}
		
		if( ListTools.isNotEmpty( wi.getStatusList() )) {
			List<String> status = new ArrayList<>();
			status.add( "published" );
			wi.setStatusList( status );
		}
		
		if (check) {
			try {
				queryFilter = wi.getQueryFilter();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取查询条件信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			try {
				if( effectivePerson.isManager() || userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), "CMSManager" )) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在判断用户是否是管理时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			//查询是否已读，需要使用相应的ID进行IN操作，效率有一些低
			List<String> readDocIds = null;
			if( "READ".equalsIgnoreCase( wi.getReadFlag() )) { //只查询阅读过的
				//查询出该用户所有已经阅读过的文档ID列表
				try {
					readDocIds = documentViewRecordServiceAdv.listDocIdsByPerson( personName, 2000 );
					if( ListTools.isEmpty( readDocIds )) {
						readDocIds = new ArrayList<>();
						readDocIds.add( "no Document readed" );
					}
					if( isManager ) {
						queryFilter.addInTerm( new InTerm( "id", new ArrayList<>( readDocIds ) ) );
					}else {
						queryFilter.addInTerm( new InTerm( "docId", new ArrayList<>( readDocIds ) ) );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户已经阅读过的文档ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}else if("UNREAD".equalsIgnoreCase( wi.getReadFlag() )) { //只查询未阅读过的
				//查询出该用户所有已经阅读过的文档ID列表
				try {
					readDocIds = documentViewRecordServiceAdv.listDocIdsByPerson( personName, 2000 );
					if( ListTools.isNotEmpty( readDocIds )) {
						if( isManager ) {
							queryFilter.addNotInTerm( new NotInTerm( "id", new ArrayList<>( readDocIds ) ) );
						}else {
							queryFilter.addNotInTerm( new NotInTerm( "docId", new ArrayList<>( readDocIds ) ) );
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户已经阅读过的文档ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			// 从Review表中查询符合条件的对象总数
			try {
				if( isManager ) {
					//直接从Document忽略权限查询
					total = documentQueryService.countWithConditionOutofPermission( queryFilter );
				}else {
					total = documentQueryService.countWithConditionInReview( personName, queryFilter );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取用户可查询到的文档数据条目数量时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			//document和Review除了sequence还有5个排序列支持title, appAlias, categoryAlias, categoryName, creatorUnitName的分页查询
			//除了sequence和title, appAlias, categoryAlias, categoryName, creatorUnitName之外，其他的列排序全部在内存进行分页
			try {
				if( isManager ) {
					if( Document.isFieldInSequence(wi.getOrderField()) ) {
						//直接从Document忽略权限查询
						searchResultList = documentQueryService.listNextWithConditionOutofPermission( id, count, wi.getOrderField(), wi.getOrderType(), queryFilter );
					}else {
						documentList = documentQueryService.listNextWithConditionOutofPermission( wi.getOrderField(), wi.getOrderType(), queryFilter, 2000 );
						//循环分页，查询传入的ID所在的位置，向后再查询N条
						if( ListTools.isNotEmpty( documentList )) {
							Boolean add2List = false;
							//放一页到searchResultList中进行返回
							for( Document document : documentList ) {
								if( StringUtils.isEmpty( id ) || document.getId().equalsIgnoreCase( id ) ) {
									add2List = true;
								}
								if( add2List ) {
									searchResultList.add( document );
								}
								if( searchResultList.size() >= count ) {
									break;
								}
							}
						}
					}
				}else {
					if( Document.isFieldInSequence(wi.getOrderField()) ) {
						// 从Review表中查询符合条件的对象，并且转换为Document对象列表
						searchResultList = documentQueryService.listNextWithConditionInReview( id, count, wi.getOrderField(), wi.getOrderType(), personName, queryFilter );
					}else {
						reviewList =  documentQueryService.listNextWithConditionInReview( wi.getOrderField(), wi.getOrderType(), personName, queryFilter, 2000 );
						//循环分页，查询传入的ID所在的位置，向后再查询N条，转换为Document放到searchResultList
						searchResultList = new ArrayList<>();
						if( ListTools.isNotEmpty( reviewList )) {
							Boolean add2List = false;
							//放一页到searchResultList中进行返回
							for( Review review : reviewList ) {
								if( StringUtils.isEmpty( id ) || review.getDocId().equalsIgnoreCase( id ) ) {
									add2List = true;
								}
								if( add2List ) {
									searchResultList.add(documentQueryService.get( review.getDocId() ) );
								}
								if( searchResultList.size() >= count ) {
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据用户可访问的文档ID列表对文档进行分页查询时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( searchResultList != null ) {
				Wo wo = null;
				for( Document document : searchResultList ) {					
					try {
						wo = Wo.copier.copy( document );						
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
							wo.setData( documentQueryService.getDocumentData( document ) );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e, "系统获取文档数据内容信息时发生异常。Id:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
					wos.add( wo );
				}
			}
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
	
	public static class Wo extends WrapOutDocumentList {
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Document, Wo> copier = WrapCopierFactory.wo( Document.class, Wo.class, null,JpaObject.FieldsInvisible);
		
	}
}