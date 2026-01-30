package com.x.teamwork.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;

public class AttachmentPersistService{
	
	private static  Logger logger = LoggerFactory.getLogger( AttachmentPersistService.class );
		
	public Attachment saveAttachment( Project project, Attachment attachment ) throws Exception {
		attachment.setProjectId( project.getId() );
		attachment.setBundleObjType( "OBJECT" );
		return save( attachment );
	}
	
	public Attachment saveAttachment( Task task, Attachment attachment ) throws Exception {
		attachment.setProjectId( task.getProject() );
		attachment.setBundleObjType( "TASK" );
		return save( attachment );
	}
	
	/**
	 * 向数据库保存Attachment对象
	 * @param wrapIn
	 */
	public Attachment save( Attachment wrapIn ) throws Exception {
		Attachment attachment = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				attachment =  emc.find( wrapIn.getId(), Attachment.class );
				if( attachment != null ){
					emc.beginTransaction( Attachment.class );
					wrapIn.copyTo( attachment, JpaObject.FieldsUnmodify );
					emc.check( attachment, CheckPersistType.all );	
					emc.commit();
				}else{
					attachment = new Attachment();
					emc.beginTransaction( Attachment.class );
					wrapIn.copyTo( attachment );
					attachment.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( attachment, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.warn( "Attachment update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				attachment = new Attachment();
				emc.beginTransaction( Attachment.class );
				wrapIn.copyTo( attachment );
				emc.persist( attachment, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.warn( "Attachment create got a error!", e);
				throw e;
			}
		}
		return attachment;
	}
	
	/**
	 * 根据ID从数据库中删除Attachment对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		Attachment attachment = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			attachment = emc.find(id, Attachment.class);
			if ( null == attachment ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( Attachment.class );
				emc.remove( attachment, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	
	
}
