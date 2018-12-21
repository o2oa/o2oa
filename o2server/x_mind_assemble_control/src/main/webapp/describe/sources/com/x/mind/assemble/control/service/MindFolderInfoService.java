package com.x.mind.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindFolderInfo;

/**
 * 脑图文件夹信息服务类
 * @author O2LEE
 *
 */
public class MindFolderInfoService{
	private ServiceMindFolderInfoQuery serviceMindFolderInfoQuery = new ServiceMindFolderInfoQuery();
	private ServiceMindFolderInfoPersist serviceMindFolderInfoPersist = new ServiceMindFolderInfoPersist();

	/**
	 * 根据用户个人名称查询用户所有的个人文件夹信息ID列表
	 * @param person
	 * @return
	 * @throws Exception 
	 */
	public List<String> listAllIdsWithPerson(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindFolderInfoQuery.listAllIdsWithPerson( emc, person );
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 根据指定的ID列表查询脑图目录信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindFolderInfo> listWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return serviceMindFolderInfoQuery.listWithIds( emc, ids );
		      } catch (Exception e) {
		            throw e;
		      }
	}

	/**
	 * 保存一个脑图目录
	 * @param mindFolderInfo
	 * @return
	 * @throws Exception 
	 */
	public MindFolderInfo save( MindFolderInfo _mindFolderInfo ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return serviceMindFolderInfoPersist.save( emc, _mindFolderInfo );
		      } catch (Exception e) {
		            throw e;
		      }
	}

	/**
	 * 根据ID获取脑图文件夹信息
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public MindFolderInfo getWithId(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindFolderInfoQuery.get(emc, folderId);
		      } catch (Exception e) {
		            throw e;
		      }
	}

	/**
	 * 根据ID删除脑图文件夹信息
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Boolean delete(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindFolderInfoPersist.delete(emc, folderId);
		      } catch (Exception e) {
		            throw e;
		      }
	}	
	
	/**
	 * 根据ID删除脑图文件夹信息，强制删除，包括下级文件夹和所有脑图文件
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteForce(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> mindIds = null;
			List<String> folderIds = serviceMindFolderInfoQuery.listAllChildrenIds( emc, folderId,  null );
			MindBaseInfo mindBaseInfo = null;
			if( folderIds == null ) {
				folderIds = new ArrayList<>();
			}
			folderIds.add(folderId);
			emc.beginTransaction( MindFolderInfo.class );
			emc.beginTransaction( MindBaseInfo.class );
			for( String _folderId : folderIds ) {
				//查询目录下的脑图信息
				mindIds = business.mindBaseInfoFactory().list(_folderId, null, null, null, null);
				if( ListTools.isNotEmpty( mindIds )) {
					for( String _mindId : mindIds ) {
						mindBaseInfo = emc.find( _mindId, MindBaseInfo.class);
						if( mindBaseInfo != null ) {
							emc.remove(mindBaseInfo,  CheckRemoveType.all );
						}
					}
				}
			}
			emc.commit();
			return true;
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 根据ID获取文件夹的下级目录个数
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Long countChildWithFolder(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindFolderInfoQuery.countChildWithFolder(emc, folderId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	public Boolean moveToFolder( List<String> targetMindIds, List<String> targetfolderIds, String folderId) throws Exception {
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( MindBaseInfo.class );
			emc.beginTransaction( MindFolderInfo.class );
			
			if( ListTools.isNotEmpty( targetMindIds )) {
				MindBaseInfo mind = null;
				for( String id : targetMindIds ) {
					 mind = emc.find( id, MindBaseInfo.class );
					 if( mind != null ) {
							mind.setFolderId(folderId);
							emc.check( mind, CheckPersistType.all );
					 }
				}
			}
			
			if( ListTools.isNotEmpty( targetfolderIds )) {
				MindFolderInfo folder = null;
				for( String id : targetfolderIds ) {
					folder = emc.find( id, MindFolderInfo.class );
					 if( folder != null ) {
						 folder.setParentId( folderId );
						 emc.check( folder, CheckPersistType.all );
					 }
				}
			}
			emc.commit();
			return true;
		} catch (Exception e) {
		    throw e;
		}
	}
}
