package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.CmsBatchOperation;
import com.x.cms.core.entity.Document;

/**
 * 对批处理操作信息持久化服务类利用Service完成事务控制
 * 
 */
public class CmsBatchOperationPersistService {
	
	CmsBatchOperationService cmsBatchOperationService = new CmsBatchOperationService();
	/**
	 * 保存批处理信息对象
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception
	 */
	public CmsBatchOperation addOperation( CmsBatchOperation cmsBatchOperation ) throws Exception {
		if( cmsBatchOperation == null ) {
			throw new Exception("cmsBatchOperation can not null for save!");
		}
		if( StringUtils.isEmpty(cmsBatchOperation.getOptType()  )) {
			throw new Exception("optType can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getObjType()  )) {
			throw new Exception("objType can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getBundle()  )) {
			throw new Exception("bundle can not empty for save CmsBatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getDescription()  )) {
			throw new Exception("description can not empty for save CmsBatchOperation!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( CmsBatchOperation.class );
			emc.persist( cmsBatchOperation, CheckPersistType.all ); 
			emc.commit();
		}
		//将批量操作信息压入队列
		ThisApplication.queueBatchOperation.send( cmsBatchOperation );
		return cmsBatchOperation;
	}
	
	/**
	 * 保存批处理信息对象
	 * 
	 * @param objType 对象类别
	 * @param optType 操作类别
	 * @param bundle 绑定的ID
	 * @param oldInfo 原来的信息
	 * @param description 操作类别
	 * @return
	 * @throws Exception
	 */
	public CmsBatchOperation addOperation( String objType, String optType, String bundle, String oldInfo, String description ) throws Exception {
		CmsBatchOperation cmsBatchOperation = new CmsBatchOperation();
		cmsBatchOperation.setOptType(optType);
		cmsBatchOperation.setObjType(objType);
		cmsBatchOperation.setBundle(bundle);
		cmsBatchOperation.setDescription(description);
		cmsBatchOperation.setErrorCount( 0 );
		cmsBatchOperation.setIsRunning( false );
		cmsBatchOperation.setOldInfo( oldInfo );
		return addOperation( cmsBatchOperation ) ;
	}
	
	/**
	 * 删除批处理信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		if( id == null ) {
			throw new Exception("id can not empty for delete!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			CmsBatchOperation cmsBatchOperation = emc.find(id, CmsBatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for CmsBatchOperation not exists with id:" + id );
			}
			emc.beginTransaction( CmsBatchOperation.class );
			emc.remove( cmsBatchOperation, CheckRemoveType.all ); 
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}		
	}

	public void addErrorTime(CmsBatchOperation operation, int i ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			CmsBatchOperation cmsBatchOperation = emc.find( operation.getId(), CmsBatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for CmsBatchOperation not exists with id:" + operation.getId() );
			}
			cmsBatchOperation.addErrorCount(1);
			emc.beginTransaction( CmsBatchOperation.class );
			emc.check( cmsBatchOperation, CheckPersistType.all ); 
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}	
	}
	
	public void initOperationRunning() throws Exception {
		List<CmsBatchOperation> operations = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			operations = cmsBatchOperationService.list( emc, 1000 );
			if( ListTools.isNotEmpty( operations )) {
				emc.beginTransaction( CmsBatchOperation.class );
				for( CmsBatchOperation operation : operations ) {
					operation.setIsRunning( false );
					emc.check( operation, CheckPersistType.all ); 
				}
				emc.commit();				
			}			
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 检查是否存在未review的Document
	 * 比如因为更新导致文档Review未生成，文档无法被看到的情况
	 */
	public void checkDocumentReviewStatus() {
		DocumentInfoService documentInfoService = new DocumentInfoService();				
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> ids = documentInfoService.listUnReviewIds(emc, 5000);
			if( ListTools.isNotEmpty( ids )) {
				CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
				for( String docId : ids ) {
					cmsBatchOperationPersistService.addOperation( 
							CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
							CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  docId,  docId, "刷新文档权限：ID=" +  docId );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查文档是否设置为IsTop，如果isTop为空，则设置为false，避免空值
	 * @param defaultIsTop 需要设置的默认的值
	 */
    public void checkDocumentIsTop( boolean defaultIsTop ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			DocumentQueryService documentQueryService = new DocumentQueryService();
			DocumentPersistService documentPersistService = new DocumentPersistService();
			List<String> ids = documentQueryService.listNULLIsTopDocIds();
			Document document = null;
			if( ListTools.isNotEmpty( ids )) {
				for( String id : ids ) {
					document = documentQueryService.get( id );
					if( document != null ){
						documentPersistService.refreshDocInfoData( document );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
    }
}
