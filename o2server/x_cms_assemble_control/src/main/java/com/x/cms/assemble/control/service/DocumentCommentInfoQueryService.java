package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.DocumentCommentContent;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.tools.filter.QueryFilter;

/**
 * 对评论信息查询的服务
 * 
 * @author O2LEE
 */
public class DocumentCommentInfoQueryService {

	private DocumentCommentInfoService documentCommentInfoService = new DocumentCommentInfoService();
	
	/**
	 * 根据评论的标识查询评论信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DocumentCommentInfo get( String id ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return documentCommentInfoService.get(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}

	public String getCommentContent(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			DocumentCommentContent documentCommentContent = documentCommentInfoService.getContent( emc, id );
			if( documentCommentContent !=  null ) {
				return documentCommentContent.getContent();
			}else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据ID列表查询评论信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( DocumentCommentInfo.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据过滤条件查询符合要求的评论信息数量
	 * @param effectivePerson
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( EffectivePerson effectivePerson, QueryFilter queryFilter ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return documentCommentInfoService.countWithFilter( emc, queryFilter );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据过滤条件查询符合要求的评论信息列表
	 * @param effectivePerson
	 * @param pageSize
	 * @param pageNum
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, Integer pageNum, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<DocumentCommentInfo> documentCommentInfoList = null;
		List<DocumentCommentInfo> result = new ArrayList<>();
		Integer maxCount = 20;
		Integer startNumber = 0;		
		
		if( pageNum == 0 ) { pageNum = 1; }
		if( pageSize == 0 ) { pageSize = 20; }
		maxCount = pageSize * pageNum;
		startNumber = pageSize * ( pageNum -1 );
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "orderNumber";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "asc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			documentCommentInfoList = documentCommentInfoService.listWithFilter(emc, maxCount, orderField, orderType, queryFilter );
			
			if( ListTools.isNotEmpty( documentCommentInfoList )) {
				for( int i = 0; i<documentCommentInfoList.size(); i++ ) {
					if( i >= startNumber ) {
						result.add( documentCommentInfoList.get( i ));
					}
				}
			}			
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据条件查询符合条件的评论信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param effectivePerson
	 * @param pageSize
	 * @param lastId
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( EffectivePerson effectivePerson, Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<DocumentCommentInfo> documentCommentInfoList = null;
		Integer maxCount = 20;
		DocumentCommentInfo documentCommentInfo = null;
		
		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "orderNumber";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "asc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( lastId != null ) {
				documentCommentInfo = emc.find( lastId, DocumentCommentInfo.class );
			}
			if( documentCommentInfo != null ) {
				documentCommentInfoList = documentCommentInfoService.listWithFilter(emc, maxCount, documentCommentInfo.getSequence(), orderField, orderType, queryFilter );
			}else {
				documentCommentInfoList = documentCommentInfoService.listWithFilter(emc, maxCount, null, orderField, orderType, queryFilter );
			}	
		} catch (Exception e) {
			throw e;
		}
		return documentCommentInfoList;
	}

	
}
