package com.x.teamwork.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.BatchOperation;

/**
 * 对批处理操作信息持久化服务类利用Service完成事务控制
 * 
 */
public class BatchOperationPersistService {
	
	BatchOperationService cmsBatchOperationService = new BatchOperationService();
	/**
	 * 保存批处理信息对象
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception
	 */
	public BatchOperation addOperation( BatchOperation cmsBatchOperation ) throws Exception {
		if( cmsBatchOperation == null ) {
			throw new Exception("cmsBatchOperation can not null for save!");
		}
		if( StringUtils.isEmpty(cmsBatchOperation.getOptType()  )) {
			throw new Exception("optType can not empty for save BatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getObjType()  )) {
			throw new Exception("objType can not empty for save BatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getBundle()  )) {
			throw new Exception("bundle can not empty for save BatchOperation!");
		}
		if( StringUtils.isEmpty( cmsBatchOperation.getDescription()  )) {
			throw new Exception("description can not empty for save BatchOperation!");
		}		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			emc.beginTransaction( BatchOperation.class );
			emc.persist( cmsBatchOperation, CheckPersistType.all ); 
			emc.commit();
			//将批量操作信息压入队列
			ThisApplication.queueBatchOperation.send( cmsBatchOperation );
		} catch ( Exception e ) {
			throw e;
		}		
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
	public BatchOperation addOperation( String objType, String optType, String bundle, String oldInfo, String description ) throws Exception {
		BatchOperation cmsBatchOperation = new BatchOperation();
		cmsBatchOperation.setOptType(optType);
		cmsBatchOperation.setObjType(objType);
		cmsBatchOperation.setBundle(bundle);
		cmsBatchOperation.setDescription(description);
		cmsBatchOperation.setErrorCount( 0 );
		cmsBatchOperation.setIsRunning( false );
		cmsBatchOperation.setOldInfo( oldInfo );
		addOperation( cmsBatchOperation );	
		return cmsBatchOperation;
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
			BatchOperation cmsBatchOperation = emc.find(id, BatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for BatchOperation not exists with id:" + id );
			}
			emc.beginTransaction( BatchOperation.class );
			emc.remove( cmsBatchOperation, CheckRemoveType.all ); 
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}		
	}

	public void addErrorTime(BatchOperation operation, int i ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			BatchOperation cmsBatchOperation = emc.find( operation.getId(), BatchOperation.class );
			if( cmsBatchOperation == null ) {
				throw new Exception("Entity for BatchOperation not exists with id:" + operation.getId() );
			}
			cmsBatchOperation.addErrorCount(1);
			emc.beginTransaction( BatchOperation.class );
			emc.check( cmsBatchOperation, CheckPersistType.all ); 
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}	
	}
	
	public void initOperationRunning() throws Exception {
		List<BatchOperation> operations = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			operations = cmsBatchOperationService.list( emc, 1000 );
			if( ListTools.isNotEmpty( operations )) {
				emc.beginTransaction( BatchOperation.class );
				for( BatchOperation operation : operations ) {
					operation.setIsRunning( false );
					emc.check( operation, CheckPersistType.all ); 
				}
				emc.commit();				
			}			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
