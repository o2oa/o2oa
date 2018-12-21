package com.x.okr.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.entity.OkrTaskHandled;

/**
 * 类   名：OkrTaskHandledService<br/>
 * 实体类：OkrTaskHandled<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class OkrTaskHandledService{
	
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
	 * 根据ID从数据库中删除OkrTaskHandled对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrTaskHandled okrTaskHandled = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrTaskHandled = emc.find(id, OkrTaskHandled.class);
			if (null == okrTaskHandled) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
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
