package com.x.mind.assemble.control.service;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindRecycleInfo;

/**
 * 脑图信息查询操作服务类：查询
 * @author O2LEE
 *
 */
class ServiceMindInfoQuery{

	/**
	 * 根据指定的ID列表获取脑图基本信息列表
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<MindBaseInfo> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindBaseInfoFactory().list(ids);
	}
	
	/**
	 * 根据ID获取指定的脑图基础信息对象
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindBaseInfo getMindBaseInfo(EntityManagerContainer emc, String id) throws Exception {
		Business business = new Business( emc );
		return business.mindBaseInfoFactory().get(id);
	}
	
	/**
	 * 根据ID获取指定的脑图详细内容信息对象
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	MindContentInfo getMindContentInfo(EntityManagerContainer emc, String id) throws Exception {
		return emc.find(id, MindContentInfo.class );
	}
	
	/**
	 * 根据ID获取指定脑图的详细内容
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	String getMindContent(EntityManagerContainer emc, String id) throws Exception {
		MindContentInfo mindContentInfo = emc.find(id, MindContentInfo.class );
		if( mindContentInfo != null ) {
			return mindContentInfo.getContent();
		}
		return null;
	}

	/**
	 * 根据目录ID获取目录下的所有脑图信息ID列表
	 * @param emc
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsWithFolder(EntityManagerContainer emc, String folderId) throws Exception {
		Business business = new Business( emc );
		return business.mindBaseInfoFactory().list(folderId, null, null, null, null);
	}

	/**
	 * 查询脑图， 下一页
	 * @param emc
	 * @param id
	 * @param count
	 * @param name
	 * @param folderId
	 * @param shared
	 * @param creator
	 * @param creatorUnit
	 * @param sharePersons
	 * @param shareUnits
	 * @param shareGroups
	 * @param orderField
	 * @param orderType
	 * @param inMindIds
	 * @return
	 * @throws Exception
	 */
	public List<MindBaseInfo> listNextPageWithFilter(EntityManagerContainer emc, String id, Integer count, String key,
			String folderId, Boolean shared, String creator, String creatorUnit, List<String> sharePersons, List<String> shareUnits, List<String> shareGroups, 
			String orderField, String orderType, List<String> inMindIds ) throws Exception {
		Business business = new Business(emc);
		MindBaseInfo entity = null;
		Object sequenceFieldValue = null;
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		//查询出ID对应的记录相应的排序列数据值
		if( StringUtils.isNotEmpty(id) && !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ){
			entity = emc.find( id, MindBaseInfo.class );
			if( entity != null ){
				if( "folderId".equals( orderField  )){//文件夹
					sequenceFieldValue = PropertyUtils.getProperty( entity, "folder_sequence" );
				}else if( "createTime".equals( orderField  )){//创建时间
					sequenceFieldValue = PropertyUtils.getProperty( entity, "createTime" );
				}else if( "updateTime".equals( orderField  )){//创建时间
					sequenceFieldValue = PropertyUtils.getProperty( entity, "updateTime" );
				}else if( "shared".equals( orderField  )){//是否已分享
					sequenceFieldValue = PropertyUtils.getProperty( entity, "shared_sequence" );
				}else if( "creator".equals( orderField  )){//创建者
					sequenceFieldValue = PropertyUtils.getProperty( entity, "creator_sequence" );
				}else if( "creatorUnit".equals( orderField  )){//创建者所属组织
					sequenceFieldValue = PropertyUtils.getProperty( entity, "creatorUnit_sequence" );
				}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
					sequenceFieldValue = PropertyUtils.getProperty( entity,  JpaObject.sequence_FIELDNAME );
				}
			}
		}
		return business.mindBaseInfoFactory().listNextPageWithFilter( count, key, folderId, shared, creator, creatorUnit, 
				sequenceFieldValue, sharePersons, shareUnits, shareGroups, orderField, orderType, inMindIds);
	}
	
	public List<MindRecycleInfo> listRecycleNextPageWithFilter(EntityManagerContainer emc, String id, Integer count, String key,
			String folderId, Boolean shared, String creator, String creatorUnit, String orderField, String orderType, List<String> inMindIds ) throws Exception {
		Business business = new Business(emc);
		MindRecycleInfo entity = null;
		Object sequenceFieldValue = null;
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		//查询出ID对应的记录相应的排序列数据值
		if( StringUtils.isNotEmpty(id) && !StringUtils.equalsIgnoreCase( id, StandardJaxrsAction.EMPTY_SYMBOL ) ){
			entity = emc.find( id, MindRecycleInfo.class );
			if( entity != null ){
				if( "folderId".equals( orderField  )){//文件夹
					sequenceFieldValue = PropertyUtils.getProperty( entity, "folder_sequence" );
				}else if( "createTime".equals( orderField  )){//创建时间
					sequenceFieldValue = PropertyUtils.getProperty( entity, "createTime" );
				}else if( "shared".equals( orderField  )){//是否已分享
					sequenceFieldValue = PropertyUtils.getProperty( entity, "shared_sequence" );
				}else if( "creator".equals( orderField  )){//创建者
					sequenceFieldValue = PropertyUtils.getProperty( entity, "creator_sequence" );
				}else if( "creatorUnit".equals( orderField  )){//创建者所属组织
					sequenceFieldValue = PropertyUtils.getProperty( entity, "creatorUnit_sequence" );
				}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
					sequenceFieldValue = PropertyUtils.getProperty( entity,  JpaObject.sequence_FIELDNAME );
				}
			}
		}
		return business.mindRecycleInfoFactory().listNextPageWithFilter( count, key, folderId, shared, creator, creatorUnit, 
				sequenceFieldValue, orderField, orderType, inMindIds);
	}

	public Long countMindWithFolder(EntityManagerContainer emc, String folderId) throws Exception {
		Business business = new Business( emc );
		return business.mindBaseInfoFactory().countMindWithFolder(folderId);
	}
}
