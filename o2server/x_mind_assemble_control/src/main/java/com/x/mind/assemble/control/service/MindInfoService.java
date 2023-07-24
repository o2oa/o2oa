package com.x.mind.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.common.date.DateOperation;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindContentInfo;
import com.x.mind.entity.MindIconInfo;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;

/**
 * 脑图信息服务类
 * @author O2LEE
 *
 */
public class MindInfoService{
	private ServiceMindInfoQuery serviceMindInfoQuery = new ServiceMindInfoQuery();
	private ServiceMindInfoPersist serviceMindInfoPersist = new ServiceMindInfoPersist();
	private ServiceMindVersionInfoQuery serviceMindVersionInfoQuery = new ServiceMindVersionInfoQuery();
	private ServiceMindVersionInfoPersist serviceMindVersionInfoPersist = new ServiceMindVersionInfoPersist();
	private ServiceMindShareRecordQuery serviceMindShareRecordQuery = new ServiceMindShareRecordQuery();
	private ServiceMindShareRecordPersist serviceMindShareRecordPersist = new ServiceMindShareRecordPersist();
	private DateOperation dssateOperation = new DateOperation();
	/**
	 * 根据指定的目录ID查询目录下所有的脑图信息ID列表
	 * @param person
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsWithFolder(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.listIdsWithFolder( emc, folderId );
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 根据指定的ID查询脑图信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindBaseInfo getMindBaseInfo(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.getMindBaseInfo(emc, id);
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 根据脑图ID获取脑图缩略图Base64编码信息
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public MindIconInfo getMindIconInfo(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( mindId, MindIconInfo.class );
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 根据ID获取回收站脑图信息
	 * @param recycleId
	 * @return
	 * @throws Exception 
	 */
	public MindRecycleInfo getMindRecycleInfo(String recycleId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( recycleId, MindRecycleInfo.class );
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 根据指定的ID列表查询脑图信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindBaseInfo> listMindBaseInfoWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.listWithIds( emc, ids );
	      } catch (Exception e) {
	            throw e;
	      }
	}

	/**
	 * 保存一个脑图信息
	 * @param _mindBaseInfo
	 * @param content
	 * @return
	 * @throws Exception 
	 */
	public MindBaseInfo save( MindBaseInfo _mindBaseInfo, String content, int maxVersionCount ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			
			//保存脑图信息
			MindBaseInfo mindBaseInfo = serviceMindInfoPersist.save( emc, _mindBaseInfo,  content);
			
			String mindId = mindBaseInfo.getId();
			//在脑图进行保存的时候，需要根据已经存在的版本情况来确定是否需要新添加一个新的版本
			MindVersionInfo mindVersionInfo = null;
			//查询最新的个版本的情况
			mindVersionInfo = serviceMindVersionInfoQuery.getLatestVersionWithMind( emc, mindId );
			if( mindVersionInfo != null ) {
				//如果存在版本，那么对比一下版本信息
				//10分钟之类，对比数据大小改变程度超过20%的改变，则创建新版本，否则，不用创建新版本，
				long diff_min = dssateOperation.getDiff( mindVersionInfo.getUpdateTime(), mindBaseInfo.getUpdateTime() );
				if( diff_min < 10 ) {
					MindContentInfo mindContentInfo = serviceMindInfoQuery.getMindContentInfo(emc,  mindId );
					MindVersionContent mindVersionContent = serviceMindVersionInfoQuery.getMindVersionContentInfo(emc, mindVersionInfo.getId() );
					//对比数据量的大小，如果超过20%的变化，那么新创建一个新版本
					if( dataDiff( mindContentInfo.getContent(), mindVersionContent.getContent() ) > 0.2 ) {
						//超过10分钟创建一个新版本
						serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
					}
				}else {
					//超过10分钟创建一个新版本
					serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
				}
			}else {
				serviceMindVersionInfoPersist.createFromMind( emc, _mindBaseInfo, content, maxVersionCount );
			}
			return mindBaseInfo;
	      } catch (Exception e) {
	            throw e;
	      }
	}

	private double dataDiff(String new_content, String old_content ) {
		int new_length = 0;
		int old_length = 0;
		if( new_content != null ) {
			new_length = new_content.getBytes().length;
		}
		if( old_content != null ) {
			old_length = old_content.getBytes().length;
		}
		if( old_length == new_length ) {
			return 0;
		}else if( old_length > new_length ) {
			return ((double)old_length - (double)new_length)/(double)old_length;
		}else {
			return ((double)new_length - (double)old_length)/(double)new_length;
		}
	}

	/**
	 * 查询脑图， 下一页
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
	public List<MindBaseInfo> listNextPageWithFilter(String id, Integer count, String key, String folderId, Boolean shared,
			String creator, String creatorUnit, List<String> sharePersons, List<String> shareUnits, List<String> shareGroups, 
			String orderField, String orderType, List<String> inMindIds ) throws Exception {
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return serviceMindInfoQuery.listNextPageWithFilter( emc, id, count, key, folderId, shared,
					creator, creatorUnit, sharePersons, shareUnits, shareGroups, orderField, orderType, inMindIds );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询回收站脑图， 下一页
	 * @param id
	 * @param count
	 * @param name
	 * @param folderId
	 * @param shared
	 * @param creator
	 * @param creatorUnit
	 * @param orderField
	 * @param orderType
	 * @return
	 * @throws Exception 
	 */
	public List<MindRecycleInfo> listRecycleNextPageWithFilter(String id, Integer count, String key, String folderId, Boolean shared,
			String creator, String creatorUnit, String orderField, String orderType, List<String> inMindIds ) throws Exception {
		if( orderField == null || orderField.isEmpty() ){
			orderField =  JpaObject.sequence_FIELDNAME;
		}
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return serviceMindInfoQuery.listRecycleNextPageWithFilter( emc, id, count, key, folderId, shared,
					creator, creatorUnit, orderField, orderType, inMindIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据ID获取脑图的内容
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public String getContent(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				return serviceMindInfoQuery.getMindContent(emc, id);
		      } catch (Exception e) {
		            throw e;
		      }
	}
	
	/**
	 * 查询某个目录下的脑图数量
	 * @param folderId
	 * @return
	 * @throws Exception 
	 */
	public Long countMindWithFolder(String folderId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoQuery.countMindWithFolder(emc, folderId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 根据脑图ID删除脑图信息
	 * 基础信息、详细内容以及分享的信息
	 * @param mindId
	 * @return 
	 * @throws Exception 
	 */
	public Boolean destroyMind(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.destroyMind(emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 根据脑图ID将脑图信息放入回收站
	 * 基础信息、以及分享的信息
	 * @param mindId
	 * @return 
	 * @return 
	 * @throws Exception 
	 */
	public Boolean recycle(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.recycle(emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 根据ID从回收站还原脑图信息
	 * 基础信息、以及分享的信息
	 * @param recycleId
	 * @return 
	 * @throws Exception 
	 */
	public Boolean restore(String recycleId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindInfoPersist.restore(emc, recycleId);
		      } catch (Exception e) {
		    throw e;
		      }
	}	
	
	/**
	 * 更新脑图的缩略图
	 * @param mindId
	 * @param base64
	 * @throws Exception 
	 */
	public void updateIcon(String mindId, String base64) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindIconInfo mindIconInfo = emc.find( mindId, MindIconInfo.class );
			emc.beginTransaction( MindIconInfo.class );
			if( mindIconInfo != null ) {
				mindIconInfo.setContent(base64);
				emc.check( mindIconInfo, CheckPersistType.all );
			}else {
				mindIconInfo = new MindIconInfo();
				mindIconInfo.setId(mindId);
				mindIconInfo.setContent(base64);
				emc.persist( mindIconInfo, CheckPersistType.all );
			}
			emc.commit();
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 根据脑图ID获取脑图缩略图Base64编码内容
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public String getMindIconBase64(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindIconInfo mindIconInfo = emc.find( mindId, MindIconInfo.class );
			if( mindIconInfo != null ) {
				return mindIconInfo.getContent();
			}
			return null;
		      } catch (Exception e) {
		    throw e;
		      }
	}
	
	/**
	 * 根据脑图ID获取该脑图所有的版本，只保留10个
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.listVersionsWithMindId( emc, mindId);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 根据ID列表获取脑图版本信息列表
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<MindVersionInfo> listVersionWithIds(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.listWithIds(emc, ids);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 根据指定ID获取脑图版本信息对象
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public MindVersionInfo getMindVersionInfo(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.getMindVersionInfo(emc, id);
		      } catch (Exception e) {
		    throw e;
		      }
	}

	/**
	 * 根据指定的ID，获取指定脑图版本的详细内容
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public String getMindVersionContent(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindVersionInfoQuery.getMindVersionContent(emc, id);
		} catch (Exception e) {
		    throw e;
		}
	}

	/**
	 * 分享脑图信息到指定的人员组织
	 * @param mindBaseInfo 
	 * @param mindShareRecords
	 * @throws Exception 
	 */
	public void share(MindBaseInfo mindBaseInfo, List<MindShareRecord> mindShareRecords ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			serviceMindShareRecordPersist.share( emc, mindBaseInfo, mindShareRecords );
		} catch (Exception e) {
		    throw e;
		}
	}
	
	/**
	 * 根据脑图ID，获取脑图分享记录
	 * @param mindId 
	 * @throws Exception 
	 */
	public List<String> listShareRecordWithMindId(String mindId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listIdsWithMindId( emc, mindId );
		} catch (Exception e) {
		    throw e;
		}
	}

	/**
	 * 取消分享
	 * @param shareRecordId
	 * @throws Exception
	 */
	public void shareCancel(String shareRecordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			serviceMindShareRecordPersist.shareCancel(emc, shareRecordId);
		} catch (Exception e) {
		    throw e;
		}
	}

	public MindShareRecord getMindShareRecord(String shareRecordId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find( shareRecordId, MindShareRecord.class );
		} catch (Exception e) {
		    throw e;
		}
	}

	public List<MindShareRecord> listSharedRecords( List<String> ids ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedRecords(emc, ids );
		} catch (Exception e) {
		    throw e;
		}
	}
	
	public List<String> listSharedRecordIds(String source, List<String> targetList, List<String> inMindIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedRecordIds( emc, source, targetList, inMindIds );
		} catch (Exception e) {
		    throw e;
		}
	}

	public List<String> listSharedMindIdsFromRecord(String source, List<String> targetList, List<String> inMindIds) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return serviceMindShareRecordQuery.listSharedMindIdsFromRecord( emc, source, targetList, inMindIds );
		} catch (Exception e) {
		    throw e;
		}
	}

	public Boolean moveToFolder( List<String> targetMindIds, String folderId ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			MindBaseInfo mind = null;
			if( ListTools.isNotEmpty( targetMindIds )) {
				emc.beginTransaction( MindBaseInfo.class );
				for( String id : targetMindIds ) {
					 mind = emc.find( id, MindBaseInfo.class );
					 if( mind != null ) {
							mind.setFolderId(folderId);
							emc.check( mind, CheckPersistType.all );
					 }
				}
				emc.commit();
			}
			return true;
		} catch (Exception e) {
		    throw e;
		}
	}
}
