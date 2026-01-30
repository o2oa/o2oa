package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.ChatContent;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

/**
 * 对工作交流信息查询的服务
 * 
 * @author O2LEE
 */
class ChatService {

	/**
	 * 根据工作交流的标识查询工作交流的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected Chat get(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.chatFactory().get( flag );
	}
	
	/**
	 * 根据工作交流的标识查询工作交流的信息
	 * @param emc
	 * @param flag  主要是ID
	 * @return
	 * @throws Exception 
	 */
	protected ChatContent getContent(EntityManagerContainer emc, String flag) throws Exception {
		Business business = new Business( emc );
		return business.chatFactory().getContent( flag );
	}

	/**
	 * 根据过滤条件查询符合要求的工作交流信息数量
	 * @param emc
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected Long countWithFilter( EntityManagerContainer emc, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.chatFactory().countWithFilter(queryFilter);
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作交流信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	protected List<Chat> listWithFilter( EntityManagerContainer emc, Integer maxCount, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.chatFactory().listWithFilter(maxCount, orderField, orderType, queryFilter);
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作交流信息列表
	 * @param emc
	 * @param maxCount
	 * @param orderField
	 * @param orderType
	 * @param projectIds
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	protected List<Chat> listWithFilterNext( EntityManagerContainer emc, Integer maxCount, String sequenceFieldValue, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		Business business = new Business( emc );
		return business.chatFactory().listWithFilter(maxCount, sequenceFieldValue, orderField, orderType, queryFilter);
	}

	/**
	 * 向数据库持久化工作交流信息
	 * @param emc
	 * @param chat
	 * @return
	 * @throws Exception 
	 */
	protected Chat create( EntityManagerContainer emc, Chat object, String lobContent ) throws Exception {
		ChatContent chatContent_entity = null;
		Chat chat_entity = null;
		if( StringUtils.isEmpty( object.getId() )) {
			object.setId( Chat.createId() );
		}else {
			 chat_entity = emc.find( object.getId(), Chat.class );
			 chatContent_entity = emc.find( object.getId(), ChatContent.class );
		}
		
		emc.beginTransaction( Chat.class );
		emc.beginTransaction( ChatContent.class );
		if( chat_entity != null ) {
			object.copyTo( chat_entity );
			emc.check( chat_entity, CheckPersistType.all );
		}else {
			emc.persist( object, CheckPersistType.all );
		}
		
		if( chatContent_entity != null ) {
			chatContent_entity.setContent( lobContent );
			emc.check( chat_entity, CheckPersistType.all );
		}else {
			chatContent_entity = new ChatContent();
			chatContent_entity.setId( object.getId() );
			chatContent_entity.setContent( lobContent );
			emc.persist( chatContent_entity, CheckPersistType.all );
		}
		emc.commit();
		return object;
	}

	/**
	 * 根据工作交流标识删除工作交流信息
	 * @param emc
	 * @param id
	 * @throws Exception 
	 */
	protected void delete(EntityManagerContainer emc, String id) throws Exception {
		Chat chat = emc.find( id, Chat.class );
		ChatContent chatContent = emc.find( id, ChatContent.class );
		emc.beginTransaction( Chat.class );
		emc.beginTransaction( ChatContent.class );
		if( chat != null ) {
			emc.remove( chat , CheckRemoveType.all );
		}
		if( chatContent != null ) {
			emc.remove( chatContent , CheckRemoveType.all );
		}
		emc.commit();
	}
}
