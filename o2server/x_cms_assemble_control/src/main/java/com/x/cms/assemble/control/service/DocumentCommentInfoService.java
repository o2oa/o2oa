package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommentContent;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.express.tools.filter.QueryFilter;

class DocumentCommentInfoService {

	/**
	 * 根据评论信息的标识查询评论信息的信息
	 * @param emc
	 * @param id  主要是ID
	 * @return
	 * @throws Exception
	 */
	public DocumentCommentInfo get(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.documentCommentInfoFactory().get( id );
	}

	public DocumentCommentContent getContent(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.documentCommentInfoFactory().getContent(id);
	}

	/**
	 * 根据过滤条件查询符合要求的评论信息信息数量
	 * @param emc
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( EntityManagerContainer emc, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.documentCommentInfoFactory().countWithFilter( queryFilter );
	}

	/**
	 * 根据过滤条件查询符合要求的评论信息信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.documentCommentInfoFactory().listWithFilter(maxCount, orderField, orderType, queryFilter);
	}

	/**
	 * 根据条件查询符合条件的评论信息信息ID，根据上一条的sequnce查询指定数量的信息
	 * @param emc
	 * @param maxCount
	 * @param sequnce
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public List<DocumentCommentInfo> listWithFilter( EntityManagerContainer emc, Integer maxCount, String sequnce, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.documentCommentInfoFactory().listWithFilter(maxCount, sequnce, orderField, orderType, queryFilter);
	}

	/**
	 * 向数据库持久化评论信息信息
	 * @param emc
	 * @param object
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public DocumentCommentInfo save( EntityManagerContainer emc, DocumentCommentInfo object, String content ) throws Exception {
		Document document = null;
		DocumentCommentInfo documentCommentInfo = null;
		DocumentCommentContent documentCommentContent = null;
		if( StringUtils.isEmpty( object.getId() )  ){
			object.setId( DocumentCommentInfo.createId() );
		}

		Business business = new Business(emc);
		documentCommentInfo = emc.find( object.getId(), DocumentCommentInfo.class );
		documentCommentContent = emc.find( object.getId(), DocumentCommentContent.class );
		document = emc.find( object.getId(), Document.class );

		emc.beginTransaction( DocumentCommentContent.class );
		emc.beginTransaction( DocumentCommentInfo.class );

		Integer maxOrder = 0;

		if( documentCommentInfo == null ){ // 保存一个新的对象
			maxOrder = business.documentCommentInfoFactory().getMaxOrder( object.getDocumentId() );
			if( maxOrder == null ) {
				maxOrder = 0;
			}
			documentCommentInfo = new DocumentCommentInfo();
			object.copyTo( documentCommentInfo );
			if( StringUtils.isNotEmpty( object.getId() ) ){
				documentCommentInfo.setId( object.getId() );
			}
			documentCommentInfo.setOrderNumber( maxOrder + 1 );
			emc.persist( documentCommentInfo, CheckPersistType.all);
			//document 评论数加1
			if( document != null ) {
				emc.beginTransaction( Document.class );
				document.addCommentCount(1);
				emc.check( document, CheckPersistType.all );
			}
		}else{ //对象已经存在，更新对象信息
			Integer orderNumber = documentCommentInfo.getOrderNumber();
			if(StringUtils.isNotEmpty( documentCommentInfo.getCreatorName() )) {
				object.setCreatorName(documentCommentInfo.getCreatorName());
			}
			object.copyTo( documentCommentInfo, JpaObject.FieldsUnmodify  );
			object.setOrderNumber(orderNumber);
			emc.check( documentCommentInfo, CheckPersistType.all );
		}

		if( documentCommentContent == null ){ // 保存一个新的评论内容对象
			documentCommentContent = new DocumentCommentContent();
			documentCommentContent.setId( documentCommentInfo.getId() );
			documentCommentContent.setContent( content );
			emc.persist( documentCommentContent, CheckPersistType.all);
		}else{ //对象已经存在，更新评论内容对象信息
			documentCommentContent.setContent( content );
			emc.check( documentCommentContent, CheckPersistType.all );
		}

		emc.commit();
		return documentCommentInfo;
	}

	/**
	 * 根据评论信息标识删除评论信息信息
	 * @param emc
	 * @param id
	 * @throws Exception
	 */
	public void delete(EntityManagerContainer emc, String id ) throws Exception {
		DocumentCommentInfo documentCommentInfo = emc.find( id, DocumentCommentInfo.class );
		DocumentCommentContent documentCommentConent = emc.find( id, DocumentCommentContent.class );
		if( documentCommentInfo != null ) {
			Document document = emc.find( documentCommentInfo.getDocumentId(), Document.class );
			emc.beginTransaction( DocumentCommentInfo.class );
			if( documentCommentInfo != null ) {
				emc.remove( documentCommentInfo , CheckRemoveType.all );
				if( document != null ) {
					emc.beginTransaction( Document.class );
					document.subCommentCount( 1 );
					emc.check( document, CheckPersistType.all );
				}
			}
			if( documentCommentConent != null ) {
				emc.remove( documentCommentConent , CheckRemoveType.all );
			}
			Business business = new Business( emc );
			List<String> commendIdList = business.documentCommendFactory().listByComment(documentCommentInfo.getId(), null);
			emc.delete(DocumentCommend.class, commendIdList);
			emc.commit();
		}
	}


}
