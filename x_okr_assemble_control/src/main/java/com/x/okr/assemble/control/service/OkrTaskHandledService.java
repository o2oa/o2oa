package com.x.okr.assemble.control.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.WrapInOkrTaskHandled;
import com.x.okr.entity.OkrTaskHandled;

/**
 * 类   名：OkrTaskHandledService<br/>
 * 实体类：OkrTaskHandled<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrTaskHandledService{
	private Logger logger = LoggerFactory.getLogger( OkrTaskHandledService.class );
	private BeanCopyTools<WrapInOkrTaskHandled, OkrTaskHandled> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrTaskHandled.class, OkrTaskHandled.class, null, WrapInOkrTaskHandled.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrTaskHandled对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrTaskHandled get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrTaskHandled.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrTaskHandled对象
	 * @param wrapIn
	 */
	public OkrTaskHandled save( WrapInOkrTaskHandled wrapIn ) throws Exception {
		OkrTaskHandled okrTaskHandled = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrTaskHandled =  emc.find( wrapIn.getId(), OkrTaskHandled.class );
				if( okrTaskHandled != null ){
					emc.beginTransaction( OkrTaskHandled.class );
					wrapin_copier.copy( wrapIn, okrTaskHandled );
					emc.check( okrTaskHandled, CheckPersistType.all );	
					emc.commit();
				}else{
					okrTaskHandled = new OkrTaskHandled();
					emc.beginTransaction( OkrTaskHandled.class );
					wrapin_copier.copy( wrapIn, okrTaskHandled );
					okrTaskHandled.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrTaskHandled, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrTaskHandled update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrTaskHandled = new OkrTaskHandled();
				emc.beginTransaction( OkrTaskHandled.class );
				wrapin_copier.copy( wrapIn, okrTaskHandled );
				emc.persist( okrTaskHandled, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrTaskHandled create got a error!", e);
				throw e;
			}
		}
		return okrTaskHandled;
	}
	
	/**
	 * 根据ID从数据库中删除OkrTaskHandled对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrTaskHandled okrTaskHandled = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrTaskHandled = emc.find(id, OkrTaskHandled.class);
			if (null == okrTaskHandled) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrTaskHandled.class );
				emc.remove( okrTaskHandled, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
