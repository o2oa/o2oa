package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindFolderInfo;

/**
 * 脑图文件夹信息查询操作服务类：查询
 * @author O2LEE
 *
 */
class ServiceMindFolderInfoQuery{

	/**
	 * 根据用户个人名称查询用户所有的个人文件夹信息ID列表
	 * @param emc
	 * @param person
	 * @return
	 * @throws Exception 
	 */
	List<String> listAllIdsWithPerson(EntityManagerContainer emc, String person) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(null, null, null, person, null);
	}

	/**
	 * 根据指定的ID列表查询脑图目录信息列表
	 * @param emc
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	List<MindFolderInfo> listWithIds(EntityManagerContainer emc, List<String> ids) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(ids);
	}

	/**
	 * 根据ID获取脑图文件夹信息对象
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindFolderInfo get(EntityManagerContainer emc,String id) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().get(id);
	}

	/**
	 * 根据ID获取文件夹的下级目录个数
	 * @param emc
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public Long countChildWithFolder(EntityManagerContainer emc, String folderId) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().countChildWithFolder(folderId);
	}

	/**
	 * 根据文件夹ID，获取所有的下级文件夹的ID，递归查询
	 * @param emc
	 * @param folderId
	 * @param allFolderIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllChildrenIds(EntityManagerContainer emc,String folderId, List<String> allFolderIds) throws Exception {
		if( allFolderIds == null) {
			allFolderIds = new ArrayList<>();
		}
		List<String> childrenIds = null;
		childrenIds = listChildrenIds(emc, folderId);
		if( ListTools.isNotEmpty( childrenIds )) {
			for( String id : childrenIds ) {
				if( !allFolderIds.contains( id )) {
					allFolderIds.add( id );
					allFolderIds = listAllChildrenIds( emc, id, allFolderIds);
				}
			}
		}
		return allFolderIds;
	}

	/**
	 * 根据上级文件夹ID获取下级文件夹ID列表
	 * @param emc
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<String> listChildrenIds(EntityManagerContainer emc, String parentId) throws Exception {
		Business business = new Business( emc );
		return business.mindFolderInfoFactory().list(parentId, null, null, null, null);
	}
}