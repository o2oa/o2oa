package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentCategoryIdEmptyException;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.core.entity.Document;

import net.sf.ehcache.Element;

public class ExcuteListDraftNextWithFilter extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListDraftNextWithFilter.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutDocument>> execute( HttpServletRequest request, String id, Integer count, WrapInFilter wrapIn, EffectivePerson effectivePerson ) {
		ActionResult<List<WrapOutDocument>> result = new ActionResult<>();
		List<WrapOutDocument> wraps = null;
		List<Document> documentList = null;
		Boolean check = true;
		
		String cacheKey = getCacheKeyFormWrapInFilter( "draft", id, count, wrapIn );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutDocument> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if(  wrapIn.getCategoryIdList() == null ||  wrapIn.getCategoryIdList().isEmpty() ){
					check = false;
					Exception exception = new DocumentCategoryIdEmptyException();
					result.error( exception );
				}
			}
			
			if( check ){
				try {
					documentList = documentServiceAdv.listMyDraft( effectivePerson.getName(), wrapIn.getCategoryIdList() );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在查询用户草稿信息列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( documentList != null ){
					try {
						wraps = WrapTools.document_wrapout_copier.copy( documentList );
						result.setCount( Long.parseLong( documentList.size() + "" ) );
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);
					} catch (Exception e) {
						Exception exception = new DocumentInfoProcessException( e, "系统在将分页查询结果转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}	
		}
			
		return result;
	}
	
	private String getCacheKeyFormWrapInFilter( String flag, String id, Integer count, WrapInFilter wrapIn ) {
		
		String cacheKey = ApplicationCache.concreteCacheKey( id, count, flag );
		
		if( wrapIn.getTitle() != null && !wrapIn.getTitle().isEmpty() ){
			cacheKey = ApplicationCache.concreteCacheKey( cacheKey, wrapIn.getTitle() );
		}
		if( wrapIn.getOrderType() != null && !wrapIn.getOrderType().isEmpty() ){
			cacheKey = ApplicationCache.concreteCacheKey( cacheKey, wrapIn.getOrderType() );
		}
		if( wrapIn.getAppIdList() != null && !wrapIn.getAppIdList().isEmpty() ){
			for( String key : wrapIn.getAppIdList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getCategoryIdList() != null && !wrapIn.getCategoryIdList().isEmpty() ){
			for( String key : wrapIn.getCategoryIdList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getCreateDateList() != null && !wrapIn.getCreateDateList().isEmpty() ){
			for( String key : wrapIn.getCreateDateList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getPublishDateList() != null && !wrapIn.getPublishDateList().isEmpty() ){
			for( String key : wrapIn.getPublishDateList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getPublisherList() != null && !wrapIn.getPublisherList().isEmpty() ){
			for( String key : wrapIn.getPublisherList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getStatusList() != null && !wrapIn.getStatusList().isEmpty() ){
			for( String key : wrapIn.getStatusList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}		
		return cacheKey;
	}
}