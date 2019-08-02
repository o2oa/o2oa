package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Chat;
import com.x.teamwork.core.entity.ChatContent;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;


/**
 * 对工作交流信息查询的服务
 * 
 * @author O2LEE
 */
public class ChatQueryService {

	private ChatService chatService = new ChatService();
	
	/**
	 * 根据工作交流的标识查询工作交流信息
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public Chat get( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return chatService.get(emc, flag );
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getContent(String id) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ChatContent chatContent = chatService.getContent(emc, id );
			return chatContent == null ? "":chatContent.getContent();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据ID列表查询工作交流信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Chat> list(List<String> ids) throws Exception {
		if (ListTools.isEmpty( ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( Chat.class,  ids );
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作交流信息数量
	 * @param queryFilter
	 * @return
	 * @throws Exception
	 */
	public Long countWithFilter( QueryFilter queryFilter ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return chatService.countWithFilter(emc, queryFilter);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据过滤条件查询符合要求的工作交流信息列表
	 * @param currentPerson
	 * @param pageSize
	 * @param pageNum
	 * @param orderField
	 * @param orderType
	 * @param group
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public List<Chat> listWithFilter( Integer pageSize, Integer pageNum, String orderField, String orderType, QueryFilter queryFilter ) throws Exception {
		List<Chat> chatList = null;
		List<Chat> result = new ArrayList<>();
		Integer maxCount = 20;
		Integer startNumber = 0;
		if( pageNum == 0 ) { pageNum = 1; }
		if( pageSize == 0 ) { pageSize = 20; }
		maxCount = pageSize * pageNum;
		startNumber = pageSize * ( pageNum -1 );
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			chatList = chatService.listWithFilter(emc, maxCount, orderField, orderType, queryFilter );			
			if( ListTools.isNotEmpty( chatList )) {
				for( int i = 0; i<chatList.size(); i++ ) {
					if( i >= startNumber ) {
						result.add( chatList.get( i ));
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据过滤条件查询符合要求的工作交流信息列表
	 * @param currentPerson
	 * @param pageSize
	 * @param pageNum
	 * @param orderField
	 * @param orderType
	 * @param group
	 * @param title
	 * @return
	 * @throws Exception
	 */
	public List<Chat> listWithFilterNext( Integer pageSize, String lastId, String orderField, String orderType, QueryFilter queryFilter  ) throws Exception {
		List<Chat> chatList = null;
		Integer maxCount = 20;

		if( pageSize == 0 ) { pageSize = 20; }
		
		if( StringUtils.isEmpty( orderField ) ) { 
			orderField = "createTime";
		}
		if( StringUtils.isEmpty( orderType ) ) { 
			orderType = "desc";
		}
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Chat chat = chatService.get(emc, lastId );	
			if( chat != null ) {
				chatList = chatService.listWithFilterNext(emc, maxCount, chat.getId(), orderField, orderType, queryFilter );
			}else {
				chatList = chatService.listWithFilterNext(emc, maxCount, null, orderField, orderType, queryFilter );
			}
		} catch (Exception e) {
			throw e;
		}
		return chatList;
	}
}
