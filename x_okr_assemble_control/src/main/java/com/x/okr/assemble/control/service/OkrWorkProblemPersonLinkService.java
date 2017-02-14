package com.x.okr.assemble.control.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.jaxrs.okrworkproblempersonlink.WrapInOkrWorkProblemPersonLink;
import com.x.okr.entity.OkrWorkProblemPersonLink;

/**
 * 类   名：OkrWorkProblemPersonLinkService<br/>
 * 实体类：OkrWorkProblemPersonLink<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProblemPersonLinkService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProblemPersonLinkService.class );
	private BeanCopyTools<WrapInOkrWorkProblemPersonLink, OkrWorkProblemPersonLink> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkProblemPersonLink.class, OkrWorkProblemPersonLink.class, null, WrapInOkrWorkProblemPersonLink.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrWorkProblemPersonLink对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkProblemPersonLink get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkProblemPersonLink.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkProblemPersonLink对象
	 * @param wrapIn
	 */
	public OkrWorkProblemPersonLink save( WrapInOkrWorkProblemPersonLink wrapIn ) throws Exception {
		OkrWorkProblemPersonLink okrWorkProblemPersonLink = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemPersonLink =  emc.find( wrapIn.getId(), OkrWorkProblemPersonLink.class );
				if( okrWorkProblemPersonLink != null ){
					emc.beginTransaction( OkrWorkProblemPersonLink.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemPersonLink );
					emc.check( okrWorkProblemPersonLink, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkProblemPersonLink = new OkrWorkProblemPersonLink();
					emc.beginTransaction( OkrWorkProblemPersonLink.class );
					wrapin_copier.copy( wrapIn, okrWorkProblemPersonLink );
					okrWorkProblemPersonLink.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkProblemPersonLink, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkProblemPersonLink update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProblemPersonLink = new OkrWorkProblemPersonLink();
				emc.beginTransaction( OkrWorkProblemPersonLink.class );
				wrapin_copier.copy( wrapIn, okrWorkProblemPersonLink );
				emc.persist( okrWorkProblemPersonLink, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkProblemPersonLink create got a error!", e);
				throw e;
			}
		}
		return okrWorkProblemPersonLink;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkProblemPersonLink对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkProblemPersonLink okrWorkProblemPersonLink = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkProblemPersonLink = emc.find(id, OkrWorkProblemPersonLink.class);
			if (null == okrWorkProblemPersonLink) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkProblemPersonLink.class );
				emc.remove( okrWorkProblemPersonLink, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
